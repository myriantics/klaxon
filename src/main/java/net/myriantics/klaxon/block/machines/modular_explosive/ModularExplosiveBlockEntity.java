package net.myriantics.klaxon.block.machines.modular_explosive;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.mechanics.explosive_catalyst.AbstractExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntities;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;

public class ModularExplosiveBlockEntity extends BlockEntity {

    private ExplosiveCatalystData explosiveCatalystData = ExplosiveCatalystData.ZERO;
    private int fuseTime = -1;
    private int maxFuseTime = 0;
    private boolean modifyWorld = true;

    protected ModularExplosiveBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public ModularExplosiveBlockEntity(BlockPos pos, BlockState blockState) {
        this(KlaxonBlockEntities.MODULAR_EXPLOSIVE.value(), pos, blockState);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
    }

    public void setData(ExplosiveCatalystData newData) {
        this.explosiveCatalystData = newData;
        this.setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.modifyWorld = tag.getBoolean(KlaxonNBTIds.MODIFY_WORLD);
        this.maxFuseTime = this.sanitizeMaxFuseTime(tag.getInt(KlaxonNBTIds.MAX_FUSE_TIME));
        this.fuseTime = this.sanitizeFuseTime(tag.getInt(KlaxonNBTIds.FUSE_TIME));
        if (tag.contains(KlaxonNBTIds.EXPLOSIVE_CATALYST_DATA)) {
            this.explosiveCatalystData = ExplosiveCatalystData.CODEC.decode(NbtOps.INSTANCE, tag.get(KlaxonNBTIds.EXPLOSIVE_CATALYST_DATA)).mapOrElse(Pair::getFirst, (pair) -> ExplosiveCatalystData.ZERO);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(KlaxonNBTIds.MAX_FUSE_TIME, this.maxFuseTime);
        tag.putInt(KlaxonNBTIds.FUSE_TIME, this.fuseTime);
        tag.putBoolean(KlaxonNBTIds.MODIFY_WORLD, this.modifyWorld);
        ExplosiveCatalystData.CODEC.encode(this.explosiveCatalystData, NbtOps.INSTANCE, new CompoundTag()).ifSuccess(dataTag -> tag.put(KlaxonNBTIds.EXPLOSIVE_CATALYST_DATA, dataTag));
    }

    public static void serverTick(Level level, BlockPos pos, BlockState blockState, ModularExplosiveBlockEntity modularExplosiveBlockEntity) {
        if (modularExplosiveBlockEntity.fuseTime > 0) {
            modularExplosiveBlockEntity.fuseTime--;
        } else if (modularExplosiveBlockEntity.fuseTime != -1 && modularExplosiveBlockEntity.maxFuseTime != 0) {
            modularExplosiveBlockEntity.detonate();
        }
    }

    public boolean isCountingDown() {
        return this.fuseTime != -1 && this.maxFuseTime != 0;
    }

    public void detonate() {
        Level level = this.level;
        BlockPos pos = this.worldPosition;

        if (level != null) {
            level.removeBlock(pos, false);
            if (!this.explosiveCatalystData.equals(ExplosiveCatalystData.ZERO)) {
                ExplosiveCatalystContext.Block context = this.createContext();
                Holder<AbstractExplosiveCatalystBehavior> behaviorHolder = this.explosiveCatalystData.behavior();

                behaviorHolder.value().createExplosion(context, pos.getCenter(), this.explosiveCatalystData, this.modifyWorld);
            }
        }
    }

    public void defuse() {
        this.fuseTime = -1;
    }

    public ExplosiveCatalystContext.Block createContext() {
        return new ExplosiveCatalystContext.Block(this.level, this.components(), this.worldPosition);
    }

    public void onRedstoneImpulse() {
        if (this.fuseTime == 0 || this.maxFuseTime == 0) {
            this.detonate();
        } else {
            this.fuseTime = this.maxFuseTime;
        }
    }

    private int sanitizeFuseTime(int fuseTime) {
        return Math.clamp(-1, fuseTime, this.maxFuseTime);
    }

    private int sanitizeMaxFuseTime(int maxFuseTime) {
        return Math.max(0, maxFuseTime);
    }
}
