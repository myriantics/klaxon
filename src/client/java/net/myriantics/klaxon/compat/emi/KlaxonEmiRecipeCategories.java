package net.myriantics.klaxon.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeSorting;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;

import java.util.ArrayList;

// also yoinked from spectrum
public abstract class KlaxonEmiRecipeCategories {
    public static final ArrayList<EmiRecipeCategory> CATEGORIES = new ArrayList<>();

    public static final EmiRecipeCategory BLAST_PROCESSING = register(
            KlaxonRecipeTypes.BLAST_PROCESSING_RECIPE_ID,
            EmiStack.of(KlaxonItems.DEEPSLATE_BLAST_PROCESSOR)
    );
    public static final EmiRecipeCategory EXPLOSIVE_CATALYST_DEFINITION = register(
            KlaxonRecipeTypes.EXPLOSIVE_CATALYST_DEFINITION_ID,
            EmiStack.of(Items.TNT)
    );
    public static final EmiRecipeCategory NETHER_REACTION = register(
            KlaxonRecipeTypes.NETHER_REACTION_RECIPE_ID,
            EmiStack.of(KlaxonItems.NETHER_REACTOR_CORE)
    );
    public static final EmiRecipeCategory WORLD_ITEM_APPLICATION = register(
            KlaxonRecipeTypes.WORLD_ITEM_APPLICATION_RECIPE_ID,
            EmiStack.of(Items.DISPENSER)
    );

    private static KlaxonCategory register(String name, EmiRenderable icon) {
        KlaxonCategory category = new KlaxonCategory(KlaxonCommon.locate(name), icon);
        CATEGORIES.add(category);
        return category;
    }

    public static EmiRecipeCategory of(String name, EmiRenderable icon) {
        return of(KlaxonCommon.locate(name), icon);
    }

    public static EmiRecipeCategory of(ResourceLocation id, EmiRenderable icon) {
        return new KlaxonCategory(id, icon);
    }

    private static class KlaxonCategory extends EmiRecipeCategory {
        private final String key;

        public KlaxonCategory(ResourceLocation id, EmiRenderable icon) {
            this(id, icon, "container." + id.getNamespace() + "." + id.getPath() +".title");
        }

        public KlaxonCategory(ResourceLocation id, EmiRenderable icon, String key) {
            super(id, icon, icon, EmiRecipeSorting.compareOutputThenInput());
            this.key = key;
        }

        @Override
        public Component getName() {
            return Component.translatable(key);
        }
    }
}
