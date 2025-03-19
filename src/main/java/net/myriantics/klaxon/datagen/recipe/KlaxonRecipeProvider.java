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
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeJsonProvider;
import net.myriantics.klaxon.recipe.hammering.HammeringRecipe;
import net.myriantics.klaxon.recipe.hammering.HammeringRecipeJsonProvider;
import net.myriantics.klaxon.recipe.hammering.HammeringRecipeSerializer;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerRecipe;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerRecipeJsonProvider;
import net.myriantics.klaxon.recipe.makeshift_crafting.MakeshiftRecipeJsonProvider;
import net.myriantics.klaxon.recipe.makeshift_crafting.shaped.MakeshiftShapedCraftingRecipe;
import net.myriantics.klaxon.recipe.makeshift_crafting.shapeless.MakeshiftShapelessCraftingRecipe;

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
                String requiredModId = condition.toJson().get("values").getAsJsonArray().get(0).toString();

                // remove the freaking quotes put around it to prevent crash
                requiredModId = requiredModId.substring(1, requiredModId.length() - 1);
                return KlaxonCommon.locate(typeId + "/" + requiredModId + "/" + path);
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

        try {
        // if the recipe has resource conditions, apply them
        if (conditions.length > 0) {
            withConditions(consumer, conditions).accept(getJsonProvider(recipeId, recipe));
        } else {
            consumer.accept(getJsonProvider(recipeId, recipe));
        }
        } catch (IllegalStateException e) {
            // i said i dont WANT any stinking recipe advancements
            KlaxonCommon.LOGGER.info("I DEFY YOU MOJANG");
        }

    }

    public void acceptOverrideRecipe(Consumer<RecipeJsonProvider> consumer, Identifier id) {
        // accept a blank recipe with the "never loads" resource condition
        withConditions(consumer, DefaultResourceConditions.not(DefaultResourceConditions.allModsLoaded(KlaxonCommon.MOD_ID)))
                .accept(getJsonProvider(id, new HammeringRecipe(id, Ingredient.ofItems(Items.DIRT), ItemStack.EMPTY)));
    }

    // WACK BS OMG
    // but it works :)
    private RecipeJsonProvider getJsonProvider(Identifier recipeId, Recipe<?> recipe) {


        if (recipe instanceof ShapedRecipe shapedRecipe) {
            CraftingBS craftingBS = getShapedCraftingPatternFromIngredients(recipe.getIngredients(), shapedRecipe.getWidth(), shapedRecipe.getHeight());

            ShapedRecipeJsonBuilder builder = ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, shapedRecipe.getOutput(null).getItem());

            for (String string : craftingBS.pattern) {
                builder = builder.pattern(string);
            }

            for (Ingredient ingredient : craftingBS.keyMap.keySet()) {
                builder = builder.input(craftingBS.keyMap.get(ingredient).toString().charAt(0), ingredient);
            }

            Item outputItem = recipe.getOutput(null).getItem();

            // oh FINE i'll add recipe advancements
            builder = builder.criterion(Registries.ITEM.getId(outputItem).getPath(), FabricRecipeProvider.conditionsFromItem(outputItem));

            // dont even ask bro
            AtomicReference<RecipeJsonProvider> provider = new AtomicReference<>();
            builder.offerTo((provider::set), recipeId);

            // check for makeshift crafting
            if (shapedRecipe instanceof MakeshiftShapedCraftingRecipe makeshiftShapedCraftingRecipe) {
                return new MakeshiftRecipeJsonProvider(provider.get(), makeshiftShapedCraftingRecipe.getSerializer(), makeshiftShapedCraftingRecipe);
            }
            return provider.get();
        }
        if (recipe instanceof ShapelessRecipe shapelessRecipe) {
            ItemStack outputStack = shapelessRecipe.getOutput(null);

            ShapelessRecipeJsonBuilder builder = ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, outputStack.getItem(), outputStack.getCount());

            for (Ingredient ingredient : shapelessRecipe.getIngredients()) {
                builder.input(ingredient);
            }

            Item outputItem = recipe.getOutput(null).getItem();


            // oh FINE i'll add recipe advancements
            builder = builder.criterion(Registries.ITEM.getId(outputItem).getPath(), FabricRecipeProvider.conditionsFromItem(outputItem));

            // dont even ask bro
            AtomicReference<RecipeJsonProvider> provider = new AtomicReference<>();
            builder.offerTo((provider::set), recipeId);

            // check for makeshift crafting
            if (shapelessRecipe instanceof MakeshiftShapelessCraftingRecipe makeshiftShapelessCraftingRecipe) {
                return new MakeshiftRecipeJsonProvider(provider.get(), makeshiftShapelessCraftingRecipe.getSerializer(), makeshiftShapelessCraftingRecipe);
            }
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

            Item outputItem = recipe.getOutput(null).getItem();

            // oh FINE i'll add recipe advancements
            builder = builder.criterion(Registries.ITEM.getId(outputItem).getPath(), FabricRecipeProvider.conditionsFromItem(outputItem));

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

            Item outputItem = recipe.getOutput(null).getItem();

            // oh FINE i'll add recipe advancements
            builder = builder.criterion(Registries.ITEM.getId(outputItem).getPath(), FabricRecipeProvider.conditionsFromItem(outputItem));

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

            Item outputItem = recipe.getOutput(null).getItem();

            // oh FINE i'll add recipe advancements
            builder = builder.criterion(Registries.ITEM.getId(outputItem).getPath(), FabricRecipeProvider.conditionsFromItem(outputItem));

            // dont even ask bro
            AtomicReference<RecipeJsonProvider> provider = new AtomicReference<>();
            builder.offerTo((provider::set), recipeId);
            return provider.get();
        }
        if (recipe instanceof BlastProcessingRecipe blastProcessingRecipe) {
            return new BlastProcessingRecipeJsonProvider(blastProcessingRecipe);
        }
        if (recipe instanceof HammeringRecipe hammeringRecipe) {
            return new HammeringRecipeJsonProvider(hammeringRecipe);
        }
        if (recipe instanceof ItemExplosionPowerRecipe itemExplosionPowerRecipe) {
            return new ItemExplosionPowerRecipeJsonProvider(itemExplosionPowerRecipe);
        }

        KlaxonCommon.LOGGER.info("oopsy daisy no json provider for crafting recipe :(");
        return null;
    }

    // hacky bs that lets me keep my 1.21 datagen code
    private CraftingBS getShapedCraftingPatternFromIngredients(DefaultedList<Ingredient> ingredients, int width, int height) {
        Map<Ingredient, Integer> ingredientCharacterMap = new HashMap<>(Map.of());

        int incrementor = 0;

        StringBuilder patternStringBuilder = new StringBuilder();

        for (Ingredient ingredient : ingredients) {
            // serializers dont like empty ingredients in the key
            if (ingredient.equals(Ingredient.EMPTY))  {
                patternStringBuilder.append(" ");
                continue;
            }

            Integer workingIncrementor = ingredientCharacterMap.get(ingredient);
            if (workingIncrementor == null) {
                ingredientCharacterMap.put(ingredient, incrementor);
                workingIncrementor = incrementor;
                incrementor++;
            }
            patternStringBuilder.append(workingIncrementor);
        }

        String patternString = patternStringBuilder.toString();
        // 012 345 678
        // 012
        // 345
        // 678

        // more fucky bullshit
        ArrayList<String> patternStrings = new ArrayList<>();
        for (int i = 0; i <= patternString.length(); i++) {
            if (i != 0 && i % width == 0) {
                patternStrings.add(patternString.substring(i - width, i));
            }
        }

        return new CraftingBS(patternStrings.toArray(new String[] {}), ingredientCharacterMap);
    }

    // gotcha stinker
    @Override
    protected Identifier getRecipeIdentifier(Identifier identifier) {
        return identifier;
    }

    private record CraftingBS(String[] pattern, Map<Ingredient, Integer> keyMap) {}
}
