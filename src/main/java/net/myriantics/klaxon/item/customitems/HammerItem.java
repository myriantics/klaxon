package net.myriantics.klaxon.item.customitems;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.recipe.RecipeType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
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
            player.sendMessage(Text.literal("balls"));

            RecipeType<HammerRecipe> type = HammerRecipe.Type.INSTANCE;
            CraftingInventory dummyInventory = new CraftingInventory(player.currentScreenHandler, 1, 1);
            // player.currentScreenHandler my savior holy frick :|
            dummyInventory.setStack(0, player.getOffHandStack());
            player.sendMessage(dummyInventory.getStack(0).getName());

            Optional<HammerRecipe> match = world.getRecipeManager()
                    .getFirstMatch(type, dummyInventory, world);

            if(match.isEmpty()) {
                player.sendMessage(Text.literal("sadge"));
                return ActionResult.PASS;
            }

            if(world.isClient) {
                return ActionResult.SUCCESS;
            }
            world.playSound(player, interactionPos, SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK, SoundCategory.PLAYERS, 2, 2f);
            player.getInventory().offerOrDrop(match.get().getOutput(world.getRegistryManager()).copy());
            player.getOffHandStack().decrement(1);
                player.sendMessage(Text.literal("greg is goodge"));
            }
        return ActionResult.PASS;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}
