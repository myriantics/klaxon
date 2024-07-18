package net.myriantics.klaxon.item.tools;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.*;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.recipes.hammer.HammerRecipe;
import net.myriantics.klaxon.util.KlaxonTags;

import java.util.Optional;

public class HammerItem extends Item {

    public HammerItem(Settings settings) {
        super(settings);

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
        return state.isIn(KlaxonTags.Blocks.HAMMER_MINEABLE) || super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public boolean isSuitableFor(BlockState state) {
        return state.isIn(KlaxonTags.Blocks.HAMMER_MINEABLE);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if (state.isIn(KlaxonTags.Blocks.HAMMER_MINEABLE)) {
            if (state.isIn(KlaxonTags.Blocks.GLASS_BLOCKS) || state.isIn(KlaxonTags.Blocks.GLASS_PANES)) {
                // haha glass go smash
                // may just make it have ludicrous mining speed for any hammer mineable blocks but we'll see how this goes
                return 20.0F;
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

        if(interactionState.isIn(KlaxonTags.Blocks.HAMMER_INTERACTION_POINT)) {

            RecipeType<HammerRecipe> type = HammerRecipe.Type.INSTANCE;
            CraftingInventory dummyInventory = new CraftingInventory(player.currentScreenHandler, 1, 1);
            // player.currentScreenHandler my savior holy frick :|
            dummyInventory.setStack(0, player.getOffHandStack());

            Optional<HammerRecipe> match = world.getRecipeManager()
                    .getFirstMatch(type, dummyInventory, world);

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
        return ActionResult.PASS;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isIn(KlaxonTags.Items.STEEL_INGOTS);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}
