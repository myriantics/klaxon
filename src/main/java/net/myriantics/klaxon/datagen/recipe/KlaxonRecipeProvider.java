package net.myriantics.klaxon.datagen.recipe;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.AllModsLoadedResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.NotResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.TrueResourceCondition;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.datagen.recipe.providers.*;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipe;

import java.util.*;
import java.util.concurrent.CompletableFuture;

// structure for this kinda yoinked from energized power
public class KlaxonRecipeProvider extends FabricRecipeProvider {
    private final HashMap<Identifier, Integer> recipeIdOccurrencesMap = new HashMap<>();

    public KlaxonRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        new KlaxonToolUsageRecipeProvider(this, exporter).generateRecipes();
        new KlaxonBlastProcessingRecipeProvider(this, exporter).generateRecipes();
        new KlaxonCraftingRecipeProvider(this, exporter).generateRecipes();
        new KlaxonMakeshiftCraftingRecipeProvider(this, exporter).generateRecipes();
        new KlaxonItemExplosionPowerRecipeProvider(this, exporter).generateRecipes();
        new KlaxonSmeltingRecipeProvider(this, exporter).generateRecipes();
        new KlaxonOreProcessingRecipeProvider(this, exporter).generateRecipes();
        new KlaxonRecipeOverrideProvider(this, exporter).generateRecipes();
        new KlaxonItemCoolingRecipeProvider(this, exporter).generateRecipes();
        new KlaxonNetherReactionRecipeProvider(this, exporter).generateRecipes();
        new KlaxonManualItemApplicationRecipeProvider(this, exporter).generateRecipes();
    }

    public Identifier computeRecipeIdentifier(String typeId, String path, final ResourceCondition... conditions) {
        for (ResourceCondition condition : conditions) {
            if (condition instanceof AllModsLoadedResourceCondition allModsLoadedResourceCondition) {
                return KlaxonCommon.locate(typeId + "/" + allModsLoadedResourceCondition.modIds().getFirst() + "/" + path);
            }
        }

        return KlaxonCommon.locate(typeId + "/" + path);
    }

    public void acceptRecipeWithConditions(RecipeExporter exporter, Identifier recipeId, Recipe<?> recipe, final ResourceCondition... conditions) {

        Identifier proposedId = null;

        // iterate through them all to check if theyre the same as the active recipe's id
        for (Identifier potentiallySpentIdentifier : recipeIdOccurrencesMap.keySet()) {
            // if there is a match, attach a discriminator to the end of the recipe id
            if (potentiallySpentIdentifier.equals(recipeId)) {
                proposedId = recipeId.withPath(recipeId.getPath() + "_" + recipeIdOccurrencesMap.get(potentiallySpentIdentifier));
                break;
            }
        }

        if (proposedId == null) {
            // if no duplicate recipe was found, add a new entry with the associated number of 1
            recipeIdOccurrencesMap.put(recipeId, 1);
        } else {
            // notify the dev of the recipe accomodation :)
            KlaxonCommon.LOGGER.info("Accommodated for duplicate recipe: " + recipeId);
            // if a duplicate recipe was found, increment the counter in the map
            recipeIdOccurrencesMap.put(recipeId, recipeIdOccurrencesMap.get(recipeId) + 1);
            // make sure to update the recipe id to include the discriminator
            recipeId = proposedId;
        }

        // if the recipe has resource conditions, apply them
        if (conditions.length > 0) {
            withConditions(exporter, conditions).accept(recipeId, recipe, null);
        } else {
            exporter.accept(recipeId, recipe, null);
        }
    }

    public void acceptOverrideRecipe(RecipeExporter exporter, Identifier id) {
        // accept a REALLY FUNNY recipe with the "never loads" resource condition
        withConditions(exporter, new NotResourceCondition(new TrueResourceCondition()))
                .accept(id, new ToolUsageRecipe(Ingredient.ofItems(Items.PISTON), Ingredient.ofItems(Items.END_ROD), new ItemStack(Items.SHEEP_SPAWN_EGG)), null);
    }

    // gotcha stinker
    @Override
    protected Identifier getRecipeIdentifier(Identifier identifier) {
        return identifier;
    }
}
