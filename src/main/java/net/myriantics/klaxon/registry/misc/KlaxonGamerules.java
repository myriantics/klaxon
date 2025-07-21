package net.myriantics.klaxon.registry.misc;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonGamerules {
    public static final GameRules.Key<GameRules.BooleanRule> SHOULD_BLAST_PROCESSOR_EXPLOSION_MODIFY_WORLD = registerBooleanRule(
            "shouldBlastProcessorExplosionModifyWorld", GameRules.Category.MISC, true
    );

    private static GameRules.Key<GameRules.BooleanRule> registerBooleanRule(String name, GameRules.Category category, boolean defaultValue) {
        return GameRuleRegistry.register(name, category, GameRuleFactory.createBooleanRule(defaultValue));
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's GameRules!");
    }
}
