package net.myriantics.klaxon.datagen.advancement.providers;

import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.EntityType;
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
        super(consumer, STAGE);
    }

    public static final String STAGE = "stage_one";

    public static final String WATCH_BLAST_PROCESSOR_CRAFT = "watch_blast_processor_craft";
    public static final String OBTAIN_HALLNOX_POD = "obtain_hallnox_pod";

    public static final String WATCH_NETHER_REACTOR_CORE_ACTIVATE = "watch_nether_reactor_core_activate";
    public static final String USE_HAMMER_TO_MAKE_METAL_PLATE = "use_hammer_to_make_metal_plate";
    public static final String OBTAIN_ANY_RUBBER_GLOB = "obtain_any_rubber_glob";
    public static final String HAMMER_WALLJUMP_NORMAL = "hammer_walljump_normal";
    public static final String MAKESHIFT_ITEM_FULL_REPAIR = "makeshift_item_full_repair";

    public static final String HAMMER_WALLJUMP_BOOSTED = "hammer_walljump_boosted";
    public static final String HAMMER_WALLJUMP_MINECART = "hammer_walljump_minecart";
    public static final String USE_CABLE_SHEARS_TO_MAKE_METAL_WIRE = "use_cable_shears_to_make_metal_wire";
    public static final String OBTAIN_STEEL_CLEAVER = "obtain_steel_cleaver";
    public static final String OBTAIN_STEEL_WRENCH = "obtain_steel_wrench";
    public static final String OBTAIN_ANY_STEEL_ARMOR = "obtain_any_steel_armor";

    public static final String ROTATE_RAIL_WITH_WRENCH = "rotate_rail_with_wrench";
    public static final String OBTAIN_GRAPPLE_WINCH = "obtain_grapple_winch";

    public static final String GRAPPLE_WINCH_GRAPPLE_ENDER_DRAGON = "grapple_ender_dragon";
    public static final String GRAPPLE_WINCH_DE_ANCHOR_GRAPPLE_CLAW = "grapple_winch_de_anchor_grapple_claw";
    public static final String GRAPPLE_WINCH_INTENTIONALLY_DISCONNECT_CABLE = "grapple_winch_intentionally_disconnect_cable";

    public static final String GRAPPLE_WINCH_VEINMINE_GLOWSTONE = "grapple_winch_veinmine_glowstone";

    @Override
    public AdvancementEntry generateAdvancements() {
        AdvancementEntry root = generateRoot();
        generateAdvancements(root);
        return root;
    }

    private AdvancementEntry generateRoot() {
        return addRootAdvancement(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, AdvancementFrame.TASK, TickCriterion.Conditions.createTick());
    }

    private void generateAdvancements(AdvancementEntry root) {
        AdvancementEntry watchBlastProcessorCraft = addTask(root, WATCH_BLAST_PROCESSOR_CRAFT, KlaxonItems.FRACTURED_RAW_IRON, BlockActivationCriterion.Conditions.create(KlaxonBlockTags.BLAST_PROCESSORS));
        AdvancementEntry obtainHallnoxPod = addTask(root, OBTAIN_HALLNOX_POD, KlaxonItems.HALLNOX_POD, InventoryChangedCriterion.Conditions.items(KlaxonItems.HALLNOX_POD));

        AdvancementEntry watchNetherReactorCoreActivate = addTask(obtainHallnoxPod, WATCH_NETHER_REACTOR_CORE_ACTIVATE, KlaxonItems.NETHER_REACTOR_CORE, BlockActivationCriterion.Conditions.create(KlaxonBlockTags.NETHER_REACTOR_CORES));
        AdvancementEntry hammerCraftMetalPlate = addTask(watchBlastProcessorCraft, USE_HAMMER_TO_MAKE_METAL_PLATE, KlaxonItems.CRUDE_STEEL_PLATE, ToolUsageRecipeCraftCriterion.Conditions.createHammering(Ingredient.fromTag(KlaxonConventionalItemTags.PLATES)));
        AdvancementEntry obtainAnyRubberGlob = addTask(watchBlastProcessorCraft, OBTAIN_ANY_RUBBER_GLOB, KlaxonItems.RUBBER_GLOB, InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(KlaxonItemTags.MOLTEN_INCLUSIVE_RUBBER_GLOBS)));
        AdvancementEntry normalHammerWalljump = addTask(watchBlastProcessorCraft, HAMMER_WALLJUMP_NORMAL, KlaxonItems.STEEL_HAMMER, WalljumpAbilityCriterion.Conditions.createNormalWalljump());
        AdvancementEntry makeshiftItemFullRepair = addTask(watchBlastProcessorCraft, MAKESHIFT_ITEM_FULL_REPAIR, Items.ANVIL, ItemRepairCriterion.Conditions.createFullRepairFromTag(KlaxonItemTags.MAKESHIFT_CRAFTED_EQUIPMENT));

        AdvancementEntry boostedHammerWalljump = addGoal(normalHammerWalljump, HAMMER_WALLJUMP_BOOSTED, Items.BLAZE_POWDER,  WalljumpAbilityCriterion.Conditions.createStrengthWalljump());
        AdvancementEntry minecartHammerWalljump = addGoal(normalHammerWalljump, HAMMER_WALLJUMP_MINECART, Items.CAULDRON, WalljumpAbilityCriterion.Conditions.createMinecartWalljump());
        AdvancementEntry cableShearCraftMetalWire = addTask(hammerCraftMetalPlate, USE_CABLE_SHEARS_TO_MAKE_METAL_WIRE, KlaxonItems.STEEL_CABLE_SHEARS, ToolUsageRecipeCraftCriterion.Conditions.createWirecutting(Ingredient.fromTag(KlaxonConventionalItemTags.WIRES)));
        AdvancementEntry obtainSteelCleaver = addTask(hammerCraftMetalPlate, OBTAIN_STEEL_CLEAVER, KlaxonItems.STEEL_CLEAVER, InventoryChangedCriterion.Conditions.items(KlaxonItems.STEEL_CLEAVER));
        AdvancementEntry obtainSteelWrench = addTask(hammerCraftMetalPlate, OBTAIN_STEEL_WRENCH, KlaxonItems.STEEL_WRENCH, InventoryChangedCriterion.Conditions.items(KlaxonItems.STEEL_WRENCH));
        AdvancementEntry obtainAnySteelArmor = addGoal(hammerCraftMetalPlate, OBTAIN_ANY_STEEL_ARMOR, KlaxonItems.STEEL_CHESTPLATE, InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(KlaxonItemTags.STEEL_ARMOR)));

        AdvancementEntry editRailWithSteelWrench = addTask(obtainSteelWrench, ROTATE_RAIL_WITH_WRENCH, Items.RAIL, WrenchUsageCriterion.Conditions.createRotation(BlockTags.RAILS));
        ItemStack loadedGrappleWinch = new ItemStack(KlaxonItems.GRAPPLE_WINCH);
        loadedGrappleWinch.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(new ItemStack(KlaxonItems.STEEL_GRAPPLE_CLAW)));
        AdvancementEntry obtainGrappleWinch = addTask(cableShearCraftMetalWire, OBTAIN_GRAPPLE_WINCH, loadedGrappleWinch, InventoryChangedCriterion.Conditions.items(KlaxonItems.GRAPPLE_WINCH));

        AdvancementEntry grappleWinchDeAnchorGrappleClaw = addTask(obtainGrappleWinch, GRAPPLE_WINCH_DE_ANCHOR_GRAPPLE_CLAW, KlaxonItems.STEEL_GRAPPLE_CLAW, OneOffCriterion.Conditions.createDeAnchorGrappleClaw());
        AdvancementEntry grappleWinchIntentionallyDisconnectCable = addTask(obtainGrappleWinch, GRAPPLE_WINCH_INTENTIONALLY_DISCONNECT_CABLE, KlaxonItems.STEEL_WIRE, OneOffCriterion.Conditions.createGrappleWinchIntentionallyDisconnectCable());
        ItemStack enchantedGrappleClawStack = new ItemStack(KlaxonItems.STEEL_GRAPPLE_CLAW);
        enchantedGrappleClawStack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        AdvancementEntry grappleWinchGrappleEnderDragon = addHiddenChallenge(obtainGrappleWinch, GRAPPLE_WINCH_GRAPPLE_ENDER_DRAGON, enchantedGrappleClawStack, EntityGrappleCriterion.Conditions.create(EntityType.ENDER_DRAGON));

        AdvancementEntry grappleWinchVeinMine = addChallenge(grappleWinchDeAnchorGrappleClaw, GRAPPLE_WINCH_VEINMINE_GLOWSTONE, Items.GLOWSTONE, GrappleWinchVeinMineCriterion.Conditions.create(Blocks.GLOWSTONE));
    }
}
