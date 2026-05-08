package net.myriantics.klaxon.block.machines.modular_explosive;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.component.configuration.ModularExplosiveBlockConfigComponent;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystVessel;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystContextParams;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import net.myriantics.klaxon.registry.misc.KlaxonWorldEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonExplosiveCatalystBehaviorTags;
import org.jetbrains.annotations.Nullable;

public class ModularExplosiveBlockEntity extends BlockEntity implements ExplosiveCatalystVessel {

    private ExplosiveCatalystData explosiveCatalystData = ExplosiveCatalystData.ZERO;
    private int fuseTime = -1;
    private int maxFuseTime = 0;
    private boolean modifyWorld = true;
    private boolean exposeExplosiveCatalystData = true;

    protected ModularExplosiveBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public ModularExplosiveBlockEntity(BlockPos pos, BlockState blockState) {
        this(KlaxonBlockEntityTypes.MODULAR_EXPLOSIVE.value(), pos, blockState);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(KlaxonDataComponentTypes.EXPLOSIVE_CATALYST_DATA.value(), this.explosiveCatalystData);
        components.set(KlaxonDataComponentTypes.MODULAR_EXPLOSIVE_BLOCK_CONFIG.value(), new ModularExplosiveBlockConfigComponent(this.maxFuseTime, this.modifyWorld, this.exposeExplosiveCatalystData));
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        @Nullable ExplosiveCatalystData data = componentInput.get(KlaxonDataComponentTypes.EXPLOSIVE_CATALYST_DATA.value());
        if (data != null) {
            this.explosiveCatalystData = data;
        }
        @Nullable ModularExplosiveBlockConfigComponent config = componentInput.get(KlaxonDataComponentTypes.MODULAR_EXPLOSIVE_BLOCK_CONFIG.value());
        if (config != null) {
            this.maxFuseTime = config.maxFuseTime();
            this.modifyWorld = config.modifyWorld();
        }
    }

    public void setData(ExplosiveCatalystData newData) {
        this.explosiveCatalystData = newData;
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), (Block.UPDATE_ALL_IMMEDIATE));
        }
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
        this.exposeExplosiveCatalystData = tag.getBoolean(KlaxonNBTIds.SHOULD_EXPOSE_CATALYST_DATA);
        if (this.level != null && this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 4);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(KlaxonNBTIds.MAX_FUSE_TIME, this.maxFuseTime);
        tag.putInt(KlaxonNBTIds.FUSE_TIME, this.fuseTime);
        tag.putBoolean(KlaxonNBTIds.MODIFY_WORLD, this.modifyWorld);
        ExplosiveCatalystData.CODEC.encode(this.explosiveCatalystData, NbtOps.INSTANCE, new CompoundTag()).ifSuccess(dataTag -> tag.put(KlaxonNBTIds.EXPLOSIVE_CATALYST_DATA, dataTag));
        tag.putBoolean(KlaxonNBTIds.SHOULD_EXPOSE_CATALYST_DATA, this.exposeExplosiveCatalystData);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = this.saveCustomOnly(registries);
        tag.remove(KlaxonNBTIds.MAX_FUSE_TIME);
        tag.remove(KlaxonNBTIds.FUSE_TIME);
        tag.remove(KlaxonNBTIds.MODIFY_WORLD);
        tag.remove(KlaxonNBTIds.SHOULD_EXPOSE_CATALYST_DATA);
        if (!this.exposeExplosiveCatalystData) {
            ExplosiveCatalystData.CODEC.encode(ExplosiveCatalystData.OBFUSCATED, NbtOps.INSTANCE, new CompoundTag()).ifSuccess(dataTag -> tag.put(KlaxonNBTIds.EXPLOSIVE_CATALYST_DATA, dataTag));
        }
        return tag;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState blockState, ModularExplosiveBlockEntity modularExplosiveBlockEntity) {
        if (modularExplosiveBlockEntity.fuseTime > 0) {
            modularExplosiveBlockEntity.decrementFuseTime();
        } else if (modularExplosiveBlockEntity.fuseTime != -1 && modularExplosiveBlockEntity.maxFuseTime != 0) {
            modularExplosiveBlockEntity.detonate();
        }
    }

    private void decrementFuseTime() {
        this.updateFuseTime(this.fuseTime - 1);
    }

    private void updateFuseTime(int newTime) {
        Level level = this.level;
        if (level != null) {
            BlockPos pos = this.worldPosition;
            BlockState state = level.getBlockState(pos);

            if (state.getBlock() instanceof ModularExplosiveBlock block) {
                block.updateFuseState(level, pos, state, newTime, this.maxFuseTime);
            }
        }
        this.fuseTime = newTime;
    }

    public int getFuseTime() {
        return this.fuseTime;
    }

    public int getMaxFuseTime() {
        return this.maxFuseTime;
    }

    public void detonate() {
        Level level = this.level;
        BlockPos pos = this.worldPosition;

        if (level instanceof ServerLevel serverLevel) {
            Holder<ExplosiveCatalystBehavior> behaviorHolder = this.explosiveCatalystData.get(serverLevel);
            if (behaviorHolder.is(KlaxonExplosiveCatalystBehaviorTags.RUNS_DESTROY_BLOCK_EFFECTS_FOR_MODULAR_EXPLOSIVE_BLOCK)) {
                KlaxonServerPlayNetworkHandler.syncWorldEvent(serverLevel, pos, KlaxonWorldEvents.SPAWN_BLOCK_BREAK_PARTICLES);
            }
            level.removeBlock(pos, false);
            ExplosiveCatalystContext context = this.createContext(serverLevel);
            behaviorHolder.value().createExplosion(context, pos.getCenter(), behaviorHolder.value().transformExplosiveCatalystData(context, this.explosiveCatalystData), this.modifyWorld);
        }
    }

    public void defuse() {
        this.updateFuseTime(-1);
    }

    public ExplosiveCatalystContext createContext(ServerLevel serverLevel) {
        return new ExplosiveCatalystContext(serverLevel, this.components())
                .add(KlaxonExplosiveCatalystContextParams.SUPPORT_STATE, serverLevel.getBlockState(this.getBlockPos().below()))
                .add(KlaxonExplosiveCatalystContextParams.BLOCK_POS, this.getBlockPos());
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

    @Override
    public boolean shouldExposeExplosiveCatalystData() {
        return this.exposeExplosiveCatalystData;
    }

    @Override
    public ExplosiveCatalystData getRawData() {
        return this.explosiveCatalystData;
    }

    @Override
    public ExplosiveCatalystData getEffectiveCatalystData() {
        if (this.level instanceof ServerLevel serverLevel) {
            ExplosiveCatalystContext context = this.createContext(serverLevel);
            Holder<ExplosiveCatalystBehavior> behaviorHolder = this.explosiveCatalystData.get(serverLevel);
            return behaviorHolder.value().transformExplosiveCatalystData(context, this.explosiveCatalystData);
        } else {
            return ExplosiveCatalystData.ZERO;
        }
    }
}
