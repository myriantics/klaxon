package net.myriantics.klaxon.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.item.Item;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.behavior.BlastProcessorBehavior;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.block.customblocks.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.compat.emi.recipes.BlastProcessingEmiRecipe;
import net.myriantics.klaxon.compat.emi.recipes.HammeringEmiRecipe;
import net.myriantics.klaxon.compat.emi.recipes.ItemExplosionPowerEmiInfoRecipe;
import net.myriantics.klaxon.recipe.KlaxonRecipeTypes;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerRecipe;

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

        registry.addWorkstation(KlaxonEmiRecipeCategories.BLAST_PROCESSING, EmiStack.of(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR));
        registry.addWorkstation(KlaxonEmiRecipeCategories.ITEM_EXPLOSION_POWER, EmiStack.of(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR));
    }

    private void registerRecipes(EmiRegistry registry) {
        addAll(registry, KlaxonRecipeTypes.HAMMERING, HammeringEmiRecipe::new);
        addAllConditional(registry, KlaxonRecipeTypes.ITEM_EXPLOSION_POWER, ItemExplosionPowerEmiInfoRecipe::new);
        addBlastProcessorBehaviorItemExplosionPowerRecipes(registry);
        addAll(registry, KlaxonRecipeTypes.BLAST_PROCESSING, (recipe) -> new BlastProcessingEmiRecipe(recipe, registry, recipe.id()));
    }

    public <C extends Recipe<RecipeInput>, T extends RecipeEntry<C>> void addAll(EmiRegistry registry, RecipeType<C> type, Function<RecipeEntry<C>, EmiRecipe> constructor) {
        for (RecipeEntry<C> recipeEntry : registry.getRecipeManager().listAllOfType(type)) {
            registry.addRecipe(constructor.apply(recipeEntry));
        }
    }

    public <C extends Recipe<RecipeInput>, T extends RecipeEntry<C>> void addAllConditional(EmiRegistry registry, RecipeType<C> type, Function<RecipeEntry<C>, EmiRecipe> constructor) {
        for (RecipeEntry<C> recipeEntry : registry.getRecipeManager().listAllOfType(type)) {

            // dont show hidden recipes
            if (recipeEntry.value() instanceof ItemExplosionPowerRecipe itemExplosionPowerRecipe) {
                if (!itemExplosionPowerRecipe.isHidden()) {
                    registry.addRecipe(constructor.apply(recipeEntry));
                }
            }
        }
    }

    public void addBlastProcessorBehaviorItemExplosionPowerRecipes(EmiRegistry registry) {
        for (Item entry : DeepslateBlastProcessorBlock.BEHAVIORS.keySet()) {
            BlastProcessorBehavior behavior = DeepslateBlastProcessorBlock.BEHAVIORS.get(entry);
            BlastProcessorBehavior.BlastProcessorBehaviorItemExplosionPowerEmiDataCompound data = behavior.getEmiData();

            // only add the recipe if the behavior actually passes in the data
            if (data != null) {
                registry.addRecipe(new ItemExplosionPowerEmiInfoRecipe(
                        Ingredient.ofItems(entry),
                        data.explosionPowerMin(),
                        data.explosionPowerMax(),
                        data.infoText(),
                        // append entry id to recipe id to prevent duplicate entries
                        KlaxonCommon.locate("/bp_behavior_item_explosion_power_" + Identifier.of(entry.toString()).getPath())
                        )
                );
            }
        }
    }
}
