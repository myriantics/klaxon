package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalItemTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public KlaxonItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {

        getOrCreateTagBuilder(KlaxonConventionalItemTags.STEEL_INGOTS)
                .add(KlaxonItems.STEEL_INGOT);

        getOrCreateTagBuilder(KlaxonConventionalItemTags.CRUDE_STEEL_INGOTS)
                .add(KlaxonItems.CRUDE_STEEL_INGOT);

        getOrCreateTagBuilder(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_INGOTS)
                .add(KlaxonItems.CRUDE_STEEL_INGOT);

        getOrCreateTagBuilder(KlaxonConventionalItemTags.STEEL_BLOCKS)
                .add(KlaxonBlocks.STEEL_BLOCK.asItem());

        getOrCreateTagBuilder(KlaxonConventionalItemTags.STEEL_PLATING_BLOCKS)
                .add(KlaxonBlocks.STEEL_PLATING_BLOCK.asItem());

        getOrCreateTagBuilder(KlaxonConventionalItemTags.CRUDE_STEEL_PLATING_BLOCKS)
                .add(KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK.asItem());

        getOrCreateTagBuilder(KlaxonConventionalItemTags.IRON_PLATING_BLOCKS)
                .add(KlaxonBlocks.IRON_PLATING_BLOCK.asItem());

        getOrCreateTagBuilder(KlaxonConventionalItemTags.GOLD_PLATING_BLOCKS)
                .add(KlaxonBlocks.GOLD_PLATING_BLOCK.asItem());

        getOrCreateTagBuilder(KlaxonConventionalItemTags.COPPER_PLATING_BLOCKS)
                .add(KlaxonBlocks.COPPER_PLATING_BLOCK.asItem());

        getOrCreateTagBuilder(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_BLOCKS)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_BLOCKS)
                .add(KlaxonBlocks.CRUDE_STEEL_BLOCK.asItem());

        getOrCreateTagBuilder(KlaxonConventionalItemTags.STEEL_NUGGETS)
                .add(KlaxonItems.STEEL_NUGGET);

        getOrCreateTagBuilder(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_NUGGETS)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_NUGGETS)
                .add(KlaxonItems.CRUDE_STEEL_NUGGET);

        getOrCreateTagBuilder(KlaxonConventionalItemTags.STEEL_PLATES)
                .add(KlaxonItems.STEEL_PLATE);

        getOrCreateTagBuilder(KlaxonConventionalItemTags.CRUDE_STEEL_PLATES)
                .add(KlaxonItems.CRUDE_STEEL_PLATE);

        getOrCreateTagBuilder(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_PLATES)
                .add(KlaxonItems.CRUDE_STEEL_PLATE);

        getOrCreateTagBuilder(KlaxonConventionalItemTags.IRON_PLATES)
                .add(KlaxonItems.IRON_PLATE);

        getOrCreateTagBuilder(KlaxonConventionalItemTags.COPPER_PLATES)
                .add(KlaxonItems.COPPER_PLATE);

        getOrCreateTagBuilder(KlaxonConventionalItemTags.GOLD_PLATES)
                .add(KlaxonItems.GOLD_PLATE);

        getOrCreateTagBuilder(KlaxonItemTags.MAKESHIFT_CRAFTING_INGREDIENTS)
                .add(KlaxonItems.CRUDE_STEEL_PLATE)
                .add(KlaxonItems.CRUDE_STEEL_INGOT)
                .add(KlaxonBlocks.CRUDE_STEEL_BLOCK.asItem())
                .add(KlaxonItems.CRUDE_STEEL_NUGGET);

        getOrCreateTagBuilder(KlaxonItemTags.MAKESHIFT_CRAFTED_EQUIPMENT)
                .add(KlaxonItems.STEEL_HAMMER)
                .forceAddTag(KlaxonItemTags.STEEL_ARMOR)
                .add(Items.FLINT_AND_STEEL);

        getOrCreateTagBuilder(KlaxonItemTags.STEEL_ARMOR)
                .add(KlaxonItems.STEEL_HELMET)
                .add(KlaxonItems.STEEL_CHESTPLATE)
                .add(KlaxonItems.STEEL_LEGGINGS)
                .add(KlaxonItems.STEEL_BOOTS);

        getOrCreateTagBuilder(KlaxonItemTags.HEAVY_EQUIPMENT)
                .forceAddTag(KlaxonItemTags.STEEL_ARMOR)
                .add(Items.NETHERITE_HELMET)
                .add(Items.NETHERITE_CHESTPLATE)
                .add(Items.NETHERITE_LEGGINGS)
                .add(Items.NETHERITE_BOOTS);

        getOrCreateTagBuilder(KlaxonItemTags.FERROMAGNETIC_ITEMS)
                .forceAddTag(KlaxonItemTags.STEEL_ARMOR)
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

        getOrCreateTagBuilder(KlaxonItemTags.STEEL_REPAIRABLE_FLINT_AND_STEEL)
                .add(Items.FLINT_AND_STEEL);

        getOrCreateTagBuilder(KlaxonItemTags.INFINITELY_REPAIRABLE)
                .forceAddTag(KlaxonItemTags.STEEL_ARMOR)
                .add(KlaxonItems.STEEL_HAMMER)
                .add(Items.FLINT_AND_STEEL);

        getOrCreateTagBuilder(KlaxonItemTags.NO_XP_COST_REPAIRABLE)
                .forceAddTag(KlaxonItemTags.STEEL_ARMOR)
                .add(KlaxonItems.STEEL_HAMMER)
                .add(Items.FLINT_AND_STEEL);

        getOrCreateTagBuilder(KlaxonItemTags.UNENCHANTABLE)
                .forceAddTag(KlaxonItemTags.STEEL_ARMOR)
                .add(KlaxonItems.STEEL_HAMMER)
                .add(Items.FLINT_AND_STEEL);

        getOrCreateTagBuilder(KlaxonItemTags.MAKESHIFT_REPAIR_MATERIALS)
                .forceAddTag(KlaxonItemTags.MAKESHIFT_CRAFTING_INGREDIENTS);

        getOrCreateTagBuilder(ConventionalItemTags.INGOTS)
                .forceAddTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS);

        getOrCreateTagBuilder(KlaxonItemTags.KLAXON_ROOT_ADVANCEMENT_GRANTING_ITEMS)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_INGOTS)
                .add(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR.asItem());

        getOrCreateTagBuilder(ConventionalItemTags.MINING_TOOL_TOOLS)
                .add(KlaxonItems.STEEL_HAMMER);

        getOrCreateTagBuilder(ConventionalItemTags.MELEE_WEAPON_TOOLS)
                .add(KlaxonItems.STEEL_HAMMER);

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
}
