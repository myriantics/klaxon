package net.myriantics.klaxon.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.compat.emi.recipes.BlastProcessingEmiRecipe;
import net.myriantics.klaxon.compat.emi.recipes.HammeringEmiRecipe;
import net.myriantics.klaxon.compat.emi.recipes.ItemExplosionPowerEmiInfoRecipe;
import net.myriantics.klaxon.recipe.KlaxonRecipeTypes;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerRecipe;
import net.myriantics.klaxon.util.KlaxonTags;

import java.util.function.Function;

// spectrum's emi plugin used as reference
public class KlaxonEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        RecipeManager manager = registry.getRecipeManager();

        registerCategories(registry);
        registerRecipes(registry);
    }

    private void registerCategories(EmiRegistry registry) {
        registry.addCategory(KlaxonEmiRecipeCategories.HAMMERING);
        registry.addCategory(KlaxonEmiRecipeCategories.BLAST_PROCESSING);
        registry.addCategory(KlaxonEmiRecipeCategories.ITEM_EXPLOSION_POWER);

        registry.addWorkstation(KlaxonEmiRecipeCategories.HAMMERING, EmiIngredient.of(KlaxonTags.Blocks.HAMMER_INTERACTION_POINT));
        registry.addWorkstation(KlaxonEmiRecipeCategories.BLAST_PROCESSING, EmiStack.of(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR));
        registry.addWorkstation(KlaxonEmiRecipeCategories.ITEM_EXPLOSION_POWER, EmiStack.of(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR));
    }

    private void registerRecipes(EmiRegistry registry) {
        addAll(registry, KlaxonRecipeTypes.HAMMERING, HammeringEmiRecipe::new);
        addAllConditional(registry, KlaxonRecipeTypes.ITEM_EXPLOSION_POWER, ItemExplosionPowerEmiInfoRecipe::new);
        addAll(registry, KlaxonRecipeTypes.BLAST_PROCESSING, (recipe) -> new BlastProcessingEmiRecipe(recipe, registry, recipe.id()));
    }

    public <C extends Recipe<RecipeInput>, T extends RecipeEntry<C>> void addAll(EmiRegistry registry, RecipeType<C> type, Function<RecipeEntry<C>, EmiRecipe> constructor) {
        for (RecipeEntry<C> recipeEntry : registry.getRecipeManager().listAllOfType(type)) {
            registry.addRecipe(constructor.apply(recipeEntry));
        }
    }

    public <C extends Recipe<RecipeInput>, T extends RecipeEntry<C>> void addAllConditional(EmiRegistry registry, RecipeType<C> type, Function<RecipeEntry<C>, EmiRecipe> constructor) {
        for (RecipeEntry<C> recipeEntry : registry.getRecipeManager().listAllOfType(type)) {
            boolean fail = false;

            // dont show creative mode items in the scroller
            if (recipeEntry.value() instanceof ItemExplosionPowerRecipe itemExplosionPowerRecipe) {
                for (ItemStack stack : itemExplosionPowerRecipe.getItem().getMatchingStacks()) {
                    if (stack.isIn(KlaxonTags.Items.ITEM_EXPLOSION_POWER_EMI_OMITTED)) {
                        fail = true;
                    }
                }
            }

            if (!fail) {
                registry.addRecipe(constructor.apply(recipeEntry));
            }
        }
    }


}
