package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
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
                .add(KlaxonItems.STEEL_INGOT)
                .add(KlaxonItems.CRUDE_STEEL_INGOT);

        getOrCreateTagBuilder(KlaxonConventionalItemTags.STEEL_BLOCKS)
                .add(KlaxonBlocks.STEEL_BLOCK.asItem())
                .add(KlaxonBlocks.CRUDE_STEEL_BLOCK.asItem());

        getOrCreateTagBuilder(KlaxonConventionalItemTags.STEEL_NUGGETS)
                .add(KlaxonItems.STEEL_NUGGET)
                .add(KlaxonItems.CRUDE_STEEL_NUGGET);

        getOrCreateTagBuilder(KlaxonConventionalItemTags.STEEL_PLATES)
                .add(KlaxonItems.STEEL_PLATE)
                .add(KlaxonItems.CRUDE_STEEL_PLATE);

        getOrCreateTagBuilder(KlaxonItemTags.MAKESHIFT_CRAFTING_INGREDIENTS)
                .add(KlaxonItems.CRUDE_STEEL_PLATE)
                .add(KlaxonItems.CRUDE_STEEL_INGOT)
                .add(KlaxonBlocks.CRUDE_STEEL_BLOCK.asItem())
                .add(KlaxonItems.CRUDE_STEEL_NUGGET);

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

        getOrCreateTagBuilder(KlaxonItemTags.MAGNETIC_EQUIPMENT)
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

        getOrCreateTagBuilder(KlaxonItemTags.INFINITELY_REPAIRABLE)
                .forceAddTag(KlaxonItemTags.STEEL_ARMOR)
                .add(KlaxonItems.STEEL_HAMMER)
                .add(Items.FLINT_AND_STEEL);

        getOrCreateTagBuilder(KlaxonItemTags.NO_XP_COST_REPAIRABLE)
                .forceAddTag(KlaxonItemTags.STEEL_ARMOR)
                .add(KlaxonItems.STEEL_HAMMER)
                .add(Items.FLINT_AND_STEEL);

        getOrCreateTagBuilder(KlaxonItemTags.MAKESHIFT_REPAIR_MATERIALS)
                .forceAddTag(KlaxonItemTags.MAKESHIFT_CRAFTING_INGREDIENTS);

        getOrCreateTagBuilder(ConventionalItemTags.INGOTS)
                .forceAddTag(KlaxonConventionalItemTags.STEEL_INGOTS);

        getOrCreateTagBuilder(ConventionalItemTags.MINING_TOOL_TOOLS)
                .add(KlaxonItems.STEEL_HAMMER);

        getOrCreateTagBuilder(ConventionalItemTags.MELEE_WEAPON_TOOLS)
                .add(KlaxonItems.STEEL_HAMMER);
    }
}
