package net.myriantics.klaxon.recipes.blast_processing;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerRecipe;

import java.util.Optional;

import static net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity.CATALYST_INDEX;
import static net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity.PROCESS_ITEM_INDEX;

public class BlastProcessingInator {

    private final double explosionPowerMin;
    private final double explosionPowerMax;
    private final double explosionPower;
    private final boolean requiresFire;
    private final boolean producesFire;
    private final ItemStack result;
    private final BlastProcessorOutputState outputState;

    public BlastProcessingInator(World world, Inventory inventory) {
        ItemStack processItem = inventory.getStack(PROCESS_ITEM_INDEX);
        ItemStack catalystItem = inventory.getStack(CATALYST_INDEX);

        SimpleInventory simpleInventory = (SimpleInventory) inventory;

        RecipeManager recipeManager = world.getRecipeManager();

        Optional<ItemExplosionPowerRecipe> itemExplosionPowerMatch = Optional.empty();
        Optional<BlastProcessorRecipe> blastProcessingMatch = Optional.empty();

        if (!processItem.isEmpty()) {
            blastProcessingMatch = recipeManager.getFirstMatch(BlastProcessorRecipe.Type.INSTANCE, simpleInventory, world);
        }

        if (!catalystItem.isEmpty()) {
            itemExplosionPowerMatch = recipeManager.getFirstMatch(ItemExplosionPowerRecipe.Type.INSTANCE, simpleInventory, world);
        }

        if (itemExplosionPowerMatch.isPresent()) {
            this.explosionPower = itemExplosionPowerMatch.get().getExplosionPower();
            this.producesFire = itemExplosionPowerMatch.get().producesFire();
        } else {
            this.explosionPower = 0.0;
            this.producesFire = false;
        }

        if (world.getServer() != null) {
            world.getServer().sendMessage(Text.literal("processItem: " + catalystItem.getItem().toString()));
            world.getServer().sendMessage(Text.literal("catalystItem: " + catalystItem.getItem().toString()));
        }

        BlastProcessorOutputState interimOutputState;

        if (blastProcessingMatch.isPresent()) {
            this.explosionPowerMin = blastProcessingMatch.get().getExplosionPowerMin();
            this.explosionPowerMax = blastProcessingMatch.get().getExplosionPowerMax();
            this.requiresFire = blastProcessingMatch.get().requiresFire();
            this.result = blastProcessingMatch.get().getOutput(null);
            if (explosionPower > 0) {
                if (explosionPower < explosionPowerMin) {
                    interimOutputState = BlastProcessorOutputState.UNDERPOWERED;
                } else if (explosionPower > explosionPowerMax) {
                    interimOutputState = BlastProcessorOutputState.OVERPOWERED;
                } else {
                    if (requiresFire == producesFire || producesFire) {
                        interimOutputState = BlastProcessorOutputState.SUCCESS;
                    } else {
                        interimOutputState = BlastProcessorOutputState.MISSING_FIRE;
                    }
                }
            } else {
                interimOutputState = BlastProcessorOutputState.MISSING_FUEL;
            }
        } else {
            this.explosionPowerMin = 0.0;
            this.explosionPowerMax = 0.0;
            this.requiresFire = false;
            this.result = ItemStack.EMPTY;
            interimOutputState = BlastProcessorOutputState.MISSING_RECIPE;
        }

        this.outputState = interimOutputState;
    }

    public double getExplosionPowerMin() {
        return explosionPowerMin;
    }

    public double getExplosionPowerMax() {
        return explosionPowerMax;
    }

    public double getExplosionPower() {
        return explosionPower;
    }

    public boolean requiresFire() {
        return requiresFire;
    }

    public boolean producesFire() {
        return producesFire;
    }

    public ItemStack getResult() {
        return result;
    }

    public BlastProcessorOutputState getOutputState() {
        return outputState;
    }

    private void selfDestructButton(World world) {
        if (world.getServer() != null) {
            world.getServer().sendMessage(Text.literal("curse you perry the platypus"));
        }
    }
}
