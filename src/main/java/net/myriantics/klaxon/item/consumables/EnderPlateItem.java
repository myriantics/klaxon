package net.myriantics.klaxon.item.consumables;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.EnderPlateEntity;

public class EnderPlateItem extends Item {
    public EnderPlateItem(Settings settings) {
        super(settings);
    }

    private float initialPlayerYaw;
    private int usageTicks;

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        this.initialPlayerYaw = user.headYaw;
        return TypedActionResult.consume(user.getMainHandStack());
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if(this.usageTicks >= 40) {
            this.
        }

        this.usageTicks++;
    }

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        return stack.isOf(this);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if(user instanceof PlayerEntity player) {

            float headVelocity =

            ItemStack handStack = user.getStackInHand(player.getActiveHand());
            player.getItemCooldownManager().set(this, 10);

            if(!world.isClient) {
                user.sendMessage(Text.literal("greg = " + Math.abs(user.getYaw())));
                EnderPlateEntity enderPlateEntity = new EnderPlateEntity(world, user);
                enderPlateEntity.setItem(handStack);
                enderPlateEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 3.0F, 0.2F);
                world.spawnEntity(enderPlateEntity);
            }

            player.incrementStat(Stats.USED.getOrCreateStat(this));
            if (player.getAbilities().creativeMode) {
                handStack.decrement(1);
            }
        }
    }
}
