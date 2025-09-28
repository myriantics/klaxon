package net.myriantics.klaxon.item.equipment.tools.grapple_winch;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.predicate.item.ItemPredicate;
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
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import net.myriantics.klaxon.util.KlaxonMathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class GrappleWinchItem extends RangedWeaponItem {

    static protected ItemPredicate PROJECTILES = ItemPredicate.Builder.create().tag(KlaxonItemTags.GRAPPLE_CLAWS).build();

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
        projectile.setVelocity(shooter, shooter.getPitch(), shooter.getYaw() + yaw, 0.0F, speed, divergence);
    }

    @Override
    public void onStoppedUsing(ItemStack winchStack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) playerEntity;

            if (!access.klaxon$hasActiveConnection()) {
                int i = this.getMaxUseTime(winchStack, user) - remainingUseTicks;
                float f = getPullProgress(i);
                if (!(f < 0.1)) {
                    ChargedProjectilesComponent chargedProjectilesComponent = winchStack.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);
                    if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
                        if (world instanceof ServerWorld serverWorld) {
                            // shoot a grapple claw if we have one loaded
                            Vec3d eyePos = playerEntity.getEyePos();
                            GrappleClawEntity grappleClaw = new GrappleClawEntity(serverWorld, playerEntity, eyePos.x, eyePos.y, eyePos.z, chargedProjectilesComponent.getProjectiles().get(0), winchStack);
                            grappleClaw.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, 2.5F, 1.0F);
                            serverWorld.spawnEntity(grappleClaw);
                            grappleClaw.attachCable((ServerPlayerEntity) playerEntity);
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
                    }

                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                }
            } else if (access.klaxon$hasActiveConnection()) {
                access.klaxon$resetWinchCableLength();
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) user;
        ItemStack winchStack = user.getStackInHand(hand);
        ItemStack ammoStack = user.getProjectileType(winchStack);
        ChargedProjectilesComponent chargedProjectilesComponent = winchStack.get(DataComponentTypes.CHARGED_PROJECTILES);

        // proceed if connection is active or winch has a grapple claw stored
        if (access.klaxon$hasActiveConnection() || (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty())) {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(winchStack);
        } else if (!ammoStack.isEmpty()) {
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
        if (clickType.equals(ClickType.RIGHT) && slot.canTakePartial(player)) {
            // don't allow any right click item movements or actions when a connection is active
            if (((PlayerEntityGrappleAccess) player).klaxon$hasActiveConnection()) {
                return true;
            }

            World world = player.getWorld();

            List<ItemStack> projectiles = stack.get(DataComponentTypes.CHARGED_PROJECTILES) instanceof ChargedProjectilesComponent component ? component.getProjectiles() : List.of();

            if (!projectiles.isEmpty() && otherStack.isEmpty()) {
                if (!world.isClient()) {
                    // yoink stack from front of list and set cursor stack to it
                    ItemStack firstProjectileStack = projectiles.getFirst();
                    cursorStackReference.set(firstProjectileStack);

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
            } else if (projectiles.isEmpty() && PROJECTILES.test(otherStack)) {
                if (loadIfPossible(stack, otherStack, player)) {
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
                }
                return true;
            }
        }

        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    public static boolean loadIfPossible(ItemStack winchStack, ItemStack loadingStack, @Nullable LivingEntity entity) {
        @Nullable ChargedProjectilesComponent originalProjectiles = winchStack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if ((originalProjectiles == null || originalProjectiles.isEmpty()) && PROJECTILES.test(loadingStack)) {
            if (entity != null && entity.getWorld().isClient()) {
                return true;
            } else {
                // if the entity doesn't have grapple access or it doesn't have an active connection, proceed.
                if (!(entity instanceof PlayerEntityGrappleAccess access) || !access.klaxon$hasActiveConnection()) {
                    List<ItemStack> list = load(winchStack, loadingStack, entity);
                    // load grapple winch if we don't have a grapple claw loaded
                    winchStack.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(list));

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        ChargedProjectilesComponent chargedProjectilesComponent = stack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
            ItemStack itemStack = chargedProjectilesComponent.getProjectiles().get(0);
            tooltip.add(Text.translatable("item.klaxon.grapple_winch.projectile").append(ScreenTexts.SPACE).append(itemStack.toHoverableText()));
        } else {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) {
                return;
            }

            // initialize max cable length
            double maxCableLength = player.getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH);

            MutableText valuesText;

            // only render live numbers if cable length is greater than 0
            // ensures no divide by 0
            if (maxCableLength > 0 && ((PlayerEntityGrappleAccess) player).klaxon$hasActiveConnection()) {
                double truncatedCableLength = KlaxonMathHelper.roundToTenth(((PlayerEntityGrappleAccess) player).klaxon$getCurrentWinchCableLength());
                double ratio = truncatedCableLength / maxCableLength;

                valuesText = Texts.bracketed(Text.literal(truncatedCableLength + "/" + maxCableLength));

                // format text according to cable ratio
                if (ratio >= 1.0) {
                    valuesText = valuesText.formatted(Formatting.RED);
                } else if (ratio >= 0.75) {
                    valuesText = valuesText.formatted(Formatting.YELLOW);
                } else {
                    valuesText = valuesText.formatted(Formatting.GREEN);
                }
            } else {
                valuesText = Texts.bracketed(Text.literal("--/--"));
            }

            // add the tooltip
            tooltip.add(Text.translatable("item.klaxon.grapple_winch.current_winch_cable_length")
                    .formatted(Formatting.GRAY)
                    .append(ScreenTexts.SPACE)
                    .append(valuesText)
            );
        }
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
