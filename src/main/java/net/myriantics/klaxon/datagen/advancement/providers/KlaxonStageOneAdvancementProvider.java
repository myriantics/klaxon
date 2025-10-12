package net.myriantics.klaxon.datagen.advancement.providers;

import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.myriantics.klaxon.advancement.criterion.*;
import net.myriantics.klaxon.datagen.advancement.KlaxonAdvancementSubProvider;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalItemTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.function.Consumer;

public class KlaxonStageOneAdvancementProvider extends KlaxonAdvancementSubProvider {
    public KlaxonStageOneAdvancementProvider(Consumer<AdvancementEntry> consumer) {
        super(consumer, "stage_one");
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
        AdvancementEntry obtainHallnoxPod = addTask(root, "obtain_hallnox_pod", KlaxonItems.HALLNOX_POD, InventoryChangedCriterion.Conditions.items(KlaxonItems.HALLNOX_POD));

        AdvancementEntry watchNetherReactorCoreActivate = addTask(obtainHallnoxPod, "watch_nether_reactor_core_activate", KlaxonItems.NETHER_REACTOR_CORE, BlockActivationCriterion.Conditions.create(KlaxonBlockTags.NETHER_REACTOR_CORES));
        AdvancementEntry hammerCraftMetalPlate = addTask(watchBlastProcessorCraft, "use_hammer_to_make_metal_plate", KlaxonItems.CRUDE_STEEL_PLATE, ToolUsageRecipeCraftCriterion.Conditions.createHammering(Ingredient.fromTag(KlaxonConventionalItemTags.PLATES)));
        AdvancementEntry obtainAnyRubberGlob = addTask(watchBlastProcessorCraft, "obtain_any_rubber_glob", KlaxonItems.RUBBER_GLOB, InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(KlaxonItemTags.MOLTEN_INCLUSIVE_RUBBER_GLOBS)));
        AdvancementEntry normalHammerWalljump = addTask(watchBlastProcessorCraft, "hammer_walljump_normal", KlaxonItems.STEEL_HAMMER, WalljumpAbilityCriterion.Conditions.createNormalWalljump());
        AdvancementEntry makeshiftItemFullRepair = addTask(watchBlastProcessorCraft, "makeshift_item_full_repair", Items.ANVIL, ItemRepairCriterion.Conditions.createFullRepairFromTag(KlaxonItemTags.MAKESHIFT_CRAFTED_EQUIPMENT));

        AdvancementEntry boostedHammerWalljump = addGoal(normalHammerWalljump, "hammer_walljump_boosted", Items.BLAZE_POWDER,  WalljumpAbilityCriterion.Conditions.createStrengthWalljump());
        AdvancementEntry minecartHammerWalljump = addGoal(normalHammerWalljump, "hammer_walljump_minecart", Items.CAULDRON, WalljumpAbilityCriterion.Conditions.createMinecartWalljump());
        AdvancementEntry cableShearCraftMetalWire = addTask(hammerCraftMetalPlate, "use_cable_shears_to_make_metal_wire", KlaxonItems.STEEL_CABLE_SHEARS, ToolUsageRecipeCraftCriterion.Conditions.createWirecutting(Ingredient.fromTag(KlaxonConventionalItemTags.WIRES)));
        AdvancementEntry obtainSteelCleaver = addTask(hammerCraftMetalPlate, "obtain_steel_cleaver", KlaxonItems.STEEL_CLEAVER, InventoryChangedCriterion.Conditions.items(KlaxonItems.STEEL_CLEAVER));
        AdvancementEntry obtainSteelWrench = addTask(hammerCraftMetalPlate, "obtain_steel_wrench", KlaxonItems.STEEL_WRENCH, InventoryChangedCriterion.Conditions.items(KlaxonItems.STEEL_WRENCH));
        AdvancementEntry obtainAnySteelArmor = addGoal(hammerCraftMetalPlate, "obtain_any_steel_armor", KlaxonItems.STEEL_CHESTPLATE, InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(KlaxonItemTags.STEEL_ARMOR)));

        AdvancementEntry editRailWithSteelWrench = addTask(obtainSteelWrench, "rotate_rail_with_wrench", Items.RAIL, WrenchUsageCriterion.Conditions.createRotation(BlockTags.RAILS));
        ItemStack loadedGrappleWinch = new ItemStack(KlaxonItems.GRAPPLE_WINCH);
        loadedGrappleWinch.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(new ItemStack(KlaxonItems.STEEL_GRAPPLE_CLAW)));
        AdvancementEntry obtainGrappleWinch = addTask(cableShearCraftMetalWire, "obtain_grapple_winch", loadedGrappleWinch, InventoryChangedCriterion.Conditions.items(KlaxonItems.GRAPPLE_WINCH));

        AdvancementEntry grappleWinchDeAnchorGrappleClaw = addTask(obtainGrappleWinch, "grapple_winch_de_anchor_grapple_claw", KlaxonItems.STEEL_GRAPPLE_CLAW, OneOffCriterion.Conditions.createDeAnchorGrappleClaw());
        AdvancementEntry grappleWinchIntentionallyDisconnectCable = addTask(obtainGrappleWinch, "grapple_winch_intentionally_disconnect_cable", KlaxonItems.STEEL_WIRE, OneOffCriterion.Conditions.createGrappleWinchIntentionallyDisconnectCable());

        AdvancementEntry grappleWinchVeinMine = addChallenge(grappleWinchDeAnchorGrappleClaw, "grapple_winch_vein_mine_glowstone", Items.GLOWSTONE, GrappleWinchVeinMineCriterion.Conditions.create(Blocks.GLOWSTONE));
    }
}
