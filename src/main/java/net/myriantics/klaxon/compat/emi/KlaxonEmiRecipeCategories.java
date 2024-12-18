package net.myriantics.klaxon.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeSorting;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.block.Blocks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.item.KlaxonItems;

// also yoinked from spectrum
public class KlaxonEmiRecipeCategories {
    public static final EmiRecipeCategory BLAST_PROCESSING = new KlaxonCategory(KlaxonCommon.locate("blast_processing"), EmiStack.of(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR));
    public static final EmiRecipeCategory HAMMERING = new KlaxonCategory(KlaxonCommon.locate("hammering"), EmiStack.of(KlaxonItems.STEEL_HAMMER));
    public static final EmiRecipeCategory ITEM_EXPLOSION_POWER = new KlaxonCategory(KlaxonCommon.locate("item_explosion_power"), EmiStack.of(Blocks.TNT));


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
