package net.myriantics.klaxon.datagen.lang.providers.tag;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

public final class KlaxonEnUsBlockTagLanguageProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsBlockTagLanguageProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        generateMiningTagTranslations();
        generateBehaviorTagTranslations();
        generateNetherReactionTagTranslations();
        generateManualItemApplicationTagTranslations();
        generateCategoryTagTranslations();
    }

    private void generateMiningTagTranslations() {
        // mining and instabreaking tags
        addTag(KlaxonBlockTags.HAMMER_MINEABLE, "Hammer Mineable");
        addTag(KlaxonBlockTags.HAMMER_INSTABREAKABLE, "Hammer Instabreakable");
        addTag(KlaxonBlockTags.CLEAVER_MINEABLE, "Cleaver Mineable");
        addTag(KlaxonBlockTags.CLEAVER_INSTABREAKABLE, "Cleaver Instabreakable");
        addTag(KlaxonBlockTags.WRENCH_MINEABLE, "Wrench Mineable");
        addTag(KlaxonBlockTags.WRENCH_INSTABREAKABLE, "Wrench Instabreakable");
        addTag(KlaxonBlockTags.CABLE_SHEARS_MINEABLE, "Cable Shears Mineable");
        addTag(KlaxonBlockTags.CABLE_SHEARS_INSTABREAKABLE, "Cable Shears Instabreakable");

        // breaking power tags
        addTag(KlaxonBlockTags.NEEDS_STEEL_TOOL, "Needs Steel Tool");
        addTag(KlaxonBlockTags.INCORRECT_FOR_STEEL_TOOL, "Incorrect For Steel Tool");

        // grapple winch / claw
        addTag(KlaxonBlockTags.GRAPPLE_CLAW_BREAKABLE, "Grapple Claw Breakable");
        addTag(KlaxonBlockTags.GRAPPLE_CLAW_VEINMINEABLE, "Grapple Claw Veinmineable");
    }

    private void generateBehaviorTagTranslations() {
        // wrench
        addTag(KlaxonBlockTags.WRENCH_PICKUP_ALLOWLIST, "Wrench Pickup Allowlist");
        addTag(KlaxonBlockTags.WRENCH_PICKUP_DENYLIST, "Wrench Pickup Denylist");
        addTag(KlaxonBlockTags.WRENCH_ROTATION_ALLOWLIST, "Wrench Rotation Allowlist");
        addTag(KlaxonBlockTags.WRENCH_ROTATION_DENYLIST, "Wrench Rotation Denylist");

        // ferromagnetism
        addTag(KlaxonBlockTags.FERROMAGNETIC_BLOCKS, "Ferromagnetic Blocks");

        // temperature
        addTag(KlaxonBlockTags.COLD_BLOCKS, "Cold Blocks");

        // misc
        addTag(KlaxonBlockTags.HALLNOX_POD_NATURAL_GROWTH_INHIBITING, "Hallnox Pod Natural Growth Inhibiting");
    }

    private void generateNetherReactionTagTranslations() {
        addTag(KlaxonBlockTags.NETHER_REACTION_IMMUNE, "Nether Reaction Immune");

        // other misc stuff
        addTag(KlaxonBlockTags.STEEL_CASING_CONVERTIBLE, "Nether Reaction Steel Casing Convertible");
        addTag(KlaxonBlockTags.AIR_CONVERTIBLE, "Nether Reaction Air Convertible");
        addTag(KlaxonBlockTags.FIRE_CONVERTIBLE, "Nether Reaction Fire Convertible");

        // misc stuff
        addTag(KlaxonBlockTags.SHROOMLIGHT_CONVERTIBLE, "Nether Reaction Shroomlight Convertible");
        addTag(KlaxonBlockTags.BONE_BLOCK_CONVERTIBLE, "Nether Reaction Bone Block Convertible");
        addTag(KlaxonBlockTags.WITHER_SKELETON_SKULL_CONVERTIBLE, "Nether Reaction Wither Skeleton Skull Convertible");
        addTag(KlaxonBlockTags.WITHER_SKELETON_WALL_SKULL_CONVERTIBLE, "Nether Reaction Wither Skeleton Wall Skull Convertible");

        // netherrack & ores
        addTag(KlaxonBlockTags.NETHER_QUARTZ_ORE_CONVERTIBLE, "Nether Reaction Nether Quartz Ore Convertible");
        addTag(KlaxonBlockTags.NETHER_GOLD_ORE_CONVERTIBLE, "Nether Reaction Nether Gold Ore Convertible");
        addTag(KlaxonBlockTags.NETHERRACK_CONVERTIBLE, "Nether Reaction Netherrack Convertible");

        // blackstone
        addTag(KlaxonBlockTags.BLACKSTONE_CONVERTIBLE, "Nether Reaction Blackstone Convertible");
        addTag(KlaxonBlockTags.BLACKSTONE_STAIRS_CONVERTIBLE, "Nether Reaction Blackstone Stairs Convertible");
        addTag(KlaxonBlockTags.BLACKSTONE_SLAB_CONVERTIBLE, "Nether Reaction Blackstone Slab Convertible");
        addTag(KlaxonBlockTags.BLACKSTONE_WALL_CONVERTIBLE, "Nether Reaction Blackstone Wall Convertible");

        // basalt
        addTag(KlaxonBlockTags.BASALT_CONVERTIBLE, "Nether Reaction Basalt Convertible");

        // soul sand / soil and stuff
        addTag(KlaxonBlockTags.SOUL_SAND_CONVERTIBLE, "Nether Reaction Soul Sand Convertible");
        addTag(KlaxonBlockTags.SOUL_SOIL_CONVERTIBLE, "Nether Reaction Soul Soil Convertible");
        addTag(KlaxonBlockTags.SOUL_TORCH_CONVERTIBLE, "Nether Reaction Soul Torch Convertible");
        addTag(KlaxonBlockTags.SOUL_WALL_TORCH_CONVERTIBLE, "Nether Reaction Soul Wall Torch Convertible");
        addTag(KlaxonBlockTags.SOUL_LANTERN_CONVERTIBLE, "Nether Reaction Soul Lantern Convertible");
        addTag(KlaxonBlockTags.SOUL_CAMPFIRE_CONVERTIBLE, "Nether Reaction Soul Campfire Convertible");

        // misc crimson crap
        addTag(KlaxonBlockTags.CRIMSON_NYLIUM_CONVERTIBLE, "Nether Reaction Crimson Nylium Convertible");
        addTag(KlaxonBlockTags.CRIMSON_FUNGUS_CONVERTIBLE, "Nether Reaction Crimson Fungus Convertible");
        addTag(KlaxonBlockTags.CRIMSON_ROOTS_CONVERTIBLE, "Nether Reaction Crimson Roots Convertible");
        addTag(KlaxonBlockTags.NETHER_WART_BLOCK_CONVERTIBLE, "Nether Reaction Nether Wart Block Convertible");
        addTag(KlaxonBlockTags.WEEPING_VINE_CONVERTIBLE, "Nether Reaction Weeping Vine Convertible");

        // crimson stems / hyphaes
        addTag(KlaxonBlockTags.CRIMSON_STEM_CONVERTIBLE, "Nether Reaction Crimson Stem Convertible");
        addTag(KlaxonBlockTags.STRIPPED_CRIMSON_STEM_CONVERTIBLE, "Nether Reaction Stripped Crimson Stem Convertible");
        addTag(KlaxonBlockTags.CRIMSON_HYPHAE_CONVERTIBLE, "Nether Reaction Crimson Hyphae Convertible");
        addTag(KlaxonBlockTags.STRIPPED_CRIMSON_HYPHAE_CONVERTIBLE, "Nether Reaction Stripped Crimson Hyphae Convertible");

        // crimson planks - normal building blocks
        addTag(KlaxonBlockTags.CRIMSON_PLANKS_CONVERTIBLE, "Nether Reaction Crimson Planks Convertible");
        addTag(KlaxonBlockTags.CRIMSON_STAIRS_CONVERTIBLE, "Nether Reaction Crimson Stairs Convertible");
        addTag(KlaxonBlockTags.CRIMSON_SLAB_CONVERTIBLE, "Nether Reaction Crimson Slab Convertible");
        addTag(KlaxonBlockTags.CRIMSON_FENCE_CONVERTIBLE, "Nether Reaction Crimson Fence Convertible");
        addTag(KlaxonBlockTags.CRIMSON_FENCE_GATE_CONVERTIBLE, "Nether Reaction Crimson Fence Gate Convertible");
        addTag(KlaxonBlockTags.CRIMSON_SIGN_CONVERTIBLE, "Nether Reaction Crimson Sign Convertible");
        addTag(KlaxonBlockTags.CRIMSON_WALL_SIGN_CONVERTIBLE, "Nether Reaction Crimson Wall Sign Convertible");
        addTag(KlaxonBlockTags.CRIMSON_HANGING_SIGN_CONVERTIBLE, "Nether Reaction Crimson Hanging Sign Convertible");
        addTag(KlaxonBlockTags.CRIMSON_WALL_HANGING_SIGN_CONVERTIBLE, "Nether Reaction Crimson Wall Hanging Sign Convertible");

        // redstone components
        addTag(KlaxonBlockTags.CRIMSON_BUTTON_CONVERTIBLE, "Nether Reaction Crimson Button Convertible");
        addTag(KlaxonBlockTags.CRIMSON_PRESSURE_PLATE_CONVERTIBLE, "Nether Reaction Crimson Pressure Plate Convertible");

        // warped stuff
        addTag(KlaxonBlockTags.WARPED_STEM_CONVERTIBLE, "Nether Reaction Warped Stem Convertible");
        addTag(KlaxonBlockTags.WARPED_FUNGUS_CONVERTIBLE, "Nether Reaction Warped Fungus Convertible");
        addTag(KlaxonBlockTags.TWISTING_VINES_CONVERTIBLE, "Nether Reaction Twisting Vines Convertible");
        addTag(KlaxonBlockTags.WARPED_WART_BLOCK_CONVERTIBLE, "Nether Reaction Warped Wart Block Convertible");
        addTag(KlaxonBlockTags.WARPED_NYLIUM_CONVERTIBLE, "Nether Reaction Warped Nylium Convertible");
    }

    private void generateManualItemApplicationTagTranslations() {
        addTag(KlaxonBlockTags.NETHER_REACTOR_CORE_CONVERTIBLE, "Manual Item Application Nether Reactor Core Convertible");
        addTag(KlaxonBlockTags.CRUDE_NETHER_REACTOR_CORE_CONVERTIBLE, "Manual Item Application Crude Nether Reactor Core Convertible");
    }

    private void generateCategoryTagTranslations() {
        // machines
        addTag(KlaxonBlockTags.BLAST_PROCESSORS, "Blast Processors");
        addTag(KlaxonBlockTags.MACHINES, "KLAXON's Machines");

        // wire spools
        addTag(KlaxonBlockTags.WIRE_SPOOLS, "Wire Spools");

        // pipe matrices
        addTag(KlaxonBlockTags.PIPE_MATRICES, "Pipe Matrices");
        addTag(KlaxonBlockTags.COPPER_PIPE_MATRICES, "Copper Pipe Matrices");

        // misc
        addTag(KlaxonBlockTags.HALLNOX_STEMS, "Hallnox Stems");
    }
}
