package net.myriantics.klaxon.registry.custom;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.BlastProcessorCatalystBehavior;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.ItemBlastProcessorCatalystBehavior;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.behaviors.*;
import net.myriantics.klaxon.registry.KlaxonRegistries;

public class KlaxonBlastProcessorCatalystBehaviors {
    public static final Identifier DEFAULT_ID = locateBehaviorId("default");
    public static final Identifier FIREWORK_ROCKET_ID = locateBehaviorId("firework_rocket");
    public static final Identifier FIREWORK_STAR_ID = locateBehaviorId("firework_star");
    public static final Identifier BEDLIKE_EXPLODABLE_ID = locateBehaviorId("bedlike_explodable");
    public static final Identifier WIND_CHARGE_ID = locateBehaviorId("wind_charge");
    public static final Identifier DRAGONS_BREATH_ID = locateBehaviorId("dragons_breath");
    public static final Identifier GLOWSTONE_ID = locateBehaviorId("glowstone");
    public static final Identifier TNT_MINECART_ID = locateBehaviorId("tnt_minecart");
    public static final Identifier END_CRYSTAL_ID = locateBehaviorId("end_crystal");
    public static final Identifier CREEPER_HEAD_ID = locateBehaviorId("creeper_head");

    public static final BlastProcessorCatalystBehavior DEFAULT = registerBehavior(DEFAULT_ID,
            new ItemBlastProcessorCatalystBehavior(DEFAULT_ID));
    public static final BlastProcessorCatalystBehavior FIREWORK_ROCKET = registerBehavior(FIREWORK_ROCKET_ID,
            new FireworkRocketBlastProcessorCatalystBehavior(FIREWORK_ROCKET_ID));
    public static final BlastProcessorCatalystBehavior FIREWORK_STAR = registerBehavior(FIREWORK_STAR_ID,
            new FireworkStarBlastProcessorCatalystBehavior(FIREWORK_STAR_ID));
    public static final BlastProcessorCatalystBehavior BEDLIKE_EXPLODABLE = registerBehavior(BEDLIKE_EXPLODABLE_ID,
            new BedlikeExplodableBlastProcessorCatalystBehavior(BEDLIKE_EXPLODABLE_ID));
    public static final BlastProcessorCatalystBehavior WIND_CHARGE = registerBehavior(WIND_CHARGE_ID,
            new WindChargeBlastProcessorCatalystBehavior(WIND_CHARGE_ID));
    public static final BlastProcessorCatalystBehavior DRAGONS_BREATH = registerBehavior(DRAGONS_BREATH_ID,
            new DragonsBreathBlastProcessorCatalystBehavior(DRAGONS_BREATH_ID));
    public static final BlastProcessorCatalystBehavior GLOWSTONE_DUST = registerBehavior(GLOWSTONE_ID,
            new GlowstoneBlastProcessorCatalystBehavior(GLOWSTONE_ID));
    public static final BlastProcessorCatalystBehavior TNT_MINECART = registerBehavior(TNT_MINECART_ID,
            new TntMinecartBlastProcessorCatalystBehavior(TNT_MINECART_ID));
    public static final BlastProcessorCatalystBehavior END_CRYSTAL = registerBehavior(END_CRYSTAL_ID,
            new EndCrystalBlastProcessorCatalystBehavior(END_CRYSTAL_ID));
    public static final BlastProcessorCatalystBehavior CREEPER_HEAD = registerBehavior(CREEPER_HEAD_ID,
            new CreeperHeadBlastProcessorCatalystBehavior(CREEPER_HEAD_ID));

    private static Identifier locateBehaviorId(String name) {
        return KlaxonCommon.locate(name + "_behavior");
    }

    private static BlastProcessorCatalystBehavior registerBehavior(Identifier id, BlastProcessorCatalystBehavior behavior) {
        return Registry.register(KlaxonRegistries.BLAST_PROCESSOR_BEHAVIORS, id, behavior);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Blast Processor Behaviors!");
    }
}
