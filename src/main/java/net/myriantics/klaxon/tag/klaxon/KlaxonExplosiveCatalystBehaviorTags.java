package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.tags.TagKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.explosive_catalyst.AbstractExplosiveCatalystBehavior;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;

public abstract class KlaxonExplosiveCatalystBehaviorTags {

    public static final TagKey<AbstractExplosiveCatalystBehavior> DOES_NOT_RUN_DISPENSER_EFFECTS = create("does_not_run_dispenser_effects");
    public static final TagKey<AbstractExplosiveCatalystBehavior> RUNS_DESTROY_BLOCK_EFFECTS_FOR_MODULAR_EXPLOSIVE_BLOCK = create("runs_destroy_block_effects_for_modular_explosive_block");
    public static final TagKey<AbstractExplosiveCatalystBehavior> UNUSABLE_FOR_CRAFTING = create("unusable_for_crafting");

    private static TagKey<AbstractExplosiveCatalystBehavior> create(String name) {
        return TagKey.create(KlaxonRegistryKeys.EXPLOSIVE_CATALYST_BEHAVIOR, KlaxonCommon.locate(name));
    }
}
