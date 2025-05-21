package net.myriantics.klaxon.datagen.advancement.providers;

import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.advancement.criterion.ItemRepairCriterion;
import net.myriantics.klaxon.advancement.criterion.BlockActivationCriterion;
import net.myriantics.klaxon.advancement.criterion.ToolUsageRecipeCraftCriterion;
import net.myriantics.klaxon.advancement.criterion.WalljumpAbilityCriterion;
import net.myriantics.klaxon.datagen.advancement.KlaxonAdvancementSubProvider;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.registry.minecraft.KlaxonItems;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalItemTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.function.Consumer;

public class KlaxonPreludeAdvancementProvider extends KlaxonAdvancementSubProvider {
    public KlaxonPreludeAdvancementProvider(Consumer<AdvancementEntry> consumer) {
        super(consumer, "prelude");
    }

    @Override
    public AdvancementEntry generateAdvancements() {
        AdvancementEntry root = generateRoot();
        generateAdvancements(root);
        return root;
    }

    private AdvancementEntry generateRoot() {
        return addRootAdvancement(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, AdvancementFrame.TASK, InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(KlaxonItemTags.KLAXON_ROOT_ADVANCEMENT_GRANTING_ITEMS)));
    }

    private void generateAdvancements(AdvancementEntry root) {
        AdvancementEntry watchBlastProcessorCraft = addTask(root, "watch_blast_processor_craft", KlaxonItems.FRACTURED_RAW_IRON, BlockActivationCriterion.Conditions.create(KlaxonBlockTags.BLAST_PROCESSORS));
        AdvancementEntry hammerCraftMetalPlate = addTask(watchBlastProcessorCraft, "use_hammer_to_make_metal_plate", KlaxonItems.CRUDE_STEEL_PLATE, ToolUsageRecipeCraftCriterion.Conditions.createHammering(Ingredient.fromTag(KlaxonConventionalItemTags.PLATES)));
        AdvancementEntry obtainSteelCleaver = addTask(hammerCraftMetalPlate, "obtain_steel_cleaver", KlaxonItems.STEEL_CLEAVER, InventoryChangedCriterion.Conditions.items(KlaxonItems.STEEL_CLEAVER));
        AdvancementEntry obtainAnyRubberGlob = addTask(watchBlastProcessorCraft, "obtain_any_rubber_glob", KlaxonItems.RUBBER_GLOB, InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(KlaxonItemTags.MOLTEN_INCLUSIVE_RUBBER_GLOBS)));
        AdvancementEntry hammerWalljump = addTask(watchBlastProcessorCraft, "hammer_walljump", KlaxonItems.STEEL_HAMMER, WalljumpAbilityCriterion.Conditions.createWalljump(true));
        AdvancementEntry strengthWalljump = addChallenge(hammerWalljump, "hammer_walljump_strength", Items.BLAZE_POWDER, false, WalljumpAbilityCriterion.Conditions.createStrengthWalljump(), AdvancementRewards.Builder.experience(400));
        AdvancementEntry minecartWalljump = addChallenge(hammerWalljump, "hammer_walljump_minecart", Blocks.CAULDRON, true, WalljumpAbilityCriterion.Conditions.createMinecartWalljump(), AdvancementRewards.Builder.experience(400));
        AdvancementEntry obtainFullSteelArmor = addGoal(hammerCraftMetalPlate, "obtain_full_steel_armor", KlaxonItems.STEEL_CHESTPLATE, InventoryChangedCriterion.Conditions.items(KlaxonItems.STEEL_HELMET, KlaxonItems.STEEL_CHESTPLATE, KlaxonItems.STEEL_LEGGINGS, KlaxonItems.STEEL_BOOTS));
        AdvancementEntry makeshiftItemFullRepair = addTask(hammerCraftMetalPlate, "makeshift_item_full_repair", Blocks.ANVIL, ItemRepairCriterion.Conditions.createFullRepairFromTag(KlaxonItemTags.MAKESHIFT_CRAFTED_EQUIPMENT));
    }
}
