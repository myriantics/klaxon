package net.myriantics.klaxon.item.customitems;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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

        if(player.isSneaking() && context.getSide() == Direction.UP && interactionState.isIn(KlaxonTags.Blocks.HAMMER_INTERACTION_POINT)) {
            player.playSound(BlockSoundGroup.NETHERITE.getBreakSound(), 2f, 2f);
            interactionState.updateNeighbors(context.getWorld(), interactionPos, 1);

            CraftingInventory inventory = new CraftingInventory(ScreenHandler(null, 0)
            }, 1, 1);
            inventory.setStack(0, player.getOffHandStack());

            Optional<HammerRecipe> match = world.getRecipeManager()
                    .getFirstMatch(HammerRecipe.Type.INSTANCE, inventory, world);
            if(match.isPresent()) {
                player.getInventory().offerOrDrop(match.get().getOutput(DynamicRegistryManager.EMPTY).copy());
                player.getOffHandStack().decrement(1);
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.PASS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}
