package net.myriantics.klaxon.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.myriantics.klaxon.compat.emi.KlaxonEmiRecipeCategories;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipe;

import java.util.List;

public class ExplosiveCatalystDefinitionEmiRecipe extends EmiInfoRecipe {

    private static final Style EXPLOSION_POWER_CONSTANT_STYLE = Style.EMPTY.withBold(true).withColor(Colors.BLACK);
    private static final Style EXPLOSION_POWER_MIN_STYLE = Style.EMPTY.withBold(true).withColor(Colors.BLUE);
    private static final Style EXPLOSION_POWER_MAX_STYLE = Style.EMPTY.withBold(true).withColor(Colors.RED);

    // constant recipe-defined recipes
    public ExplosiveCatalystDefinitionEmiRecipe(RecipeEntry<ExplosiveCatalystDefinitionRecipe> recipeEntry) {
        super(List.of(EmiIngredient.of(recipeEntry.value().getIngredient())),
                List.of(
                        Text.translatable("klaxon.emi.text.explosion_power_info.explosion_power.constant", recipeEntry.value().getExplosionPower()).setStyle(EXPLOSION_POWER_CONSTANT_STYLE)
                ),
                recipeEntry.id());
    }

    // constant behavior-defined recipes with a description
    public ExplosiveCatalystDefinitionEmiRecipe(RecipeEntry<ExplosiveCatalystDefinitionRecipe> recipeEntry, Text behaviorDescription) {
        super(List.of(EmiIngredient.of(recipeEntry.value().getIngredient())),
                List.of(
                        Text.translatable("klaxon.emi.text.explosion_power_info.explosion_power.constant", recipeEntry.value().getExplosionPower()).setStyle(EXPLOSION_POWER_CONSTANT_STYLE),
                        behaviorDescription
                ),
                recipeEntry.id());
    }

    // variable behavior-defined recipes with a description
    // mins and maxes are manually defined in lang file
    public ExplosiveCatalystDefinitionEmiRecipe(RecipeEntry<ExplosiveCatalystDefinitionRecipe> recipeEntry, Text explosionPowerMin, Text explosionPowerMax, Text behaviorDescription) {
        super(List.of(EmiIngredient.of(recipeEntry.value().getIngredient())),
                List.of(
                        Text.translatable("klaxon.emi.text.explosion_power_info.explosion_power.min", explosionPowerMin).setStyle(EXPLOSION_POWER_MIN_STYLE),
                        Text.translatable("klaxon.emi.text.explosion_power_info.explosion_power.max", explosionPowerMax).setStyle(EXPLOSION_POWER_MAX_STYLE),
                        behaviorDescription
                ),
                recipeEntry.id());
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return KlaxonEmiRecipeCategories.EXPLOSIVE_CATALYST_DEFINITION;
    }

}
