package net.myriantics.klaxon.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.RecipeInput;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.BlastProcessorCatalystBehavior;
import net.myriantics.klaxon.recipe.blast_processor_behavior.BlastProcessorBehaviorRecipe;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.compat.emi.recipes.BlastProcessingEmiRecipe;
import net.myriantics.klaxon.compat.emi.recipes.HammeringEmiRecipe;
import net.myriantics.klaxon.compat.emi.recipes.ItemExplosionPowerEmiInfoRecipe;
import net.myriantics.klaxon.compat.emi.recipes.KlaxonEMIAnvilRecipe;
import net.myriantics.klaxon.registry.minecraft.KlaxonRecipeTypes;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerRecipe;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.Random;
import java.util.function.Function;

// spectrum's emi plugin used as reference
public class KlaxonEmiPlugin implements EmiPlugin {

    public static final Random RANDOM = new Random();

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
        registerMiscRecipes(registry);
    }

    private void registerMiscRecipes(EmiRegistry registry) {
        registry.addRecipe(new KlaxonEMIAnvilRecipe(EmiStack.of(Items.FLINT_AND_STEEL), EmiIngredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_NUGGETS), "flint_and_steel"));
        registry.addRecipe(EmiWorldInteractionRecipe.builder()
                .id(KlaxonCommon.locate("/molten_rubber_block/cooling"))
                .supportsRecipeTree(true)
                .leftInput(EmiIngredient.of(Ingredient.ofItems(KlaxonBlocks.MOLTEN_RUBBER_BLOCK)))
                .rightInput(EmiIngredient.of(Ingredient.ofItems(Items.PACKED_ICE)), true)
                .output(EmiStack.of(KlaxonBlocks.RUBBER_BLOCK))
                .build());
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
        for (RecipeEntry<BlastProcessorBehaviorRecipe> entry : registry.getRecipeManager().listAllOfType(KlaxonRecipeTypes.BLAST_PROCESSOR_BEHAVIOR)) {
            BlastProcessorBehaviorRecipe recipe = entry.value();

            BlastProcessorCatalystBehavior behavior = KlaxonRegistries.BLAST_PROCESSOR_BEHAVIORS.get(recipe.getBehaviorId());
            BlastProcessorCatalystBehavior.BlastProcessorBehaviorItemExplosionPowerEmiDataCompound data = behavior.getEmiData();

            // only add the recipe if the behavior actually passes in the data
            if (data != null) {
                registry.addRecipe(new ItemExplosionPowerEmiInfoRecipe(
                        recipe.getIngredient(),
                        data.explosionPowerMin(),
                        data.explosionPowerMax(),
                        data.infoText(),
                        entry.id())
                );
            }
        }
    }
}
