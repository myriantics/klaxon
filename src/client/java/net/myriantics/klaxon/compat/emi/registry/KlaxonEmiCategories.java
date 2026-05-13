package net.myriantics.klaxon.compat.emi.registry;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeSorting;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeTypes;

import java.util.ArrayList;

public abstract class KlaxonEmiCategories {
    private static final ArrayList<EmiRecipeCategory> CATEGORIES = new ArrayList<>();

    public static final EmiRecipeCategory BLAST_PROCESSING = register(
            KlaxonRecipeTypes.BLAST_PROCESSING,
            KlaxonItems.STEEL_BLAST_PROCESSOR
    );
    public static final EmiRecipeCategory EXPLOSIVE_CATALYST_DEFINITION = register(
            "explosive_catalyst_definition",
            KlaxonItems.STEEL_BLAST_PROCESSOR
    );
    public static final EmiRecipeCategory NETHER_REACTION = register(
            KlaxonRecipeTypes.NETHER_REACTION,
            KlaxonItems.NETHER_REACTOR_CORE
    );
    public static final EmiRecipeCategory WORLD_ITEM_APPLICATION = register(
            KlaxonRecipeTypes.WORLD_ITEM_APPLICATION,
            KlaxonItems.PRECISION_DISPENSER
    );

    public static void init(EmiRegistry registry) {
        CATEGORIES.forEach(registry::addCategory);
        KlaxonCommon.LOGGER.info("Registered KLAXON's EMI Categories!");
    }

    private static EmiRecipeCategory register(String name, Holder<Item> iconHolder) {
        return register(name, iconHolder.value());
    }

    private static <T extends Recipe<?>> EmiRecipeCategory register(Holder<RecipeType<T>> type, Holder<Item> iconHolder) {
        return register(type, iconHolder.value());
    }

    private static EmiRecipeCategory register(String name, ItemLike icon) {
        return register(name, EmiStack.of(icon));
    }

    private static <T extends Recipe<?>> EmiRecipeCategory register(Holder<RecipeType<T>> type, ItemLike icon) {
        return register(type, EmiStack.of(icon));
    }

    private static <T extends Recipe<?>> EmiRecipeCategory register(Holder<RecipeType<T>> type, EmiRenderable icon) {
        return register(
                type.unwrapKey().get().location().getPath(),
                icon
        );
    }

    private static EmiRecipeCategory register(String name, EmiRenderable icon) {
        EmiRecipeCategory category = of(name, icon);
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
