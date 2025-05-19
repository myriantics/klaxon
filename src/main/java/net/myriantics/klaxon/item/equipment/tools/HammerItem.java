package net.myriantics.klaxon.item.equipment.tools;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.api.InstabreakMiningToolItem;
import net.myriantics.klaxon.component.configuration.ToolUseRecipeConfigComponent;
import net.myriantics.klaxon.mixin.AnvilScreenHandlerInvoker;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeLogic;
import net.myriantics.klaxon.registry.minecraft.KlaxonDataComponentTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.util.EquipmentSlotHelper;

import java.util.List;

public class HammerItem extends InstabreakMiningToolItem {

    public HammerItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, KlaxonBlockTags.HAMMER_MINEABLE, settings
                .component(KlaxonDataComponentTypes.TOOL_USE_RECIPE_CONFIG, new ToolUseRecipeConfigComponent(SoundEvents.BLOCK_ANVIL_LAND))
        );
    }

    // Walljumping is now component based
    public static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material, float baseAttackDamage, float attackSpeed) {
        return AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, material.getAttackDamage() + baseAttackDamage, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, attackSpeed, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                ).build();
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        damageItem(stack, attacker);
        return super.postHit(stack, target, attacker);
    }

    @Override
    public boolean isCorrectForInstabreak(ItemStack stack, BlockState state) {
        return state.isIn(KlaxonBlockTags.HAMMER_INSTABREAKABLE);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        Vec3d clickedPos = context.getHitPos();
        ItemStack toolStack = context.getStack();
        Hand usedHand = context.getHand();

        boolean didAnvilMimicrySucceed = false;

        // check if player is actually in the position to hammer stuff before doing anything
        if (player != null && ToolUsageRecipeLogic.isPlayerValid(player)) {

            List<ItemEntity> selectedItems = world.getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class), Box.of(clickedPos, 0.8, 0.8, 0.8), (e) -> true);

            // if there aren't any dropped items in the targeted area, don't do anything
            if (selectedItems.isEmpty()) {
                return ActionResult.PASS;
            }

            // damage item only after we're sure there are items selected
            damageItem(toolStack, player);

            // run recipe and dropping code for each selected dropped item
            for (ItemEntity targetItemEntity : selectedItems) {

                // break one item off of the target entity stack
                ItemStack targetStack = targetItemEntity.getStack().copy();
                Position outputPos = targetItemEntity.getPos();

                // dont run recipe stuff on the client
                if (player instanceof ServerPlayerEntity serverPlayer) {

                    // initialize output stack
                    ItemStack outputStack = ItemStack.EMPTY.copy();

                    // high five emoji from the emoji movie
                    Hand oppositeHand = EquipmentSlotHelper.getOppositeHand(usedHand);

                    ItemStack appliedStack = player.getStackInHand(oppositeHand).copy();

                    // check if there's something to apply before attempting to do an anvil interaction
                    if (!appliedStack.isEmpty()) {
                        // get results of anvil interaction
                        AnvilScreenHandler screenHandler = processAnvilInteraction(serverPlayer, (ServerWorld) world, context.getBlockPos(), targetStack, appliedStack);
                        ItemStack anvilOutputStack = screenHandler.getStacks().get(screenHandler.getResultSlotIndex());

                        // only do this if we're sure the interaction actually had an output
                        if (!anvilOutputStack.isEmpty()) {
                            // item in targeted entity will be replaced with anviled version
                            targetItemEntity.setStack(anvilOutputStack);

                            // update exp costs and everything - this is done after other calculations because shits fucky
                            ((AnvilScreenHandlerInvoker)screenHandler).klaxon$invokeOnTakeOutput(player, anvilOutputStack);

                            // now we can decrement the applied stack once the calculations have been done - only decrements when not in creative
                            if (!serverPlayer.isCreative()) serverPlayer.setStackInHand(oppositeHand, screenHandler.getStacks().get(1));

                            didAnvilMimicrySucceed = true;
                        }
                    }

                    // spawn the dropped output item
                    ItemScatterer.spawn(
                            world,
                            outputPos.getX(),
                            outputPos.getY(),
                            outputPos.getZ(),
                            outputStack
                    );

                } else {
                    // spawn hammering particle effects
                    ToolUsageRecipeLogic.spawnToolUseParticleEffects(world, targetStack, 5, targetItemEntity);
                }
            }

            if (!world.isClient()) {
                // trip sculk sensors
                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, clickedPos);

                if (didAnvilMimicrySucceed) toolStack.damage(4, player, EquipmentSlotHelper.convert(context.getHand()));
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private static void damageItem(ItemStack stack, LivingEntity attacker) {
        stack.damage(1, attacker, EquipmentSlotHelper.convert(attacker.getActiveHand()));
    }

    private AnvilScreenHandler processAnvilInteraction(ServerPlayerEntity player, ServerWorld world, BlockPos pos, ItemStack targetStack, ItemStack appliedStack) {
        // we don't need to do any further processing if there are no items to apply

        // KlaxonCommon.LOGGER.info("Tried to process Anvil Recipe with stqck: " + targetStack.getItem());

        AnvilScreenHandler screenHandler = new AnvilScreenHandler(player.currentScreenHandler.syncId, player.getInventory(), ScreenHandlerContext.create(world, pos));
        // define target stack as stack to be worked on
        screenHandler.setStackInSlot(0, 0, targetStack.copy());
        // define stack opposite to hammer as stack to be applied
        screenHandler.setStackInSlot(1, 0, appliedStack.copy());
        // make sure we update result
        screenHandler.updateResult();

        // yoink the output slot
        Slot outputSlot = screenHandler.getSlot(2);

        // if we can't take output, no need to continue
        if (!((AnvilScreenHandlerInvoker)screenHandler).klaxon$invokeCanTakeOutput(player, outputSlot.hasStack())) return screenHandler;

        // KlaxonCommon.LOGGER.info("Valid Anvil Recipe Detected - Proceeding");
        // KlaxonCommon.LOGGER.info("Output Slot Stack Contents: " + outputSlot.getStack().toString());

        return screenHandler;
    }

    public enum UsageType implements StringIdentifiable {
        WALLJUMP_SUCCEEDED,
        STRENGTH_WALLJUMP_SUCCEEDED,
        WALLJUMP_FAILED,
        MINECART_WALLJUMP_SUCCESS;

        private static final Codec<UsageType> CODEC = StringIdentifiable.createCodec(UsageType::values);

        @Override
        public String asString() {
            return this.toString().toLowerCase();
        }

        public static Codec<UsageType> getCodec() {
            return CODEC;
        }
    }
}
