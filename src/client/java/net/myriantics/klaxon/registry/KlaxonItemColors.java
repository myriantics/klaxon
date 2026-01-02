package net.myriantics.klaxon.registry;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.mixin.registry.sync.client.ItemColorsMixin;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Item;
import net.minecraft.util.Colors;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public abstract class KlaxonItemColors {

    static {
        registerDyedComponent(Colors.WHITE, KlaxonItems.CRESTED_STEEL_HELMET);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Initialized KLAXON's Item Colors!");
    }

    private static void registerDyedComponent(int defaultColor, Item... items) {
        register(
                (stack, tintIndex) -> tintIndex > 0 ? -1 : DyedColorComponent.getColor(stack, defaultColor),
                items
        );
    }

    private static void register(ItemColorProvider provider, Item... items) {
        ColorProviderRegistry.ITEM.register(provider, items);
    }
}
