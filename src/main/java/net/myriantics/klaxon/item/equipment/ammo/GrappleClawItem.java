package net.myriantics.klaxon.item.equipment.ammo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

public class GrappleClawItem extends Item implements ProjectileItem {
    public GrappleClawItem(Item.Settings settings) {
        super(settings);
    }

    public GrappleClawEntity createGrappleClaw(World world, ItemStack stack, LivingEntity shooter, @Nullable ItemStack shotFrom) {
        return new GrappleClawEntity(world, shooter, stack.copyWithCount(1), shotFrom);
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        GrappleClawEntity grappleClawEntity = new GrappleClawEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack.copyWithCount(1), null);
        grappleClawEntity.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
        return grappleClawEntity;
    }

    @Override
    public ProjectileItem.Settings getProjectileSettings() {
        return ProjectileItem.Settings.builder().power(0.3f).uncertainty(3.0f).build();
    }
}
