package net.myriantics.klaxon.compat.emi.recipes.blast_processing;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.myriantics.klaxon.compat.emi.recipes.AbstractBlastProcessingEmiRecipe;
import net.myriantics.klaxon.recipe.RecipeOutputCompound;
import net.myriantics.klaxon.recipe.blast_processing.special.DecoratedPotShatteringBlastProcessingRecipe;

import java.util.List;
import java.util.Random;

public class DecoratedPotShatteringBlastProcessingEmiRecipe extends AbstractBlastProcessingEmiRecipe<DecoratedPotShatteringBlastProcessingRecipe> {

    private final Ingredient ingredient;
    private final List<EmiIngredient> inputs;
    private final List<Item> potDecoratorItems;
    private final List<EmiStack> outputs;

    public DecoratedPotShatteringBlastProcessingEmiRecipe(DecoratedPotShatteringBlastProcessingRecipe recipe, ResourceLocation id, List<Item> potDecoratorItems) {
        super(recipe, id);
        this.ingredient = recipe.getIngredient();
        this.inputs = List.of(EmiIngredient.of(this.ingredient));
        this.potDecoratorItems = potDecoratorItems;
        this.outputs = potDecoratorItems.stream().map(EmiStack::of).toList();
    }

    @Override
    protected SlotWidget addInputSlot(WidgetHolder widgets, int x, int y) {
        return new GeneratedSlotWidget(
                random -> EmiStack.of(this.createRandomDecoratedPotStack(random)),
                this.unique,
                x, y
        );
    }

    @Override
    protected SlotWidget addResultSlot(WidgetHolder widgets, int index, int x, int y) {
        if (index > 4) {
            return new SlotWidget(EmiStack.EMPTY, x, y);
        } else {
            return new GeneratedSlotWidget(
                    random -> {
                        // eat the first one to match pot stack
                        random.nextInt();
                        ItemStack[] outputStacks = this.recipe.gatherPotDecorationStacks(this.createRandomPotDecorations(random));

                        if (index < outputStacks.length) {
                            RecipeOutputCompound.setRecipeOutputChanceLore(outputStacks[index], this.recipe.successChance);
                            return EmiStack.of(outputStacks[index]);
                        } else {
                            return EmiStack.EMPTY;
                        }
                    },
                    this.unique,
                    x, y
            );
        }
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return this.inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return this.outputs;
    }

    private ItemStack createRandomDecoratedPotStack(Random random) {
        ItemStack selectedPotStack = this.ingredient.getItems()[random.nextInt(this.ingredient.getItems().length)].copy();

        selectedPotStack.set(DataComponents.POT_DECORATIONS, this.createRandomPotDecorations(random));

        return selectedPotStack;
    }

    private PotDecorations createRandomPotDecorations(Random random) {
        return new PotDecorations(
                this.selectRandomPotDecoratorItem(random),
                this.selectRandomPotDecoratorItem(random),
                this.selectRandomPotDecoratorItem(random),
                this.selectRandomPotDecoratorItem(random)
        );
    }

    private Item selectRandomPotDecoratorItem(Random random) {
        return this.potDecoratorItems.get(random.nextInt(this.potDecoratorItems.size()));
    }
}
