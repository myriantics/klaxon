package net.myriantics.klaxon.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.registry.minecraft.KlaxonItems;
import net.myriantics.klaxon.tag.compat.KlaxonCompatItemTags;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalItemTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public KlaxonItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        // build material tags
        buildMaterialIngotTags();
        buildMaterialGlobTags();
        buildMaterialStorageBlockTags();
        buildMaterialNuggetTags();
        buildMaterialPlateTags();
        buildMaterialSheetTags();
        buildFracturedMaterialTags();

        // build makeshift crafting tags
        buildMakeshiftCraftingIngredientTags();
        buildMakeshiftCraftingLogisticsTags();

        // build recipe tags
        buildCraftingTags();

        // build advancement tags
        buildAdvancementRelatedTags();

        // build mechanics tags
        buildMechanicsTags();
        buildAnvilRelatedTags();
        buildBlastProcessorCatalystBehaviorTags();

        // build categorical tags
        buildEquipmentCategoryTags();

        // build compat tags
        buildCompatTags();
    }

    private void buildMaterialIngotTags() {
        getOrCreateTagBuilder(ConventionalItemTags.INGOTS)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_INGOTS)
                .forceAddTag(KlaxonConventionalItemTags.CRUDE_STEEL_INGOTS);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.STEEL_INGOTS)
                .add(KlaxonItems.STEEL_INGOT);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.CRUDE_STEEL_INGOTS)
                .add(KlaxonItems.CRUDE_STEEL_INGOT);
    }

    private void buildMaterialGlobTags() {
        getOrCreateTagBuilder(KlaxonConventionalItemTags.GLOBS)
                .forceAddTag(KlaxonConventionalItemTags.RUBBER_GLOBS)
                .forceAddTag(KlaxonConventionalItemTags.MOLTEN_RUBBER_GLOBS);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.RUBBER_GLOBS)
                .add(KlaxonItems.RUBBER_GLOB);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.MOLTEN_RUBBER_GLOBS)
                .add(KlaxonItems.MOLTEN_RUBBER_GLOB);
    }

    private void buildMaterialStorageBlockTags() {
        getOrCreateTagBuilder(ConventionalItemTags.STORAGE_BLOCKS)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_BLOCKS)
                .forceAddTag(KlaxonConventionalItemTags.CRUDE_STEEL_BLOCKS)
                .forceAddTag(KlaxonConventionalItemTags.RUBBER_BLOCKS)
                .forceAddTag(KlaxonConventionalItemTags.MOLTEN_RUBBER_BLOCKS);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.STEEL_BLOCKS)
                .add(KlaxonBlocks.STEEL_BLOCK.asItem());
        getOrCreateTagBuilder(KlaxonConventionalItemTags.CRUDE_STEEL_BLOCKS)
                .add(KlaxonBlocks.CRUDE_STEEL_BLOCK.asItem());
        getOrCreateTagBuilder(KlaxonConventionalItemTags.RUBBER_BLOCKS)
                .add(KlaxonBlocks.RUBBER_BLOCK.asItem())
                .add(KlaxonBlocks.RUBBER_SHEET_BLOCK.asItem());
        getOrCreateTagBuilder(KlaxonConventionalItemTags.MOLTEN_RUBBER_BLOCKS)
                .add(KlaxonBlocks.MOLTEN_RUBBER_BLOCK.asItem());
    }

    private void buildMaterialNuggetTags() {
        getOrCreateTagBuilder(ConventionalItemTags.NUGGETS)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_NUGGETS)
                .forceAddTag(KlaxonConventionalItemTags.CRUDE_STEEL_NUGGETS);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.STEEL_NUGGETS)
                .add(KlaxonItems.STEEL_NUGGET);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.CRUDE_STEEL_NUGGETS)
                .add(KlaxonItems.CRUDE_STEEL_NUGGET);
    }

    private void buildMakeshiftCraftingIngredientTags() {
        getOrCreateTagBuilder(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_NUGGETS)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_NUGGETS)
                .forceAddTag(KlaxonConventionalItemTags.CRUDE_STEEL_NUGGETS);
        getOrCreateTagBuilder(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_INGOTS)
                .forceAddTag(KlaxonConventionalItemTags.CRUDE_STEEL_INGOTS);
        getOrCreateTagBuilder(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_BLOCKS)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_BLOCKS)
                .forceAddTag(KlaxonConventionalItemTags.CRUDE_STEEL_BLOCKS);
        getOrCreateTagBuilder(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_PLATES)
                .forceAddTag(KlaxonConventionalItemTags.CRUDE_STEEL_PLATES);
        getOrCreateTagBuilder(KlaxonItemTags.MOLTEN_INCLUSIVE_RUBBER_GLOBS)
                .forceAddTag(KlaxonConventionalItemTags.RUBBER_GLOBS)
                .forceAddTag(KlaxonConventionalItemTags.MOLTEN_RUBBER_GLOBS);
        getOrCreateTagBuilder(KlaxonItemTags.MOLTEN_INCLUSIVE_RUBBER_BLOCKS)
                .forceAddTag(KlaxonConventionalItemTags.RUBBER_BLOCKS)
                .forceAddTag(KlaxonConventionalItemTags.MOLTEN_RUBBER_BLOCKS);
        getOrCreateTagBuilder(KlaxonItemTags.MOLTEN_INCLUSIVE_RUBBER_SHEETS)
                .forceAddTag(KlaxonConventionalItemTags.RUBBER_SHEETS)
                .forceAddTag(KlaxonConventionalItemTags.MOLTEN_RUBBER_SHEETS);
    }

    private void buildMaterialPlateTags() {
        getOrCreateTagBuilder(KlaxonConventionalItemTags.PLATES)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_PLATES)
                .forceAddTag(KlaxonConventionalItemTags.CRUDE_STEEL_PLATES)
                .forceAddTag(KlaxonConventionalItemTags.IRON_PLATES)
                .forceAddTag(KlaxonConventionalItemTags.COPPER_PLATES)
                .forceAddTag(KlaxonConventionalItemTags.GOLD_PLATES);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.STEEL_PLATES)
                .add(KlaxonItems.STEEL_PLATE);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.CRUDE_STEEL_PLATES)
                .add(KlaxonItems.CRUDE_STEEL_PLATE);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.IRON_PLATES)
                .add(KlaxonItems.IRON_PLATE);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.COPPER_PLATES)
                .add(KlaxonItems.COPPER_PLATE);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.GOLD_PLATES)
                .add(KlaxonItems.GOLD_PLATE);
    }

    private void buildFracturedMaterialTags() {
        getOrCreateTagBuilder(KlaxonItemTags.FRACTURED_MATERIALS)
                .forceAddTag(KlaxonItemTags.FRACTURED_COALS)
                .forceAddTag(KlaxonItemTags.FRACTURED_COPPER)
                .forceAddTag(KlaxonItemTags.FRACTURED_GOLD)
                .forceAddTag(KlaxonItemTags.FRACTURED_IRON)
                .forceAddTag(KlaxonItemTags.FRACTURED_RAW_COPPER)
                .forceAddTag(KlaxonItemTags.FRACTURED_RAW_GOLD)
                .forceAddTag(KlaxonItemTags.FRACTURED_RAW_IRON);
        getOrCreateTagBuilder(KlaxonItemTags.FRACTURED_COALS)
                .add(KlaxonItems.FRACTURED_COAL)
                .add(KlaxonItems.FRACTURED_CHARCOAL);
        getOrCreateTagBuilder(KlaxonItemTags.FRACTURED_IRON)
                .add(KlaxonItems.FRACTURED_IRON);
        getOrCreateTagBuilder(KlaxonItemTags.FRACTURED_GOLD)
                .add(KlaxonItems.FRACTURED_GOLD);
        getOrCreateTagBuilder(KlaxonItemTags.FRACTURED_COPPER)
                .add(KlaxonItems.FRACTURED_COPPER);
        getOrCreateTagBuilder(KlaxonItemTags.FRACTURED_RAW_IRON)
                .add(KlaxonItems.FRACTURED_RAW_IRON);
        getOrCreateTagBuilder(KlaxonItemTags.FRACTURED_RAW_GOLD)
                .add(KlaxonItems.FRACTURED_RAW_GOLD);
        getOrCreateTagBuilder(KlaxonItemTags.FRACTURED_RAW_COPPER)
                .add(KlaxonItems.FRACTURED_RAW_COPPER);
    }

    private void buildMaterialSheetTags() {
        getOrCreateTagBuilder(KlaxonConventionalItemTags.SHEETS)
                .forceAddTag(KlaxonConventionalItemTags.RUBBER_SHEETS)
                .forceAddTag(KlaxonConventionalItemTags.MOLTEN_RUBBER_SHEETS);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.RUBBER_SHEETS)
                .add(KlaxonItems.RUBBER_SHEET);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.MOLTEN_RUBBER_SHEETS)
                .add(KlaxonItems.MOLTEN_RUBBER_SHEET);
    }

    private void buildMakeshiftCraftingLogisticsTags() {
        getOrCreateTagBuilder(KlaxonItemTags.MAKESHIFT_CRAFTING_INGREDIENTS)
                .add(KlaxonItems.CRUDE_STEEL_PLATE)
                .add(KlaxonItems.CRUDE_STEEL_INGOT)
                .add(KlaxonBlocks.CRUDE_STEEL_BLOCK.asItem())
                .add(KlaxonItems.CRUDE_STEEL_NUGGET)
                .add(KlaxonItems.MOLTEN_RUBBER_SHEET)
                .add(KlaxonItems.MOLTEN_RUBBER_GLOB)
                .add(KlaxonBlocks.MOLTEN_RUBBER_BLOCK.asItem());
        getOrCreateTagBuilder(KlaxonItemTags.MAKESHIFT_REPAIR_MATERIALS)
                .forceAddTag(KlaxonItemTags.MAKESHIFT_CRAFTING_INGREDIENTS);
    }

    private void buildCraftingTags() {
        getOrCreateTagBuilder(KlaxonItemTags.OVERWORLD_RUBBER_EXTRACTABLE_LOGS)
                .forceAddTag(ItemTags.JUNGLE_LOGS)
                .forceAddTag(ItemTags.ACACIA_LOGS)
                .forceAddTag(ItemTags.MANGROVE_LOGS);
        getOrCreateTagBuilder(KlaxonItemTags.NETHER_RUBBER_EXTRACTABLE_LOGS)
                .forceAddTag(ItemTags.CRIMSON_STEMS)
                .forceAddTag(ItemTags.WARPED_STEMS);
        getOrCreateTagBuilder(KlaxonItemTags.GEAR_GRIP_MATERIALS)
                .add(Items.LEATHER)
                .add(KlaxonItems.RUBBER_SHEET);
    }

    private void buildAdvancementRelatedTags() {
        getOrCreateTagBuilder(KlaxonItemTags.MAKESHIFT_CRAFTED_EQUIPMENT)
                .forceAddTag(KlaxonItemTags.STEEL_ARMOR)
                .add(KlaxonItems.STEEL_HAMMER)
                .add(KlaxonItems.STEEL_CLEAVER)
                .add(KlaxonItems.STEEL_WRENCH)
                .add(KlaxonItems.STEEL_CABLE_SHEARS)
                .add(Items.FLINT_AND_STEEL);
        getOrCreateTagBuilder(KlaxonItemTags.KLAXON_ROOT_ADVANCEMENT_GRANTING_ITEMS)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_INGOTS)
                .add(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR.asItem());
    }

    private void buildMechanicsTags() {
        getOrCreateTagBuilder(KlaxonItemTags.HEAVY_EQUIPMENT)
                .forceAddTag(KlaxonItemTags.STEEL_ARMOR)
                .add(Items.NETHERITE_HELMET)
                .add(Items.NETHERITE_CHESTPLATE)
                .add(Items.NETHERITE_LEGGINGS)
                .add(Items.NETHERITE_BOOTS);
        getOrCreateTagBuilder(KlaxonItemTags.FERROMAGNETIC_ITEMS)
                .forceAddTag(KlaxonItemTags.STEEL_EQUIPMENT)
                .add(Items.NETHERITE_HELMET)
                .add(Items.NETHERITE_CHESTPLATE)
                .add(Items.NETHERITE_LEGGINGS)
                .add(Items.NETHERITE_BOOTS)
                .add(Items.IRON_HELMET)
                .add(Items.IRON_CHESTPLATE)
                .add(Items.IRON_LEGGINGS)
                .add(Items.IRON_BOOTS)
                .add(Items.CHAINMAIL_HELMET)
                .add(Items.CHAINMAIL_CHESTPLATE)
                .add(Items.CHAINMAIL_LEGGINGS)
                .add(Items.CHAINMAIL_BOOTS);
        getOrCreateTagBuilder(ItemTags.PIGLIN_LOVED)
                .add(KlaxonItems.GOLD_PLATE)
                .add(KlaxonItems.FRACTURED_RAW_GOLD)
                .add(KlaxonItems.FRACTURED_GOLD)
                .add(KlaxonBlocks.GOLD_PLATING_BLOCK.asItem());
        getOrCreateTagBuilder(ItemTags.BREAKS_DECORATED_POTS)
                .add(KlaxonItems.STEEL_HAMMER)
                .add(KlaxonItems.STEEL_CLEAVER)
                .add(KlaxonItems.STEEL_WRENCH);
        getOrCreateTagBuilder(KlaxonItemTags.RECIPE_PROCESSING_HAMMERS)
                .add(KlaxonItems.STEEL_HAMMER);
        getOrCreateTagBuilder(KlaxonItemTags.RECIPE_PROCESSING_WIRECUTTERS)
                .add(KlaxonItems.STEEL_CABLE_SHEARS);
        getOrCreateTagBuilder(KlaxonItemTags.RECIPE_PROCESSING_SHEARS)
                .add(KlaxonItems.STEEL_CABLE_SHEARS)
                .add(Items.SHEARS);
    }

    private void buildAnvilRelatedTags() {
        getOrCreateTagBuilder(KlaxonItemTags.INFINITELY_REPAIRABLE)
                .forceAddTag(KlaxonItemTags.STEEL_EQUIPMENT);
        getOrCreateTagBuilder(KlaxonItemTags.NO_XP_COST_REPAIRABLE)
                .forceAddTag(KlaxonItemTags.STEEL_EQUIPMENT);
        getOrCreateTagBuilder(KlaxonItemTags.UNENCHANTABLE)
                .forceAddTag(KlaxonItemTags.STEEL_EQUIPMENT);
    }

    private void buildEquipmentCategoryTags() {
        getOrCreateTagBuilder(KlaxonItemTags.STEEL_EQUIPMENT)
                .forceAddTag(KlaxonItemTags.STEEL_ARMOR)
                .add(Items.FLINT_AND_STEEL)
                .add(KlaxonItems.STEEL_HAMMER)
                .add(KlaxonItems.STEEL_CABLE_SHEARS)
                .add(KlaxonItems.STEEL_CLEAVER)
                .add(KlaxonItems.STEEL_WRENCH);
        getOrCreateTagBuilder(KlaxonItemTags.STEEL_ARMOR)
                .add(KlaxonItems.STEEL_HELMET)
                .add(KlaxonItems.STEEL_CHESTPLATE)
                .add(KlaxonItems.STEEL_LEGGINGS)
                .add(KlaxonItems.STEEL_BOOTS);
        getOrCreateTagBuilder(ConventionalItemTags.MINING_TOOL_TOOLS)
                .add(KlaxonItems.STEEL_HAMMER)
                .add(KlaxonItems.STEEL_CLEAVER)
                .add(KlaxonItems.STEEL_WRENCH)
                .add(KlaxonItems.STEEL_CABLE_SHEARS);
        getOrCreateTagBuilder(ConventionalItemTags.MELEE_WEAPON_TOOLS)
                .add(KlaxonItems.STEEL_HAMMER)
                .add(KlaxonItems.STEEL_CLEAVER)
                .add(KlaxonItems.STEEL_WRENCH)
                .add(KlaxonItems.STEEL_CABLE_SHEARS);

        getOrCreateTagBuilder(KlaxonConventionalItemTags.CLEAVER)
                .add(KlaxonItems.STEEL_CLEAVER);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.CLEAVERS)
                .add(KlaxonItems.STEEL_CLEAVER);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.KNIFE)
                .add(KlaxonItems.STEEL_CLEAVER);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.KNIVES)
                .add(KlaxonItems.STEEL_CLEAVER);

        getOrCreateTagBuilder(KlaxonConventionalItemTags.HAMMERS)
                .add(KlaxonItems.STEEL_HAMMER);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.HAMMER)
                .add(KlaxonItems.STEEL_HAMMER);

        getOrCreateTagBuilder(KlaxonConventionalItemTags.WRENCHES)
                .add(KlaxonItems.STEEL_WRENCH);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.WRENCH)
                .add(KlaxonItems.STEEL_WRENCH);

        getOrCreateTagBuilder(KlaxonConventionalItemTags.SHEARS)
                .forceAddTag(KlaxonItemTags.CABLE_SHEARS);
        getOrCreateTagBuilder(KlaxonItemTags.CABLE_SHEARS)
                .add(KlaxonItems.STEEL_CABLE_SHEARS);

        getOrCreateTagBuilder(ConventionalItemTags.ARMORS)
                .forceAddTag(KlaxonItemTags.STEEL_ARMOR);
        getOrCreateTagBuilder(ItemTags.HEAD_ARMOR)
                .add(KlaxonItems.STEEL_HELMET);
        getOrCreateTagBuilder(ItemTags.CHEST_ARMOR)
                .add(KlaxonItems.STEEL_CHESTPLATE);
        getOrCreateTagBuilder(ItemTags.LEG_ARMOR)
                .add(KlaxonItems.STEEL_LEGGINGS);
        getOrCreateTagBuilder(ItemTags.FOOT_ARMOR)
                .add(KlaxonItems.STEEL_BOOTS);
    }

    private void buildBlastProcessorCatalystBehaviorTags() {
        getOrCreateTagBuilder(KlaxonItemTags.BEDLIKE_EXPLODABLES)
                .add(Items.WHITE_BED)
                .add(Items.ORANGE_BED)
                .add(Items.MAGENTA_BED)
                .add(Items.LIGHT_BLUE_BED)
                .add(Items.YELLOW_BED)
                .add(Items.LIME_BED)
                .add(Items.PINK_BED)
                .add(Items.GRAY_BED)
                .add(Items.LIGHT_GRAY_BED)
                .add(Items.CYAN_BED)
                .add(Items.PURPLE_BED)
                .add(Items.BLUE_BED)
                .add(Items.BROWN_BED)
                .add(Items.GREEN_BED)
                .add(Items.RED_BED)
                .add(Items.BLACK_BED);
    }

    private void buildCompatTags() {
        getOrCreateTagBuilder(KlaxonCompatItemTags.PEDESTAL_DOWNRIGHT)
                .add(KlaxonItems.STEEL_HAMMER)
                .add(KlaxonItems.STEEL_CLEAVER);
    }
}
