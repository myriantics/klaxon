package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.tags.TagKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.registry.KlaxonRegistries;

public abstract class KlaxonExplosiveCatalystBehaviorTags {

    public static final TagKey<ExplosiveCatalystBehavior> DOES_NOT_RUN_DISPENSER_EFFECTS = create("does_not_run_dispenser_effects");
    public static final TagKey<ExplosiveCatalystBehavior> RUNS_DESTROY_BLOCK_EFFECTS_FOR_MODULAR_EXPLOSIVE_BLOCK = create("runs_destroy_block_effects_for_modular_explosive_block");
    public static final TagKey<ExplosiveCatalystBehavior> UNUSABLE_FOR_BLAST_PROCESSING = create("unusable_for_blast_processing");

    private static TagKey<ExplosiveCatalystBehavior> create(String name) {
        return TagKey.create(KlaxonRegistries.EXPLOSIVE_CATALYST_BEHAVIOR, KlaxonCommon.locate(name));
    }
}
