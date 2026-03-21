package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.item.KlaxonArmorTrimMaterials;
import net.myriantics.klaxon.registry.item.KlaxonItems;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class KlaxonArmorTrimMaterialProvider extends FabricDynamicRegistryProvider {
    public KlaxonArmorTrimMaterialProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider wrapperLookup, Entries entries) {
        entries.addAll(wrapperLookup.lookupOrThrow(Registries.TRIM_MATERIAL));
    }

    public static void generateArmorTrimMaterials(BootstrapContext<TrimMaterial> registry) {
        register(
                registry,
                KlaxonArmorTrimMaterials.STEEL,
                KlaxonItems.STEEL_INGOT,
                Style.EMPTY.withColor(7300466),
                0.3f
        );
    }

    private static void register(BootstrapContext<TrimMaterial> registry, ResourceKey<TrimMaterial> key, Item ingredient, Style style, float itemModelIndex) {
        register(registry, key, ingredient, style, itemModelIndex, Map.of());
    }

    private static void register(
            BootstrapContext<TrimMaterial> registry,
            ResourceKey<TrimMaterial> key,
            Item ingredient,
            Style style,
            float itemModelIndex,
            Map<Holder<ArmorMaterial>, String> overrideArmorMaterials
    ) {
        TrimMaterial armorTrimMaterial = TrimMaterial.create(
                key.location().getPath(),
                ingredient,
                itemModelIndex,
                Component.translatable(Util.makeDescriptionId("trim_material", key.location())).withStyle(style),
                overrideArmorMaterials
        );
        registry.register(key, armorTrimMaterial);
    }

    @Override
    public String getName() {
        return KlaxonCommon.MOD_ID + "_armor_trim_material_provider";
    }
}
