package net.myriantics.klaxon.item.equipment.tools;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.InstabreakMiningToolItem;
import net.myriantics.klaxon.mixin.AnvilScreenHandlerInvoker;
import net.myriantics.klaxon.registry.minecraft.*;
import net.myriantics.klaxon.recipe.hammering.HammeringRecipe;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.util.EquipmentSlotHelper;

import java.util.List;
import java.util.Optional;

public class HammerItem extends InstabreakMiningToolItem {

    public HammerItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, KlaxonBlockTags.HAMMER_MINEABLE, settings.maxCount(1));
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
        damageItem(stack, attacker, true);
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
        ItemStack handStack = context.getStack();

        // check if player is actually in the position to hammer stuff before doing anything
        if (player != null && canProcessHammerRecipe(player)) {

            List<ItemEntity> selectedItems = world.getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class), Box.of(clickedPos, 0.5, 0.5, 0.5), (e) -> true);

            // if there aren't any dropped items in the targeted area, don't do anything
            if (selectedItems.isEmpty()) {
                return ActionResult.PASS;
            }

            // play sound and damage item only after we're sure there are items selected
            world.playSound(player, BlockPos.ofFloored(clickedPos), SoundEvents.BLOCK_BASALT_BREAK, SoundCategory.PLAYERS, 2, 2f);
            damageItem(handStack, player, true);

            boolean recipeSuccessPresent = false;

            // run recipe and dropping code for each selected dropped item
            for (ItemEntity targetItemEntity : selectedItems) {

                // break one item off of the target entity stack
                ItemStack targetStack = targetItemEntity.getStack().copy().split(1);
                Position outputPos = targetItemEntity.getPos();

                // dont run recipe stuff on the client
                if (player instanceof ServerPlayerEntity serverPlayer) {

                    // initialize output stack
                    ItemStack outputStack = ItemStack.EMPTY.copy();

                    // high five emoji from the emoji movie
                    Hand usedHand = context.getHand();
                    Hand oppositeHand = EquipmentSlotHelper.getOppositeHand(usedHand);

                    ItemStack appliedStack = player.getStackInHand(oppositeHand).copy();

                    // check if there's something to apply before attempting to do an anvil interaction
                    if (!appliedStack.isEmpty()) {
                        // get results of anvil interaction
                        AnvilScreenHandler screenHandler = processAnvilInteraction(serverPlayer, (ServerWorld) world, context.getBlockPos(), targetStack, appliedStack);
                        ItemStack anvilOutputStack = screenHandler.getStacks().get(screenHandler.getResultSlotIndex());

                        // item in targeted entity will be replaced with anviled version
                        targetItemEntity.setStack(anvilOutputStack);

                        // update exp costs and everything - this is done after other calculations because shits fucky
                        ((AnvilScreenHandlerInvoker)screenHandler).klaxon$invokeOnTakeOutput(player, anvilOutputStack);

                        // now we can decrement the applied stack once the calculations have been done
                        serverPlayer.setStackInHand(oppositeHand, screenHandler.getStacks().get(1));
                    }

                    // only try hammering recipe if output stack is empty
                    if (outputStack.isEmpty()) {
                        RecipeInput dummyInventory = new RecipeInput() {
                            @Override
                            public ItemStack getStackInSlot(int slot) {
                                return new SimpleInventory(targetStack).getStack(slot);
                            }

                            @Override
                            public int getSize() {
                                return 1;
                            }
                        };

                        Optional<RecipeEntry<HammeringRecipe>> match = world.getRecipeManager().getFirstMatch(KlaxonRecipeTypes.HAMMERING, dummyInventory, world);

                        if (match.isPresent()) {
                            outputStack = match.get().value().craft(dummyInventory, world.getRegistryManager());

                            // indicate that at least one craft was successful
                            recipeSuccessPresent = true;
                            // decrement target dropped item's stack because a match was present, so the item was used up in crafting
                            targetStack.decrement(1);
                            if (targetStack.getCount() == 0) {
                                targetItemEntity.remove(Entity.RemovalReason.DISCARDED);
                            } else {
                                targetItemEntity.setStack(targetStack);
                            }
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
                    spawnHammeringParticleEffects(world, targetStack, 5, targetItemEntity);
                }
            }

            if (!world.isClient()) {
                // pop the hammering advancement
                if (recipeSuccessPresent) KlaxonAdvancementTriggers.triggerHammerUse((ServerPlayerEntity) player, UsageType.RECIPE_SUCCESS);
                // trip sculk sensors
                world.emitGameEvent(player, GameEvent.BLOCK_DESTROY, clickedPos);
            }

            return ActionResult.SUCCESS;

        }

        return ActionResult.PASS;
    }

    public static boolean canProcessHammerRecipe(PlayerEntity player) {
        // has conditions so that player has control - as well as item hitboxes not blocking block selection constantly
        return player.isOnGround() && player.isSneaking();
    }

    private static void damageItem(ItemStack stack, LivingEntity attacker, boolean usedProperly) {
        stack.damage(usedProperly ? 1 : 2, attacker, EquipmentSlotHelper.convert(attacker.getActiveHand()));
    }

    // yoinked from living entity
    private static void spawnHammeringParticleEffects(World world, ItemStack stack, int count, Entity source) {
        Random random = source.getRandom();
        float pitch = source.getPitch();
        float yaw = source.getYaw();

        for (int i = 0; i < count; i++) {
            Vec3d vec3d = new Vec3d(((double)random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3d = vec3d.rotateX(-pitch * (float) (Math.PI / 180.0));
            vec3d = vec3d.rotateY(-yaw * (float) (Math.PI / 180.0));
            double d = (double)(-random.nextFloat()) * 0.6 - 0.3;
            Vec3d vec3d2 = new Vec3d(((double)random.nextFloat() - 0.5) * 0.3, d, 0.6);
            vec3d2 = vec3d2.rotateX(-pitch * (float) (Math.PI / 180.0));
            vec3d2 = vec3d2.rotateY(-yaw * (float) (Math.PI / 180.0));
            vec3d2 = vec3d2.add(source.getX(), source.getEyeY(), source.getZ());
            source.getWorld().addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
        }
    }

    private AnvilScreenHandler processAnvilInteraction(ServerPlayerEntity player, ServerWorld world, BlockPos pos, ItemStack targetStack, ItemStack appliedStack) {
        // we don't need to do any further processing if there are no items to apply

        KlaxonCommon.LOGGER.info("Tried to process Anvil Recipe with stqck: " + targetStack.getItem());

        AnvilScreenHandler screenHandler = new AnvilScreenHandler(player.currentScreenHandler.syncId, player.getInventory(), ScreenHandlerContext.create(world, pos));
        // define target stack as stack to be worked on
        screenHandler.setStackInSlot(0, 0, targetStack);
        // define stack opposite to hammer as stack to be applied
        screenHandler.setStackInSlot(1, 0, appliedStack);
        // make sure we update result
        screenHandler.updateResult();

        // yoink the output slot
        Slot outputSlot = screenHandler.getSlot(2);

        // if we can't take output, no need to continue
        if (!((AnvilScreenHandlerInvoker)screenHandler).klaxon$invokeCanTakeOutput(player, outputSlot.hasStack())) return screenHandler;

        KlaxonCommon.LOGGER.info("Valid Anvil Recipe Detected - Proceeding");

        KlaxonCommon.LOGGER.info("Output Slot Stack Contents: " + outputSlot.getStack().toString());

        return screenHandler;
    }

    public enum UsageType implements StringIdentifiable {
        RECIPE_SUCCESS,
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
