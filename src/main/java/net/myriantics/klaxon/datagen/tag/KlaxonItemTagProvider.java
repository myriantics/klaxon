package net.myriantics.klaxon.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.tag.compat.KlaxonCompatItemTags;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalItemTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public KlaxonItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        // build material tags
        buildMaterialIngotTags();
        buildMaterialGlobTags();
        buildMaterialStorageBlockTags();
        buildMaterialNuggetTags();
        buildMaterialPlateTags();
        buildMaterialWireTags();
        buildMaterialSheetTags();
        buildFracturedMaterialTags();
        buildWoodTags();

        // build makeshift crafting tags
        buildMakeshiftCraftingIngredientTags();
        buildMakeshiftCraftingLogisticsTags();

        // build recipe tags
        buildCraftingTags();

        // build advancement tags
        buildAdvancementRelatedTags();

        // build mechanics tags
        buildMechanicsTags();
        buildRepairTags();
        buildAnvilRelatedTags();
        buildBlastProcessorCatalystBehaviorTags();
        buildEnchantableTags();

        // build categorical tags
        buildEquipmentCategoryTags();

        // build compat tags
        buildCompatTags();
    }


    private void buildWoodTags() {
        getOrCreateTagBuilder(ConventionalItemTags.STRIPPED_LOGS)
                .add(KlaxonItems.STRIPPED_HALLNOX_STEM.value());
        getOrCreateTagBuilder(ConventionalItemTags.STRIPPED_WOODS)
                .add(KlaxonItems.STRIPPED_HALLNOX_HYPHAE.value());
        getOrCreateTagBuilder(ItemTags.LOGS)
                .forceAddTag(KlaxonItemTags.HALLNOX_STEMS);
        getOrCreateTagBuilder(ItemTags.NON_FLAMMABLE_WOOD)
                .forceAddTag(KlaxonItemTags.HALLNOX_STEMS)
                .add(KlaxonItems.HALLNOX_PLANKS.value())
                .add(KlaxonItems.HALLNOX_SLAB.value())
                .add(KlaxonItems.HALLNOX_PRESSURE_PLATE.value())
                .add(KlaxonItems.HALLNOX_FENCE.value())
                .add(KlaxonItems.HALLNOX_TRAPDOOR.value())
                .add(KlaxonItems.HALLNOX_FENCE_GATE.value())
                .add(KlaxonItems.HALLNOX_STAIRS.value())
                .add(KlaxonItems.HALLNOX_BUTTON.value())
                .add(KlaxonItems.HALLNOX_DOOR.value())
                .add(KlaxonItems.HALLNOX_SIGN.value())
                .add(KlaxonItems.HALLNOX_HANGING_SIGN.value());
        getOrCreateTagBuilder(KlaxonItemTags.HALLNOX_STEMS)
                .add(KlaxonItems.HALLNOX_STEM.value())
                .add(KlaxonItems.STRIPPED_HALLNOX_STEM.value())
                .add(KlaxonItems.HALLNOX_HYPHAE.value())
                .add(KlaxonItems.STRIPPED_HALLNOX_HYPHAE.value());
        getOrCreateTagBuilder(ItemTags.PLANKS)
                .add(KlaxonItems.HALLNOX_PLANKS.value());
        getOrCreateTagBuilder(ItemTags.WOODEN_STAIRS)
                .add(KlaxonItems.HALLNOX_STAIRS.value());
        getOrCreateTagBuilder(ItemTags.WOODEN_SLABS)
                .add(KlaxonItems.HALLNOX_SLAB.value());

        getOrCreateTagBuilder(ItemTags.WOODEN_BUTTONS)
                .add(KlaxonItems.HALLNOX_BUTTON.value());
        getOrCreateTagBuilder(ItemTags.WOODEN_PRESSURE_PLATES)
                .add(KlaxonItems.HALLNOX_PRESSURE_PLATE.value());
        getOrCreateTagBuilder(ItemTags.WOODEN_DOORS)
                .add(KlaxonItems.HALLNOX_DOOR.value());
        getOrCreateTagBuilder(ItemTags.WOODEN_TRAPDOORS)
                .add(KlaxonItems.HALLNOX_TRAPDOOR.value());
        getOrCreateTagBuilder(ItemTags.WOODEN_FENCES)
                .add(KlaxonItems.HALLNOX_FENCE.value());
        getOrCreateTagBuilder(ConventionalItemTags.WOODEN_FENCES)
                .add(KlaxonItems.HALLNOX_FENCE.value());
        getOrCreateTagBuilder(ConventionalItemTags.WOODEN_FENCE_GATES)
                .add(KlaxonItems.HALLNOX_FENCE_GATE.value());
        getOrCreateTagBuilder(ItemTags.SIGNS)
                .add(KlaxonItems.HALLNOX_SIGN.value());
        getOrCreateTagBuilder(ItemTags.HANGING_SIGNS)
                .add(KlaxonItems.HALLNOX_HANGING_SIGN.value());

        getOrCreateTagBuilder(ItemTags.WART_BLOCKS)
                .add(KlaxonItems.HALLNOX_WART_BLOCK.value());
    }

    private void buildMaterialIngotTags() {
        getOrCreateTagBuilder(ConventionalItemTags.INGOTS)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_INGOTS)
                .forceAddTag(KlaxonConventionalItemTags.CRUDE_STEEL_INGOTS);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.STEEL_INGOTS)
                .add(KlaxonItems.STEEL_INGOT.value());
        getOrCreateTagBuilder(KlaxonConventionalItemTags.CRUDE_STEEL_INGOTS)
                .add(KlaxonItems.CRUDE_STEEL_INGOT.value());
    }

    private void buildMaterialGlobTags() {
        getOrCreateTagBuilder(KlaxonConventionalItemTags.GLOBS)
                .forceAddTag(KlaxonConventionalItemTags.RUBBER_GLOBS);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.RUBBER_GLOBS)
                .add(KlaxonItems.RUBBER_GLOB.value());
    }

    private void buildMaterialStorageBlockTags() {
        getOrCreateTagBuilder(ConventionalItemTags.STORAGE_BLOCKS)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_BLOCKS)
                .forceAddTag(KlaxonConventionalItemTags.CRUDE_STEEL_BLOCKS)
                .forceAddTag(KlaxonConventionalItemTags.RUBBER_BLOCKS)
                .forceAddTag(KlaxonConventionalItemTags.MOLTEN_RUBBER_BLOCKS);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.STEEL_BLOCKS)
                .add(KlaxonItems.STEEL_BLOCK.value());
        getOrCreateTagBuilder(KlaxonConventionalItemTags.CRUDE_STEEL_BLOCKS)
                .add(KlaxonItems.CRUDE_STEEL_BLOCK.value());
        getOrCreateTagBuilder(KlaxonConventionalItemTags.RUBBER_BLOCKS)
                .add(KlaxonItems.RUBBER_BLOCK.value())
                .add(KlaxonItems.RUBBER_SHEET_BLOCK.value());
        getOrCreateTagBuilder(KlaxonConventionalItemTags.MOLTEN_RUBBER_BLOCKS)
                .add(KlaxonItems.MOLTEN_RUBBER_BLOCK.value());
    }

    private void buildMaterialNuggetTags() {
        getOrCreateTagBuilder(ConventionalItemTags.NUGGETS)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_NUGGETS)
                .forceAddTag(KlaxonConventionalItemTags.CRUDE_STEEL_NUGGETS)
                .forceAddTag(KlaxonConventionalItemTags.COPPER_NUGGETS);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.STEEL_NUGGETS)
                .add(KlaxonItems.STEEL_NUGGET.value());
        getOrCreateTagBuilder(KlaxonConventionalItemTags.CRUDE_STEEL_NUGGETS)
                .add(KlaxonItems.CRUDE_STEEL_NUGGET.value());
        getOrCreateTagBuilder(KlaxonConventionalItemTags.COPPER_NUGGETS)
                .add(KlaxonItems.COPPER_NUGGET.value());
    }

    private void buildMaterialWireTags() {
        getOrCreateTagBuilder(KlaxonConventionalItemTags.WIRES)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_WIRES)
                .forceAddTag(KlaxonConventionalItemTags.IRON_WIRES)
                .forceAddTag(KlaxonConventionalItemTags.GOLD_WIRES)
                .forceAddTag(KlaxonConventionalItemTags.COPPER_WIRES);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.STEEL_WIRES)
                .add(KlaxonItems.STEEL_WIRE.value());
        getOrCreateTagBuilder(KlaxonConventionalItemTags.IRON_WIRES)
                .add(KlaxonItems.IRON_WIRE.value());
        getOrCreateTagBuilder(KlaxonConventionalItemTags.GOLD_WIRES)
                .add(KlaxonItems.GOLD_WIRE.value());
        getOrCreateTagBuilder(KlaxonConventionalItemTags.COPPER_WIRES)
                .add(KlaxonItems.COPPER_WIRE.value());
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
        getOrCreateTagBuilder(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_CASING)
                .add(KlaxonItems.STEEL_CASING.value())
                .add(KlaxonItems.CRUDE_STEEL_CASING.value());
        getOrCreateTagBuilder(KlaxonItemTags.MOLTEN_INCLUSIVE_RUBBER_BLOCKS)
                .forceAddTag(KlaxonConventionalItemTags.RUBBER_BLOCKS)
                .forceAddTag(KlaxonConventionalItemTags.MOLTEN_RUBBER_BLOCKS);
    }

    private void buildMaterialPlateTags() {
        getOrCreateTagBuilder(KlaxonConventionalItemTags.PLATES)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_PLATES)
                .forceAddTag(KlaxonConventionalItemTags.CRUDE_STEEL_PLATES)
                .forceAddTag(KlaxonConventionalItemTags.IRON_PLATES)
                .forceAddTag(KlaxonConventionalItemTags.COPPER_PLATES)
                .forceAddTag(KlaxonConventionalItemTags.GOLD_PLATES);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.STEEL_PLATES)
                .add(KlaxonItems.STEEL_PLATE.value());
        getOrCreateTagBuilder(KlaxonConventionalItemTags.CRUDE_STEEL_PLATES)
                .add(KlaxonItems.CRUDE_STEEL_PLATE.value());
        getOrCreateTagBuilder(KlaxonConventionalItemTags.IRON_PLATES)
                .add(KlaxonItems.IRON_PLATE.value());
        getOrCreateTagBuilder(KlaxonConventionalItemTags.COPPER_PLATES)
                .add(KlaxonItems.COPPER_PLATE.value());
        getOrCreateTagBuilder(KlaxonConventionalItemTags.GOLD_PLATES)
                .add(KlaxonItems.GOLD_PLATE.value());
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
                .add(KlaxonItems.FRACTURED_COAL.value())
                .add(KlaxonItems.FRACTURED_CHARCOAL.value());
        getOrCreateTagBuilder(KlaxonItemTags.FRACTURED_IRON)
                .add(KlaxonItems.FRACTURED_IRON.value());
        getOrCreateTagBuilder(KlaxonItemTags.FRACTURED_GOLD)
                .add(KlaxonItems.FRACTURED_GOLD.value());
        getOrCreateTagBuilder(KlaxonItemTags.FRACTURED_COPPER)
                .add(KlaxonItems.FRACTURED_COPPER.value());
        getOrCreateTagBuilder(KlaxonItemTags.FRACTURED_RAW_IRON)
                .add(KlaxonItems.FRACTURED_RAW_IRON.value());
        getOrCreateTagBuilder(KlaxonItemTags.FRACTURED_RAW_GOLD)
                .add(KlaxonItems.FRACTURED_RAW_GOLD.value());
        getOrCreateTagBuilder(KlaxonItemTags.FRACTURED_RAW_COPPER)
                .add(KlaxonItems.FRACTURED_RAW_COPPER.value());
    }

    private void buildMaterialSheetTags() {
        getOrCreateTagBuilder(KlaxonConventionalItemTags.SHEETS)
                .forceAddTag(KlaxonConventionalItemTags.RUBBER_SHEETS);
        getOrCreateTagBuilder(KlaxonConventionalItemTags.RUBBER_SHEETS)
                .add(KlaxonItems.RUBBER_SHEET.value());
    }

    private void buildMakeshiftCraftingLogisticsTags() {
        getOrCreateTagBuilder(KlaxonItemTags.MAKESHIFT_CRAFTING_INGREDIENTS)
                .add(KlaxonItems.CRUDE_STEEL_PLATE.value())
                .add(KlaxonItems.CRUDE_STEEL_INGOT.value())
                .add(KlaxonItems.CRUDE_STEEL_BLOCK.value())
                .add(KlaxonItems.CRUDE_STEEL_NUGGET.value())
                .add(KlaxonItems.MOLTEN_RUBBER_BLOCK.value());
        getOrCreateTagBuilder(KlaxonItemTags.MAKESHIFT_REPAIR_MATERIALS)
                .forceAddTag(KlaxonItemTags.MAKESHIFT_CRAFTING_INGREDIENTS);
    }

    private void buildCraftingTags() {
        getOrCreateTagBuilder(KlaxonItemTags.HIGH_YIELD_RUBBER_EXTRACTABLE_LOGS)
                .forceAddTag(ItemTags.CRIMSON_STEMS)
                .forceAddTag(ItemTags.WARPED_STEMS)
                .forceAddTag(KlaxonItemTags.HALLNOX_STEMS);
        getOrCreateTagBuilder(KlaxonItemTags.LOW_YIELD_RUBBER_EXTRACTABLE_LOGS)
                .forceAddTag(ItemTags.JUNGLE_LOGS)
                .forceAddTag(ItemTags.ACACIA_LOGS)
                .forceAddTag(ItemTags.MANGROVE_LOGS);
        getOrCreateTagBuilder(KlaxonItemTags.GEAR_GRIP_MATERIALS)
                .add(Items.LEATHER)
                .add(KlaxonItems.RUBBER_SHEET.value());
        getOrCreateTagBuilder(KlaxonItemTags.STEEL_NUGGET_COOKING_RECYCLABLES)
                .addOptionalTag(KlaxonItemTags.STEEL_EQUIPMENT)
                .add(KlaxonItems.CRUDE_STEEL_CASING.value())
                .add(KlaxonItems.CRUDE_NETHER_REACTOR_CORE.value())
                .add(KlaxonItems.CRUDE_STEEL_DOOR.value())
                .add(KlaxonItems.CRUDE_STEEL_TRAPDOOR.value());
        getOrCreateTagBuilder(KlaxonItemTags.STEEL_INGOT_COOKING_RECYCLABLES)
                .add(KlaxonItems.STEEL_BLAST_PROCESSOR.value())
                .add(KlaxonItems.PRECISION_DISPENSER.value())
                .add(KlaxonItems.STEEL_CASING.value())
                .add(KlaxonItems.NETHER_REACTOR_CORE.value())
                .add(KlaxonItems.STEEL_WORKBENCH.value())
                .add(KlaxonItems.STEEL_DOOR.value())
                .add(KlaxonItems.STEEL_TRAPDOOR.value());
        getOrCreateTagBuilder(KlaxonItemTags.SUSPICIOUS_STEW_INGREDIENTS)
                .add(KlaxonItems.HALLNOX_POD.value());
    }

    private void buildAdvancementRelatedTags() {
        getOrCreateTagBuilder(KlaxonItemTags.MAKESHIFT_CRAFTED_EQUIPMENT)
                .forceAddTag(KlaxonItemTags.STEEL_ARMOR)
                .add(KlaxonItems.STEEL_HAMMER.value())
                .add(KlaxonItems.STEEL_CLEAVER.value())
                .add(KlaxonItems.STEEL_WRENCH.value())
                .add(KlaxonItems.STEEL_CABLE_SHEARS.value())
                .add(KlaxonItems.STEEL_LIGHTER.value());
        getOrCreateTagBuilder(KlaxonItemTags.KLAXON_ROOT_ADVANCEMENT_GRANTING_ITEMS)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_INGOTS)
                .add(KlaxonItems.DEEPSLATE_BLAST_PROCESSOR.value());
    }

    private void buildMechanicsTags() {
        getOrCreateTagBuilder(ItemTags.DYEABLE)
                .add(KlaxonItems.CRESTED_STEEL_HELMET.value());
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
                .add(KlaxonItems.GOLD_PLATE.value())
                .add(KlaxonItems.FRACTURED_RAW_GOLD.value())
                .add(KlaxonItems.FRACTURED_GOLD.value())
                .add(KlaxonItems.GOLD_PLATING_BLOCK.value())
                .add(KlaxonItems.GOLD_WIRE.value())
                .add(KlaxonItems.GOLD_WIRE_SPOOL_BLOCK.value());
        getOrCreateTagBuilder(ItemTags.BREAKS_DECORATED_POTS)
                .add(KlaxonItems.STEEL_HAMMER.value())
                .add(KlaxonItems.STEEL_CLEAVER.value())
                .add(KlaxonItems.STEEL_WRENCH.value());
        getOrCreateTagBuilder(KlaxonItemTags.RECIPE_PROCESSING_HAMMERS)
                .add(KlaxonItems.STEEL_HAMMER.value());
        getOrCreateTagBuilder(KlaxonItemTags.RECIPE_PROCESSING_WIRECUTTERS)
                .add(KlaxonItems.STEEL_CABLE_SHEARS.value());
        getOrCreateTagBuilder(ItemTags.TRIM_MATERIALS)
                .add(KlaxonItems.STEEL_INGOT.value());
        getOrCreateTagBuilder(KlaxonItemTags.EFFECTIVE_AGAINST_METAL_ENTITIES)
                .forceAddTag(ConventionalItemTags.MINING_TOOL_TOOLS);
        getOrCreateTagBuilder(KlaxonItemTags.GRAPPLE_CLAW_INSTAKILL)
                .forceAddTag(KlaxonItemTags.EFFECTIVE_AGAINST_METAL_ENTITIES)
                .forceAddTag(ConventionalItemTags.MELEE_WEAPON_TOOLS);
        getOrCreateTagBuilder(KlaxonItemTags.GRAPPLE_WINCH_CABLE_DETACHERS)
                .forceAddTag(ConventionalItemTags.SHEAR_TOOLS);
        getOrCreateTagBuilder(ItemTags.BEACON_PAYMENT_ITEMS)
                .add(KlaxonItems.STEEL_INGOT.value());
        getOrCreateTagBuilder(ItemTags.CLUSTER_MAX_HARVESTABLES)
                .add(KlaxonItems.STEEL_HAMMER.value());
        getOrCreateTagBuilder(KlaxonItemTags.PICK_BLOCK_SLOT_REPLACEMENT_DISCOURAGED)
                .addOptionalTag(KlaxonItemTags.STEEL_ARMOR)
                .add(KlaxonItems.STEEL_CLEAVER.value())
                .add(KlaxonItems.STEEL_HAMMER.value())
                .add(KlaxonItems.STEEL_WRENCH.value())
                .add(KlaxonItems.STEEL_CABLE_SHEARS.value())
                .add(KlaxonItems.GRAPPLE_WINCH.value())
                .add(KlaxonItems.STEEL_LIGHTER.value());
        getOrCreateTagBuilder(ItemTags.CREEPER_IGNITERS)
                .add(KlaxonItems.STEEL_LIGHTER.value());
        getOrCreateTagBuilder(KlaxonItemTags.WRENCHABLE_INTERFACE_TRIGGERING_TOOLS)
                .addOptionalTag(KlaxonConventionalItemTags.WRENCHES);
        getOrCreateTagBuilder(KlaxonItemTags.DEFUSERS)
                .addOptionalTag(ConventionalItemTags.SHEAR_TOOLS);
        getOrCreateTagBuilder(KlaxonItemTags.MUFFLERS)
                .add(
                        Items.LEATHER_HELMET,
                        Items.LEATHER_CHESTPLATE,
                        Items.LEATHER_LEGGINGS,
                        Items.LEATHER_BOOTS,
                        Items.LEATHER_HORSE_ARMOR
                )
                .add(Items.LEATHER)
                .addOptionalTag(ItemTags.WOOL)
                .addOptionalTag(ItemTags.WOOL_CARPETS)
                .add(
                        Items.MOSS_CARPET,
                        Items.MOSS_BLOCK
                )
                .add(Items.SPONGE)
                .add(Items.COBWEB)
                .add(
                        Items.HAY_BLOCK,
                        Items.TARGET,
                        Items.DRIED_KELP_BLOCK
                )
                .addOptionalTag(ItemTags.BANNERS)
                .add(Items.HONEYCOMB_BLOCK)
                .add(Items.FEATHER)
                .add(
                        Items.WHITE_BED,
                        Items.ORANGE_BED,
                        Items.MAGENTA_BED,
                        Items.LIGHT_BLUE_BED,
                        Items.YELLOW_BED,
                        Items.LIME_BED,
                        Items.PINK_BED,
                        Items.GRAY_BED,
                        Items.LIGHT_GRAY_BED,
                        Items.CYAN_BED,
                        Items.PURPLE_BED,
                        Items.BLUE_BED,
                        Items.BROWN_BED,
                        Items.GREEN_BED,
                        Items.RED_BED,
                        Items.BLACK_BED
                )
                .add(
                        Items.ITEM_FRAME,
                        Items.GLOW_ITEM_FRAME
                )
                .add(Items.SHIELD)
                .add(
                        Items.ELYTRA,
                        Items.PHANTOM_MEMBRANE
                );
        getOrCreateTagBuilder(KlaxonItemTags.MUFFLER_REMOVERS)
                .addOptionalTag(ConventionalItemTags.SHEAR_TOOLS);
    }

    private void buildRepairTags() {
        getOrCreateTagBuilder(KlaxonItemTags.STEEL_INGOT_TOOL_MATERIAL_REPAIR_MATERIALS)
                .addOptionalTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS);
        getOrCreateTagBuilder(KlaxonItemTags.STEEL_PLATE_TOOL_MATERIAL_REPAIR_MATERIALS)
                .addOptionalTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES);
        getOrCreateTagBuilder(KlaxonItemTags.STEEL_NUGGET_TOOL_MATERIAL_REPAIR_MATERIALS)
                .addOptionalTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_NUGGETS);
        getOrCreateTagBuilder(KlaxonItemTags.STEEL_PLATE_ARMOR_MATERIAL_REPAIR_MATERIALS)
                .addOptionalTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES);
        getOrCreateTagBuilder(KlaxonItemTags.LIGHTER_REPAIR_MATERIALS)
                .add(Items.FIRE_CHARGE);
    }

    private void buildEnchantableTags() {
        getOrCreateTagBuilder(KlaxonItemTags.STREAMLINE_ENCHANTABLE)
                .forceAddTag(ItemTags.CHEST_ARMOR_ENCHANTABLE);
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
        getOrCreateTagBuilder(KlaxonItemTags.GRAPPLE_CLAWS)
                .add(KlaxonItems.STEEL_GRAPPLE_CLAW.value());
        getOrCreateTagBuilder(KlaxonItemTags.STEEL_EQUIPMENT)
                .forceAddTag(KlaxonItemTags.STEEL_ARMOR)
                .add(KlaxonItems.STEEL_HAMMER.value())
                .add(KlaxonItems.STEEL_CABLE_SHEARS.value())
                .add(KlaxonItems.STEEL_CLEAVER.value())
                .add(KlaxonItems.STEEL_WRENCH.value())
                .add(KlaxonItems.STEEL_LIGHTER.value())
                .add(KlaxonItems.GRAPPLE_WINCH.value())
                .add(KlaxonItems.STEEL_GRAPPLE_CLAW.value());
        getOrCreateTagBuilder(KlaxonItemTags.STEEL_ARMOR)
                .add(KlaxonItems.STEEL_HELMET.value())
                .add(KlaxonItems.CRESTED_STEEL_HELMET.value())
                .add(KlaxonItems.STEEL_CHESTPLATE.value())
                .add(KlaxonItems.STEEL_LEGGINGS.value())
                .add(KlaxonItems.STEEL_BOOTS.value());
        getOrCreateTagBuilder(ConventionalItemTags.TOOLS)
                .add(KlaxonItems.GRAPPLE_WINCH.value());
        getOrCreateTagBuilder(ConventionalItemTags.MINING_TOOL_TOOLS)
                .add(KlaxonItems.STEEL_HAMMER.value())
                .add(KlaxonItems.STEEL_CLEAVER.value())
                .add(KlaxonItems.STEEL_WRENCH.value())
                .add(KlaxonItems.STEEL_CABLE_SHEARS.value());
        getOrCreateTagBuilder(ConventionalItemTags.MELEE_WEAPON_TOOLS)
                .add(KlaxonItems.STEEL_HAMMER.value())
                .add(KlaxonItems.STEEL_CLEAVER.value())
                .add(KlaxonItems.STEEL_WRENCH.value())
                .add(KlaxonItems.STEEL_CABLE_SHEARS.value());

        getOrCreateTagBuilder(KlaxonConventionalItemTags.CLEAVERS)
                .add(KlaxonItems.STEEL_CLEAVER.value());
        getOrCreateTagBuilder(KlaxonConventionalItemTags.KNIVES)
                .add(KlaxonItems.STEEL_CLEAVER.value());

        getOrCreateTagBuilder(KlaxonConventionalItemTags.HAMMERS)
                .add(KlaxonItems.STEEL_HAMMER.value());

        getOrCreateTagBuilder(KlaxonConventionalItemTags.WRENCHES)
                .add(KlaxonItems.STEEL_WRENCH.value());

        getOrCreateTagBuilder(ConventionalItemTags.SHEAR_TOOLS)
                .forceAddTag(KlaxonItemTags.CABLE_SHEARS);
        getOrCreateTagBuilder(KlaxonItemTags.CABLE_SHEARS)
                .add(KlaxonItems.STEEL_CABLE_SHEARS.value());

        getOrCreateTagBuilder(ConventionalItemTags.ARMORS)
                .forceAddTag(KlaxonItemTags.STEEL_ARMOR);
        getOrCreateTagBuilder(ItemTags.HEAD_ARMOR)
                .add(KlaxonItems.STEEL_HELMET.value())
                .add(KlaxonItems.CRESTED_STEEL_HELMET.value());
        getOrCreateTagBuilder(ItemTags.CHEST_ARMOR)
                .add(KlaxonItems.STEEL_CHESTPLATE.value());
        getOrCreateTagBuilder(ItemTags.LEG_ARMOR)
                .add(KlaxonItems.STEEL_LEGGINGS.value());
        getOrCreateTagBuilder(ItemTags.FOOT_ARMOR)
                .add(KlaxonItems.STEEL_BOOTS.value());

        getOrCreateTagBuilder(ConventionalItemTags.IGNITER_TOOLS)
                .add(KlaxonItems.STEEL_LIGHTER.value());
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
                .add(KlaxonItems.STEEL_HAMMER.value())
                .add(KlaxonItems.STEEL_CLEAVER.value());
    }
}
