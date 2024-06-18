package net.myriantics.klaxon.item.tools;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.recipe.RecipeType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.recipes.HammerRecipe;
import net.myriantics.klaxon.util.KlaxonTags;

import java.util.Optional;

public class HammerItem extends Item {
    public HammerItem(Settings settings) {
        super(settings);
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
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}
