package net.myriantics.klaxon.block.machines.duct.driver.aio;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.block.machines.duct.BaseDuctComponentBlockEntity;
import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctNode;
import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctPayload;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Stack;

public class AIODuctDriverBlockEntity extends BaseDuctComponentBlockEntity {

    private Storage<ItemVariant> storageToPullFrom;
    private DuctNode nodeCache;
    private Storage<ItemVariant> ejectionStorageCache;
    private boolean isBlocked;
    private int range = 16;
    private int tickCounter = 0;
    private int ticksPerTransfer = 4;

    public AIODuctDriverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public AIODuctDriverBlockEntity(BlockPos pos, BlockState blockState) {
        super(KlaxonBlockEntityTypes.AIO_DUCT_DRIVER.value(), pos, blockState);
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if (!this.isPowered()) {
            return;
        }

        if (++this.tickCounter >= this.ticksPerTransfer) {
            this.tickCounter = 0;
            this.performShift();
        }
    }

    public boolean isPowered() {
        return this.getBlockState().hasProperty(AIODuctDriverBlock.POWERED) && this.getBlockState().getValue(AIODuctDriverBlock.POWERED);
    }

    public Direction getFacing() {
        return this.getBlockState().getValue(AIODuctDriverBlock.FACING);
    }

    public void updateCaches() {
        Direction facing = this.getFacing();
        Direction opposite = facing.getOpposite();
        BlockPos facingRelative = this.worldPosition.relative(facing);
        BlockPos oppositeRelative = this.worldPosition.relative(opposite);
        this.storageToPullFrom = ItemStorage.SIDED.find(this.level, facingRelative, opposite);
        if (this.level != null && this.level.getBlockEntity(oppositeRelative) instanceof DuctNode node) {
            this.nodeCache = node;
            this.ejectionStorageCache = null;
        } else {
            this.nodeCache = null;
            this.ejectionStorageCache = ItemStorage.SIDED.find(this.level, oppositeRelative, facing);
        }
    }

    protected void performShift() {
        @Nullable DuctPayload payload = this.getPayload();
        Direction facing = this.getFacing();
        Direction shiftDirection = facing.getOpposite();
        if (payload != null) {
            if (this.nodeCache == null) {
                if (this.ejectionStorageCache != null && payload.insert(this.ejectionStorageCache)) {
                    this.clearPayload();
                } else if (!this.isBlocked) {
                    this.dumpPayloadOnSide(shiftDirection);
                }
            } else if (this.nodeCache.push(shiftDirection, this.range)) {
                this.nodeCache.setPayload(payload);
                this.clearPayload();
            }
        }
        this.extractIntoPayload();
    }

    protected void extractIntoPayload() {
        if (!this.hasPayload()) {
            this.setPayload(new DuctPayload(4));
        }

        if (this.storageToPullFrom != null) {
            Objects.requireNonNull(this.getPayload()).extract(this.storageToPullFrom);
        }
    }
}
