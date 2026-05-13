package net.myriantics.klaxon.datagen.recipe;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.AllModsLoadedResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.NotResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.TrueResourceCondition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.datagen.recipe.providers.*;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipe;
import net.myriantics.klaxon.registry.dynamic.KlaxonToolUsageRecipeTypes;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

// structure for this kinda yoinked from energized power
public class KlaxonRecipeProvider extends FabricRecipeProvider {
    private final HashMap<ResourceLocation, Integer> recipeIdOccurrencesMap = new HashMap<>();

    public KlaxonRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        new KlaxonToolUsageRecipeProvider(this, exporter).generateRecipes();
        new KlaxonBlastProcessingRecipeProvider(this, exporter).generateRecipes();
        new KlaxonCraftingRecipeProvider(this, exporter).generateRecipes();
        new KlaxonMakeshiftCraftingRecipeProvider(this, exporter).generateRecipes();
        new KlaxonSmeltingRecipeProvider(this, exporter).generateRecipes();
        new KlaxonOreProcessingRecipeProvider(this, exporter).generateRecipes();
        new KlaxonRecipeOverrideProvider(this, exporter).generateRecipes();
        new KlaxonNetherReactionRecipeProvider(this, exporter).generateRecipes();
        new KlaxonWorldItemApplicationRecipeProvider(this, exporter).generateRecipes();
    }

    public <T extends Recipe<?>> ResourceLocation computeRecipeIdentifier(Holder<RecipeType<T>> typeHolder, String path, final ResourceCondition... conditions) {
        return this.computeRecipeIdentifier(typeHolder.unwrapKey().get().location().getPath(), path, conditions);
    }

    public ResourceLocation computeRecipeIdentifier(String typeId, String path, final ResourceCondition... conditions) {
        for (ResourceCondition condition : conditions) {
            if (condition instanceof AllModsLoadedResourceCondition allModsLoadedResourceCondition) {
                return KlaxonCommon.locate(typeId + "/" + allModsLoadedResourceCondition.modIds().getFirst() + "/" + path);
            }
        }

        return KlaxonCommon.locate(typeId + "/" + path);
    }

    public void acceptRecipeWithConditions(RecipeOutput exporter, ResourceLocation recipeId, Recipe<?> recipe, final ResourceCondition... conditions) {

        ResourceLocation proposedId = null;

        // iterate through them all to check if theyre the same as the active recipe's id
        for (ResourceLocation potentiallySpentIdentifier : recipeIdOccurrencesMap.keySet()) {
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

    public void acceptOverrideRecipe(RecipeOutput exporter, ResourceLocation id) {
        // accept a REALLY FUNNY recipe with the "never loads" resource condition
        withConditions(exporter, new NotResourceCondition(new TrueResourceCondition()))
                .accept(id, new ToolUsageRecipe(KlaxonToolUsageRecipeTypes.HAMMERING, Ingredient.of(Items.END_ROD), new ItemStack(Items.SHEEP_SPAWN_EGG), SoundEvents.PISTON_EXTEND), null);
    }

    // gotcha stinker
    @Override
    protected ResourceLocation getRecipeIdentifier(ResourceLocation identifier) {
        return identifier;
    }
}
