package net.myriantics.klaxon.registry.behavior;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors.DefaultExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors.*;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.tag.klaxon.KlaxonDimensionTypeTags;

public abstract class KlaxonExplosiveCatalystBehaviors {

    public static final Holder<ExplosiveCatalystBehavior> NO_OP = register("no_op", new NoOpExplosiveCatalystBehavior());
    public static final Holder<ExplosiveCatalystBehavior> DEFAULT = register("default", new DefaultExplosiveCatalystBehavior());
    public static final Holder<ExplosiveCatalystBehavior> FIREWORK_ROCKET = register("firework_rocket", new FireworkRocketExplosiveCatalystBehavior());
    public static final Holder<ExplosiveCatalystBehavior> FIREWORK_STAR = register("firework_star", new FireworkStarExplosiveCatalystBehavior());
    public static final Holder<ExplosiveCatalystBehavior> BEDLIKE_EXPLODABLE = register("bedlike", new BedlikeExplosiveCatalystBehavior(KlaxonDimensionTypeTags.BLOCKS_BEDLIKE_EXPLOSIVE_CATALYSTS));
    public static final Holder<ExplosiveCatalystBehavior> WIND_CHARGE = register("wind_charge", new WindChargeExplosiveCatalystBehavior());
    public static final Holder<ExplosiveCatalystBehavior> DRAGONS_BREATH = register("dragons_breath", new DragonsBreathExplosiveCatalystBehavior());
    public static final Holder<ExplosiveCatalystBehavior> GLOWSTONE = register("respawn_anchorlike", new RespawnAnchorlikeExplosiveCatalystBehavior(KlaxonDimensionTypeTags.BLOCKS_RESPAWN_ANCHORLIKE_EXPLOSIVE_CATALYSTS));
    public static final Holder<ExplosiveCatalystBehavior> TNT_MINECART = register("tnt_minecart", new TntMinecartExplosiveCatalystBehavior());
    public static final Holder<ExplosiveCatalystBehavior> END_CRYSTAL = register("end_crystal", new EndCrystalExplosiveCatalystBehavior());
    public static final Holder<ExplosiveCatalystBehavior> CREEPER_HEAD = register("creeper_head", new CreeperHeadExplosiveCatalystBehavior());

    private static Holder<ExplosiveCatalystBehavior> register(String name, ExplosiveCatalystBehavior behavior) {
        return Registry.registerForHolder(KlaxonRegistries.EXPLOSIVE_CATALYST_BEHAVIORS, KlaxonCommon.locate(name), behavior);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Blast Processor Behaviors!");
    }
}
