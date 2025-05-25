package net.myriantics.klaxon.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.compat.emi.KlaxonEmiRecipeCategories;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerRecipe;

import java.util.List;

public class ItemExplosionPowerEmiInfoRecipe extends EmiInfoRecipe {

    private final Text tooltipDisplayText;

    public ItemExplosionPowerEmiInfoRecipe(RecipeEntry<ItemExplosionPowerRecipe> recipe) {
        super(
                List.of(EmiIngredient.of(recipe.value().getIngredient())), List.of(
                Text.translatable("klaxon.emi.text.explosion_power_info.explosion_power").append(Text.literal("" + recipe.value().getExplosionPower()))),
                recipe.id()
        );

        tooltipDisplayText = null;
    }

    public ItemExplosionPowerEmiInfoRecipe(Ingredient ingredient,
                                           double explosionPowerMin, double explosionPowerMax,
                                           Text infoText,
                                           Identifier id) {
        super(List.of(
                EmiIngredient.of(ingredient)),
                List.of(
                        Text.translatable("klaxon.emi.text.explosion_power_info.explosion_power_min").append(Text.literal("" + explosionPowerMin)),
                        Text.translatable("klaxon.emi.text.explosion_power_info.explosion_power_max").append(Text.literal("" + explosionPowerMax)),
                        Text.empty(), // lazy newline
                        infoText // text is defined in the associated blast processor behavior (translatable)
                ),
                id
        );

        this.tooltipDisplayText = infoText;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return KlaxonEmiRecipeCategories.ITEM_EXPLOSION_POWER;
    }
}
