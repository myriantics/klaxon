package net.myriantics.klaxon.api;

import com.google.common.collect.Maps;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

import java.util.Iterator;
import java.util.Map;

public class ItemExplosionPowerDefinitions {
    public static Map<Item, Double> createItemExplosionPowerMap() {
        Map<Item, Double> map = Maps.newLinkedHashMap();
        defineItemExplosionPower(map, Blocks.TNT, 4.0);
        defineItemExplosionPower(map, Items.GUNPOWDER, 0.3);
        defineItemExplosionPower(map, Items.FIREWORK_STAR, 0.2);
        defineItemExplosionPower(map, Items.FIREWORK_ROCKET, 0.1);
        defineItemExplosionPower(map, Items.END_CRYSTAL, 6.0);
        defineItemExplosionPower(map, Items.FIRE_CHARGE, 0.5);

        return map;
    }

    public static void defineItemExplosionPower(Map<Item, Double> itemExplosionPowers, TagKey<Item> tag, double explosionPower) {
        Iterator<RegistryEntry<Item>> iterator = Registries.ITEM.iterateEntries(tag).iterator();

        while (iterator.hasNext()) {
            RegistryEntry<Item> registryEntry = iterator.next();

            itemExplosionPowers.put(registryEntry.value(), explosionPower);
        }
    }

    public static void defineItemExplosionPower(Map<Item, Double> itemExplosionPowers, ItemConvertible item, double explosionPower) {
        Item item2 = item.asItem();

        itemExplosionPowers.put(item2, explosionPower);
    }

}
