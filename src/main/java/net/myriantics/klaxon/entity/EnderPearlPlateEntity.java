package net.myriantics.klaxon.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.world.World;

public class EnderPearlPlateEntity extends EnderPearlEntity {
    public EnderPearlPlateEntity(World world, LivingEntity owner) {
        super(world, owner);
    }

    private int activeTicks;
    private int tickDamageDelay = 5;
    float damageAmount = 2f;

    @Override
    public void tick() {
        this.activeTicks++;
        Entity entity = this.getOwner();
        if(entity instanceof PlayerEntity && (this.activeTicks & tickDamageDelay) == 0) {
            entity.damage(this.getDamageSources().cramming(), damageAmount);
        } else {
            super.tick();
        }
    }
}
