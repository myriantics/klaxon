package net.myriantics.klaxon.item.tools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.*;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.recipes.hammer.HammerRecipe;
import net.myriantics.klaxon.util.AbilityModifierHelper;
import net.myriantics.klaxon.util.EquipmentSlotHelper;
import net.myriantics.klaxon.util.KlaxonTags;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class HammerItem extends Item implements AttackBlockCallback, AttackEntityCallback {
    public static final float ATTACK_DAMAGE = 9.0F;
    public static final float ATTACK_SPEED = -2.8F;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public HammerItem(Settings settings) {
        super(settings);
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", ATTACK_DAMAGE, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", ATTACK_SPEED, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
        AttackBlockCallback.EVENT.register(this);
        AttackEntityCallback.EVENT.register(this);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient && !state.isIn(BlockTags.FIRE)) {
            int damageAmount;
            if (state.isIn(KlaxonTags.Blocks.HAMMER_MINEABLE)) {
                // steel tools have innate unbreaking to compensate for unenchantability
                damageAmount = Math.random() < 0.2 ? 1 : 0;
            } else {
                damageAmount = 1;
            }
            stack.damage(damageAmount, miner, (e) -> {
                e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
            });
        }
        return stack.isSuitableFor(state) || super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.getWorld();
        if (!world.isClient) {
            int damageAmount = Math.random() < 0.5 ? 1 : 0;
            stack.damage(damageAmount, attacker, (e) -> {
                e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
            });
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public boolean isSuitableFor(BlockState state) {
        return state.isIn(KlaxonTags.Blocks.HAMMER_MINEABLE) && !state.isIn(KlaxonTags.Blocks.HAMMER_INTERACTION_POINT);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if (state.isIn(KlaxonTags.Blocks.HAMMER_MINEABLE)) {
            if (state.isIn(KlaxonTags.Blocks.HAMMER_INSTABREAK)) {
                // idk why you need the world and pos parameters for this the method doesnt even use them lmao
                return state.getHardness(null, null) * 30 * 5;
            } else {
                return 6.0F;
            }
        } else {
            return super.getMiningSpeedMultiplier(stack, state);
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos interactionPos = context.getBlockPos();
        BlockState interactionState = context.getWorld().getBlockState(interactionPos);
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();

        if (player == null) {
            return ActionResult.PASS;
        }

        // hammering recipe
        if(player.isOnGround() && interactionState.isIn(KlaxonTags.Blocks.HAMMER_INTERACTION_POINT)) {
            RecipeType<HammerRecipe> type = HammerRecipe.Type.INSTANCE;
            CraftingInventory dummyInventory = new CraftingInventory(player.currentScreenHandler, 1, 1);
            dummyInventory.setStack(0, player.getOffHandStack());

            Optional<HammerRecipe> match = world.getRecipeManager().getFirstMatch(type, dummyInventory, world);

            if(match.isEmpty()) {
                return ActionResult.PASS;
            }

            if(world.isClient) {
                return ActionResult.SUCCESS;
            }
            world.playSound(player, interactionPos, SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK, SoundCategory.PLAYERS, 2, 2f);
            world.spawnEntity(new ItemEntity(world,
                    interactionPos.getX() + 0.5,
                    interactionPos.getY() + 1,
                    interactionPos.getZ() + 0.5,
                    match.get().getOutput(world.getRegistryManager()).copy(),
                    0,0.2,0));
            player.getOffHandStack().decrement(1);
        }
        return ActionResult.PASS;
    }

    // block hit
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {

        BlockState targetBlockState = world.getBlockState(pos);

        if (player == null) {
            return ActionResult.PASS;
        }

        if (canWallJump(player)) {

            // may remove, idk thought itd be a good tradeoff for the power of the hammer
            // keeps wind charges relevant and encourages going up
            // may need to decrease attack cooldown or at least its impact on walljump power scaling
            // made it dynamic based on whether its an instabreak or not to account for dream shear leaf clutch edge case
            boolean processFallDamage = !targetBlockState.isIn(KlaxonTags.Blocks.HAMMER_INSTABREAK);

            processWallJumpPhysics(player, processFallDamage, 1.0f);

            player.onLanding();

            player.resetLastAttackedTicks();

            // damage it wheee
            // damage is based off of attack cooldown progress because thats cool ig
            if (!player.isCreative()) {
                player.getMainHandStack().damage((Math.random() < player.getAttackCooldownProgress(0.5f) ? 1 : 0), player, (e) -> {
                    e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                });
            }
        }

        return ActionResult.PASS;
    }

    // entity hit
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity targetEntity, @Nullable EntityHitResult hitResult) {
        if (player == null) {
            return ActionResult.PASS;
        }

        if (canWallJump(player)) {
            boolean wallJumpFallDamage = true;
            float wallJumpMultiplier = 1.0f;

            if (targetEntity.getType().isIn(KlaxonTags.Entities.BOUNCY_ENTITIES)) {
                wallJumpFallDamage = false;
                wallJumpMultiplier = 1.3f;
            }

            processWallJumpPhysics(player, wallJumpFallDamage, wallJumpMultiplier);

        }
        return ActionResult.PASS;
    }
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isIn(KlaxonTags.Items.STEEL_INGOTS);
    }
    private boolean canWallJump(PlayerEntity player) {
        return !player.isOnGround() && player.getMainHandStack().getItem() instanceof HammerItem;
    }

    // so you can walljump in creative without demolishing your world
    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    // yoinked from trident riptide physics - edited to suit my needs
    private void processWallJumpPhysics(PlayerEntity player, boolean fallDamage, float multiplier) {
        float playerYaw = player.getYaw();
        float playerPitch = player.getPitch();
        float h = MathHelper.sin(playerYaw * 0.017453292F) * MathHelper.cos(playerPitch * 0.017453292F);
        float k = MathHelper.sin(playerPitch * 0.017453292F);
        float l = -MathHelper.cos(playerYaw * 0.017453292F) * MathHelper.cos(playerPitch * 0.017453292F);
        float m = MathHelper.sqrt(h * h + k * k + l * l);
        float n = 0.8F * player.getAttackCooldownProgress(0.5f) * AbilityModifierHelper.calculate(player) * multiplier;
        h *= n / m;
        k *= n / m;
        l *= n / m;

        if (fallDamage) {
            player.handleFallDamage(player.fallDistance, 1, player.getDamageSources().fall());
        }

        player.addVelocity(h, k, l);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }
}
