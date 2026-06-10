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
import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctInteractionHandler;
import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctNode;
import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctPayload;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Stack;

public class AIODuctDriverBlockEntity extends BaseDuctComponentBlockEntity {

    private DuctInteractionHandler handler;
    private int range = 16;
    private int transferCooldown = 0;
    private int tickCounter = 0;
    private int ticksPerTransfer = 8;

    public AIODuctDriverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public AIODuctDriverBlockEntity(BlockPos pos, BlockState blockState) {
        super(KlaxonBlockEntityTypes.AIO_DUCT_DRIVER.value(), pos, blockState);
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if (this.isOnCooldown()) {
            this.transferCooldown--;
        } else if (this.isPowered()) {
            this.transferCooldown = this.ticksPerTransfer;
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
        this.getOrInitHandler();
        this.handler.updateDirection(this.level, this.worldPosition, facing, true);
        this.handler.updateDirection(this.level, this.worldPosition, opposite, true);
    }

    protected void performShift() {
        @Nullable DuctPayload payload = this.getPayload();
        Direction facing = this.getFacing();
        if (this.hasPayload() && this.getOrInitHandler().push(payload, facing, this.range)) {
            this.clearPayload();
        }
        this.extractIntoPayload();
    }

    protected void extractIntoPayload() {
        if (!this.hasPayload()) {
            this.setPayload(new DuctPayload(4));
        }

        @Nullable Storage<ItemVariant> storage = this.getOrInitHandler().getStorageForSide(this.getFacing());

        if (storage != null) {
            Objects.requireNonNull(this.getPayload()).extract(storage);
        }
    }

    protected boolean isOnCooldown() {
        return this.transferCooldown > 0;
    }

    protected DuctInteractionHandler getOrInitHandler() {
        return this.handler == null ? this.handler = DuctInteractionHandler.initialize(this.level, this.worldPosition, this.getBlockState(), this) : this.handler;
    }

    @Override
    public boolean push(DuctPayload payload, Direction inputFace, int remainingDepth) {
        return false; // this thing cant be a push target
    }

    @Override
    public String getStatusForDirection(Direction direction) {
        return this.getOrInitHandler().getStatusForDirection(direction);
    }
}
