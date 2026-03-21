package net.myriantics.klaxon.datagen.custom.providers;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.myriantics.klaxon.datagen.custom.KlaxonDynamicRegistrySubProvider;
import net.myriantics.klaxon.mechanics.grapple_winch.VeinmineGroup;
import net.myriantics.klaxon.recipe.BlockIngredient;
import net.myriantics.klaxon.registry.dynamic.KlaxonVeinmineGroups;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

public class KlaxonVeinmineGroupProvider extends KlaxonDynamicRegistrySubProvider<VeinmineGroup> {
    public KlaxonVeinmineGroupProvider(HolderLookup.Provider wrapperLookup, FabricDynamicRegistryProvider.Entries entries) {
        super(wrapperLookup, entries);
    }

    @Override
    protected void build() {
        this.add(KlaxonVeinmineGroups.GLASS, KlaxonBlockTags.GLASS_VEINMINE_GROUP);
        this.add(KlaxonVeinmineGroups.GLOWSTONE, KlaxonBlockTags.GLOWSTONE_VEINMINE_GROUP);
        this.add(KlaxonVeinmineGroups.ICE, KlaxonBlockTags.ICE_VEINMINE_GROUP);
        this.add(KlaxonVeinmineGroups.CHORUS, KlaxonBlockTags.CHORUS_VEINMINE_GROUP);
    }

    private void add(ResourceKey<VeinmineGroup> key, TagKey<Block> tagKey) {
        this.add(key, BlockIngredient.fromTag(tagKey));
    }

    private void add(ResourceKey<VeinmineGroup> key, BlockIngredient ingredient) {
        this.add(key, new VeinmineGroup(ingredient));
    }
}
