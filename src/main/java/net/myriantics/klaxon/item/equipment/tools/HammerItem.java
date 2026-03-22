package net.myriantics.klaxon.item.equipment.tools;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.component.ability.InstabreakingToolComponent;
import net.myriantics.klaxon.component.configuration.ToolUseRecipeConfigComponent;
import net.myriantics.klaxon.mixin.minecraft.anvil_emulation.AnvilMenuInvoker;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeLogic;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.util.EquipmentSlotHelper;

import java.util.List;

public class HammerItem extends DiggerItem {

    public HammerItem(Tier toolMaterial, Properties settings) {
        super(toolMaterial, KlaxonBlockTags.HAMMER_MINEABLE, settings
                .component(KlaxonDataComponentTypes.TOOL_USE_RECIPE_CONFIG.value(), new ToolUseRecipeConfigComponent(KlaxonSoundEvents.ITEM_HAMMER_USAGE, true))
                .component(KlaxonDataComponentTypes.INSTABREAK_TOOL_COMPONENT.value(), new InstabreakingToolComponent(KlaxonBlockTags.HAMMER_INSTABREAKABLE))
        );
    }

    // Walljumping is now component based
    public static ItemAttributeModifiers createAttributes(Tier material, float baseAttackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, material.getAttackDamageBonus() + baseAttackDamage, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                ).build();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        damageItem(stack, attacker);
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        Player player = context.getPlayer();
        Vec3 clickedPos = context.getClickLocation();
        ItemStack toolStack = context.getItemInHand();
        InteractionHand usedHand = context.getHand();
        InteractionHand oppositeHand = EquipmentSlotHelper.getOppositeHand(usedHand);

        boolean didAnvilMimicrySucceed = false;

        // check if player is actually in the position to hammer stuff before doing anything
        if (player != null && ToolUsageRecipeLogic.isPlayerValid(player)) {

            List<ItemEntity> selectedItems = world.getEntities(EntityTypeTest.forClass(ItemEntity.class), AABB.ofSize(clickedPos, 0.8, 0.8, 0.8), (e) -> true);

            // if there aren't any dropped items in the targeted area, don't do anything
            if (selectedItems.isEmpty()) {
                return InteractionResult.PASS;
            }


            // damage item only after we're sure there are items selected
            damageItem(toolStack, player);

            // used to limit particle spam
            int totalParticleSpawnActionsRun = 0;

            // run recipe and dropping code for each selected dropped item
            for (ItemEntity targetItemEntity : selectedItems) {

                // break one item off of the target entity stack
                ItemStack targetStack = targetItemEntity.getItem().copy();

                ItemStack appliedStack = player.getItemInHand(oppositeHand).copy();

                // check if there's something to apply before attempting to do an anvil interaction
                if (!appliedStack.isEmpty()) {
                    // get results of anvil interaction
                    AnvilMenu screenHandler = processAnvilInteraction(player, world, context.getClickedPos(), targetStack, appliedStack);
                    ItemStack anvilOutputStack = screenHandler.getItems().get(screenHandler.getResultSlot());

                    // only do this if we're sure the interaction actually had an output
                    if (!anvilOutputStack.isEmpty()) {
                        didAnvilMimicrySucceed = true;

                        // dont run recipe stuff on the client
                        if (player instanceof ServerPlayer serverPlayer)  {
                            // item in targeted entity will be replaced with anviled version
                            targetItemEntity.setItem(anvilOutputStack);

                            // update exp costs and everything - this is done after other calculations because shits fucky
                            ((AnvilMenuInvoker)screenHandler).klaxon$invokeOnTakeOutput(player, anvilOutputStack);

                            // now we can decrement the applied stack once the calculations have been done - only decrements when not in creative
                            if (!serverPlayer.isCreative()) serverPlayer.setItemInHand(oppositeHand, screenHandler.getItems().get(1));
                        } else {
                            // protect against spawning too many particles
                            if (totalParticleSpawnActionsRun < ToolUsageRecipeLogic.MAX_PARTICLE_CREATION_ACTIONS_PER_ACTION) {
                                // spawn hammering particle effects
                                ToolUsageRecipeLogic.spawnToolUseParticleEffects(world, targetStack, 5, targetItemEntity);
                                totalParticleSpawnActionsRun++;
                            }
                        }
                    }
                }
            }

            if (!world.isClientSide()) {
                // trip sculk sensors
                world.gameEvent(player, GameEvent.BLOCK_CHANGE, clickedPos);

                if (didAnvilMimicrySucceed) toolStack.hurtAndBreak(4, player, EquipmentSlotHelper.convert(context.getHand()));
            }
        }

        return didAnvilMimicrySucceed ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }

    private static void damageItem(ItemStack stack, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlotHelper.convert(attacker.getUsedItemHand()));
    }

    private AnvilMenu processAnvilInteraction(Player player, Level world, BlockPos pos, ItemStack targetStack, ItemStack appliedStack) {
        // we don't need to do any further processing if there are no items to apply

        // KlaxonCommon.LOGGER.info("Tried to process Anvil Recipe with stqck: " + targetStack.getItem());

        AnvilMenu screenHandler = new AnvilMenu(player.containerMenu.containerId, player.getInventory(), ContainerLevelAccess.create(world, pos));
        // define target stack as stack to be worked on
        screenHandler.setItem(0, 0, targetStack.copy());
        // define stack opposite to hammer as stack to be applied
        screenHandler.setItem(1, 0, appliedStack.copy());
        // make sure we update result
        screenHandler.createResult();

        // yoink the output slot
        Slot outputSlot = screenHandler.getSlot(2);

        // if we can't take output, no need to continue
        if (!((AnvilMenuInvoker)screenHandler).klaxon$invokeCanTakeOutput(player, outputSlot.hasItem())) return screenHandler;

        return screenHandler;
    }

    public enum UsageType implements StringRepresentable {
        NORMAL_WALLJUMP,
        BOOSTED_WALLJUMP,
        MINECART_WALLJUMP;

        private static final Codec<UsageType> CODEC = StringRepresentable.fromEnum(UsageType::values);

        @Override
        public String getSerializedName() {
            return this.toString().toLowerCase();
        }

        public static Codec<UsageType> getCodec() {
            return CODEC;
        }
    }
}
