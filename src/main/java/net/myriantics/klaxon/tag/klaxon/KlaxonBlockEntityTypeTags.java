package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonBlockEntityTypeTags {
    public static TagKey<BlockEntityType<?>> NETHER_REACTION_OVERWRITABLE = createTag("nether_reaction_overwritable");

    private static TagKey<BlockEntityType<?>> createTag(String name) {
        return TagKey.of(RegistryKeys.BLOCK_ENTITY_TYPE, KlaxonCommon.locate(name));
    }
}
