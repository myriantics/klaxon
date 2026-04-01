package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.tags.TagKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.explosive_catalyst.AbstractExplosiveCatalystBehavior;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;

public abstract class KlaxonExplosiveCatalystBehaviorTags {

    public static final TagKey<AbstractExplosiveCatalystBehavior> DOES_NOT_RUN_DISPENSER_EFFECTS = create("does_not_run_dispenser_effects");

    private static TagKey<AbstractExplosiveCatalystBehavior> create(String name) {
        return TagKey.create(KlaxonRegistryKeys.EXPLOSIVE_CATALYST_BEHAVIOR, KlaxonCommon.locate(name));
    }
}
