package net.myriantics.klaxon.item.equipment.ammo;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.GrappleClawEntity;

public class GrappleClawItem extends Item implements ProjectileItem {
    public GrappleClawItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        GrappleClawEntity grappleClawEntity = new GrappleClawEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack, null);
        grappleClawEntity.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
        return grappleClawEntity;
    }
}
