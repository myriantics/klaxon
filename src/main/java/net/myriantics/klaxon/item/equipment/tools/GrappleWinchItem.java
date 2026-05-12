package net.myriantics.klaxon.item.equipment.tools;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.item.equipment.ammo.GrappleClawItem;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.GrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.GrappleWinchConnectionManager;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class GrappleWinchItem extends ProjectileWeaponItem {

    private static final Predicate<ItemStack> PROJECTILES = (stack -> {
        if (stack.is(KlaxonItemTags.GRAPPLE_CLAWS)) {
            return true;
        }

        /*
        if (stack.isOf(Items.TRIDENT) && !EnchantmentHelper.hasAnyEnchantmentsWith(stack, EnchantmentEffectComponentTypes.TRIDENT_SPIN_ATTACK_STRENGTH)) {
            return true;
        }*/

        return false;
    });

    private static final ResourceLocation BASE_WINCH_CABLE_LENGTH = KlaxonCommon.locate("base_winch_cable_length");

    public GrappleWinchItem(Properties settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return PROJECTILES;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 20;
    }

    public static ItemAttributeModifiers createAttributeModifiers(Tier material, float baseAttackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, material.getAttackDamageBonus() + baseAttackDamage, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        KlaxonEntityAttributes.WINCH_CABLE_LENGTH,
                        new AttributeModifier(BASE_WINCH_CABLE_LENGTH, 64, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HAND
                ).build();
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
        if (shooter.isFallFlying()) {
            Vec3 projectileSpeed = Vec3.directionFromRotation(shooter.getXRot(), shooter.getYRot());
            projectile.shoot(projectileSpeed.x, projectileSpeed.y, projectileSpeed.z, (float)(shooter.getDeltaMovement().length() + projectileSpeed.length()) * speed, divergence);
        } else {
            projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot() + yaw, 0.0F, speed, divergence);
        }


        // if this is the first projectile shot, attach the server player's cable to it.
        if (index == 0 && shooter instanceof ServerPlayer serverPlayer && projectile instanceof GrapplingHook hook) {
            ServerGrappleWinchConnectionManager.get(serverPlayer.serverLevel()).connect(serverPlayer, hook);
        }
    }

    @Override
    protected Projectile createProjectile(Level world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        ProjectileItem projItem = projectileStack.getItem() instanceof ProjectileItem item ? item : (GrappleClawItem) KlaxonItems.STEEL_GRAPPLE_CLAW;
        return projItem.asProjectile(world, shooter.getEyePosition(), projectileStack, shooter.getNearestViewDirection());
    }

    @Override
    public void releaseUsing(ItemStack winchStack, Level world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof Player playerEntity) {
            GrappleWinchConnectionManager manager = GrappleWinchConnectionManager.get(world);
            @Nullable GrappleWinchConnection connection = manager.fromPlayer(playerEntity);

            if (connection == null) {
                int i = this.getUseDuration(winchStack, user) - remainingUseTicks;
                float pullProgress = getPullProgress(i);
                if (!(pullProgress < 0.1)) {
                    ChargedProjectiles chargedProjectilesComponent = winchStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);

                    if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
                        Item projectileItem = chargedProjectilesComponent.getItems().getFirst().getItem();

                        if (world instanceof ServerLevel serverWorld) {
                            // shoot a grapple claw if we have one loaded
                            // grapple claw is attached in the shoot() method
                            shoot(
                                    serverWorld,
                                    playerEntity,
                                    playerEntity.getUsedItemHand(),
                                    winchStack,
                                    chargedProjectilesComponent.getItems(),
                                    50f/20 + (pullProgress * 10f/20) + (playerEntity.isFallFlying() ? 10f/20 : 0),
                                    1.0f,
                                    true,
                                    null
                            );
                        }

                        // play sound
                        world.playSound(
                                playerEntity,
                                playerEntity.getX(),
                                playerEntity.getY(),
                                playerEntity.getZ(),
                                KlaxonSoundEvents.ITEM_GRAPPLE_WINCH_LAUNCH,
                                SoundSource.PLAYERS,
                                1.0F,
                                1.0F / (world.getRandom().nextFloat() * 0.8F + 1.2F) + pullProgress * 0.5F
                        );
                        world.gameEvent(
                                GameEvent.ENTITY_ACTION,
                                playerEntity.getEyePosition(),
                                GameEvent.Context.of(playerEntity)
                        );

                        playerEntity.awardStat(Stats.ITEM_USED.get(projectileItem));
                        playerEntity.awardStat(Stats.ITEM_USED.get(this));
                    }
                }
            } else {
                connection.resetCableLength();
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        GrappleWinchConnectionManager manager = GrappleWinchConnectionManager.get(level);
        @Nullable GrappleWinchConnection connection = manager.fromPlayer(user);
        ItemStack winchStack = user.getItemInHand(hand);
        ItemStack offhandStack = user.getItemInHand(hand.equals(InteractionHand.OFF_HAND) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
        boolean supportsCable = this.canSupportCable(winchStack);
        boolean offhandSupportsCable = offhandStack.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(offhandStack);
        ItemStack ammoStack = user.getProjectile(winchStack);
        ChargedProjectiles chargedProjectilesComponent = winchStack.get(DataComponents.CHARGED_PROJECTILES);
        boolean isLoaded = chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty();

        // creative mode players can load intangible grapple claws in whenever, even if they don't have claws
        if (!PROJECTILES.test(ammoStack) && user.isCreative()) {
            ammoStack = new ItemStack(KlaxonItems.STEEL_GRAPPLE_CLAW);
            ammoStack.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
        }

        // proceed if connection is active or winch has a grapple claw stored
        // make sure offhand cannot support cable before trying to charge back
        if ((connection != null && supportsCable) || (isLoaded && !offhandSupportsCable)) {
            user.startUsingItem(hand);
            return InteractionResultHolder.consume(winchStack);
        } else if (!isLoaded && !ammoStack.isEmpty()) {
            if (!level.isClientSide()) {
                loadIfPossible(winchStack, ammoStack, user);
            }
            level.playSound(
                    user,
                    user.getX(),
                    user.getEyeY(),
                    user.getZ(),
                    KlaxonSoundEvents.ITEM_GRAPPLE_WINCH_LOAD,
                    SoundSource.PLAYERS,
                    0.7f + level.getRandom().nextFloat() * 0.3f,
                    0.7f + level.getRandom().nextFloat() * 0.3f
            );
            level.gameEvent(
                    GameEvent.ENTITY_ACTION,
                    user.getEyePosition(),
                    GameEvent.Context.of(user)
            );

            user.awardStat(Stats.ITEM_USED.get(this));
            user.awardStat(Stats.ITEM_USED.get(ammoStack.getItem()));
            return InteractionResultHolder.success(winchStack);
        } else {
            return InteractionResultHolder.fail(winchStack);
        }
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
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack otherStack, Slot slot, ClickAction clickType, Player player, SlotAccess cursorStackReference) {
        GrappleWinchConnectionManager manager = GrappleWinchConnectionManager.get(player.level());
        @Nullable GrappleWinchConnection connection = manager.fromPlayer(player);

        if (clickType.equals(ClickAction.SECONDARY) && slot.allowModification(player)) {

            // don't allow any right click item movements or actions when a connection is active
            if (connection != null) {
                return true;
            }

            Level world = player.level();

            List<ItemStack> projectiles = stack.get(DataComponents.CHARGED_PROJECTILES) instanceof ChargedProjectiles component ? component.getItems() : List.of();

            if (!projectiles.isEmpty()) {
                ItemStack firstProjectileStack = projectiles.getFirst();

                if (!firstProjectileStack.has(DataComponents.INTANGIBLE_PROJECTILE)) {
                    if (ItemStack.isSameItemSameComponents(firstProjectileStack, otherStack)) {
                        otherStack.grow(firstProjectileStack.getCount());
                    } else if (otherStack.isEmpty()) {
                        cursorStackReference.set(firstProjectileStack);
                    }
                }

                // clear projectiles on take
                stack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);

                world.playSound(
                        player,
                        player.getX(),
                        player.getEyeY(),
                        player.getZ(),
                        KlaxonSoundEvents.ITEM_GRAPPLE_WINCH_UNLOAD,
                        SoundSource.PLAYERS,
                        0.7f + world.getRandom().nextFloat() * 0.3f,
                        0.7f + world.getRandom().nextFloat() * 0.3f
                );
                world.gameEvent(
                        GameEvent.ENTITY_ACTION,
                        player.getEyePosition(),
                        GameEvent.Context.of(player)
                );

                return true;
            } else if (PROJECTILES.test(otherStack)) {

                stack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(draw(stack, otherStack, player)));

                world.playSound(
                        player,
                        player.getX(),
                        player.getEyeY(),
                        player.getZ(),
                        KlaxonSoundEvents.ITEM_GRAPPLE_WINCH_LOAD,
                        SoundSource.PLAYERS,
                        0.7f + world.getRandom().nextFloat() * 0.3f,
                        0.7f + world.getRandom().nextFloat() * 0.3f
                );
                world.gameEvent(
                        GameEvent.ENTITY_ACTION,
                        player.getEyePosition(),
                        GameEvent.Context.of(player)
                );
                return true;
            }
        }

        return super.overrideOtherStackedOnMe(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    public static boolean loadIfPossible(ItemStack winchStack, ItemStack loadingStack, @Nullable LivingEntity entity) {
        // make sure we're actually trying to load into a grapple winch
        if (!winchStack.is(KlaxonItems.GRAPPLE_WINCH)) {
            return false;
        }

        @Nullable ChargedProjectiles originalProjectiles = winchStack.get(DataComponents.CHARGED_PROJECTILES);
        if ((originalProjectiles == null || originalProjectiles.isEmpty()) && PROJECTILES.test(loadingStack)) {
            List<ItemStack> list = draw(winchStack, loadingStack, entity);
            if (list.isEmpty()) {
                return false;
            }

            if (entity == null || !entity.level().isClientSide()) {
                if (!list.isEmpty()) {
                    winchStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(list));
                }
            }

            return true;
        }

        return false;
    }

    public boolean canSupportCable(ItemStack winchStack) {
        return winchStack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).isEmpty();
    }

    @Override
    public boolean useOnRelease(ItemStack stack) {
        return stack.is(this) && !stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).isEmpty();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 72000;
    }
}
