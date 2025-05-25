package net.myriantics.klaxon.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.RecipeInput;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.BlastProcessorCatalystBehavior;
import net.myriantics.klaxon.compat.emi.recipes.*;
import net.myriantics.klaxon.recipe.blast_processor_behavior.BlastProcessorBehaviorRecipe;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.registry.minecraft.KlaxonItems;
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
        registerCategories(registry);
        registerWorkstations(registry);
        registerRecipes(registry);
    }

    private void registerCategories(EmiRegistry registry) {
        registry.addCategory(KlaxonEmiRecipeCategories.TOOL_USAGE);
        registry.addCategory(KlaxonEmiRecipeCategories.BLAST_PROCESSING);
        registry.addCategory(KlaxonEmiRecipeCategories.ITEM_EXPLOSION_POWER);
        registry.addCategory(KlaxonEmiRecipeCategories.ITEM_COOLING);
    }

    private void registerWorkstations(EmiRegistry registry) {
        registry.addWorkstation(KlaxonEmiRecipeCategories.BLAST_PROCESSING, EmiStack.of(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR));
        registry.addWorkstation(KlaxonEmiRecipeCategories.ITEM_EXPLOSION_POWER, EmiStack.of(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR));

        registry.addWorkstation(KlaxonEmiRecipeCategories.ITEM_COOLING, EmiStack.of(PotionContentsComponent.createStack(Items.SPLASH_POTION, Potions.WATER)));
        registry.addWorkstation(KlaxonEmiRecipeCategories.ITEM_COOLING, EmiStack.of(Items.POWDER_SNOW_BUCKET));
        registry.addWorkstation(KlaxonEmiRecipeCategories.ITEM_COOLING, EmiStack.of(Items.WATER_BUCKET));
        registry.addWorkstation(KlaxonEmiRecipeCategories.ITEM_COOLING, EmiStack.of(Items.CAULDRON));

        // Steel Hammer can mimic AnvilScreenHandler functionality
        registry.addWorkstation(VanillaEmiRecipeCategories.ANVIL_REPAIRING, EmiStack.of(KlaxonItems.STEEL_HAMMER));
    }

    private void registerRecipes(EmiRegistry registry) {
        addAll(registry, KlaxonRecipeTypes.TOOL_USAGE, ToolUsageEmiRecipe::new);
        addAllConditional(registry, KlaxonRecipeTypes.ITEM_EXPLOSION_POWER, ItemExplosionPowerEmiInfoRecipe::new);
        addBlastProcessorBehaviorItemExplosionPowerRecipes(registry);
        addAll(registry, KlaxonRecipeTypes.BLAST_PROCESSING, (recipe) -> new BlastProcessingEmiRecipe(recipe, registry, recipe.id()));
        registerMiscRecipes(registry);
        addAll(registry, KlaxonRecipeTypes.ITEM_COOLING, ItemCoolingEmiRecipe::new);
    }

    private void registerMiscRecipes(EmiRegistry registry) {
        registry.addRecipe(new KlaxonEMIAnvilRecipe(EmiStack.of(Items.FLINT_AND_STEEL), EmiIngredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_NUGGETS), "flint_and_steel"));
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

            // only add the recipe if the behavior actually passes in the data
            if (behavior != null && behavior.getEmiData()!= null) {
                BlastProcessorCatalystBehavior.BlastProcessorBehaviorItemExplosionPowerEmiDataCompound data = behavior.getEmiData();

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
