package net.myriantics.klaxon.recipe.tool_usage;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.component.configuration.ToolUseRecipeConfigComponent;
import net.myriantics.klaxon.registry.minecraft.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.minecraft.KlaxonRecipeTypes;
import net.myriantics.klaxon.util.EquipmentSlotHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// Inspiration taken from AE2's Item Transformation system
public abstract class ToolUsageRecipeLogic {
    private static Set<Item> VALID_RECIPE_TOOL_CACHE = new HashSet<>();
    public static final int MAX_SOUNDS_PER_ACTION = 4;
    public static final int MAX_PARTICLE_CREATION_ACTIONS_PER_ACTION = 16;

    public static boolean test(World world, ItemStack stack) {
        return getUsableTools(world).contains(stack.getItem());
    }

    private static Set<Item> getUsableTools(World world) {
        if (VALID_RECIPE_TOOL_CACHE.isEmpty()) {

            Set<Item> newCache = new HashSet<>();
            for (RecipeEntry<ToolUsageRecipe> entry : world.getRecipeManager().listAllOfType(KlaxonRecipeTypes.TOOL_USAGE)) {
                // add all the compatible items to the new cache
                for (ItemStack stack : entry.value().getRequiredTool().getMatchingStacks()) {
                    newCache.add(stack.getItem());
                }
            }

            // update stored cache
            VALID_RECIPE_TOOL_CACHE = newCache;
            return newCache;
        } else {
            return VALID_RECIPE_TOOL_CACHE;
        }
    }

    /**
     * Called on server & client. Handles recipe logic for ToolUsageRecipes. Called in ItemStackMixin.
     * @param context
     * Item usage context go brr.
     * @return
     * Returns ActionResult.SUCCESS if recipe succeeds - ActionResult.PASS otherwise.
     */
    public static ActionResult runRecipeLogic(ItemUsageContext context, @Nullable ActionResult original) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        Vec3d clickedPos = context.getHitPos();
        ItemStack toolStack = context.getStack();
        Hand usedHand = context.getHand();

        // if we're not provided an action result, default to pass
        if (original == null) original = ActionResult.PASS;

        // make sure player is valid for recipe processing before doing anything
        if (!isPlayerValid(player)) return original;

        ToolUseRecipeConfigComponent component = ToolUseRecipeConfigComponent.get(toolStack);
        if (component == null) component = new ToolUseRecipeConfigComponent(SoundEvents.BLOCK_STONE_BREAK);

        // defines if tool can cosmetically hit items, making sound and particles but not sculk vibrations
        boolean canCosmeticUse = component.canCosmeticUse();

        boolean recipeSuccess = false;
        // this is in place to prevent hammering from taking up the whole sound cap
        int totalPlayedSounds = 0;
        int totalParticleSpawnActions = 0;

        List<ItemEntity> selectedItems = world.getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class), Box.of(clickedPos, 0.8, 0.8, 0.8), (e) -> true);

        // if there aren't any dropped items in the targeted area, don't do anything
        if (selectedItems.isEmpty()) {
            return original;
        }

        for (ItemEntity targetItemEntity : selectedItems) {
            ItemStack targetStack = targetItemEntity.getStack().copy();
            Position outputPos = targetItemEntity.getPos();

            SoundEvent recipeSoundOverride = null;
            boolean targetRecipeSuccess = false;

            // necessary so that the client knows if it's completed a recipe or not
            if (world.isClient()) {
                RecipeInput dummyInventory = getRecipeInput(targetStack, toolStack);

                Optional<RecipeEntry<ToolUsageRecipe>> match = world.getRecipeManager().getFirstMatch(KlaxonRecipeTypes.TOOL_USAGE, dummyInventory, world);

                // change recipe success indicator and recipe sound override
                if (match.isPresent()) {
                    targetRecipeSuccess = true;
                    SoundEvent soundEvent = match.get().value().getSoundOverride();
                    recipeSoundOverride = soundEvent == null || soundEvent.equals(SoundEvents.INTENTIONALLY_EMPTY) ? null : soundEvent;
                }

                // spawn particles if recipe was successful or cosmetic usage is enabled
                if ((match.isPresent() || canCosmeticUse) && totalParticleSpawnActions < MAX_PARTICLE_CREATION_ACTIONS_PER_ACTION) {
                    spawnToolUseParticleEffects(world, targetStack, 5, targetItemEntity);
                    totalParticleSpawnActions++;
                }
            }

            if (world instanceof ServerWorld serverWorld) {
                RecipeInput dummyInventory = getRecipeInput(targetStack, toolStack);

                Optional<RecipeEntry<ToolUsageRecipe>> match = world.getRecipeManager().getFirstMatch(KlaxonRecipeTypes.TOOL_USAGE, dummyInventory, world);

                if (match.isPresent()) {
                    targetRecipeSuccess = true;

                    targetStack.decrement(1);
                    if (targetStack.getCount() == 0) {
                        targetItemEntity.discard();
                    } else {
                        targetItemEntity.setStack(targetStack);
                    }

                    ItemStack outputStack = match.get().value().craft(dummyInventory, serverWorld.getRegistryManager());

                    // make sure to proc advancement trigger before spawning item
                    KlaxonAdvancementTriggers.triggerToolUsageCraft((ServerPlayerEntity) player, toolStack, outputStack);

                    // dump item out in-world
                    ItemScatterer.spawn(
                            world,
                            outputPos.getX(),
                            outputPos.getY(),
                            outputPos.getZ(),
                            outputStack
                    );

                    SoundEvent soundEvent = match.get().value().getSoundOverride();

                    // make sure to get the sound override - ignore empty ones
                    recipeSoundOverride = soundEvent == null || soundEvent.equals(SoundEvents.INTENTIONALLY_EMPTY) ? null : soundEvent;
                }
            }

            // both client and server know if a recipe was successful - also play sound for every item processed in an interaction because it sounds better and signifies that more items were processed
            // this caps out at 4 sounds because otherwise people are going to take up the whole sound cap with it
            if ((targetRecipeSuccess || canCosmeticUse) && totalPlayedSounds < MAX_SOUNDS_PER_ACTION) {
                world.playSound(player, BlockPos.ofFloored(clickedPos), recipeSoundOverride != null ? recipeSoundOverride : component.usageSound(), SoundCategory.PLAYERS, 1, 1.0f + 0.4f * world.getRandom().nextFloat());
                totalPlayedSounds++;
            }

            // commit recipe success status after all calculations
            recipeSuccess = recipeSuccess || targetRecipeSuccess;
        }

        if (world instanceof ServerWorld serverWorld) {
            if (recipeSuccess) {
                // trip sculk sensors and damage tool
                serverWorld.emitGameEvent(player, GameEvent.ITEM_INTERACT_FINISH, clickedPos);
                if (player != null) toolStack.damage(1, player, EquipmentSlotHelper.convert(usedHand));
            }
        }

        // if we succeeded at any recipes, we win. also preserve original action result if we do nothing.
        // if cosmetic usage is enabled, we also succeed because yeah
        return recipeSuccess || canCosmeticUse ? ActionResult.SUCCESS : original;
    }

    private static @NotNull RecipeInput getRecipeInput(ItemStack targetStack, ItemStack toolStack) {
        return new RecipeInput() {
            @Override
            public ItemStack getStackInSlot(int slot) {
                return switch (slot) {
                    case 0 -> toolStack;
                    case 1 -> targetStack;
                    default -> ItemStack.EMPTY;
                };
            }

            @Override
            public int getSize() {
                return 2;
            }
        };
    }

    private static void clearCache() {
        VALID_RECIPE_TOOL_CACHE.clear();
    }

    public static void onServerStarted(MinecraftServer minecraftServer) {
        clearCache();
    }

    public static void onDatapackReload(MinecraftServer minecraftServer, LifecycledResourceManager lifecycledResourceManager, boolean success) {
        if (success) clearCache();
    }

    public static void onTagsLoaded(DynamicRegistryManager registryManager, boolean success) {
        if (success) clearCache();
    }

    public static boolean isPlayerValid(@Nullable PlayerEntity player) {
        return player == null || player.isOnGround() || player instanceof FakePlayer;
    }

    // yoinked from living entity
    public static void spawnToolUseParticleEffects(World world, ItemStack stack, int count, Entity source) {
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
}
