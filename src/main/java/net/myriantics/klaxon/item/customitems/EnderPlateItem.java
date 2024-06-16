package net.myriantics.klaxon.item.customitems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.EnderPlateEntity;

public class EnderPlateItem extends Item {
    public EnderPlateItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack handStack = user.getStackInHand(hand);
        user.getItemCooldownManager().set(this, 10);

        if(!world.isClient) {
            EnderPlateEntity enderPlateEntity = new EnderPlateEntity(world, user);
            enderPlateEntity.setItem(handStack);
            enderPlateEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 3.0F, 0.2F);
            world.spawnEntity(enderPlateEntity);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            handStack.decrement(1);
        }
        return TypedActionResult.success(handStack, world.isClient());
    }
}
