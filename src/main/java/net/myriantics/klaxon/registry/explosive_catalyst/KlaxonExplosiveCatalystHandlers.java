package net.myriantics.klaxon.registry.explosive_catalyst;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystHandler;
import net.myriantics.klaxon.mechanics.explosive_catalyst.handler.*;
import net.myriantics.klaxon.registry.KlaxonRegistries;

public abstract class KlaxonExplosiveCatalystHandlers {
    public static final Holder<ExplosiveCatalystHandler> NO_OP = register(
            "no_op",
            new NoOpExplosiveCatalystHandler()
    );
    public static final Holder<ExplosiveCatalystHandler> DEFAULT = register(
            "default",
            new DefaultExplosiveCatalystHandler()
    );
    public static final Holder<ExplosiveCatalystHandler> TNT = register(
            "tnt",
            new DefaultExplosiveCatalystHandler(null, null, Level.ExplosionInteraction.TNT, null, null, null));
    public static final Holder<ExplosiveCatalystHandler> WIND_BURST = register(
            "wind_burst",
            new DefaultExplosiveCatalystHandler(
                    null,
                    AbstractWindCharge.EXPLOSION_DAMAGE_CALCULATOR,
                    Level.ExplosionInteraction.TRIGGER,
                    ParticleTypes.GUST_EMITTER_SMALL,
                    ParticleTypes.GUST_EMITTER_LARGE,
                    SoundEvents.WIND_CHARGE_BURST
            )
    );
    public static final Holder<ExplosiveCatalystHandler> CHARGED_CREEPER_MIMIC = register(
            "charged_creeper_mimic",
            new ChargedCreeperExplosiveCatalystHandler()
    );
    public static final Holder<ExplosiveCatalystHandler> DRAGONS_BREATH = register(
            "dragons_breath",
            new DragonsBreathExplosiveCatalystHandler()
    );
    public static final Holder<ExplosiveCatalystHandler> FIREWORK = register(
            "firework",
            new FireworkExplosiveCatalystHandler()
    );

    private static Holder<ExplosiveCatalystHandler> register(String name, ExplosiveCatalystHandler handler) {
        return Registry.registerForHolder(KlaxonRegistries.EXPLOSIVE_CATALYST_HANDLERS, KlaxonCommon.locate(name), handler);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Explosive Catalyst Handlers!");
    }
}
