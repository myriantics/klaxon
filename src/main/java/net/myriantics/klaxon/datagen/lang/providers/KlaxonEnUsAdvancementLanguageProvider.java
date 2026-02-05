package net.myriantics.klaxon.datagen.lang.providers;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.advancement.providers.KlaxonStageOneAdvancementProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;

public final class KlaxonEnUsAdvancementLanguageProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsAdvancementLanguageProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        generateStageOneAdvancementTranslations();
    }

    private void generateStageOneAdvancementTranslations() {
        // level 0
        addStageOneAdvancement(
                "root",
                "KLAXON - Initialization",
                "An explosive introduction"
        );

        // level 1
        addStageOneAdvancement(
                KlaxonStageOneAdvancementProvider.WATCH_BLAST_PROCESSOR_CRAFT,
                "Divide!",
                "Watch a Blast Processor craft something"
        );
        addStageOneAdvancement(
                KlaxonStageOneAdvancementProvider.OBTAIN_HALLNOX_POD,
                "Uses 96 Batteries",
                "Obtain a Hallnox Pod from a Sniffer"
        );

        // level 2
        addStageOneAdvancement(
                KlaxonStageOneAdvancementProvider.WATCH_NETHER_REACTOR_CORE_ACTIVATE,
                "THE NETHER.",
                "Detonate an explosive inside of a Nether Reactor Core"
        );
        addStageOneAdvancement(
                KlaxonStageOneAdvancementProvider.USE_HAMMER_TO_MAKE_METAL_PLATE,
                "KADOOSH!",
                "Hammer out a Metal Plate"
        );
        addStageOneAdvancement(
                KlaxonStageOneAdvancementProvider.OBTAIN_ANY_RUBBER_GLOB,
                "Biggering",
                "Obtain Rubber by blast processing a valid log"
        );
        addStageOneAdvancement(
                KlaxonStageOneAdvancementProvider.HAMMER_WALLJUMP_NORMAL,
                "Schmovin and Groovin",
                "Perform a Hammer Walljump by attacking the ground while jumping"
        );
        addStageOneAdvancement(
                KlaxonStageOneAdvancementProvider.MAKESHIFT_ITEM_FULL_REPAIR,
                "Tip-Top Condition",
                "Fully repair an item made with Makeshift Crafting"
        );

        // level 3
        addStageOneAdvancement(
                KlaxonStageOneAdvancementProvider.USE_CABLE_SHEARS_TO_MAKE_METAL_WIRE,
                "Wire So Shearious",
                "Use Cable Shears to convert a Metal Plate into Wire"
        );
        addStageOneAdvancement(
                KlaxonStageOneAdvancementProvider.HAMMER_WALLJUMP_BOOSTED,
                "Unstoppable Force",
                "Enhance your Walljump with Strength"
        );
        addStageOneAdvancement(
                KlaxonStageOneAdvancementProvider.HAMMER_WALLJUMP_MINECART,
                "Close Enough",
                "Propel a Minecart using the Steel Hammer"
        );
        addStageOneAdvancement(
                KlaxonStageOneAdvancementProvider.OBTAIN_STEEL_CLEAVER,
                "Now THIS is a Knife!",
                "Obtain a Steel Cleaver"
        );
        addStageOneAdvancement(
                KlaxonStageOneAdvancementProvider.OBTAIN_STEEL_WRENCH,
                "Problem Solver",
                "Obtain a Steel Wrench"
        );
        addStageOneAdvancement(
                KlaxonStageOneAdvancementProvider.OBTAIN_ANY_STEEL_ARMOR,
                "Safety First",
                "Obtain any Steel Armor"
        );

        // level 4
        addStageOneAdvancement(
                KlaxonStageOneAdvancementProvider.ROTATE_RAIL_WITH_WRENCH,
                "All The Live Long Day",
                "Rotate any Rail with the Steel Wrench"
        );
        addStageOneAdvancement(
                KlaxonStageOneAdvancementProvider.OBTAIN_GRAPPLE_WINCH,
                "Boring Mining Tool",
                "Obtain the Grapple Winch"
        );

        // level 5
        addStageOneAdvancement(
                KlaxonStageOneAdvancementProvider.GRAPPLE_WINCH_DE_ANCHOR_GRAPPLE_CLAW,
                "Immovable Object",
                "De-anchor a Grapple Claw by retracting it while wearing Heavy Equipment"
        );
        addStageOneAdvancement(
                KlaxonStageOneAdvancementProvider.GRAPPLE_WINCH_INTENTIONALLY_DISCONNECT_CABLE,
                "Spidey",
                "Disconnect from an anchored Grapple Claw midair to maintain speed"
        );
        addStageOneAdvancement(
                KlaxonStageOneAdvancementProvider.GRAPPLE_WINCH_GRAPPLE_ENDER_DRAGON,
                "Dragonhearted",
                "Grapple onto an Ender Dragon and take flight"
        );

        // level 6
        addStageOneAdvancement(
                KlaxonStageOneAdvancementProvider.GRAPPLE_WINCH_VEINMINE_GLOWSTONE,
                "Smash and Grab",
                "Veinmine Glowstone on your Grapple Claw's return journey"
        );
    }
}
