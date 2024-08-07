package net.myriantics.klaxon.api;

import it.unimi.dsi.fastutil.objects.Object2DoubleLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.impl.content.registry.FuelRegistryImpl;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.block.blockentities.blast_chamber.BlastProcessorBlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.IdentityHashMap;
import java.util.Map;

// completely yoinked from fuel registry code
// credit go brrrt

public class ItemExplosionPowerRegistryImpl implements ItemExplosionPowerRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemExplosionPowerRegistryImpl.class);
    private final Object2DoubleMap<ItemConvertible> itemExplosionPower = new Object2DoubleLinkedOpenHashMap<>();
    private final Object2DoubleMap<TagKey<Item>> tagExplosionPower = new Object2DoubleLinkedOpenHashMap<>();
    private volatile Map<Item, Double> explosionPowerCache = null;

    public ItemExplosionPowerRegistryImpl() {
        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
            resetCache();
        });
    }

    public Map<Item, Double> getItemExplosionPowers() {
        Map<Item, Double> ret = explosionPowerCache;

        if (ret == null) {
            explosionPowerCache = ret = new IdentityHashMap<>(ItemExplosionPowerDefinitions.createItemExplosionPowerMap());
        }

        return ret;
    }

    @Override
    public Double get(ItemConvertible item) {
        return getItemExplosionPowers().get(item.asItem());
    }

    @Override
    public void add(ItemConvertible item, Double explosionPower) {
        itemExplosionPower.put(item, explosionPower.doubleValue());
        resetCache();
    }

    @Override
    public void add(TagKey<Item> tag, Double explosionPower) {

        tagExplosionPower.put(tag, explosionPower.doubleValue());
        resetCache();
    }

    @Override
    public void remove(ItemConvertible item) {
        add(item, 0.0);
        resetCache();
    }

    @Override
    public void remove(TagKey<Item> tag) {
        add(tag, 0.0);
        resetCache();
    }

    @Override
    public void clear(ItemConvertible item) {
        itemExplosionPower.removeDouble(item);
        resetCache();
    }

    @Override
    public void clear(TagKey<Item> tag) {
        tagExplosionPower.removeDouble(tag);
        resetCache();
    }

    // prolly have to do something with this when actually using this but thats for another day
    // look at fuelregistryimpl apply function for example on how to do thing
    public void apply(Map<Item, Double> map) {
        for (TagKey<Item> tag : tagExplosionPower.keySet()) {
            double explosionPower = tagExplosionPower.getDouble(tag);

            if (explosionPower <= 0) {
                for (RegistryEntry<Item> key : Registries.ITEM.iterateEntries(tag)) {
                    final Item item = key.value();
                    map.remove(item);
                }
            } else {
                ItemExplosionPowerDefinitions.defineItemExplosionPower(map, tag, explosionPower);
            }
        }

        for (ItemConvertible item : itemExplosionPower.keySet()) {
            double explosionPower = itemExplosionPower.getDouble(item);

            if (explosionPower <= 0) {
                map.remove(item.asItem());
            } else {
                ItemExplosionPowerDefinitions.defineItemExplosionPower(map, item, explosionPower);
            }
        }
    }

    public void resetCache() {
        explosionPowerCache = null;
    }
}
