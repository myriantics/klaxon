package net.myriantics.klaxon.item.equipment.tools;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
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
import net.myriantics.klaxon.util.KlaxonMathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class GrappleWinchItem extends RangedWeaponItem {

    private static final Predicate<ItemStack> PROJECTILES = (stack -> {
        if (stack.isIn(KlaxonItemTags.GRAPPLE_CLAWS)) {
            return true;
        }

        if (stack.isOf(Items.TRIDENT) && !EnchantmentHelper.hasAnyEnchantmentsWith(stack, EnchantmentEffectComponentTypes.TRIDENT_SPIN_ATTACK_STRENGTH)) {
            return true;
        }

        return false;
    });

    private static final Identifier BASE_WINCH_CABLE_LENGTH = KlaxonCommon.locate("base_winch_cable_length");

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

    public static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material, float baseAttackDamage, float attackSpeed) {
        return AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, material.getAttackDamage() + baseAttackDamage, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, attackSpeed, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        KlaxonEntityAttributes.WINCH_CABLE_LENGTH,
                        new EntityAttributeModifier(BASE_WINCH_CABLE_LENGTH, 48, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.HAND
                ).build();
    }

    @Override
    protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
        if (shooter.isFallFlying()) {
            Vec3d projectileSpeed = Vec3d.fromPolar(shooter.getPitch(), shooter.getYaw());
            projectile.setVelocity(projectileSpeed.x, projectileSpeed.y, projectileSpeed.z, (float)(shooter.getVelocity().length() + projectileSpeed.length()) * speed, divergence);
        } else {
            projectile.setVelocity(shooter, shooter.getPitch(), shooter.getYaw() + yaw, 0.0F, speed, divergence);
        }


        // if this is the first projectile shot, attach the server player's cable to it.
        if (index == 0 && shooter instanceof ServerPlayerEntity serverPlayer && projectile instanceof GrapplingHook hook) {
            ((ServerGrappleWinchConnectionManager.Access) serverPlayer.getServerWorld()).klaxon$get().connect(serverPlayer, hook);
        }
    }

    @Override
    protected ProjectileEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        ProjectileItem projItem = projectileStack.getItem() instanceof ProjectileItem item ? item : (GrappleClawItem) KlaxonItems.STEEL_GRAPPLE_CLAW;
        return projItem.createEntity(world, shooter.getEyePos(), projectileStack, shooter.getFacing());
    }

    @Override
    public void onStoppedUsing(ItemStack winchStack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            GrappleWinchConnectionManager manager = ((GrappleWinchConnectionManager.Access) world).klaxon$get();
            assert manager != null;
            @Nullable GrappleWinchConnection connection = manager.fromPlayer(playerEntity);

            if (connection == null) {
                int i = this.getMaxUseTime(winchStack, user) - remainingUseTicks;
                float f = getPullProgress(i);
                if (!(f < 0.1)) {
                    ChargedProjectilesComponent chargedProjectilesComponent = winchStack.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);

                    if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
                        Item projectileItem = chargedProjectilesComponent.getProjectiles().getFirst().getItem();

                        if (world instanceof ServerWorld serverWorld) {
                            // shoot a grapple claw if we have one loaded
                            // grapple claw is attached in the shoot() method
                            shootAll(
                                    serverWorld,
                                    playerEntity,
                                    playerEntity.getActiveHand(),
                                    winchStack,
                                    chargedProjectilesComponent.getProjectiles(),
                                    2.5f,
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
                                SoundCategory.PLAYERS,
                                1.0F,
                                1.0F / (world.getRandom().nextFloat() * 0.8F + 1.2F) + f * 0.5F
                        );
                        world.emitGameEvent(
                                GameEvent.ENTITY_ACTION,
                                playerEntity.getEyePos(),
                                GameEvent.Emitter.of(playerEntity)
                        );

                        playerEntity.incrementStat(Stats.USED.getOrCreateStat(projectileItem));
                        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                    }
                }
            } else {
                connection.resetCableLength();
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        GrappleWinchConnectionManager manager = ((GrappleWinchConnectionManager.Access) world).klaxon$get();
        assert manager != null;
        @Nullable GrappleWinchConnection connection = manager.fromPlayer(user);

        ItemStack winchStack = user.getStackInHand(hand);
        ItemStack offhandStack = user.getStackInHand(hand.equals(Hand.OFF_HAND) ? Hand.MAIN_HAND : Hand.OFF_HAND);
        boolean supportsCable = this.canSupportCable(winchStack);
        boolean offhandSupportsCable = offhandStack.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(offhandStack);
        ItemStack ammoStack = user.getProjectileType(winchStack);
        ChargedProjectilesComponent chargedProjectilesComponent = winchStack.get(DataComponentTypes.CHARGED_PROJECTILES);
        boolean isLoaded = chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty();

        // proceed if connection is active or winch has a grapple claw stored
        // make sure offhand cannot support cable before trying to charge back
        if ((connection != null && supportsCable) || (isLoaded && !offhandSupportsCable)) {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(winchStack);
        } else if (!isLoaded && !ammoStack.isEmpty()) {
            loadIfPossible(winchStack, ammoStack, user);
            world.playSound(
                    user,
                    user.getX(),
                    user.getEyeY(),
                    user.getZ(),
                    KlaxonSoundEvents.ITEM_GRAPPLE_WINCH_LOAD,
                    SoundCategory.PLAYERS,
                    0.7f + world.getRandom().nextFloat() * 0.3f,
                    0.7f + world.getRandom().nextFloat() * 0.3f
            );
            world.emitGameEvent(
                    GameEvent.ENTITY_ACTION,
                    user.getEyePos(),
                    GameEvent.Emitter.of(user)
            );

            user.incrementStat(Stats.USED.getOrCreateStat(this));
            user.incrementStat(Stats.USED.getOrCreateStat(ammoStack.getItem()));
            return TypedActionResult.success(winchStack);
        } else {
            return TypedActionResult.fail(winchStack);
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
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        GrappleWinchConnectionManager manager = ((GrappleWinchConnectionManager.Access) player.getWorld()).klaxon$get();
        assert manager != null;
        @Nullable GrappleWinchConnection connection = manager.fromPlayer(player);

        if (clickType.equals(ClickType.RIGHT) && slot.canTakePartial(player)) {

            // don't allow any right click item movements or actions when a connection is active
            if (connection != null) {
                return true;
            }

            World world = player.getWorld();

            List<ItemStack> projectiles = stack.get(DataComponentTypes.CHARGED_PROJECTILES) instanceof ChargedProjectilesComponent component ? component.getProjectiles() : List.of();

            if (!projectiles.isEmpty()) {
                if (!world.isClient()) {
                    ItemStack firstProjectileStack = projectiles.getFirst();

                    if (!firstProjectileStack.contains(DataComponentTypes.INTANGIBLE_PROJECTILE)) {
                        if (ItemStack.areItemsAndComponentsEqual(firstProjectileStack, otherStack)) {
                            otherStack.increment(firstProjectileStack.getCount());
                        } else if (otherStack.isEmpty()) {
                            cursorStackReference.set(firstProjectileStack);
                        }
                    }

                    // replace projectiles component with a new one that has everything but the first element
                    List<ItemStack> newProjectiles = projectiles.subList(1, projectiles.size());
                    stack.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(newProjectiles));
                }

                world.playSound(
                        player,
                        player.getX(),
                        player.getEyeY(),
                        player.getZ(),
                        KlaxonSoundEvents.ITEM_GRAPPLE_WINCH_UNLOAD,
                        SoundCategory.PLAYERS,
                        0.7f + world.getRandom().nextFloat() * 0.3f,
                        0.7f + world.getRandom().nextFloat() * 0.3f
                );
                world.emitGameEvent(
                        GameEvent.ENTITY_ACTION,
                        player.getEyePos(),
                        GameEvent.Emitter.of(player)
                );

                return true;
            } else if (PROJECTILES.test(otherStack)) {
                stack.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(otherStack.split(1)));

                world.playSound(
                        player,
                        player.getX(),
                        player.getEyeY(),
                        player.getZ(),
                        KlaxonSoundEvents.ITEM_GRAPPLE_WINCH_LOAD,
                        SoundCategory.PLAYERS,
                        0.7f + world.getRandom().nextFloat() * 0.3f,
                        0.7f + world.getRandom().nextFloat() * 0.3f
                );
                world.emitGameEvent(
                        GameEvent.ENTITY_ACTION,
                        player.getEyePos(),
                        GameEvent.Emitter.of(player)
                );
                return true;
            }
        }

        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    public static boolean loadIfPossible(ItemStack winchStack, ItemStack loadingStack, @Nullable LivingEntity entity) {
        // make sure we're actually trying to load into a grapple winch
        if (!winchStack.isOf(KlaxonItems.GRAPPLE_WINCH)) {
            return false;
        }

        @Nullable ChargedProjectilesComponent originalProjectiles = winchStack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if ((originalProjectiles == null || originalProjectiles.isEmpty()) && PROJECTILES.test(loadingStack)) {
            List<ItemStack> list = load(winchStack, loadingStack, entity);
            if (list.isEmpty()) {
                return false;
            }

            if (entity == null || !entity.getWorld().isClient()) {
                if (!list.isEmpty()) {
                    winchStack.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(list));
                }
            }

            return true;
        }

        return false;
    }

    public boolean canSupportCable(ItemStack winchStack) {
        return winchStack.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT).isEmpty();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        ChargedProjectilesComponent chargedProjectilesComponent = stack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
            ItemStack itemStack = chargedProjectilesComponent.getProjectiles().get(0);
            tooltip.add(Text.translatable("klaxon.text.tooltip.grapple_winch.projectile").append(ScreenTexts.SPACE).append(itemStack.toHoverableText()));
        } else {
            // add the tooltip
            // additional advanced logic is defined in client self-mixin
            tooltip.add(
                    Text.translatable("klaxon.text.tooltip.grapple_winch.cable_length.prefix")
                    .formatted(Formatting.GRAY)
                    .append(createCableLengthDisplayText())
            );
        }
    }

    private static MutableText createCableLengthDisplayText() {
        return Texts.bracketed(Text.translatable("klaxon.text.tooltip.grapple_winch.cable_length.display", "--", "--"));
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
