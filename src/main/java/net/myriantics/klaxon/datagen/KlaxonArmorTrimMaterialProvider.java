package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.item.KlaxonArmorTrimMaterials;
import net.myriantics.klaxon.registry.item.KlaxonItems;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class KlaxonArmorTrimMaterialProvider extends FabricDynamicRegistryProvider {
    public KlaxonArmorTrimMaterialProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup, Entries entries) {
        entries.addAll(wrapperLookup.getWrapperOrThrow(RegistryKeys.TRIM_MATERIAL));
    }

    public static void generateArmorTrimMaterials(Registerable<ArmorTrimMaterial> registry) {
        register(
                registry,
                KlaxonArmorTrimMaterials.STEEL,
                KlaxonItems.STEEL_INGOT,
                Style.EMPTY.withColor(7300466),
                0.3f
        );
        register(
                registry,
                KlaxonArmorTrimMaterials.CRUDE_STEEL,
                KlaxonItems.CRUDE_STEEL_INGOT,
                Style.EMPTY.withColor(7632002),
                0.2f
        );
    }

    private static void register(Registerable<ArmorTrimMaterial> registry, RegistryKey<ArmorTrimMaterial> key, Item ingredient, Style style, float itemModelIndex) {
        register(registry, key, ingredient, style, itemModelIndex, Map.of());
    }

    private static void register(
            Registerable<ArmorTrimMaterial> registry,
            RegistryKey<ArmorTrimMaterial> key,
            Item ingredient,
            Style style,
            float itemModelIndex,
            Map<RegistryEntry<ArmorMaterial>, String> overrideArmorMaterials
    ) {
        ArmorTrimMaterial armorTrimMaterial = ArmorTrimMaterial.of(
                key.getValue().getPath(),
                ingredient,
                itemModelIndex,
                Text.translatable(Util.createTranslationKey("trim_material", key.getValue())).fillStyle(style),
                overrideArmorMaterials
        );
        registry.register(key, armorTrimMaterial);
    }

    @Override
    public String getName() {
        return KlaxonCommon.MOD_ID + "_armor_trim_material_provider";
    }
}
