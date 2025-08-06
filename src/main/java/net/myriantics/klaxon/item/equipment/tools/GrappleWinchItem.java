package net.myriantics.klaxon.item.equipment.tools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.util.PlayerEntityGrappleAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class GrappleWinchItem extends RangedWeaponItem {

    static protected ItemPredicate PROJECTILES = ItemPredicate.Builder.create().items(KlaxonItems.STEEL_GRAPPLE_CLAW).build();

    public GrappleWinchItem(Settings settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return PROJECTILES;
    }

    @Override
    public int getRange() {
        return 20;
    }

    @Override
    protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
        projectile.setVelocity(shooter, shooter.getPitch(), shooter.getYaw() + yaw, 0.0F, speed, divergence);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) playerEntity;
            ItemStack itemStack = playerEntity.getProjectileType(stack);
            if (!itemStack.isEmpty() && access.klaxon$getGrappleClaw() == null) {
                int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
                float f = getPullProgress(i);
                if (!(f < 0.1)) {
                    List<ItemStack> list = load(stack, itemStack, playerEntity);
                    if (world instanceof ServerWorld serverWorld && !list.isEmpty()) {
                        Vec3d eyePos = playerEntity.getEyePos();
                        GrappleClawEntity grappleClaw = new GrappleClawEntity(serverWorld, playerEntity, eyePos.x, eyePos.y, eyePos.z, itemStack.split(1), stack);
                        grappleClaw.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, 2.5F, 1.0F);
                        access.klaxon$setGrappleClaw(grappleClaw);
                        serverWorld.spawnEntity(grappleClaw);
                    }

                    world.playSound(
                            null,
                            playerEntity.getX(),
                            playerEntity.getY(),
                            playerEntity.getZ(),
                            SoundEvents.ITEM_TRIDENT_THROW,
                            SoundCategory.PLAYERS,
                            1.0F,
                            1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F
                    );
                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                }
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (user instanceof PlayerEntity player) {
            PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) player;
            GrappleClawEntity grappleClaw = access.klaxon$getGrappleClaw();
            if (grappleClaw != null) {
                if (grappleClaw.isAnchored()) {
                    Vec3d vec = grappleClaw.getPos().subtract(player.getPos()).multiply(0.01);
                    player.addVelocity(vec);
                } else if (!world.isClient()) {
                    Vec3d vec = player.getEyePos().subtract(grappleClaw.getPos()).multiply(0.05);
                    grappleClaw.addVelocity(vec);
                }
            }
        }

        super.usageTick(world, user, stack, remainingUseTicks);
    }

    public static float getPullProgress(int useTicks) {
        float f = useTicks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }
}
