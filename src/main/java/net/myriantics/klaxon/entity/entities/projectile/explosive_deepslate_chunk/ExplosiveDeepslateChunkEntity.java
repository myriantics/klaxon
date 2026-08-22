package net.myriantics.klaxon.entity.entities.projectile.explosive_deepslate_chunk;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystContextParams;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import net.myriantics.klaxon.tag.klaxon.KlaxonDamageTypeTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonExplosiveCatalystBehaviorTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class ExplosiveDeepslateChunkEntity extends ThrowableProjectile {

    protected @NotNull ExplosiveCatalystData data = ExplosiveCatalystData.ZERO;
    protected DataComponentMap components = DataComponentMap.EMPTY;

    public ExplosiveDeepslateChunkEntity(EntityType<? extends ExplosiveDeepslateChunkEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ExplosiveDeepslateChunkEntity(Level level, ItemStack stack, double x, double y, double z) {
        super(KlaxonEntityTypes.EXPLOSIVE_DEEPSLATE_CHUNK.value(), level);
        this.setPos(x, y, z);
        if (!level.isClientSide()) {
            this.initFromStack(stack);
        }
    }

    protected void initFromStack(ItemStack stack) {
        @Nullable ExplosiveCatalystData data = ExplosiveCatalystData.findRaw(this.level(), stack);
        if (data == null) {
            this.setData(ExplosiveCatalystData.ZERO);
        } else {
            this.setData(data);
            this.setComponents(stack.getComponentsPatch().forget(this.data.behavior(this.level()).value()::isComponentIrrelevant));
        }
    }

    public int getCooldownTicks() {
        if (this.data.behavior(this.level()).is(KlaxonExplosiveCatalystBehaviorTags.HARMLESS)) {
            return 10;
        } else {
            return (int) (this.data.explosionPower() * 20);
        }
    }

    public ExplosiveCatalystData getData() {
        return this.data;
    }

    public void setData(ExplosiveCatalystData data) {
        this.data = Objects.requireNonNull(data);
    }

    protected void setComponents(DataComponentPatch patch) {
        this.components = patch.split().added();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide()) {
            Entity entity = result.getEntity();
            entity.hurt(this.damageSources().explosion(this.getOwner(), this), Math.clamp((float) this.getDeltaMovement().length() * 2, 1f, 5f));
            if (this.data.producesFire() && !entity.fireImmune() && !entity.isInWaterOrRain()) {
                entity.igniteForTicks(Mth.floor(this.data.explosionPower()) * 8);
            }
            this.detonate(
                    this.createContext()
                            .add(KlaxonExplosiveCatalystContextParams.SUPPORT_STATE, result.getEntity().getBlockStateOn())
            );
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public boolean fireImmune() {
        return this.data.producesFire();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide()) {
            this.detonate(this.createContext()
                    .add(KlaxonExplosiveCatalystContextParams.SUPPORT_STATE, this.level().getBlockState(result.getBlockPos()))
            );
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.isRemoved() && source.is(KlaxonDamageTypeTags.DETONATES_EXPLOSIVE_DEEPSLATE_CHUNKS)) {
            if (!this.level().isClientSide()) {
                this.detonate(
                        this.createContext()
                                .add(KlaxonExplosiveCatalystContextParams.SOURCE_ENTITY, source.getEntity())
                );
            }
        }

        return super.hurt(source, amount);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && target != this.getOwner() && target != this;
    }

    protected void detonate(ExplosiveCatalystContext context) {
        this.discard();
        ExplosiveCatalystBehavior behavior = this.data.behavior(level()).value();
        behavior.createExplosion(context, this.position(), behavior.transformExplosiveCatalystData(context, this.data), this.mayBreak(this.level()));
    }

    @Override
    public boolean mayBreak(Level level) {
        return super.mayBreak(level) && (this.getOwner() instanceof Player player ? player.getAbilities().mayBuild : this.getOwner() == null || level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING));
    }

    protected ExplosiveCatalystContext createContext() {
        return new ExplosiveCatalystContext((ServerLevel) this.level(), this.components)
                .add(KlaxonExplosiveCatalystContextParams.THIS_ENTITY, this)
                .add(KlaxonExplosiveCatalystContextParams.BLOCK_POS, this.blockPosition())
                .add(KlaxonExplosiveCatalystContextParams.SOURCE_ENTITY, this.getOwner());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains(KlaxonNBTIds.EXPLOSIVE_CATALYST_DATA)) {
            this.data = ExplosiveCatalystData.CODEC.decode(NbtOps.INSTANCE, compound.get(KlaxonNBTIds.EXPLOSIVE_CATALYST_DATA)).mapOrElse(Pair::getFirst, (pair) -> ExplosiveCatalystData.ZERO);
        }
        if (compound.contains(KlaxonNBTIds.COMPONENTS)) {
            this.components = DataComponentMap.CODEC.decode(NbtOps.INSTANCE, compound.get(KlaxonNBTIds.COMPONENTS)).mapOrElse(Pair::getFirst, (pairError -> DataComponentMap.EMPTY));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        ExplosiveCatalystData.CODEC.encode(this.data, NbtOps.INSTANCE, new CompoundTag()).ifSuccess(tag -> compound.put(KlaxonNBTIds.EXPLOSIVE_CATALYST_DATA, tag));
        DataComponentMap.CODEC.encode(this.components, NbtOps.INSTANCE, new CompoundTag()).ifSuccess(tag -> compound.put(KlaxonNBTIds.COMPONENTS, tag));
    }
}
