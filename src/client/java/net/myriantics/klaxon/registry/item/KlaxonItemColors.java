package net.myriantics.klaxon.registry.item;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.Holder;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonItemColors {

    static {
        registerDyedComponent(CommonColors.WHITE, KlaxonItems.CRESTED_STEEL_HELMET);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Initialized KLAXON's Item Colors!");
    }

    private static void registerDyedComponent(int defaultColor, Holder<Item> itemHolder) {
        register(
                (stack, tintIndex) -> tintIndex > 0 ? -1 : DyedItemColor.getOrDefault(stack, defaultColor),
                itemHolder.value()
        );
    }

    private static void register(ItemColor provider, Item item) {
        ColorProviderRegistry.ITEM.register(provider, item);
    }
}
