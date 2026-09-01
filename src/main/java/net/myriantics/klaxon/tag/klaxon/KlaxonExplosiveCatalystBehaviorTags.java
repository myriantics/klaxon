package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.tags.TagKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.registry.KlaxonRegistries;

public abstract class KlaxonExplosiveCatalystBehaviorTags {

    public static final TagKey<ExplosiveCatalystBehavior> DOES_NOT_RUN_DISPENSER_EFFECTS = create("does_not_run_dispenser_effects");
    public static final TagKey<ExplosiveCatalystBehavior> RUNS_DESTROY_BLOCK_EFFECTS_FOR_MODULAR_EXPLOSIVE_BLOCK = create("runs_destroy_block_effects_for_modular_explosive_block");
    public static final TagKey<ExplosiveCatalystBehavior> UNUSABLE_FOR_BLAST_PROCESSING = create("unusable_for_blast_processing");
    public static final TagKey<ExplosiveCatalystBehavior> HARMLESS = create("harmless");
    public static final TagKey<ExplosiveCatalystBehavior> SETS_IGNORE_FALL_DAMAGE_FROM_CURRENT_IMPULSE = create("sets_ignore_fall_damage_from_current_impulse");
    public static final TagKey<ExplosiveCatalystBehavior> REQUIRES_AIRBORNE_TARGET_ENTITY_FOR_PROJECTILE_COLLISION = create("requires_airborne_target_entity_for_projectile_collision");

    private static TagKey<ExplosiveCatalystBehavior> create(String name) {
        return TagKey.create(KlaxonRegistries.EXPLOSIVE_CATALYST_BEHAVIOR, KlaxonCommon.locate(name));
    }
}
