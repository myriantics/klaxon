package net.myriantics.klaxon.registry.misc;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.level.GameRules;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonGameRules {
    public static final GameRules.Key<GameRules.BooleanValue> BLAST_PROCESSOR_EXPLOSIONS_MODIFY_WORLD = registerBooleanRule(
            "blastProcessorExplosionsModifyWorld", GameRules.Category.MISC, true
    );

    public static final GameRules.Key<GameRules.BooleanValue> DISPENSERS_PERFORM_ITEM_INTERACTION_RECIPES = registerBooleanRule(
            "dispensersPerformWorldItemInteractionRecipes", GameRules.Category.MISC, true
    );

    private static GameRules.Key<GameRules.BooleanValue> registerBooleanRule(String name, GameRules.Category category, boolean defaultValue) {
        return GameRuleRegistry.register(KlaxonCommon.locateAlt(name), category, GameRuleFactory.createBooleanRule(defaultValue));
    }

    private static GameRules.Key<GameRules.IntegerValue> registerIntRule(String name, GameRules.Category category, int defaultValue, int min, int max) {
        if (defaultValue < min || defaultValue > max) {
            throw new IllegalArgumentException("Default value of gamerule " + KlaxonCommon.locateAlt(name) + " [" + defaultValue + "] out of bounds: [" + min + "," + max + "]" );
        }
        return GameRuleRegistry.register(KlaxonCommon.locateAlt(name), category, GameRuleFactory.createIntRule(defaultValue, min, max));
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's GameRules!");
    }
}
