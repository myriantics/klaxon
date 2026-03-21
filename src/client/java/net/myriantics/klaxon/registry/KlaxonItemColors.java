package net.myriantics.klaxon.registry;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.mixin.registry.sync.client.ItemColorsMixin;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public abstract class KlaxonItemColors {

    static {
        registerDyedComponent(CommonColors.WHITE, KlaxonItems.CRESTED_STEEL_HELMET);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Initialized KLAXON's Item Colors!");
    }

    private static void registerDyedComponent(int defaultColor, Item... items) {
        register(
                (stack, tintIndex) -> tintIndex > 0 ? -1 : DyedItemColor.getOrDefault(stack, defaultColor),
                items
        );
    }

    private static void register(ItemColor provider, Item... items) {
        ColorProviderRegistry.ITEM.register(provider, items);
    }
}
