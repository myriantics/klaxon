package net.myriantics.klaxon.datagen.advancement.providers;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.advancement.criterion.*;
import net.myriantics.klaxon.advancement.criterion.grapple_winch.EntityGrappleCriterion;
import net.myriantics.klaxon.advancement.criterion.grapple_winch.GrappleWinchCableDisconnectCriterion;
import net.myriantics.klaxon.advancement.criterion.grapple_winch.GrappleWinchVeinMineCriterion;
import net.myriantics.klaxon.datagen.advancement.KlaxonAdvancementSubProvider;
import net.myriantics.klaxon.loot.predicates.LootItemEntityOwnerCondition;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.muffling.MufflerActionType;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalItemTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.Optional;
import java.util.function.Consumer;

public class KlaxonStageOneAdvancementProvider extends KlaxonAdvancementSubProvider {
    public KlaxonStageOneAdvancementProvider(Consumer<AdvancementHolder> consumer) {
        super(consumer, STAGE);
    }

    public static final String STAGE = "stage_one";

    // 1
    public static final String WATCH_BLAST_PROCESSOR_CRAFT = "watch_blast_processor_craft";
    public static final String OBTAIN_HALLNOX_POD = "obtain_hallnox_pod";

    // 2
    public static final String HARVEST_DRAGONS_BREATH_FROM_OMINOUS_DEEPSLATE_BLAST_PROCESSOR = "harvest_dragons_breath_from_ominous_deepslate_blast_processor";
    public static final String WATCH_NETHER_REACTOR_CORE_ACTIVATE = "watch_nether_reactor_core_activate";
    public static final String USE_HAMMER_TO_MAKE_METAL_PLATE = "use_hammer_to_make_metal_plate";
    public static final String OBTAIN_ANY_RUBBER_GLOB = "obtain_any_rubber_glob";
    public static final String HAMMER_WALLJUMP_NORMAL = "hammer_walljump_normal";
    public static final String MAKESHIFT_ITEM_FULL_REPAIR = "makeshift_item_full_repair";

    // 3
    public static final String OBTAIN_MODULAR_EXPLOSIVE = "craft_modular_explosive";
    public static final String APPLY_ANY_MUFFLER = "apply_any_muffler";
    public static final String HAMMER_WALLJUMP_BOOSTED = "hammer_walljump_boosted";
    public static final String HAMMER_WALLJUMP_MINECART = "hammer_walljump_minecart";
    public static final String LIGHT_MULTIPLE_FIRES_WITH_ONE_LIGHTER_USE = "light_multiple_fires_with_one_lighter_use";
    public static final String USE_CABLE_SHEARS_TO_MAKE_METAL_WIRE = "use_cable_shears_to_make_metal_wire";
    public static final String OBTAIN_STEEL_CLEAVER = "obtain_steel_cleaver";
    public static final String PERFORM_ANY_WRENCH_ACTION = "perform_any_wrench_action";
    public static final String OBTAIN_ANY_STEEL_ARMOR = "obtain_any_steel_armor";

    // 4
    public static final String EXTEND_MODULAR_EXPLOSIVE_FUSE = "extend_modular_explosive_fuse";
    public static final String REMOVE_ANY_MUFFLER = "remove_any_muffler";
    public static final String APPLY_EPIC_MUFFLER = "apply_epic_muffler";
    public static final String ROTATE_RAIL_WITH_WRENCH = "rotate_rail_with_wrench";
    public static final String OBTAIN_GRAPPLE_WINCH = "obtain_grapple_winch";

    // 5
    public static final String GRAPPLE_WINCH_GRAPPLE_ENDER_DRAGON = "grapple_ender_dragon";
    public static final String GRAPPLE_WINCH_DE_ANCHOR_GRAPPLE_CLAW = "grapple_winch_de_anchor_grapple_claw";
    public static final String GRAPPLE_WINCH_INTENTIONALLY_DISCONNECT_CABLE = "grapple_winch_intentionally_disconnect_cable";
    public static final String GRAPPLE_WINCH_MOUNT_LEVITATION_BUG = "grapple_winch_mount_levitation_bug";

    // 6
    public static final String GRAPPLE_WINCH_VEINMINE_GLOWSTONE = "grapple_winch_veinmine_glowstone";

    @Override
    public AdvancementHolder generateAdvancements() {
        AdvancementHolder root = generateRoot();
        generateAdvancements(root);
        return root;
    }

    private AdvancementHolder generateRoot() {
        return addRootAdvancement(KlaxonItems.DEEPSLATE_BLAST_PROCESSOR.value(), AdvancementType.TASK, PlayerTrigger.TriggerInstance.tick());
    }

    private void generateAdvancements(AdvancementHolder root) {
        // level 1
        AdvancementHolder watchBlastProcessorCraft = addTask(root, WATCH_BLAST_PROCESSOR_CRAFT, KlaxonItems.FRACTURED_RAW_IRON.value(), BlockActivationCriterion.Conditions.create(KlaxonBlockTags.BLAST_PROCESSORS));
        AdvancementHolder obtainHallnoxPod = addTask(root, OBTAIN_HALLNOX_POD, KlaxonItems.HALLNOX_POD.value(), InventoryChangeTrigger.TriggerInstance.hasItems(KlaxonItems.HALLNOX_POD.value()));

        ItemStack enchantedDragonsBreath = new ItemStack(Items.DRAGON_BREATH);
        enchantedDragonsBreath.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        // level 2
        AdvancementHolder harvestDragonsBreathFromOminousDeepslateBlastProcessor = addHiddenTask(watchBlastProcessorCraft, HARVEST_DRAGONS_BREATH_FROM_OMINOUS_DEEPSLATE_BLAST_PROCESSOR, enchantedDragonsBreath, PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(ItemPredicate.Builder.item().of(Items.GLASS_BOTTLE), Optional.of(ContextAwarePredicate.create(new LootItemEntityOwnerCondition(Optional.of(EntityPredicate.Builder.entity().of(KlaxonEntityTypes.OMINOUS_DEEPSLATE_BLAST_PROCESSOR.value()).build()), LootContext.EntityTarget.THIS)))));
        AdvancementHolder watchNetherReactorCoreActivate = addTask(obtainHallnoxPod, WATCH_NETHER_REACTOR_CORE_ACTIVATE, KlaxonItems.NETHER_REACTOR_CORE.value(), BlockActivationCriterion.Conditions.create(KlaxonBlockTags.NETHER_REACTOR_CORES));
        AdvancementHolder hammerCraftMetalPlate = addTask(watchBlastProcessorCraft, USE_HAMMER_TO_MAKE_METAL_PLATE, KlaxonItems.CRUDE_STEEL_PLATE.value(), ToolUsageRecipeCraftCriterion.Conditions.createHammering(Ingredient.of(KlaxonConventionalItemTags.PLATES)));
        AdvancementHolder obtainAnyRubberGlob = addTask(watchBlastProcessorCraft, OBTAIN_ANY_RUBBER_GLOB, KlaxonItems.RUBBER_GLOB.value(), InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(KlaxonItems.RUBBER_GLOB.value())));
        AdvancementHolder normalHammerWalljump = addTask(watchBlastProcessorCraft, HAMMER_WALLJUMP_NORMAL, KlaxonItems.STEEL_HAMMER.value(), WalljumpAbilityCriterion.Conditions.createNormalWalljump());
        AdvancementHolder makeshiftItemFullRepair = addTask(watchBlastProcessorCraft, MAKESHIFT_ITEM_FULL_REPAIR, Items.ANVIL, ItemRepairCriterion.Conditions.createFullRepairFromTag(KlaxonItemTags.MAKESHIFT_CRAFTED_EQUIPMENT));

        // level 3
        AdvancementHolder obtainModularExplosive = addTask(hammerCraftMetalPlate, OBTAIN_MODULAR_EXPLOSIVE, KlaxonItems.MODULAR_EXPLOSIVE_BLOCK.value(), InventoryChangeTrigger.TriggerInstance.hasItems(KlaxonItems.MODULAR_EXPLOSIVE_BLOCK.value()));
        AdvancementHolder applyAnyMuffler = addTask(hammerCraftMetalPlate, APPLY_ANY_MUFFLER, Items.LEATHER, MufflerInteractionCriterion.Conditions.create(MufflerActionType.APPLY, null));
        AdvancementHolder boostedHammerWalljump = addGoal(normalHammerWalljump, HAMMER_WALLJUMP_BOOSTED, Items.BLAZE_POWDER,  WalljumpAbilityCriterion.Conditions.createStrengthWalljump());
        AdvancementHolder minecartHammerWalljump = addGoal(normalHammerWalljump, HAMMER_WALLJUMP_MINECART, Items.CAULDRON, WalljumpAbilityCriterion.Conditions.createMinecartWalljump());
        ItemStack openLighterStack = new ItemStack(KlaxonItems.STEEL_LIGHTER);
        openLighterStack.set(KlaxonDataComponentTypes.USE_ACTION_MODEL_PREDICATE_OVERRIDE.value(), 0.67f);
        AdvancementHolder erectFirewallWithLighter = addGoal(hammerCraftMetalPlate, LIGHT_MULTIPLE_FIRES_WITH_ONE_LIGHTER_USE, openLighterStack, OneOffCriterion.Conditions.createErectFirewall());
        AdvancementHolder cableShearCraftMetalWire = addTask(hammerCraftMetalPlate, USE_CABLE_SHEARS_TO_MAKE_METAL_WIRE, KlaxonItems.STEEL_CABLE_SHEARS.value(), ToolUsageRecipeCraftCriterion.Conditions.createWirecutting(Ingredient.of(KlaxonConventionalItemTags.WIRES)));
        AdvancementHolder obtainSteelCleaver = addTask(hammerCraftMetalPlate, OBTAIN_STEEL_CLEAVER, KlaxonItems.STEEL_CLEAVER.value(), InventoryChangeTrigger.TriggerInstance.hasItems(KlaxonItems.STEEL_CLEAVER.value()));
        AdvancementHolder performAnyWrenchAction = addTask(hammerCraftMetalPlate, PERFORM_ANY_WRENCH_ACTION, KlaxonItems.STEEL_WRENCH.value(), WrenchUsageCriterion.Conditions.createWildcard(null));
        AdvancementHolder obtainAnySteelArmor = addGoal(hammerCraftMetalPlate, OBTAIN_ANY_STEEL_ARMOR, KlaxonItems.STEEL_CHESTPLATE.value(), InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(KlaxonItemTags.STEEL_ARMOR)));

        // level 4
        AdvancementHolder extendModularExplosiveFuse = addTask(obtainModularExplosive, EXTEND_MODULAR_EXPLOSIVE_FUSE, Items.STRING, RecipeCraftedTrigger.TriggerInstance.craftedItem(KlaxonCommon.locate("crafting/fuse_extension/modular_explosive_block")));
        AdvancementHolder removeAnyMuffler = addTask(applyAnyMuffler, REMOVE_ANY_MUFFLER, Items.SHEARS, MufflerInteractionCriterion.Conditions.create(MufflerActionType.REMOVE, null));
        AdvancementHolder applyEpicMuffler = addHiddenChallenge(applyAnyMuffler, APPLY_EPIC_MUFFLER, Items.ELYTRA, MufflerInteractionCriterion.Conditions.create(MufflerActionType.APPLY, ItemPredicate.Builder.item().of(KlaxonItemTags.EPIC_MUFFLERS).build()));
        AdvancementHolder editRailWithSteelWrench = addTask(performAnyWrenchAction, ROTATE_RAIL_WITH_WRENCH, Items.RAIL, WrenchUsageCriterion.Conditions.createRotation(BlockTags.RAILS));
        ItemStack loadedGrappleWinch = new ItemStack(KlaxonItems.GRAPPLE_WINCH);
        loadedGrappleWinch.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(new ItemStack(KlaxonItems.STEEL_GRAPPLE_CLAW)));
        AdvancementHolder obtainGrappleWinch = addTask(cableShearCraftMetalWire, OBTAIN_GRAPPLE_WINCH, loadedGrappleWinch, InventoryChangeTrigger.TriggerInstance.hasItems(KlaxonItems.GRAPPLE_WINCH.value()));

        // level 5
        AdvancementHolder grappleWinchDeAnchorGrappleClaw = addTask(obtainGrappleWinch, GRAPPLE_WINCH_DE_ANCHOR_GRAPPLE_CLAW, KlaxonItems.STEEL_GRAPPLE_CLAW.value(), OneOffCriterion.Conditions.createDeAnchorGrappleClaw());
        AdvancementHolder grappleWinchIntentionallyDisconnectCable = addTask(obtainGrappleWinch, GRAPPLE_WINCH_INTENTIONALLY_DISCONNECT_CABLE, KlaxonItems.STEEL_WIRE.value(), GrappleWinchCableDisconnectCriterion.Conditions.createPlayer(CableDetachmentReason.INVALID_HELD_ITEMS, EntityPredicate.Builder.entity().of(EntityType.PLAYER).moving(MovementPredicate.speed(MinMaxBounds.Doubles.atLeast(5f/20))).build()));
        ItemStack enchantedGrappleClawStack = new ItemStack(KlaxonItems.STEEL_GRAPPLE_CLAW);
        enchantedGrappleClawStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        AdvancementHolder grappleWinchGrappleEnderDragon = addHiddenChallenge(obtainGrappleWinch, GRAPPLE_WINCH_GRAPPLE_ENDER_DRAGON, enchantedGrappleClawStack, EntityGrappleCriterion.Conditions.create(EntityType.ENDER_DRAGON));
        AdvancementHolder grappleWinchLevitationBug = addHiddenChallenge(obtainGrappleWinch, GRAPPLE_WINCH_MOUNT_LEVITATION_BUG, Items.ALLIUM, OneOffCriterion.Conditions.createGrappleWinchLevitationBug());

        // level 6
        AdvancementHolder grappleWinchVeinMine = addGoal(grappleWinchDeAnchorGrappleClaw, GRAPPLE_WINCH_VEINMINE_GLOWSTONE, Items.GLOWSTONE, GrappleWinchVeinMineCriterion.Conditions.create(Blocks.GLOWSTONE));
    }
}
