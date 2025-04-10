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
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.datagen.recipe.providers.*;
import net.myriantics.klaxon.recipe.hammering.HammeringRecipe;

import java.util.*;
import java.util.concurrent.CompletableFuture;

// structure for this kinda yoinked from energized power
public class KlaxonRecipeProvider extends FabricRecipeProvider {
    private final HashMap<RecipeType<?>, ArrayList<Identifier>> spentRecipeIdentifiersByRecipeType;

    public KlaxonRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);

        HashMap<RecipeType<?>, ArrayList<Identifier>> interimMap = new java.util.HashMap<>(Map.of());

        for (RecipeType<?> type : Registries.RECIPE_TYPE) {
            interimMap.put(type, new ArrayList<>());
        }

        this.spentRecipeIdentifiersByRecipeType = interimMap;
    }

    @Override
    public void generate(RecipeExporter exporter) {
        new KlaxonHammeringRecipeProvider(this).generateRecipes(exporter);
        new KlaxonBlastProcessingRecipeProvider(this).generateRecipes(exporter);
        new KlaxonCraftingRecipeProvider(this).generateRecipes(exporter);
        new KlaxonMakeshiftCraftingRecipeProvider(this).generateRecipes(exporter);
        new KlaxonItemExplosionPowerRecipeProvider(this).generateRecipes(exporter);
        new KlaxonSmeltingRecipeProvider(this).generateRecipes(exporter);
        new KlaxonOreProcessingRecipeProvider(this).generateRecipes(exporter);
        new KlaxonRecipeOverrideProvider(this).generateRecipes(exporter);
        new KlaxonBlastProcessorBehaviorRecipeProvider(this).generateRecipes(exporter);
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
        // get all the spent identifiers for the recipe type
        if (spentRecipeIdentifiersByRecipeType.containsKey(recipe.getType())) {
            // iterate through them all to check if theyre the same as the active recipe's id
            for (Identifier potentiallySpentIdentifier : spentRecipeIdentifiersByRecipeType.get(recipe.getType())) {
                // if there is a match, attach a discriminator to the end of the recipe id
                if (potentiallySpentIdentifier.equals(recipeId)) {
                    int discriminator = 1;

                    for (Identifier discriminatorDetectorIdentifier : spentRecipeIdentifiersByRecipeType.get(recipe.getType())) {
                        String path = recipeId.getPath();

                        // this might cause a problem if some item has a name like "skibid_i"
                        // but i dont care lol
                        // it'd just become leetspeak anyways
                        // like skibid_1 yk?
                        // thatd be epic
                        if (path.charAt(path.length() - 2) == '_') {
                            // give the raw path without any numbers
                            path = path.substring(0, path.length() - 2);
                        }

                        Identifier proposedRecipeId = recipeId.withPath(path + "_" + discriminator);

                        // if a valid open recipe slot is found, set recipeId to that
                        if (!discriminatorDetectorIdentifier.equals(proposedRecipeId)) {
                            recipeId = proposedRecipeId;
                            KlaxonCommon.LOGGER.info("Accommodated for duplicate recipe ID " + recipeId);
                        }

                        // if the recipe was a dupe, increase the discriminator number
                        discriminator++;
                    }
                }
            }
        }

        // add the new recipe id to the map for that recipe type
        spentRecipeIdentifiersByRecipeType.get(recipe.getType()).add(recipeId);

        // if the recipe has resource conditions, apply them
        if (conditions.length > 0) {
            withConditions(exporter, conditions).accept(recipeId, recipe, null);
        } else {
            exporter.accept(recipeId, recipe, null);
        }
    }

    public void acceptOverrideRecipe(RecipeExporter exporter, Identifier id) {
        // accept a blank recipe with the "never loads" resource condition
        withConditions(exporter, new NotResourceCondition(new TrueResourceCondition()))
                .accept(id, new HammeringRecipe(Ingredient.ofItems(Items.DIRT), ItemStack.EMPTY), null);
    }

    // gotcha stinker
    @Override
    protected Identifier getRecipeIdentifier(Identifier identifier) {
        return identifier;
    }
}
