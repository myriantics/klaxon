package net.myriantics.klaxon.item.consumables;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
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
    boolean throwing;

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        throwing = true;
        if(!world.isClient) {
            user.sendMessage(Text.literal("Test commence" + user.getYaw()));
        }
        this.initialPlayerYaw = user.headYaw;
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getMainHandStack());
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (throwing && user instanceof PlayerEntity player && !world.isClient) {
            //if(this.usageTicks >= 40) {}
            player.sendMessage(Text.literal("Test ticked"));
        }
        this.usageTicks++;
    }

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        return stack.isOf(this);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        // this method was proccing twice for some reason. this should fix that
        if(!throwing) {
            return;
        }
        throwing = false;

        if(user instanceof PlayerEntity player) {

            ItemStack handStack = player.getStackInHand(player.getActiveHand());
            player.getItemCooldownManager().set(this, 10);

            float shittyVeloRootCalc = ((this.initialPlayerYaw - player.getYaw()) / ((float) this.usageTicks / 20));
            double jankVeloCalc = (0.5)*(Math.pow(2.72f, 0.0064 * shittyVeloRootCalc));

            if(world.isClient) {
                player.sendMessage(Text.literal("testVal = " + jankVeloCalc));
            }

            if(!world.isClient) {
                EnderPlateEntity enderPlateEntity = new EnderPlateEntity(world, user);
                enderPlateEntity.setItem(handStack);
                enderPlateEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, (float)jankVeloCalc, 0.2F);
                world.spawnEntity(enderPlateEntity);
            }

            player.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!player.getAbilities().creativeMode) {
                handStack.decrement(1);
        }
        }
    }
}
