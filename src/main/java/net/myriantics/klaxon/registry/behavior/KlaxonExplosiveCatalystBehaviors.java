package net.myriantics.klaxon.registry.behavior;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ItemExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors.*;
import net.myriantics.klaxon.registry.KlaxonRegistries;

public abstract class KlaxonExplosiveCatalystBehaviors {

    public static final ExplosiveCatalystBehavior DEFAULT = register("default", ItemExplosiveCatalystBehavior::new);
    public static final ExplosiveCatalystBehavior FIREWORK_ROCKET = register("firework_rocket", FireworkRocketExplosiveCatalystBehavior::new);
    public static final ExplosiveCatalystBehavior FIREWORK_STAR = register("firework_star", FireworkStarExplosiveCatalystBehavior::new);
    public static final ExplosiveCatalystBehavior BEDLIKE_EXPLODABLE = register("bedlike_explodable", BedlikeExplodableExplosiveCatalystBehavior::new);
    public static final ExplosiveCatalystBehavior WIND_CHARGE = register("wind_charge", WindChargeExplosiveCatalystBehavior::new);
    public static final ExplosiveCatalystBehavior DRAGONS_BREATH = register("dragons_breath", DragonsBreathExplosiveCatalystBehavior::new);
    public static final ExplosiveCatalystBehavior GLOWSTONE = register("glowstone", GlowstoneExplosiveCatalystBehavior::new);
    public static final ExplosiveCatalystBehavior TNT_MINECART = register("tnt_minecart", TntMinecartExplosiveCatalystBehavior::new);
    public static final ExplosiveCatalystBehavior END_CRYSTAL = register("end_crystal", EndCrystalExplosiveCatalystBehavior::new);
    public static final ExplosiveCatalystBehavior CREEPER_HEAD = register("creeper_head", CreeperHeadExplosiveCatalystBehavior::new);

    private static ExplosiveCatalystBehavior register(String name, ExplosiveCatalystBehaviorInitializer initializer) {
        Identifier id = KlaxonCommon.locate(name);
        return Registry.register(KlaxonRegistries.EXPLOSIVE_CATALYST_BEHAVIORS, id, initializer.apply(id));
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Blast Processor Behaviors!");
    }

    private interface ExplosiveCatalystBehaviorInitializer {
        ExplosiveCatalystBehavior apply(Identifier id);
    }
}
