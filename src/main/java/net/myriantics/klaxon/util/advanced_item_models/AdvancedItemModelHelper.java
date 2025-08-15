package net.myriantics.klaxon.util.advanced_item_models;

import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public abstract class AdvancedItemModelHelper {
    public static Identifier getAlternateModelId(Identifier identifier, String suffix) {
        return identifier.withPath((path) -> path + suffix);
    }

    public static Identifier getMirroredId(Identifier identifier) {
        return identifier.withPath((path) -> path + "_mirrored");
    }
}
