package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonBlockEntityTypeTags {
    public static TagKey<BlockEntityType<?>> NETHER_REACTION_OVERWRITABLE = createTag("nether_reaction_overwritable");

    private static TagKey<BlockEntityType<?>> createTag(String name) {
        return TagKey.create(Registries.BLOCK_ENTITY_TYPE, KlaxonCommon.locate(name));
    }
}
