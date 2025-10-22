package net.myriantics.klaxon.registry.behavior;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.behavior.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.api.behavior.explosive_catalyst.ItemExplosiveCatalystBehavior;
import net.myriantics.klaxon.api.behavior.explosive_catalyst.behaviors.*;
import net.myriantics.klaxon.registry.KlaxonRegistries;

public abstract class KlaxonBlastProcessorCatalystBehaviors {
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

    public static final ExplosiveCatalystBehavior DEFAULT = registerBehavior(DEFAULT_ID,
            new ItemExplosiveCatalystBehavior(DEFAULT_ID));
    public static final ExplosiveCatalystBehavior FIREWORK_ROCKET = registerBehavior(FIREWORK_ROCKET_ID,
            new FireworkRocketExplosiveCatalystBehavior(FIREWORK_ROCKET_ID));
    public static final ExplosiveCatalystBehavior FIREWORK_STAR = registerBehavior(FIREWORK_STAR_ID,
            new FireworkStarExplosiveCatalystBehavior(FIREWORK_STAR_ID));
    public static final ExplosiveCatalystBehavior BEDLIKE_EXPLODABLE = registerBehavior(BEDLIKE_EXPLODABLE_ID,
            new BedlikeExplodableExplosiveCatalystBehavior(BEDLIKE_EXPLODABLE_ID));
    public static final ExplosiveCatalystBehavior WIND_CHARGE = registerBehavior(WIND_CHARGE_ID,
            new WindChargeExplosiveCatalystBehavior(WIND_CHARGE_ID));
    public static final ExplosiveCatalystBehavior DRAGONS_BREATH = registerBehavior(DRAGONS_BREATH_ID,
            new DragonsBreathExplosiveCatalystBehavior(DRAGONS_BREATH_ID));
    public static final ExplosiveCatalystBehavior GLOWSTONE_DUST = registerBehavior(GLOWSTONE_ID,
            new GlowstoneExplosiveCatalystBehavior(GLOWSTONE_ID));
    public static final ExplosiveCatalystBehavior TNT_MINECART = registerBehavior(TNT_MINECART_ID,
            new TntMinecartExplosiveCatalystBehavior(TNT_MINECART_ID));
    public static final ExplosiveCatalystBehavior END_CRYSTAL = registerBehavior(END_CRYSTAL_ID,
            new EndCrystalExplosiveCatalystBehavior(END_CRYSTAL_ID));
    public static final ExplosiveCatalystBehavior CREEPER_HEAD = registerBehavior(CREEPER_HEAD_ID,
            new CreeperHeadExplosiveCatalystBehavior(CREEPER_HEAD_ID));

    private static Identifier locateBehaviorId(String name) {
        return KlaxonCommon.locate(name);
    }

    private static ExplosiveCatalystBehavior registerBehavior(Identifier id, ExplosiveCatalystBehavior behavior) {
        return Registry.register(KlaxonRegistries.BLAST_PROCESSOR_BEHAVIORS, id, behavior);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Blast Processor Behaviors!");
    }
}
