package net.myriantics.klaxon.compat.emi.registry;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

public final class KlaxonEmiWorkstations {
    private final EmiRegistry emiRegistry; // named this way instead of just registry so tab autocomplete defaults to reg methods over this

    private KlaxonEmiWorkstations(EmiRegistry emiRegistry) {
        this.emiRegistry = emiRegistry;
    }

    public static void init(EmiRegistry registry) {
        new KlaxonEmiWorkstations(registry).initInternal();
    }

    private void initInternal() {
        // klaxon workstations
        register(KlaxonEmiCategories.BLAST_PROCESSING, KlaxonItems.DEEPSLATE_BLAST_PROCESSOR);
        register(KlaxonEmiCategories.EXPLOSIVE_CATALYST_DEFINITION, KlaxonItems.DEEPSLATE_BLAST_PROCESSOR);
        register(KlaxonEmiCategories.WORLD_ITEM_APPLICATION, Items.DISPENSER);
        register(KlaxonEmiCategories.WORLD_ITEM_APPLICATION, KlaxonItems.PRECISION_DISPENSER);
        register(KlaxonEmiCategories.NETHER_REACTION, EmiIngredient.of(KlaxonBlockTags.NETHER_REACTOR_CORES));

        // vanilla workstations

        // Steel Hammer can mimic AnvilScreenHandler functionality
        register(VanillaEmiRecipeCategories.ANVIL_REPAIRING, KlaxonItems.STEEL_HAMMER);
        // Steel Workbench is crafting table but steel
        register(VanillaEmiRecipeCategories.CRAFTING, KlaxonItems.STEEL_WORKBENCH);
        // Blast Processors can mimic Blasting Smelting functionality when using a catalyst that produces Fire
        register(VanillaEmiRecipeCategories.BLASTING, KlaxonItems.DEEPSLATE_BLAST_PROCESSOR);

        KlaxonCommon.LOGGER.info("Registered KLAXON's EMI Workstations!");
    }

    private void register(EmiRecipeCategory category, Holder<Item> itemHolder) {
        this.register(category, itemHolder.value());
    }

    private void register(EmiRecipeCategory category, ItemLike itemLike) {
        this.register(category, EmiStack.of(itemLike));
    }

    private void register(EmiRecipeCategory category, EmiIngredient ingredient) {
        this.emiRegistry.addWorkstation(category, ingredient);
    }
}
