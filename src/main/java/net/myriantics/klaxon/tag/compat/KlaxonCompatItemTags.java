package net.myriantics.klaxon.tag.compat;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class KlaxonCompatItemTags {

    // modIds
    public static String COMBAT_AMENITIES_MODID = "combatamenities";
    public static String SUPPLEMENTARIES_MODID = "supplementaries";

    public static TagKey<Item> PEDESTAL_DOWNRIGHT = createSupplementariesCompatItemTag("pedestal_downright");

    private static TagKey<Item> createSupplementariesCompatItemTag(String name) {
        return createCompatItemTag(SUPPLEMENTARIES_MODID, name);
    }

    private static TagKey<Item> createCompatItemTag(String namespace, String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, name));
    }
}
