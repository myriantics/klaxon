package net.myriantics.klaxon.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.BlastProcessorCatalystBehavior;
import net.myriantics.klaxon.compat.emi.recipes.*;
import net.myriantics.klaxon.recipe.blast_processor_behavior.BlastProcessorBehaviorRecipe;
import net.myriantics.klaxon.recipe.manual_item_application.ManualItemApplicationRecipe;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;
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
        // Blast Processors can mimic Blasting Smelting functionality when using a catalyst that produces Fire
        registry.addWorkstation(VanillaEmiRecipeCategories.BLASTING, EmiStack.of(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR));
    }

    private void registerRecipes(EmiRegistry registry) {
        addAll(registry, KlaxonRecipeTypes.TOOL_USAGE, ToolUsageEmiRecipe::new);
        addAllConditional(registry, KlaxonRecipeTypes.ITEM_EXPLOSION_POWER, ItemExplosionPowerEmiInfoRecipe::new);
        addAll(registry, KlaxonRecipeTypes.BLAST_PROCESSING, (recipe) -> new BlastProcessingEmiRecipe(recipe, registry, recipe.id()));
        registerMiscRecipes(registry);
        addAll(registry, KlaxonRecipeTypes.ITEM_COOLING, ItemCoolingEmiRecipe::new);
        addAll(registry, KlaxonRecipeTypes.MANUAL_ITEM_APPLICATION, (entry) -> {
            ManualItemApplicationRecipe recipe = entry.value();
            return EmiWorldInteractionRecipe.builder()
                    .id(entry.id())
                    .leftInput(EmiIngredient.of(recipe.getValidBlockInputs()))
                    .rightInput(EmiIngredient.of(recipe.getInputIngredient()), false)
                    .output(EmiStack.of(recipe.getOutputBlock()))
                    .build();
        });
    }

    private void registerMiscRecipes(EmiRegistry registry) {
        registry.addRecipe(new KlaxonEMIAnvilRecipe(EmiStack.of(Items.FLINT_AND_STEEL), EmiIngredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_NUGGETS), "flint_and_steel"));
    }

    public <C extends Recipe<V>, T extends RecipeEntry<C>, V extends RecipeInput> void addAll(EmiRegistry registry, RecipeType<C> type, Function<RecipeEntry<C>, EmiRecipe> constructor) {
        for (RecipeEntry<C> recipeEntry : registry.getRecipeManager().listAllOfType(type)) {
            registry.addRecipe(constructor.apply(recipeEntry));
        }
    }

    public <C extends Recipe<V>, T extends RecipeEntry<C>, V extends RecipeInput> void addAllConditional(EmiRegistry registry, RecipeType<C> type, Function<RecipeEntry<C>, EmiRecipe> constructor) {
        for (RecipeEntry<C> recipeEntry : registry.getRecipeManager().listAllOfType(type)) {

            // dont show hidden recipes
            if (recipeEntry.value() instanceof ItemExplosionPowerRecipe itemExplosionPowerRecipe) {
                if (!itemExplosionPowerRecipe.isHidden()) {
                    boolean behaviorSearchFailed = true;
                    // crude code to apply more advanced descriptions to recipes if needed
                    for (RecipeEntry<BlastProcessorBehaviorRecipe> behaviorRecipeEntry : registry.getRecipeManager().listAllOfType(KlaxonRecipeTypes.BLAST_PROCESSOR_BEHAVIOR)) {
                        if (behaviorRecipeEntry.value().getIngredient().equals(itemExplosionPowerRecipe.getIngredient())) {
                            Identifier behaviorId = behaviorRecipeEntry.value().getBehaviorId();
                            BlastProcessorCatalystBehavior behavior = KlaxonRegistries.BLAST_PROCESSOR_BEHAVIORS.get(behaviorId);
                            if (behavior != null && behavior.isVariable()) {
                                registry.addRecipe(new ItemExplosionPowerEmiInfoRecipe(new RecipeEntry<>(recipeEntry.id(), itemExplosionPowerRecipe), minFromBehaviorId(behaviorId), maxFromBehaviorId(behaviorId), descriptionFromBehaviorId(behaviorId)));
                            } else {
                                registry.addRecipe(new ItemExplosionPowerEmiInfoRecipe(new RecipeEntry<>(recipeEntry.id(), itemExplosionPowerRecipe), descriptionFromBehaviorId(behaviorId)));
                            }
                            behaviorSearchFailed = false;
                        }
                    }

                    // if we couldn't find a matching behavior recipe, add regular one to registry
                    if (behaviorSearchFailed) registry.addRecipe(constructor.apply(recipeEntry));
                }
            }
        }
    }

    private static Text maxFromBehaviorId(Identifier behaviorId) {
        return Text.translatable("klaxon.emi.text.explosion_power_info.behavior." + behaviorId.getPath() + ".max");
    }

    private static Text minFromBehaviorId(Identifier behaviorId) {
        return Text.translatable("klaxon.emi.text.explosion_power_info.behavior." + behaviorId.getPath() + ".min");
    }

    private static Text descriptionFromBehaviorId(Identifier behaviorId) {
        return Text.translatable("klaxon.emi.text.explosion_power_info.behavior." + behaviorId.getPath() + ".description");
    }

}
