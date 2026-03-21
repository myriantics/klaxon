package net.myriantics.klaxon.item.equipment.ammo;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import org.jetbrains.annotations.Nullable;

public class GrappleClawItem extends Item implements ProjectileItem {
    public GrappleClawItem(Item.Properties settings) {
        super(settings);
    }

    public GrappleClawEntity createGrappleClaw(Level world, ItemStack stack, LivingEntity shooter, @Nullable ItemStack shotFrom) {
        return new GrappleClawEntity(world, shooter, stack.copyWithCount(1), shotFrom);
    }

    @Override
    public Projectile asProjectile(Level world, Position pos, ItemStack stack, Direction direction) {
        GrappleClawEntity grappleClawEntity = new GrappleClawEntity(world, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1), null);
        return grappleClawEntity;
    }

    @Override
    public ProjectileItem.DispenseConfig createDispenseConfig() {
        return ProjectileItem.DispenseConfig.builder().power(0.3f).uncertainty(3.0f).build();
    }
}
