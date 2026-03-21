package net.myriantics.klaxon.mechanics.advanced_item_models;

import net.minecraft.resources.ResourceLocation;

public abstract class AdvancedItemModelHelper {
    public static ResourceLocation getAlternateModelId(ResourceLocation identifier, String suffix) {
        return identifier.withPath((path) -> path + suffix);
    }

    public static ResourceLocation getMirroredId(ResourceLocation identifier) {
        return identifier.withPath((path) -> path + "_mirrored");
    }
}
