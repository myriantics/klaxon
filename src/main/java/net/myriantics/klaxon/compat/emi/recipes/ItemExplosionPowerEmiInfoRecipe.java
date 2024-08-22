package net.myriantics.klaxon.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.text.Text;
import net.myriantics.klaxon.compat.emi.KlaxonEmiRecipeCategories;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerRecipe;

import java.util.List;

public class ItemExplosionPowerEmiInfoRecipe extends EmiInfoRecipe {
    public ItemExplosionPowerEmiInfoRecipe(ItemExplosionPowerRecipe recipe) {
        super(List.of(EmiIngredient.of(recipe.getItem())), List.of(
                Text.translatable("klaxon.emi.text.explosion_power"),
                Text.literal("" + recipe.getExplosionPower())),
                recipe.getId());
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return KlaxonEmiRecipeCategories.ITEM_EXPLOSION_POWER;
    }
}
