package net.myriantics.klaxon.entity.entities.grapple_claw;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.myriantics.klaxon.component.configuration.GrappleClawComponent;
import net.myriantics.klaxon.mechanics.grapple_winch.VeinmineGroup;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.GrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.GrappleWinchConnectionManager;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.util.KlaxonItemStackHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class GrappleClawBlockDestructionHelper {

    /**
     * Called while checking for block collision, for each currently colliding block.
     */
    public static void onBlockPosIntersection(GrappleClawEntity grappleClaw, Level world, BlockState occupiedState, BlockPos pos) {
        // make sure we're neither anchored nor removed
        if (grappleClaw.klaxon$isAnchored() || grappleClaw.isRemoved()) {
            return;
        }

        VoxelShape occupiedStateShape = occupiedState.getCollisionShape(world, pos);

        // make sure we actually collide with the target bounding box
        if (occupiedStateShape.isEmpty() || !grappleClaw.getBoundingBox().intersects(occupiedStateShape.bounds().move(pos))) {
            return;
        }

        // maybe pop a sound from this later
        boolean success = tryBreakingBlocks(grappleClaw, world, occupiedState, pos);
    }

    protected static boolean tryBreakingBlocks(GrappleClawEntity grappleClaw, Level world, BlockState occupiedState, BlockPos pos) {
        // make sure grapple claw can break block
        if (!canBreakBlock(grappleClaw, world, occupiedState, pos)) {
            return false;
        }

        Player attachedPlayer = grappleClaw.getAttachedPlayer();

        // try to veinmine before breaking the block :)
        if (!veinmineBlocksIfValid(grappleClaw, world, occupiedState, pos, attachedPlayer)) {
            breakBlock(grappleClaw, world, occupiedState, pos, grappleClaw.getOwner());
        }
        return true;
    }

    private static boolean veinmineBlocksIfValid(GrappleClawEntity grappleClaw, Level world, BlockState originState, BlockPos originPos, Player owner) {
        int maxVeinminedBlocks = grappleClaw.getPickupItemStackOrigin().getOrDefault(KlaxonDataComponentTypes.GRAPPLE_CLAW_COMPONENT, GrappleClawComponent.DEFAULT).veinmineCap();

        // don't veinmine anything if the source block is not veinmineable
        // also declare failure if radius is 0
        if (maxVeinminedBlocks == 0 || !canVeinmineBlock(grappleClaw, world, originState, originPos, owner)) {
            return false;
        }

        if (world.isClientSide) {
            return true;
        }

        Predicate<BlockState> veinminePredicate = (state) -> state.is(originState.getBlock());

        // check for any matching veinmine groups
        for (VeinmineGroup group : ((ServerLevel) world).getServer().reloadableRegistries().get().registryOrThrow(KlaxonRegistryKeys.VEINMINE_GROUP)) {
            if (group.ingredient().test(originState)) {
                veinminePredicate = group.ingredient();
                break;
            }
        }

        // init loot context
        LootParams.Builder lootContextBuilder = new LootParams.Builder((ServerLevel) world)
                .withParameter(LootContextParams.ORIGIN, grappleClaw.getEyePosition())
                .withParameter(LootContextParams.TOOL, grappleClaw.getPickupItemStackOrigin());

        // Output stacks to be merged and output at the grapple winch's position
        ArrayList<ItemStack> outputStacks = new ArrayList<>();

        // Contains all of the positions to check on the next pass
        List<BlockPos> targetPositions = List.of(originPos);

        // counts how many blocks we've broken - used to increment stat at the end
        int blocksBroken = 0;

        while (!targetPositions.isEmpty() && blocksBroken < maxVeinminedBlocks) {
            ArrayList<BlockPos> newTargetPositions = new ArrayList<>();

            // iterate through the current target positions
            for (BlockPos targetPos : targetPositions) {
                BlockState targetState = world.getBlockState(targetPos);

                if (veinminePredicate.test(targetState)) {
                    // condense dropped stacks so we don't get 5 billion item entities
                    for (ItemStack droppedStack : world.getBlockState(targetPos).getDrops(lootContextBuilder)) {
                        KlaxonItemStackHelper.insertAndMerge(outputStacks, droppedStack);
                    }

                    world.destroyBlock(targetPos, false, owner);

                    // cancel operation if we've exceeded the max blocks broken
                    if (blocksBroken++ > maxVeinminedBlocks) {
                        break;
                    }

                    // add the next round of target positions
                    for (Offset offset : Offset.values()) {
                        newTargetPositions.add(targetPos.offset(offset.getOffsetVector()));
                    }
                }
            }

            // update target positions list
            targetPositions = newTargetPositions;
        }

        // drop all of the output stacks at the grapple claw's location, ready to be dragged
        for (ItemStack stack : outputStacks) {
            grappleClaw.draggedItemsContainer.add(grappleClaw.spawnAtLocation(stack));
        }

        // pop advancement trigger and increase mined stat
        if (owner instanceof ServerPlayer serverPlayer) {
            KlaxonAdvancementTriggers.triggerGrappleWinchVeinMine(serverPlayer, originState);
            serverPlayer.awardStat(Stats.BLOCK_MINED.get(originState.getBlock()), blocksBroken);
        }

        return true;
    }

    /**
     * @param world - the world
     * @param targetState - state of block to break
     * @param targetPos - block to break
     * @param owner - entity to credit block break to
     */
    private static void breakBlock(GrappleClawEntity grappleClaw, Level world, BlockState targetState, BlockPos targetPos, @Nullable Entity owner) {
        // don't break blocks on clientside
        if (!world.isClientSide()) {
            world.destroyBlock(targetPos, true, owner);

            if (owner instanceof ServerPlayer serverPlayer) {
                serverPlayer.awardStat(Stats.BLOCK_MINED.get(targetState.getBlock()));
            }
        }
    }

    private static boolean canVeinmineBlock(GrappleClawEntity grappleClaw, Level world, BlockState state, BlockPos pos, Player attachedPlayer) {
        if (attachedPlayer == null) {
            return false;
        }
        GrappleWinchConnection connection = GrappleWinchConnectionManager.get(world).fromHook(grappleClaw);
        return connection != null && connection.isRetracting() && state.is(KlaxonBlockTags.GRAPPLE_CLAW_VEINMINEABLE);
    }

    private static boolean canBreakBlock(GrappleClawEntity grappleClaw, Level world, BlockState state, BlockPos pos) {
        if (grappleClaw.hasHookedEntity()) {
            return false;
        }
        return grappleClaw.mayInteract(world, pos) && (state.is(KlaxonBlockTags.GRAPPLE_CLAW_BREAKABLE) || state.canBeReplaced() || state.getDestroySpeed(world, pos) == 0);
    }

    /**
     *
     * @param grappleClaw
     * @param start
     * @param end
     * @param destructive - Used to determine if this raycast is being used for movement calculations & destruction OR collision checking when de-anchoring. It is intended that this parameter not collide with breakable blocks - as the grapple claw will penetrate through them once de-anchored.
     * @return
     */
    public static BlockHitResult raycast(GrappleClawEntity grappleClaw, Vec3 start, Vec3 end, boolean destructive) {
        Level world = grappleClaw.level();

        return BlockGetter.traverseBlocks(start, end, null, (s, blockPos) -> {
            BlockState targetState = grappleClaw.level().getBlockState(blockPos);
            VoxelShape shape = targetState.getCollisionShape(world, blockPos);

            BlockHitResult hitResult = world.clipWithInteractionOverride(start, end, blockPos, shape, targetState);

            // if we didn't collide with the block, cancel operation
            if (hitResult == null) {
                return null;
            }


            // ignore blocks that we can break - in fact, actually try to break them :)
            if (canBreakBlock(grappleClaw, world, targetState, blockPos)) {
                // only break blocks if this raycast is declared as destructive tho
                if (destructive) {
                    // proc projectile hit effects
                    targetState.onProjectileHit(world, targetState, hitResult, grappleClaw);
                    tryBreakingBlocks(grappleClaw, world, targetState, blockPos);
                }
                return null;
            }

            return hitResult;
        }, (s) -> {
            Vec3 vec = start.subtract(end);
            return BlockHitResult.miss(end, Direction.getNearest(vec.x(), vec.y(), vec.z()), BlockPos.containing(end));
        });
    }
}
