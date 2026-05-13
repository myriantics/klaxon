package net.myriantics.klaxon.registry.item;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.Holder;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import org.jetbrains.annotations.Nullable;

public abstract class KlaxonItemColors {

    static {
        registerDyedComponent(CommonColors.WHITE, KlaxonItems.CRESTED_STEEL_HELMET);
        registerExplosiveCatalystDataComponent(CommonColors.WHITE, KlaxonItems.MODULAR_EXPLOSIVE_BLOCK);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Initialized KLAXON's Item Colors!");
    }

    private static void registerExplosiveCatalystDataComponent(int defaultColor, Holder<Item> itemHolder) {
        register(
                (stack, tintIndex) -> {
                    if (tintIndex > 0) {
                        return CommonColors.WHITE;
                    } else {
                        @Nullable Level level = Minecraft.getInstance().level;
                        if (level != null) {
                            @Nullable ExplosiveCatalystData data = stack.get(KlaxonDataComponentTypes.EXPLOSIVE_CATALYST_DATA.value());
                            if (data != null) {
                                return data.behavior(level).value().color;
                            }
                        }
                        return defaultColor;
                    }
                },
                itemHolder.value()
        );
    }

    private static void registerDyedComponent(int defaultColor, Holder<Item> itemHolder) {
        register(
                (stack, tintIndex) -> tintIndex > 0 ? CommonColors.WHITE : DyedItemColor.getOrDefault(stack, defaultColor),
                itemHolder.value()
        );
    }

    private static void register(ItemColor provider, Item item) {
        ColorProviderRegistry.ITEM.register(provider, item);
    }
}
