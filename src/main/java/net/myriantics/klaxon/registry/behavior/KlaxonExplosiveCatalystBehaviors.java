package net.myriantics.klaxon.registry.behavior;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ItemExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors.*;
import net.myriantics.klaxon.registry.KlaxonRegistries;

public abstract class KlaxonExplosiveCatalystBehaviors {

    public static final Holder<ExplosiveCatalystBehavior> DEFAULT = register("default", ItemExplosiveCatalystBehavior::new);
    public static final Holder<ExplosiveCatalystBehavior> FIREWORK_ROCKET = register("firework_rocket", FireworkRocketExplosiveCatalystBehavior::new);
    public static final Holder<ExplosiveCatalystBehavior> FIREWORK_STAR = register("firework_star", FireworkStarExplosiveCatalystBehavior::new);
    public static final Holder<ExplosiveCatalystBehavior> BEDLIKE_EXPLODABLE = register("bedlike_explodable", BedlikeExplodableExplosiveCatalystBehavior::new);
    public static final Holder<ExplosiveCatalystBehavior> WIND_CHARGE = register("wind_charge", WindChargeExplosiveCatalystBehavior::new);
    public static final Holder<ExplosiveCatalystBehavior> DRAGONS_BREATH = register("dragons_breath", DragonsBreathExplosiveCatalystBehavior::new);
    public static final Holder<ExplosiveCatalystBehavior> GLOWSTONE = register("glowstone", GlowstoneExplosiveCatalystBehavior::new);
    public static final Holder<ExplosiveCatalystBehavior> TNT_MINECART = register("tnt_minecart", TntMinecartExplosiveCatalystBehavior::new);
    public static final Holder<ExplosiveCatalystBehavior> END_CRYSTAL = register("end_crystal", EndCrystalExplosiveCatalystBehavior::new);
    public static final Holder<ExplosiveCatalystBehavior> CREEPER_HEAD = register("creeper_head", CreeperHeadExplosiveCatalystBehavior::new);

    private static Holder<ExplosiveCatalystBehavior> register(String name, ExplosiveCatalystBehaviorInitializer initializer) {
        ResourceLocation id = KlaxonCommon.locate(name);
        return Registry.registerForHolder(KlaxonRegistries.EXPLOSIVE_CATALYST_BEHAVIORS, id, initializer.apply(id));
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Blast Processor Behaviors!");
    }

    private interface ExplosiveCatalystBehaviorInitializer {
        ExplosiveCatalystBehavior apply(ResourceLocation id);
    }
}
