package net.myriantics.klaxon.block.machines.duct.driver.aio;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.block.machines.duct.BaseDuctComponentBlockEntity;
import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctPayload;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AIODuctDriverBlockEntity extends BaseDuctComponentBlockEntity {

    private Storage<ItemVariant> storageToPullFrom;
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
            this.dumpPayloadOnSide(this.getFacing().getOpposite());
            this.extractIntoPayload();
        }
    }

    public boolean isPowered() {
        return this.getBlockState().hasProperty(AIODuctDriverBlock.POWERED) && this.getBlockState().getValue(AIODuctDriverBlock.POWERED);
    }

    public Direction getFacing() {
        return this.getBlockState().getValue(AIODuctDriverBlock.FACING);
    }

    public void updateAnchoredContainer() {
        Direction facing = this.getFacing();
        this.storageToPullFrom = ItemStorage.SIDED.find(this.level, this.worldPosition.relative(facing), facing.getOpposite());
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
