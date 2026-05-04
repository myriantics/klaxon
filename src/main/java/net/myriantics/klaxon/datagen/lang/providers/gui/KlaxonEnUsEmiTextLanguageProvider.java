package net.myriantics.klaxon.datagen.lang.providers.gui;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;

public final class KlaxonEnUsEmiTextLanguageProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsEmiTextLanguageProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        generateNetherReactionTranslations();
        generateExplosiveCatalystDefinitionTranslations();
        generateToolUsageTranslations();
    }

    private void generateNetherReactionTranslations() {
        addEmiText("nether_reaction.hover", "Detonate any Explosive inside a Nether Reactor Core to activate it.");
    }

    private void generateExplosiveCatalystDefinitionTranslations() {
        addEmiExplosionPowerInfo("tooltip.data.base_explosion_power", "Base Explosion Power: %s");
        addEmiExplosionPowerInfo("tooltip.data.base_fiery", "Fiery");


        addEmiExplosionPowerInfo("explosion_power.constant", "Explosion power: %1$s");
        addEmiExplosionPowerInfo("explosion_power.min", "Exp. Power Min: %1$s");
        addEmiExplosionPowerInfo("explosion_power.max", "Exp. Power Max: %1$s");

        addBehaviorDescription("default", "Always spawns a normal explosion.");

        addBehaviorDescription("bedlike_explodable", "Only explodes where a Bed would.");

        addBehaviorDescription("glowstone", "Only explodes where a Respawn Anchor would.");

        addBehaviorDescription("firework_rocket", "Explosion power based on firework ingredients. Creates firework explosion.");
        addBehaviorMin("firework_rocket", "0.3");
        addBehaviorMax("firework_rocket", "19.2");

        addBehaviorDescription("firework_star", "Explosion power based on firework star ingredients.");
        addBehaviorMin("firework_star", "0.8");
        addBehaviorMax("firework_star", "8.1");

        addBehaviorDescription("wind_charge", "Summons a wind charge burst.");

        addBehaviorDescription("dragons_breath", "Summons a cloud of Dragon's Breath.");

        addBehaviorDescription("end_crystal", "Becomes fiery when near Bedrock.");

        addBehaviorDescription("creeper_head", "Mimicks a Charged Creeper Explosion.");

        addBehaviorDescription("tnt_minecart", "Explosion power based on signal strength.");
        addBehaviorMin("tnt_minecart", "5.2");
        addBehaviorMax("tnt_minecart", "8.0");
    }

    private void generateToolUsageTranslations() {
        addEmiText("tool_usage.dropped_item", "Dropped Item");
        addEmiText("tool_usage.tool", "Tool needed to perform recipe");
        addEmiText("tool_usage.use", "Use Tool Near Dropped Item");
        addEmiText("tool_usage.use_compact", "Use Tool On Item");
    }

    private void addBehaviorDescription(String key, String name) {
        addEmiExplosionPowerInfo("behavior.klaxon." + key + ".description", name);
    }

    private void addBehaviorMin(String key, String name) {
        addEmiExplosionPowerInfo("behavior.klaxon." + key + ".min", name);
    }

    private void addBehaviorMax(String key, String name) {
        addEmiExplosionPowerInfo("behavior.klaxon." + key + ".max", name);
    }
}
