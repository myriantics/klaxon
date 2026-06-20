package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.myriantics.klaxon.datagen.NamedIngredient;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.recipe.RecipeOutputCompound;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.List;

public class KlaxonBlastProcessingRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonBlastProcessingRecipeProvider(KlaxonRecipeProvider provider, RecipeOutput exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildBlastProcessingRecipes();
        buildExplosiveDisassemblyRecipes();
    }

    private void buildBlastProcessingRecipes() {
        // brick cracking
        addBlastProcessingRecipe(NamedIngredient.ofItems(Items.DEEPSLATE_BRICKS), 0.4f, 0.9f, new ItemStack(Items.CRACKED_DEEPSLATE_BRICKS));
        addBlastProcessingRecipe(NamedIngredient.ofItems(Items.DEEPSLATE_TILES), 0.4f, 0.9f, new ItemStack(Items.CRACKED_DEEPSLATE_TILES));
        addBlastProcessingRecipe(NamedIngredient.ofItems(Items.NETHER_BRICKS), 0.3f, 0.8f, new ItemStack(Items.CRACKED_NETHER_BRICKS));
        addBlastProcessingRecipe(NamedIngredient.ofItems(Items.POLISHED_BLACKSTONE_BRICKS), 0.3f, 0.8f, new ItemStack(Items.CRACKED_POLISHED_BLACKSTONE_BRICKS));
        addBlastProcessingRecipe(NamedIngredient.ofItems(Items.STONE_BRICKS), 0.3f, 0.8f, new ItemStack(Items.CRACKED_STONE_BRICKS));
        addBlastProcessingRecipe(NamedIngredient.ofItems(Items.INFESTED_STONE_BRICKS), 0.1f, 0.3f, new ItemStack(Items.INFESTED_CRACKED_STONE_BRICKS));

        // pot cracking
        addDecoratedPotCrackingBlastProcessingRecipe(NamedIngredient.ofItems(Items.DECORATED_POT), 0.1f, 0.4f);

        // pot shattering
        addDecoratedPotShatteringBlastProcessingRecipe(NamedIngredient.ofItems(Items.DECORATED_POT), 0.4f, 1.2f);

        // misc
        addBlastProcessingRecipe(NamedIngredient.ofItems(Items.COAL), 0.3f, 1.4f, RecipeOutputCompound.of(KlaxonItems.FRACTURED_COAL, 1.0, KlaxonItems.FRACTURED_COAL, 0.5));
        addBlastProcessingRecipe(NamedIngredient.ofItems(Items.CHARCOAL), 0.2f, 1.2f, RecipeOutputCompound.of(KlaxonItems.FRACTURED_CHARCOAL, 1.0, KlaxonItems.FRACTURED_CHARCOAL, 0.4));
        addBlastProcessingRecipe(NamedIngredient.fromTag(KlaxonItemTags.HIGH_YIELD_RUBBER_EXTRACTABLE_LOGS), 0.8f, 2.0f, builder -> builder.guaranteed(new ItemStack(KlaxonItems.RUBBER_GLOB, 3)).chance(new ItemStack(KlaxonItems.RUBBER_GLOB, 3), 0.5));
        addBlastProcessingRecipe(NamedIngredient.fromTag(KlaxonItemTags.LOW_YIELD_RUBBER_EXTRACTABLE_LOGS), 0.8f, 2.0f, builder -> builder.guaranteed(new ItemStack(KlaxonItems.RUBBER_GLOB)).chance(new ItemStack(KlaxonItems.RUBBER_GLOB, 2), 0.4));
    }

    private void buildExplosiveDisassemblyRecipes() {
        // casings
        addExplosiveDisassemblyRecipe(
                NamedIngredient.ofItems(KlaxonItems.STEEL_CASING.value()),
                1.5f,
                4.0f,
                builder -> builder
                        .chance(KlaxonItems.STEEL_INGOT, 0.8)
                        .chance(KlaxonItems.STEEL_PLATE, 0.8)
                        .chance(KlaxonItems.STEEL_NUGGET, 4, 0.4)
        );
        addExplosiveDisassemblyRecipe(
                NamedIngredient.ofItems(KlaxonItems.CRUDE_STEEL_CASING.value()),
                0.8f,
                2.4f,
                builder -> builder
                        .chance(KlaxonItems.CRUDE_STEEL_INGOT, 0.6)
                        .chance(KlaxonItems.CRUDE_STEEL_PLATE, 0.6)
                        .chance(KlaxonItems.CRUDE_STEEL_NUGGET, 4, 0.3)
        );

        // nether reactor cores
        addExplosiveDisassemblyRecipe(
                NamedIngredient.ofItems(KlaxonItems.NETHER_REACTOR_CORE.value()),
                1.5f,
                4.0f,
                builder -> builder
                        .chance(KlaxonItems.STEEL_INGOT, 0.8)
                        .chance(KlaxonItems.STEEL_PLATE, 0.8)
                        .chance(KlaxonItems.HALLNOX_POD, 0.5)
                        .chance(KlaxonItems.STEEL_NUGGET, 4, 0.4)
        );
        addExplosiveDisassemblyRecipe(
                NamedIngredient.ofItems(KlaxonItems.CRUDE_NETHER_REACTOR_CORE.value()),
                0.8f,
                2.4f,
                builder -> builder
                        .chance(KlaxonItems.CRUDE_STEEL_INGOT, 0.6)
                        .chance(KlaxonItems.CRUDE_STEEL_PLATE, 0.6)
                        .chance(KlaxonItems.HALLNOX_POD, 0.4)
                        .chance(KlaxonItems.CRUDE_STEEL_NUGGET, 4, 0.3)
        );
        // machines
        addExplosiveDisassemblyRecipe(
                NamedIngredient.ofItems(KlaxonItems.PRECISION_DISPENSER.value()),
                1.5f,
                2.4f,
                builder -> builder
                        .chance(KlaxonItems.STEEL_PLATE, 2, 0.75)
                        .chance(KlaxonItems.STEEL_INGOT, 2, 0.75)
                        .chance(KlaxonItems.STEEL_NUGGET, 5, 0.35)
                        .chance(Items.GOLD_NUGGET, 3, 0.2)
        );


        // minecart disassembly
        addExplosiveDisassemblyRecipe(
                NamedIngredient.ofItems(Items.MINECART),
                1.5f,
                3.0f,
                this::baseMinecartDisassemblyDrops
        );
        addExplosiveDisassemblyRecipe(
                NamedIngredient.ofItems(Items.CHEST_MINECART),
                1.6f,
                3.0f,
                builder -> this.baseMinecartDisassemblyDrops(builder)
                        .chance(Items.STICK, 8, 0.8)
                        .chance(Items.IRON_NUGGET, 0.2)
        );
        addExplosiveDisassemblyRecipe(
                NamedIngredient.ofItems(Items.HOPPER_MINECART),
                1.8f,
                3.0f,
                builder -> builder
                        .guaranteed(new ItemStack(Items.IRON_INGOT, 2))
                        .chance(Items.IRON_INGOT, 3, 0.4)
                        .chance(Items.STICK, 8, 0.8)
                        .chance(Items.IRON_NUGGET, 0.3)
        );
        addExplosiveDisassemblyRecipe(
                NamedIngredient.ofItems(Items.FURNACE_MINECART),
                1.7f,
                3.0f,
                builder -> this.baseMinecartDisassemblyDrops(builder)
                        .chance(Items.COBBLESTONE, 4, 0.5)
                        .chance(Items.GRAVEL, 2, 0.2)
        );

        // minecart decoupling
        addExplosiveDisassemblyRecipe(
                NamedIngredient.ofItems(Items.CHEST_MINECART),
                0.5f,
                1.6f,
                builder -> builder.guaranteed(Items.MINECART, Items.CHEST)
        );
        addExplosiveDisassemblyRecipe(
                NamedIngredient.ofItems(Items.HOPPER_MINECART),
                0.5f,
                1.8f,
                builder -> builder.guaranteed(Items.MINECART, Items.HOPPER)
        );
        addExplosiveDisassemblyRecipe(
                NamedIngredient.ofItems(Items.FURNACE_MINECART),
                0.5f,
                1.7f,
                builder -> builder.guaranteed(Items.MINECART, Items.FURNACE)
        );
    }

    protected RecipeOutputCompound.Builder baseMinecartDisassemblyDrops(RecipeOutputCompound.Builder builder) {
        return builder
                .guaranteed(Items.IRON_INGOT)
                .chance(Items.IRON_INGOT, 0.5);
    }
}
