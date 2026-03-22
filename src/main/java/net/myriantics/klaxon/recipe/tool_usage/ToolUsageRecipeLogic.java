package net.myriantics.klaxon.recipe.tool_usage;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.component.configuration.ToolUseRecipeConfigComponent;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;
import net.myriantics.klaxon.util.EquipmentSlotHelper;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// Inspiration taken from AE2's Item Transformation system
public abstract class ToolUsageRecipeLogic {

    private static Set<Item> VALID_TOOLS_CACHE = new HashSet<>();
    public static final int MAX_SOUNDS_PER_ACTION = 4;
    public static final int MAX_PARTICLE_CREATION_ACTIONS_PER_ACTION = 16;

    public static boolean test(Level world, ItemStack stack) {
        return getValidToolsCache(world).contains(stack.getItem());
    }

    private static Set<Item> getValidToolsCache(Level world) {
        if (VALID_TOOLS_CACHE.isEmpty()) {
            Set<Item> newCache = new HashSet<>();
            for (Holder<ToolUsageRecipeType> type : world.registryAccess().registryOrThrow(KlaxonRegistryKeys.TOOL_USAGE_RECIPE_TYPE).asHolderIdMap()) {
                for (ItemStack stack : type.value().validTools().getItems()) {
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
    public static ToolUsageRecipeResult runRecipeLogic(UseOnContext context) {
        Level world = context.getLevel();
        Player player = context.getPlayer();
        Vec3 clickedPos = context.getClickLocation();
        ItemStack toolStack = context.getItemInHand();
        InteractionHand usedHand = context.getHand();

        // make sure player is valid for recipe processing before doing anything
        if (!isPlayerValid(player)) {
            return ToolUsageRecipeResult.FAIL;
        }

        ToolUseRecipeConfigComponent component = toolStack.getOrDefault(KlaxonDataComponentTypes.TOOL_USE_RECIPE_CONFIG.value(), ToolUseRecipeConfigComponent.DEFAULT);

        boolean didAtLeastOneRecipeSucceed = false;
        // this is in place to prevent hammering from taking up the whole sound cap
        int totalPlayedSounds = 0;
        int totalParticleSpawnActions = 0;

        List<ItemEntity> selectedItems = world.getEntities(EntityTypeTest.forClass(ItemEntity.class), AABB.ofSize(clickedPos, 0.8, 0.8, 0.8), (e) -> true);

        // if there aren't any dropped items in the targeted area, don't do anything
        if (selectedItems.isEmpty()) {
            return ToolUsageRecipeResult.FAIL;
        }

        ResourceKey<ToolUsageRecipeType> type = null;
        for (Holder<ToolUsageRecipeType> entry : world.registryAccess().registryOrThrow(KlaxonRegistryKeys.TOOL_USAGE_RECIPE_TYPE).asHolderIdMap()) {
            if (entry.value().validTools().test(toolStack) && entry.unwrapKey().isPresent()) {
                type = entry.unwrapKey().get();
                break;
            }
        }

        // defines if tool can cosmetically hit items, making sound and particles but not sculk vibrations
        boolean canCosmeticUse = component.canCosmeticUse();

        if (type == null) {
            return ToolUsageRecipeResult.FAIL;
        }

        for (ItemEntity targetItemEntity : selectedItems) {
            ItemStack targetStack = targetItemEntity.getItem().copy();
            Position outputPos = targetItemEntity.position();

            SoundEvent recipeSoundOverride = null;
            boolean targetRecipeSuccess = false;

            ToolUsageRecipeInput dummyInventory = new ToolUsageRecipeInput(toolStack, targetStack, type);
            Optional<RecipeHolder<ToolUsageRecipe>> match = world.getRecipeManager().getRecipeFor(KlaxonRecipeTypes.TOOL_USAGE, dummyInventory, world);

            // change recipe success indicator and recipe sound override
            if (match.isPresent()) {
                targetRecipeSuccess = true;
                SoundEvent soundEvent = match.get().value().getSound();
                recipeSoundOverride = soundEvent == null || soundEvent.equals(SoundEvents.EMPTY) ? null : soundEvent;

                if (!world.isClientSide()) {
                    targetStack.shrink(1);
                    if (targetStack.getCount() == 0) {
                        targetItemEntity.discard();
                    } else {
                        targetItemEntity.setItem(targetStack);
                    }

                    ItemStack outputStack = match.get().value().assemble(dummyInventory, world.registryAccess());

                    // make sure to proc advancement trigger before spawning item
                    KlaxonAdvancementTriggers.triggerToolUsageCraft((ServerPlayer) player, toolStack, outputStack);

                    // dump item out in-world
                    Containers.dropItemStack(
                            world,
                            outputPos.x(),
                            outputPos.y(),
                            outputPos.z(),
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
                world.playSound(player, BlockPos.containing(clickedPos), recipeSoundOverride != null ? recipeSoundOverride : component.usageSound(), SoundSource.PLAYERS, 1, 1.0f + 0.4f * world.getRandom().nextFloat());
                totalPlayedSounds++;
            }

            // commit recipe success status after all calculations
            didAtLeastOneRecipeSucceed |= targetRecipeSuccess;
        }

        if (world instanceof ServerLevel serverWorld) {
            if (didAtLeastOneRecipeSucceed) {
                // trip sculk sensors and damage tool
                serverWorld.gameEvent(player, GameEvent.ITEM_INTERACT_FINISH, clickedPos);
                if (player != null) toolStack.hurtAndBreak(1, player, EquipmentSlotHelper.convert(usedHand));
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

    public static void onDatapackReload(MinecraftServer minecraftServer, CloseableResourceManager lifecycledResourceManager, boolean success) {
        if (success) clearCache();
    }

    public static void onTagsLoaded(RegistryAccess registryManager, boolean success) {
        if (success) clearCache();
    }

    public static boolean isPlayerValid(@Nullable Player player) {
        return player == null || player.onGround() || player instanceof FakePlayer;
    }

    // yoinked from living entity
    public static void spawnToolUseParticleEffects(Level world, ItemStack stack, int count, Entity source) {
        if (stack.isEmpty()) {
            return;
        }

        RandomSource random = source.getRandom();
        float pitch = source.getXRot();
        float yaw = source.getYRot();

        for (int i = 0; i < count; i++) {
            Vec3 vec3d = new Vec3(((double)random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3d = vec3d.xRot(-pitch * (float) (Math.PI / 180.0));
            vec3d = vec3d.yRot(-yaw * (float) (Math.PI / 180.0));
            double d = (double)(-random.nextFloat()) * 0.6 - 0.3;
            Vec3 vec3d2 = new Vec3(((double)random.nextFloat() - 0.5) * 0.3, d, 0.6);
            vec3d2 = vec3d2.xRot(-pitch * (float) (Math.PI / 180.0));
            vec3d2 = vec3d2.yRot(-yaw * (float) (Math.PI / 180.0));
            vec3d2 = vec3d2.add(source.getX(), source.getEyeY(), source.getZ());
            source.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
        }
    }
}
