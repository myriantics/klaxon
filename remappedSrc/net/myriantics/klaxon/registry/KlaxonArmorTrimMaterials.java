package net.myriantics.klaxon.registry;

import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.myriantics.klaxon.KlaxonCommon;

import java.util.Map;

public class KlaxonArmorTrimMaterials {
    // not gonna do this rn lol - will come later
    public static final RegistryKey<ArmorTrimMaterial> STEEL = of("steel");
    public static final RegistryKey<ArmorTrimMaterial> CRUDE_STEEL = of("crude_steel");

    public static void bootstrap(Registerable<ArmorTrimMaterial> registry) {
        register(registry, STEEL, KlaxonItems.STEEL_INGOT, Style.EMPTY.withColor(7300466), 0.3f);
        register(registry, CRUDE_STEEL, KlaxonItems.CRUDE_STEEL_INGOT, Style.EMPTY.withColor(7632002), 0.2f);
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

    private static RegistryKey<ArmorTrimMaterial> of(String id) {
        return RegistryKey.of(RegistryKeys.TRIM_MATERIAL, KlaxonCommon.locate(id));
    }
}
