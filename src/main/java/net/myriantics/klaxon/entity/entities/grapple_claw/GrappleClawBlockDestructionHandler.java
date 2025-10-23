package net.myriantics.klaxon.entity.entities.grapple_claw;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.Offset;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.misc.KlaxonGameRules;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.util.KlaxonItemStackHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GrappleClawBlockDestructionHandler {
    private final GrappleClawEntity grappleClaw;

    public GrappleClawBlockDestructionHandler(GrappleClawEntity grappleClaw) {
        this.grappleClaw = grappleClaw;
    }

    /**
     * Called while checking for block collision, for each currently colliding block.
     */
    public void onBlockPosIntersection(World world, BlockState occupiedState, BlockPos pos) {
        // make sure we're neither anchored nor removed
        if (this.grappleClaw.isAnchored() || this.grappleClaw.isRemoved()) {
            return;
        }

        VoxelShape occupiedStateShape = occupiedState.getCollisionShape(world, pos);

        // make sure we actually collide with the target bounding box
        if (occupiedStateShape.isEmpty() || !this.grappleClaw.getBoundingBox().intersects(occupiedStateShape.getBoundingBox().offset(pos))) {
            return;
        }

        // maybe pop a sound from this later
        boolean success = this.tryBreakingBlocks(world, occupiedState, pos);
    }

    protected boolean tryBreakingBlocks(World world, BlockState occupiedState, BlockPos pos) {
        // make sure projectiles can break blocks
        if (!this.canBreakBlock(world, occupiedState, pos)) {
            return false;
        }

        PlayerEntity attachedPlayer = grappleClaw.getAttachedPlayer();

        // try to veinmine before breaking the block :)
        if (this.veinmineBlocksIfValid(world, occupiedState, pos, attachedPlayer)) {
            return this.veinmineBlocksIfValid(world, occupiedState, pos, attachedPlayer);
        } else {
            this.breakBlock(world, occupiedState, pos, grappleClaw.getOwner());
            return true;
        }
    }

    private boolean veinmineBlocksIfValid(World world, BlockState originState, BlockPos originPos, PlayerEntity owner) {
        int radius = world.getGameRules().getInt(KlaxonGameRules.GRAPPLE_CLAW_VEINMINE_RADIUS);

        // don't veinmine anything if the source block is not veinmineable
        // also declare failure if radius is 0
        if (radius == 0 || !canVeinmineBlock(world, originState, originPos, owner)) {
            return false;
        }

        Block veinminedBlock = originState.getBlock();

        if (world.isClient) {
            return true;
        }

        // init loot context
        LootContextParameterSet.Builder lootContextBuilder = new LootContextParameterSet.Builder((ServerWorld) world)
                .add(LootContextParameters.ORIGIN, grappleClaw.getEyePos())
                .add(LootContextParameters.TOOL, grappleClaw.getItemStack());

        // Output stacks to be merged and output at the grapple winch's position
        ArrayList<ItemStack> outputStacks = new ArrayList<>();

        // Positions we've already checked through and destroyed if possible - to be ignored when checking for new positions.
        ArrayList<BlockPos> processedPositions = new ArrayList<>();

        // Contains all of the positions to check on the next pass
        List<BlockPos> targetPositions = List.of(originPos);

        // counts how many blocks we've broken - used to increment stat at the end
        int blocksBroken = 0;

        for (int x = 0; x < radius; x++) {
            ArrayList<BlockPos> newTargetPositions = new ArrayList<>();

            // iterate through the current target positions
            for (BlockPos newOriginPos : targetPositions) {
                // iterate through all offset directions from the checking pos
                for (Offset offset : Offset.values()) {
                    BlockPos targetPos = newOriginPos.add(offset.getOffsetVector());
                    BlockState targetState = world.getBlockState(targetPos);

                    // make sure we haven't processed position before
                    if (!processedPositions.contains(targetPos) && targetState.isOf(veinminedBlock)) {
                        // condense dropped stacks so we don't get 5 billion item entities
                        for (ItemStack droppedStack : world.getBlockState(targetPos).getDroppedStacks(lootContextBuilder)) {
                            KlaxonItemStackHelper.insertAndMerge(outputStacks, droppedStack);
                        }

                        world.breakBlock(targetPos, false, owner);

                        blocksBroken++;
                        processedPositions.add(targetPos);
                        newTargetPositions.add(targetPos);
                    }
                }
            }

            // update target positions list
            targetPositions = newTargetPositions;
        }

        // drop all of the output stacks at the grapple claw's location, ready to be dragged
        for (ItemStack stack : outputStacks) {
            grappleClaw.draggedItems.add(grappleClaw.dropStack(stack));
        }

        // pop advancement trigger and increase mined stat
        if (owner instanceof ServerPlayerEntity serverPlayer) {
            KlaxonAdvancementTriggers.triggerGrappleWinchVeinMine(serverPlayer, originState);
            serverPlayer.increaseStat(Stats.MINED.getOrCreateStat(originState.getBlock()), blocksBroken);
        }

        return true;
    }

    /**
     * @param world - the world
     * @param targetState - state of block to break
     * @param targetPos - block to break
     * @param owner - entity to credit block break to
     */
    private void breakBlock(World world, BlockState targetState, BlockPos targetPos, @Nullable Entity owner) {
        // don't break blocks on clientside
        if (!world.isClient()) {
            world.breakBlock(targetPos, true, owner);

            if (owner instanceof ServerPlayerEntity serverPlayer) {
                targetState.getBlock().afterBreak(world, serverPlayer, targetPos, targetState, world.getBlockEntity(targetPos), this.grappleClaw.getItemStack());
            }
        }
    }

    private boolean canVeinmineBlock(World world, BlockState state, BlockPos pos, PlayerEntity attachedPlayer) {
        boolean playerValid = this.grappleClaw.isCableAttached() && attachedPlayer != null && ((PlayerEntityGrappleAccess) attachedPlayer).klaxon$isRetracting();

        return playerValid && state.isIn(KlaxonBlockTags.GRAPPLE_CLAW_VEINMINEABLE);
    }

    private boolean canBreakBlock(World world, BlockState state, BlockPos pos) {
        if (!world.getGameRules().getBoolean(GameRules.PROJECTILES_CAN_BREAK_BLOCKS)) {
            return false;
        }

        return state.isIn(KlaxonBlockTags.GRAPPLE_CLAW_BREAKABLE) || state.isReplaceable() || state.getHardness(world, pos) == 0;
    }
}
