package net.myriantics.klaxon.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeSorting;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeType;
import net.myriantics.klaxon.registry.KlaxonDynamicRegistries;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.ArrayList;

// also yoinked from spectrum
public abstract class KlaxonEmiRecipeCategories {
    public static final ArrayList<EmiRecipeCategory> CATEGORIES = new ArrayList<>();

    public static final EmiRecipeCategory BLAST_PROCESSING = register(
            KlaxonRecipeTypes.BLAST_PROCESSING_RECIPE_ID,
            EmiStack.of(KlaxonItems.DEEPSLATE_BLAST_PROCESSOR)
    );
    public static final EmiRecipeCategory ITEM_EXPLOSION_POWER = register(
            KlaxonRecipeTypes.ITEM_EXPLOSION_POWER_RECIPE_ID,
            EmiStack.of(Items.TNT)
    );
    public static final EmiRecipeCategory ITEM_COOLING = register(
            KlaxonRecipeTypes.COOLING_RECIPE_ID,
            EmiIngredient.of(Ingredient.ofItems(Items.WATER_BUCKET))
    );
    public static final EmiRecipeCategory NETHER_REACTION = register(
            KlaxonRecipeTypes.NETHER_REACTION_RECIPE_ID,
            EmiStack.of(KlaxonItems.NETHER_REACTOR_CORE)
    );

    private static KlaxonCategory register(String name, EmiRenderable icon) {
        KlaxonCategory category = new KlaxonCategory(KlaxonCommon.locate(name), icon);
        CATEGORIES.add(category);
        return category;
    }

    public static EmiRecipeCategory of(String name, EmiRenderable icon) {
        return of(KlaxonCommon.locate(name), icon);
    }

    public static EmiRecipeCategory of(Identifier id, EmiRenderable icon) {
        return new KlaxonCategory(id, icon);
    }

    private static class KlaxonCategory extends EmiRecipeCategory {
        private final String key;

        public KlaxonCategory(Identifier id, EmiRenderable icon) {
            this(id, icon, "container." + id.getNamespace() + "." + id.getPath() +".title");
        }

        public KlaxonCategory(Identifier id, EmiRenderable icon, String key) {
            super(id, icon, icon, EmiRecipeSorting.compareOutputThenInput());
            this.key = key;
        }

        @Override
        public Text getName() {
            return Text.translatable(key);
        }
    }
}
