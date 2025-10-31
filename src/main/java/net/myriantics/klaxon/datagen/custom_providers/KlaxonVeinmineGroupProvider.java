package net.myriantics.klaxon.datagen.custom_providers;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.mechanics.grapple_winch.VeinmineGroup;
import net.myriantics.klaxon.recipe.BlockIngredient;
import net.myriantics.klaxon.registry.dynamic.KlaxonVeinmineGroups;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonVeinmineGroupProvider extends FabricDynamicRegistryProvider {
    public KlaxonVeinmineGroupProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        add(entries, KlaxonVeinmineGroups.GLASS, KlaxonBlockTags.GLASS_VEINMINE_GROUP);
        add(entries, KlaxonVeinmineGroups.ICE, KlaxonBlockTags.ICE_VEINMINE_GROUP);
        add(entries, KlaxonVeinmineGroups.GLOWSTONE, KlaxonBlockTags.GLOWSTONE_VEINMINE_GROUP);
        add(entries, KlaxonVeinmineGroups.CHORUS, KlaxonBlockTags.CHORUS_VEINMINE_GROUP);
    }

    private static void add(Entries entries, RegistryKey<VeinmineGroup> key, TagKey<Block> tagKey) {
        add(entries, key, BlockIngredient.fromTag(tagKey));
    }

    private static void add(Entries entries, RegistryKey<VeinmineGroup> key, BlockIngredient ingredient) {
        add(entries, key, new VeinmineGroup(ingredient));
    }

    private static void add(Entries entries, RegistryKey<VeinmineGroup> key, VeinmineGroup group) {
        entries.add(key, group);
    }

    @Override
    public String getName() {
        return "klaxon_veinmine_group_provider";
    }
}
