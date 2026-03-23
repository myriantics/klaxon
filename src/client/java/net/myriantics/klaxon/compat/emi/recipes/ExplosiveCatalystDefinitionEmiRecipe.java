package net.myriantics.klaxon.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.myriantics.klaxon.compat.emi.registry.KlaxonEmiCategories;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipe;

import java.util.List;

public class ExplosiveCatalystDefinitionEmiRecipe extends EmiInfoRecipe {

    private static final Style EXPLOSION_POWER_CONSTANT_STYLE = Style.EMPTY.withBold(true).withColor(CommonColors.BLACK);
    private static final Style EXPLOSION_POWER_MIN_STYLE = Style.EMPTY.withBold(true).withColor(CommonColors.BLUE);
    private static final Style EXPLOSION_POWER_MAX_STYLE = Style.EMPTY.withBold(true).withColor(CommonColors.RED);

    // constant recipe-defined recipes
    public ExplosiveCatalystDefinitionEmiRecipe(RecipeHolder<ExplosiveCatalystDefinitionRecipe> recipeEntry) {
        super(List.of(EmiIngredient.of(recipeEntry.value().getIngredient())),
                List.of(
                        Component.translatable("klaxon.emi.text.explosion_power_info.explosion_power.constant", recipeEntry.value().getData().explosionPower()).setStyle(EXPLOSION_POWER_CONSTANT_STYLE)
                ),
                recipeEntry.id());
    }

    // constant behavior-defined recipes with a description
    public ExplosiveCatalystDefinitionEmiRecipe(RecipeHolder<ExplosiveCatalystDefinitionRecipe> recipeEntry, Component behaviorDescription) {
        super(List.of(EmiIngredient.of(recipeEntry.value().getIngredient())),
                List.of(
                        Component.translatable("klaxon.emi.text.explosion_power_info.explosion_power.constant", recipeEntry.value().getData().explosionPower()).setStyle(EXPLOSION_POWER_CONSTANT_STYLE),
                        behaviorDescription
                ),
                recipeEntry.id());
    }

    // variable behavior-defined recipes with a description
    // mins and maxes are manually defined in lang file
    public ExplosiveCatalystDefinitionEmiRecipe(RecipeHolder<ExplosiveCatalystDefinitionRecipe> recipeEntry, Component explosionPowerMin, Component explosionPowerMax, Component behaviorDescription) {
        super(List.of(EmiIngredient.of(recipeEntry.value().getIngredient())),
                List.of(
                        Component.translatable("klaxon.emi.text.explosion_power_info.explosion_power.min", explosionPowerMin).setStyle(EXPLOSION_POWER_MIN_STYLE),
                        Component.translatable("klaxon.emi.text.explosion_power_info.explosion_power.max", explosionPowerMax).setStyle(EXPLOSION_POWER_MAX_STYLE),
                        behaviorDescription
                ),
                recipeEntry.id());
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return KlaxonEmiCategories.EXPLOSIVE_CATALYST_DEFINITION;
    }

}
