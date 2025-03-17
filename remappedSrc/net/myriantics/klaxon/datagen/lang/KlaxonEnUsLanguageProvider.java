package net.myriantics.klaxon.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.myriantics.klaxon.registry.KlaxonBlocks;
import net.myriantics.klaxon.registry.KlaxonEntities;
import net.myriantics.klaxon.registry.KlaxonStatusEffects;
import net.myriantics.klaxon.registry.KlaxonItemGroups;
import net.myriantics.klaxon.registry.KlaxonItems;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonEntityTypeTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonStatusEffectTags;
import net.myriantics.klaxon.registry.KlaxonDamageTypes;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class KlaxonEnUsLanguageProvider extends FabricLanguageProvider {
    public KlaxonEnUsLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        generateItemTranslations(translationBuilder);
        generateBlockTranslations(translationBuilder);
        generateEntityTypeTranslations(translationBuilder);
        generateStatusEffectTranslations(translationBuilder);

        generateItemTagTranslations(translationBuilder);
        generateBlockTagTranslations(translationBuilder);
        generateEntityTypeTagTranslations(translationBuilder);
        generateStatusEffectTagTranslations(translationBuilder);

        generateDeathMessageTranslations(translationBuilder);
        generateItemGroupTranslations(translationBuilder);
    }

    private static void generateItemTranslations(TranslationBuilder translationBuilder) {
        // equipment
        translationBuilder.add(KlaxonItems.STEEL_HAMMER, "Steel Hammer");
        translationBuilder.add(KlaxonItems.STEEL_HELMET, "Steel Helmet");
        translationBuilder.add(KlaxonItems.STEEL_CHESTPLATE, "Steel Chestplate");
        translationBuilder.add(KlaxonItems.STEEL_LEGGINGS, "Steel Leggings");
        translationBuilder.add(KlaxonItems.STEEL_BOOTS, "Steel Boots");

        // fractured ores
        translationBuilder.add(KlaxonItems.FRACTURED_RAW_IRON, "Fractured Raw Iron");
        translationBuilder.add(KlaxonItems.FRACTURED_RAW_GOLD, "Fractured Raw Gold");
        translationBuilder.add(KlaxonItems.FRACTURED_RAW_COPPER, "Fractured Raw Copper");
        translationBuilder.add(KlaxonItems.FRACTURED_IRON_FRAGMENTS, "Fractured Iron Fragments");
        translationBuilder.add(KlaxonItems.FRACTURED_COPPER_FRAGMENTS, "Fractured Gold Fragments");
        translationBuilder.add(KlaxonItems.FRACTURED_GOLD_FRAGMENTS, "Fractured Copper Fragments");

        // alloy blends
        translationBuilder.add(KlaxonItems.CRUDE_STEEL_MIXTURE, "Crude Steel Mixture");

        // ingots
        translationBuilder.add(KlaxonItems.STEEL_INGOT, "Steel Ingot");
        translationBuilder.add(KlaxonItems.CRUDE_STEEL_INGOT, "Crude Steel Ingot");

        // nuggets
        translationBuilder.add(KlaxonItems.STEEL_NUGGET, "Steel Nugget");
        translationBuilder.add(KlaxonItems.CRUDE_STEEL_NUGGET, "Crude Steel Nugget");

        // plates
        translationBuilder.add(KlaxonItems.STEEL_PLATE, "Steel Plate");
        translationBuilder.add(KlaxonItems.CRUDE_STEEL_PLATE, "Crude Steel Plate");
        translationBuilder.add(KlaxonItems.IRON_PLATE, "Iron Plate");
        translationBuilder.add(KlaxonItems.GOLD_PLATE, "Gold Plate");
        translationBuilder.add(KlaxonItems.COPPER_PLATE, "Copper Plate");
    }

    private static void generateBlockTranslations(TranslationBuilder translationBuilder) {
        // machines
        translationBuilder.add(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "Deepslate Blast Processor");

        // storage blocks
        translationBuilder.add(KlaxonBlocks.STEEL_BLOCK, "Block of Steel");
        translationBuilder.add(KlaxonBlocks.CRUDE_STEEL_BLOCK, "Block of Crude Steel");

        // plating blocks
        translationBuilder.add(KlaxonBlocks.STEEL_PLATING_BLOCK, "Steel Plating Block");
        translationBuilder.add(KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK, "Crude Steel Plating Block");
        translationBuilder.add(KlaxonBlocks.IRON_PLATING_BLOCK, "Iron Plating Block");
        translationBuilder.add(KlaxonBlocks.COPPER_PLATING_BLOCK, "Copper Plating Block");
        translationBuilder.add(KlaxonBlocks.GOLD_PLATING_BLOCK, "Gold Plating Block");

        // decor
        translationBuilder.add(KlaxonBlocks.STEEL_DOOR, "Steel Door");
        translationBuilder.add(KlaxonBlocks.STEEL_TRAPDOOR, "Steel Trapdoor");
        translationBuilder.add(KlaxonBlocks.CRUDE_STEEL_DOOR, "Crude Steel Door");
        translationBuilder.add(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR, "Crude Steel Trapdoor");
    }

    private static void generateEntityTypeTranslations(TranslationBuilder translationBuilder) {

    }

    private static void generateStatusEffectTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(KlaxonStatusEffects.HEAVY.value(), "Heavy");
    }

    private static void generateItemTagTranslations(TranslationBuilder translationBuilder) {
        // category tags
        translationBuilder.add(KlaxonItemTags.STEEL_ARMOR, "KLAXON's Steel Armor");

        // gameplay related tags
        translationBuilder.add(KlaxonItemTags.HEAVY_EQUIPMENT, "Heavy Equipment");
        translationBuilder.add(KlaxonItemTags.FERROMAGNETIC_ITEMS, "Ferromagnetic Items");
        translationBuilder.add(KlaxonItemTags.FERROMAGNETIC_ITEM_BLACKLIST, "Ferromagnetic Item Blacklist");

        // anvil related tags
        translationBuilder.add(KlaxonItemTags.INFINITELY_REPAIRABLE, "Infinitely Repairable Items");
        translationBuilder.add(KlaxonItemTags.NO_XP_COST_REPAIRABLE, "Items Repairable with No EXP");
        translationBuilder.add(KlaxonItemTags.UNENCHANTABLE, "Unenchantable Items");
        translationBuilder.add(KlaxonItemTags.STEEL_REPAIRABLE_FLINT_AND_STEEL, "Steel Nugget Repairable Flint & Steel");
        translationBuilder.add(KlaxonItemTags.MAKESHIFT_REPAIR_MATERIALS, "Makeshift Repair Materials");

        // makeshift crafting ingredients
        translationBuilder.add(KlaxonItemTags.MAKESHIFT_CRAFTING_INGREDIENTS, "Makeshift Crafting Ingredients");

        // makeshift crafting recipe specific tags
        translationBuilder.add(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_BLOCKS, "Crude Inclusive Steel Blocks");
        translationBuilder.add(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS, "Crude Inclusive Steel Ingots");
        translationBuilder.add(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_NUGGETS, "Crude Inclusive Steel Nuggets");
        translationBuilder.add(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES, "Crude Inclusive Steel Plates");

        // advancement related tags
        translationBuilder.add(KlaxonItemTags.MAKESHIFT_CRAFTED_EQUIPMENT, "Makeshift Crafted Equipment");
    }

    private static void generateBlockTagTranslations(TranslationBuilder translationBuilder) {

        // mining
        translationBuilder.add(KlaxonBlockTags.HAMMER_INSTABREAKABLE, "Hammer Instabreakable Blocks");
        translationBuilder.add(KlaxonBlockTags.HAMMER_MINEABLE, "Hammer Mineable Blocks");

        // gameplay tags
        translationBuilder.add(KlaxonBlockTags.FERROMAGNETIC_BLOCKS, "Ferromagnetic Blocks");

        // advancement related tags
        translationBuilder.add(KlaxonBlockTags.BLAST_PROCESSORS, "Blast Processors");
    }

    private static void generateEntityTypeTagTranslations(TranslationBuilder translationBuilder) {
        // gameplay
        translationBuilder.add(KlaxonEntityTypeTags.FERROMAGNETIC_ENTITIES, "Ferromagnetic Entities");
        translationBuilder.add(KlaxonEntityTypeTags.HEAVY_HITTERS, "Heavy Hitting Entities");
    }

    private static void generateStatusEffectTagTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(KlaxonStatusEffectTags.HEAVY_STATUS_EFFECTS, "Heavy Status Effects");
    }

    private static void generateDeathMessageTranslations(TranslationBuilder translationBuilder) {
        addDeathMessageTranslations(translationBuilder, KlaxonDamageTypes.HAMMER_BONKING, "%1$s was bonked by %2$s",
                "%1$s was bonked by %2$s using %3$s");
        addDeathMessageTranslations(translationBuilder, KlaxonDamageTypes.HAMMER_WALLOPING, "%1$s was walloped by %2$s",
                "%1$s was walloped by %2$s using %3$s");
    }

    private static void generateItemGroupTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(KlaxonItemGroups.KLAXON_BUILDING_BLOCKS_ID, "KLAXON's Building Blocks");
        translationBuilder.add(KlaxonItemGroups.KLAXON_EQUIPMENT_ID, "KLAXON's Equipment");
        translationBuilder.add(KlaxonItemGroups.KLAXON_MACHINES_ID, "KLAXON's Machines");
        translationBuilder.add(KlaxonItemGroups.KLAXON_MATERIALS_ID, "KLAXON's Materials");
    }

    public static void addDeathMessageTranslations(TranslationBuilder translationBuilder, RegistryKey<DamageType> damageType, String value, @Nullable String itemValue) {
        String key = "death.attack." + damageType.getValue().getPath();

        translationBuilder.add(key, value);
        if (itemValue != null) translationBuilder.add(key + ".item", itemValue);
    }
}
