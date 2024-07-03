package net.myriantics.klaxon.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonMain;
import net.myriantics.klaxon.item.KlaxonItems;

public class EnderPlateEntity extends ThrownItemEntity {
    public EnderPlateEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public EnderPlateEntity(World world, LivingEntity owner) {
        super(KlaxonEntities.ENDER_PEARL_PLATE_ENTITY_TYPE, owner, world);
    }

    public EnderPlateEntity(World world, double x, double y, double z) {
        super(KlaxonEntities.ENDER_PEARL_PLATE_ENTITY_TYPE, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return KlaxonItems.ENDER_PEARL_PLATE_ITEM;
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        super.onBlockCollision(state);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
    }

    @Override
    public void tick() {
        Entity entity = this.getOwner();
        if(entity instanceof PlayerEntity && !entity.isAlive()) {
            this.discard();
        } else if (entity instanceof PlayerEntity){
            entity.damage(this.getDamageSources().cramming(), 1f);
            super.tick();
        }
    }
}
