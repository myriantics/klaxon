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
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.component.configuration.ToolUseRecipeConfigComponent;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;
import net.myriantics.klaxon.util.EquipmentSlotHelper;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// Inspiration taken from AE2's Item Transformation system
public abstract class ToolUsageRecipeLogic {

    private static Set<Item> VALID_TOOLS_CACHE = new HashSet<>();
    public static final int MAX_SOUNDS_PER_ACTION = 4;
    public static final int MAX_PARTICLE_CREATION_ACTIONS_PER_ACTION = 16;

    public static boolean test(World world, ItemStack stack) {
        return getValidToolsCache(world).contains(stack.getItem());
    }

    private static Set<Item> getValidToolsCache(World world) {
        if (VALID_TOOLS_CACHE.isEmpty()) {
            Set<Item> newCache = new HashSet<>();
            for (RegistryEntry<ToolUsageRecipeType> type : world.getRegistryManager().get(KlaxonRegistryKeys.TOOL_USAGE_RECIPE_TYPE).getIndexedEntries()) {
                for (ItemStack stack : type.value().validTools().getMatchingStacks()) {
                    newCache.add(stack.getItem());
                }
            }

            // update stored cache
            VALID_TOOLS_CACHE = newCache;
            return newCache;
        } else {
            return VALID_TOOLS_CACHE;
        }
    }

    /**
     * Called on server & client. Handles recipe logic for ToolUsageRecipes. Called in ItemStackMixin.
     * @param context
     * Item usage context go brr.
     * @return
     * Returns ActionResult.SUCCESS if recipe succeeds - ActionResult.PASS otherwise.
     */
    public static ToolUsageRecipeResult runRecipeLogic(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        Vec3d clickedPos = context.getHitPos();
        ItemStack toolStack = context.getStack();
        Hand usedHand = context.getHand();

        // make sure player is valid for recipe processing before doing anything
        if (!isPlayerValid(player)) {
            return ToolUsageRecipeResult.FAIL;
        }

        ToolUseRecipeConfigComponent component = toolStack.getOrDefault(KlaxonDataComponentTypes.TOOL_USE_RECIPE_CONFIG, ToolUseRecipeConfigComponent.DEFAULT);

        boolean didAtLeastOneRecipeSucceed = false;
        // this is in place to prevent hammering from taking up the whole sound cap
        int totalPlayedSounds = 0;
        int totalParticleSpawnActions = 0;

        List<ItemEntity> selectedItems = world.getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class), Box.of(clickedPos, 0.8, 0.8, 0.8), (e) -> true);

        // if there aren't any dropped items in the targeted area, don't do anything
        if (selectedItems.isEmpty()) {
            return ToolUsageRecipeResult.FAIL;
        }

        RegistryKey<ToolUsageRecipeType> type = null;
        for (RegistryEntry<ToolUsageRecipeType> entry : world.getRegistryManager().get(KlaxonRegistryKeys.TOOL_USAGE_RECIPE_TYPE).getIndexedEntries()) {
            if (entry.value().validTools().test(toolStack) && entry.getKey().isPresent()) {
                type = entry.getKey().get();
                break;
            }
        }

        // defines if tool can cosmetically hit items, making sound and particles but not sculk vibrations
        boolean canCosmeticUse = component.canCosmeticUse();

        if (type == null) {
            return ToolUsageRecipeResult.FAIL;
        }

        for (ItemEntity targetItemEntity : selectedItems) {
            ItemStack targetStack = targetItemEntity.getStack().copy();
            Position outputPos = targetItemEntity.getPos();

            SoundEvent recipeSoundOverride = null;
            boolean targetRecipeSuccess = false;

            ToolUsageRecipeInput dummyInventory = new ToolUsageRecipeInput(toolStack, targetStack, type);
            Optional<RecipeEntry<ToolUsageRecipe>> match = world.getRecipeManager().getFirstMatch(KlaxonRecipeTypes.TOOL_USAGE, dummyInventory, world);

            // change recipe success indicator and recipe sound override
            if (match.isPresent()) {
                targetRecipeSuccess = true;
                SoundEvent soundEvent = match.get().value().getSound();
                recipeSoundOverride = soundEvent == null || soundEvent.equals(SoundEvents.INTENTIONALLY_EMPTY) ? null : soundEvent;

                if (!world.isClient()) {
                    targetStack.decrement(1);
                    if (targetStack.getCount() == 0) {
                        targetItemEntity.discard();
                    } else {
                        targetItemEntity.setStack(targetStack);
                    }

                    ItemStack outputStack = match.get().value().craft(dummyInventory, world.getRegistryManager());

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

                }
            }

            // spawn particles if recipe was successful or cosmetic usage is enabled
            if ((targetRecipeSuccess || canCosmeticUse) && totalParticleSpawnActions < MAX_PARTICLE_CREATION_ACTIONS_PER_ACTION) {
                spawnToolUseParticleEffects(world, targetStack, 5, targetItemEntity);
                totalParticleSpawnActions++;
            }

            // this caps out at 4 sounds because otherwise people are going to take up the whole sound cap with it
            if ((targetRecipeSuccess || canCosmeticUse) && totalPlayedSounds < MAX_SOUNDS_PER_ACTION) {
                world.playSound(player, BlockPos.ofFloored(clickedPos), recipeSoundOverride != null ? recipeSoundOverride : component.usageSound(), SoundCategory.PLAYERS, 1, 1.0f + 0.4f * world.getRandom().nextFloat());
                totalPlayedSounds++;
            }

            // commit recipe success status after all calculations
            didAtLeastOneRecipeSucceed |= targetRecipeSuccess;
        }

        if (world instanceof ServerWorld serverWorld) {
            if (didAtLeastOneRecipeSucceed) {
                // trip sculk sensors and damage tool
                serverWorld.emitGameEvent(player, GameEvent.ITEM_INTERACT_FINISH, clickedPos);
                if (player != null) toolStack.damage(1, player, EquipmentSlotHelper.convert(usedHand));
            }
        }

        // if we succeeded at any recipes, we win. also preserve original action result if we do nothing.
        // if cosmetic usage is enabled, we also succeed because yeah
        if (didAtLeastOneRecipeSucceed) {
            return ToolUsageRecipeResult.COSMETIC_SUCCESS;
        } else {
            if (canCosmeticUse) {
                return ToolUsageRecipeResult.COSMETIC_SUCCESS;
            } else {
                return ToolUsageRecipeResult.FAIL;
            }
        }
    }

    private static void clearCache() {
        VALID_TOOLS_CACHE.clear();
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
