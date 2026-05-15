package net.myriantics.klaxon.registry.explosive_catalyst;

import net.minecraft.resources.ResourceKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.registry.KlaxonRegistries;

public abstract class KlaxonExplosiveCatalystBehaviors {

    public static final ResourceKey<ExplosiveCatalystBehavior> NO_OP = create("no_op");
    public static final ResourceKey<ExplosiveCatalystBehavior> DEFAULT = create("default");
    public static final ResourceKey<ExplosiveCatalystBehavior> TNT = create("tnt");
    public static final ResourceKey<ExplosiveCatalystBehavior> FIREWORK_ROCKET = create("firework_rocket");
    public static final ResourceKey<ExplosiveCatalystBehavior> FIREWORK_STAR = create("firework_star");
    public static final ResourceKey<ExplosiveCatalystBehavior> BEDLIKE = create("bedlike");
    public static final ResourceKey<ExplosiveCatalystBehavior> RESPAWN_ANCHORLIKE = create("respawn_anchorlike");
    public static final ResourceKey<ExplosiveCatalystBehavior> WIND_BURST = create("wind_charge");
    public static final ResourceKey<ExplosiveCatalystBehavior> DRAGONS_BREATH = create("dragons_breath");
    public static final ResourceKey<ExplosiveCatalystBehavior> TNT_MINECART = create("tnt_minecart");
    public static final ResourceKey<ExplosiveCatalystBehavior> END_CRYSTAL = create("end_crystal");
    public static final ResourceKey<ExplosiveCatalystBehavior> CHARGED_CREEPER_MIMIC = create("creeper_head");

    private static ResourceKey<ExplosiveCatalystBehavior> create(String name) {
        return ResourceKey.create(KlaxonRegistries.EXPLOSIVE_CATALYST_BEHAVIOR, KlaxonCommon.locate(name));
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Blast Processor Behaviors!");
    }
}
