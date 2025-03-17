package net.myriantics.klaxon.datagen.recipe;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.DefaultResourceConditions;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.datagen.recipe.providers.*;
import net.myriantics.klaxon.recipe.hammering.HammeringRecipe;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

// structure for this kinda yoinked from energized power
public class KlaxonRecipeProvider extends FabricRecipeProvider {
    private final HashMap<RecipeType<?>, ArrayList<Identifier>> spentRecipeIdentifiersByRecipeType;

    public KlaxonRecipeProvider(FabricDataOutput output) {
        super(output);

        HashMap<RecipeType<?>, ArrayList<Identifier>> interimMap = new java.util.HashMap<>(Map.of());

        for (RecipeType<?> type : Registries.RECIPE_TYPE) {
            interimMap.put(type, new ArrayList<>());
        }

        this.spentRecipeIdentifiersByRecipeType = interimMap;
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> consumer) {
        new KlaxonHammeringRecipeProvider(this).generateRecipes(consumer);
        new KlaxonBlastProcessingRecipeProvider(this).generateRecipes(consumer);
        new KlaxonCraftingRecipeProvider(this).generateRecipes(consumer);
        new KlaxonMakeshiftCraftingRecipeProvider(this).generateRecipes(consumer);
        new KlaxonItemExplosionPowerRecipeProvider(this).generateRecipes(consumer);
        new KlaxonSmeltingRecipeProvider(this).generateRecipes(consumer);
        new KlaxonOreProcessingRecipeProvider(this).generateRecipes(consumer);
        new KlaxonRecipeOverrideProvider(this).generateRecipes(consumer);
    }

    public Identifier computeRecipeIdentifier(String typeId, String path, final ConditionJsonProvider... conditions) {
        for (ConditionJsonProvider condition : conditions) {
            if (condition.getConditionId().equals(Identifier.tryParse("fabric:all_mods_loaded"))) {
                return KlaxonCommon.locate(typeId + "/" + condition.toJson().get("values").getAsJsonArray().get(0) + "/" + path);
            }
        }

        return KlaxonCommon.locate(typeId + "/" + path);
    }

    public void acceptRecipeWithConditions(Consumer<RecipeJsonProvider> consumer, Identifier recipeId, Recipe<?> recipe, final ConditionJsonProvider... conditions) {
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
            withConditions(consumer, conditions).accept(getJsonProvider(recipeId, recipe));
        } else {
            consumer.accept(getJsonProvider(recipeId, recipe));
        }
    }

    public void acceptOverrideRecipe(Consumer<RecipeJsonProvider> consumer, Identifier id) {
        // accept a blank recipe with the "never loads" resource condition
        withConditions(consumer, DefaultResourceConditions.not(DefaultResourceConditions.allModsLoaded(KlaxonCommon.MOD_ID)))
                .accept(getJsonProvider(id, new HammeringRecipe(Ingredient.ofItems(Items.DIRT), ItemStack.EMPTY)));
    }

    // WACK BS OMG
    // but it works :)
    private RecipeJsonProvider getJsonProvider(Identifier recipeId, Recipe<?> recipe) {
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            CraftingBS craftingBS = getShapedCraftingPatternFromIngredients(recipe.getIngredients());

            ShapedRecipeJsonBuilder builder = ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, shapedRecipe.getOutput(null).getItem());

            builder = builder
                    .pattern(craftingBS.pattern[0])
                    .pattern(craftingBS.pattern[1])
                    .pattern(craftingBS.pattern[2]);

            for (Ingredient ingredient : craftingBS.keyMap.keySet()) {
                builder = builder.input(craftingBS.keyMap.get(ingredient), ingredient);
            }

            // dont even ask bro
            AtomicReference<RecipeJsonProvider> provider = new AtomicReference<>();
            builder.offerTo((provider::set), recipeId);
            return provider.get();
        }
        if (recipe instanceof ShapelessRecipe shapelessRecipe) {
            ShapelessRecipeJsonBuilder builder = ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, shapelessRecipe.getOutput(null).getItem());

            for (Ingredient ingredient : shapelessRecipe.getIngredients()) {
                builder.input(ingredient);
            }

            // dont even ask bro
            AtomicReference<RecipeJsonProvider> provider = new AtomicReference<>();
            builder.offerTo((provider::set), recipeId);
            return provider.get();
        }
        if (recipe instanceof SmeltingRecipe smeltingRecipe) {
            CookingRecipeJsonBuilder builder = CookingRecipeJsonBuilder.createSmelting(
                    smeltingRecipe.getIngredients().getFirst(),
                    RecipeCategory.MISC,
                    smeltingRecipe.getOutput(null).getItem(),
                    smeltingRecipe.getExperience(),
                    smeltingRecipe.getCookTime()
            );

            // dont even ask bro
            AtomicReference<RecipeJsonProvider> provider = new AtomicReference<>();
            builder.offerTo((provider::set), recipeId);
            return provider.get();
        }
        if (recipe instanceof SmokingRecipe smokingRecipe) {
            CookingRecipeJsonBuilder builder = CookingRecipeJsonBuilder.createSmoking(
                    smokingRecipe.getIngredients().getFirst(),
                    RecipeCategory.MISC,
                    smokingRecipe.getOutput(null).getItem(),
                    smokingRecipe.getExperience(),
                    smokingRecipe.getCookTime()
            );

            // dont even ask bro
            AtomicReference<RecipeJsonProvider> provider = new AtomicReference<>();
            builder.offerTo((provider::set), recipeId);
            return provider.get();
        }
        if (recipe instanceof BlastingRecipe blastingRecipe) {
            CookingRecipeJsonBuilder builder = CookingRecipeJsonBuilder.createBlasting(
                    blastingRecipe.getIngredients().getFirst(),
                    RecipeCategory.MISC,
                    blastingRecipe.getOutput(null).getItem(),
                    blastingRecipe.getExperience(),
                    blastingRecipe.getCookTime()
            );

            // dont even ask bro
            AtomicReference<RecipeJsonProvider> provider = new AtomicReference<>();
            builder.offerTo((provider::set), recipeId);
            return provider.get();
        }

        KlaxonCommon.LOGGER.info("oopsy daisy no json provider for crafting recipe :(");
        return null;
    }

    // hacky bs that lets me keep my 1.21 datagen code
    private CraftingBS getShapedCraftingPatternFromIngredients(DefaultedList<Ingredient> ingredients) {
        Map<Ingredient, Character> ingredientCharacterMap = new HashMap<>(Map.of());

        int incrementor = 0;

        StringBuilder patternStringBuilder = new StringBuilder();

        for (Ingredient ingredient : ingredients) {
            Character workingChar = ingredientCharacterMap.get(ingredient);
            if (workingChar == null) {
                char newChar = (char) incrementor++;
                ingredientCharacterMap.put(ingredient, newChar);
                workingChar = newChar;
            }
            patternStringBuilder.append(workingChar);
        }

        String patternString = patternStringBuilder.toString();

        String[] pattern = {patternString.substring(0, 2), patternString.substring(3, 5), patternString.substring(6, 9)};

        return new CraftingBS(pattern, ingredientCharacterMap);
    }

    // gotcha stinker
    @Override
    protected Identifier getRecipeIdentifier(Identifier identifier) {
        return identifier;
    }

    private record CraftingBS(String[] pattern, Map<Ingredient, Character> keyMap) {}
}
