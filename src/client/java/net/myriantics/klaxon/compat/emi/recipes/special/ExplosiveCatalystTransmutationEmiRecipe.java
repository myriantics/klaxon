package net.myriantics.klaxon.compat.emi.recipes.special;

import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.myriantics.klaxon.recipe.custom_crafting.explosive_catalyst_transmutation.ExplosiveCatalystTransmutationRecipe;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ExplosiveCatalystTransmutationEmiRecipe extends EmiPatternCraftingRecipe {

    private final ShapedRecipePattern pattern;
    private final Item[] possibleCatalysts;
    private final Map<Item, ExplosiveCatalystData> item2DataMap;
    private final ItemStack result;

    public ExplosiveCatalystTransmutationEmiRecipe(ExplosiveCatalystTransmutationRecipe recipe, ResourceLocation id, Item[] possibleCatalysts, Map<Item, ExplosiveCatalystData> item2DataMap) {
        super(
                List.of(
                        EmiIngredient.of(recipe.pattern.ingredients().stream().map(EmiIngredient::of).toList())
                ),
                EmiStack.of(recipe.result),
                id,
                false
        );
        this.possibleCatalysts = possibleCatalysts;
        this.item2DataMap = item2DataMap;
        this.result = recipe.result;
        this.pattern = recipe.pattern;
    }

    @Override
    public SlotWidget getInputWidget(int slot, int x, int y) {
        if (slot == 4) {
            return new GeneratedSlotWidget(
                    random -> EmiStack.of(this.getCatalystStack(random)),
                    this.unique,
                    x, y
            );
        } else {
            return new SlotWidget(
                    EmiIngredient.of(this.pattern.ingredients().get(slot)),
                    x, y
            );
        }
    }

    @Override
    public SlotWidget getOutputWidget(int x, int y) {
        return new GeneratedSlotWidget(
                random -> {
                    ItemStack resultStack = this.result.copy();
                    ItemStack catalystStack = this.getCatalystStack(random);
                    @Nullable ExplosiveCatalystData data = ExplosiveCatalystTransmutationRecipe.getDataForStack(catalystStack, stack -> this.item2DataMap.get(stack.getItem()));
                    if (data != null) {
                        resultStack.set(KlaxonDataComponentTypes.EXPLOSIVE_CATALYST_DATA.value(), data);
                    }
                    return EmiStack.of(resultStack);
                },
                this.unique,
                x, y
        );
    }

    private ItemStack getCatalystStack(Random random) {
        int selected = random.nextInt(this.possibleCatalysts.length + 1);
        if (selected == 0) {
            return ItemStack.EMPTY;
        } else {
            return new ItemStack(this.possibleCatalysts[selected - 1]);
        }
    }
}
