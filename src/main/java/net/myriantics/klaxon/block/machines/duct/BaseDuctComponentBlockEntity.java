package net.myriantics.klaxon.block.machines.duct;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctNode;
import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctPayload;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import org.jetbrains.annotations.Nullable;

public abstract class BaseDuctComponentBlockEntity extends BlockEntity implements DuctNode {

    private @Nullable DuctPayload payload;

    public BaseDuctComponentBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    protected void clearPayload() {
        this.setPayload(null);
    }

    public void setPayload(@Nullable DuctPayload payload) {
        this.payload = payload;
        this.setChanged();
    }

    protected boolean hasPayload() {
        return this.payload != null;
    }

    protected boolean hasNonEmptyPayload() {
        return this.hasPayload() && !this.payload.isEmpty();
    }

    @Override
    public @Nullable DuctPayload getPayload() {
        return this.payload;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(KlaxonNBTIds.DUCT_PAYLOAD)) {
            this.payload = DuctPayload.load(tag.getCompound(KlaxonNBTIds.DUCT_PAYLOAD), registries);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.payload != null) {
            CompoundTag payloadTag = new CompoundTag();
            this.payload.save(payloadTag, registries);
            tag.put(KlaxonNBTIds.DUCT_PAYLOAD, payloadTag);
        }
    }

    public int computeAnalogFullnessStrength() {
        if (this.payload == null) {
            return 0;
        } else {
            float f = 0.0F;

            for (int i = 0; i < this.payload.stacks.size(); i++) {
                ItemStack itemStack = this.payload.stacks.get(i);
                if (!itemStack.isEmpty()) {
                    f += (float)itemStack.getCount() / itemStack.getMaxStackSize();
                }
            }

            f /= this.payload.stacks.size();
            return Mth.lerpDiscrete(f, 0, 15);
        }
    }
}
