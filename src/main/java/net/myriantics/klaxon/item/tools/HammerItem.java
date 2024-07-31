package net.myriantics.klaxon.item.tools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.myriantics.klaxon.recipes.hammer.HammerRecipe;
import net.myriantics.klaxon.util.EquipmentSlotHelper;
import net.myriantics.klaxon.util.KlaxonTags;

import java.util.Optional;

public class HammerItem extends Item {
    public static final float ATTACK_DAMAGE = 9.0F;
    public static final float ATTACK_SPEED = -2.8F;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public HammerItem(Settings settings) {
        super(settings);
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", ATTACK_DAMAGE, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", ATTACK_SPEED, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
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
            if (state.isIn(KlaxonTags.Blocks.GLASS_BLOCKS) || state.isIn(KlaxonTags.Blocks.GLASS_PANES)) {
                // haha glass go smash
                // may just make it have ludicrous mining speed for any hammer mineable blocks but we'll see how this goes
                return 20.0F;
            } else if (state.isIn(KlaxonTags.Blocks.HAMMER_INTERACTION_POINT)){
                return 0.0F;
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

        // walljump (TOTALLY NOT YOINKED FROM TRIDENT CODE WITH TWEAKS)
        if (!player.isOnGround()) {
            float playerYaw = player.getYaw();
            float playerPitch = player.getPitch();
            float h = MathHelper.sin(playerYaw * 0.017453292F) * MathHelper.cos(playerPitch * 0.017453292F);
            float k = MathHelper.sin(playerPitch * 0.017453292F);
            float l = -MathHelper.cos(playerYaw * 0.017453292F) * MathHelper.cos(playerPitch * 0.017453292F);
            float m = MathHelper.sqrt(h * h + k * k + l * l);
            float n = 0.8F * player.getAttackCooldownProgress(0.5f);
            h *= n / m;
            k *= n / m;
            l *= n / m;
            // may remove, idk thought itd be a good tradeoff for the power of the hammer
            player.handleFallDamage(player.fallDistance, 1, player.getDamageSources().fall());

            player.addVelocity(h, k, l);

            player.resetLastAttackedTicks();

            if (world.isClient) {
                return ActionResult.SUCCESS;
            }

            // damage it wheee
            // damage is based off of attack cooldown progress because thats cool ig
            if (!player.isCreative()) {
                context.getStack().damage((Math.random() < player.getAttackCooldownProgress(0.5f) ? 1 : 0), player, (e) -> {
                    e.sendEquipmentBreakStatus(EquipmentSlotHelper.convert(context.getHand()));
                });
            }

            // hammering recipe
        } else if(interactionState.isIn(KlaxonTags.Blocks.HAMMER_INTERACTION_POINT)) {
            RecipeType<HammerRecipe> type = HammerRecipe.Type.INSTANCE;
            CraftingInventory dummyInventory = new CraftingInventory(player.currentScreenHandler, 1, 1);
            dummyInventory.setStack(0, player.getOffHandStack());

            Optional<HammerRecipe> match = world.getRecipeManager().getFirstMatch(type, dummyInventory, world);

            if(match.isEmpty()) {
                return ActionResult.FAIL;
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

        // dummy usage, just like hitting something in lethal and it makes the fail sound
        // need to add some kind of sound to signify that the jump or recipe didnt work
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isIn(KlaxonTags.Items.STEEL_INGOTS);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }
}
