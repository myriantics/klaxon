package net.myriantics.klaxon.advancement;

import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.advancement.criterion.BlockActivationCriterion;

public class KlaxonAdvancementCriteria {

    public static final BlockActivationCriterion BLOCK_ACTIVATION_CRITERION = register("block_activation", new BlockActivationCriterion());

    private static <T extends Criterion<?>> T register(String name, T criterion) {
        return Registry.register(Registries.CRITERION, KlaxonCommon.locate(name), criterion);
    }

    public static void registerAdvancementCriteria() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Advancement Criteria!");
    }
}
