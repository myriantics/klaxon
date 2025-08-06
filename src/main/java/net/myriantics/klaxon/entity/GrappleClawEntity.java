package net.myriantics.klaxon.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.myriantics.klaxon.item.equipment.ammo.GrappleClawItem;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.util.PlayerEntityGrappleAccess;
import org.jetbrains.annotations.Nullable;

public class GrappleClawEntity extends PersistentProjectileEntity {
    public GrappleClawEntity(EntityType<? extends GrappleClawEntity> entityType, World world) {
        super(entityType, world);
    }

    public GrappleClawEntity(World world, double x, double y, double z, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(KlaxonEntityTypes.STEEL_GRAPPLE_CLAW, x, y, z, world, stack, shotFrom);
    }

    public GrappleClawEntity(World world, PlayerEntity player, double x, double y, double z, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(KlaxonEntityTypes.STEEL_GRAPPLE_CLAW, x, y, z, world, stack, shotFrom);
        setOwner(player);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(KlaxonItems.STEEL_GRAPPLE_CLAW);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        this.kill();
        return super.damage(source, amount);
    }

    @Override
    protected void onHit(LivingEntity target) {
        super.onHit(target);
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    public boolean isAnchored() {
        return inGround;
    }

    @Override
    public void tick() {
        if (!(getOwner() instanceof PlayerEntity) || !this.removeIfInvalid((PlayerEntity) getOwner())) super.tick();
    }

    private void setPlayerGrappleClaw(@Nullable GrappleClawEntity grappleClaw) {
        Entity entity = this.getOwner();
        if (entity instanceof PlayerEntityGrappleAccess access) {
            access.klaxon$setGrappleClaw(grappleClaw);
        }
    }

    private void clearPlayerGrappleClawIfNeeded() {
        Entity entity = this.getOwner();
        if (entity instanceof PlayerEntityGrappleAccess access && this.equals(access.klaxon$getGrappleClaw())) {
            access.klaxon$setGrappleClaw(null);
        }
    }

    private boolean removeIfInvalid(PlayerEntity player) {
        ItemStack itemStack = player.getMainHandStack();
        ItemStack itemStack2 = player.getOffHandStack();
        boolean bl = itemStack.isOf(KlaxonItems.GRAPPLE_WINCH);
        boolean bl2 = itemStack2.isOf(KlaxonItems.GRAPPLE_WINCH);
        if (!player.isRemoved() && player.isAlive() && (bl || bl2) && !(this.squaredDistanceTo(player) > 4096.0)) {
            return false;
        } else {
            this.discard();
            return true;
        }
    }

    @Override
    public void setOwner(@Nullable Entity entity) {
        super.setOwner(entity);
        setPlayerGrappleClaw(this);
    }

    @Override
    public void onRemoved() {
        clearPlayerGrappleClawIfNeeded();
        super.onRemoved();
    }

    @Override
    public void remove(RemovalReason reason) {
        clearPlayerGrappleClawIfNeeded();
        super.remove(reason);
    }
}
