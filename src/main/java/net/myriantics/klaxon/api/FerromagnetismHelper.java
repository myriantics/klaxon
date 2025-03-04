package net.myriantics.klaxon.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.Map;
import java.util.Optional;

public abstract class FerromagnetismHelper {
    private static final Map<RegistryEntry<Item>, Boolean> DYNAMIC_FERROMAGNETIC_ITEMS_CACHE = Map.of();
    private static final Map<RegistryEntry<Block>, Boolean> DYNAMIC_FERROMAGNETIC_BLOCKS_CACHE = Map.of();
    private static final Map<RegistryEntry<EntityType<?>>, Boolean> DYNAMIC_FERROMAGNETIC_ENTITIES_CACHE = Map.of();
    private static final String[] FERROMAGNETIC_KEYWORDS = new String[] {
            "iron",
            "steel",
            "ferro",
            "magnet",
            "netherite"
    };

    public static boolean isItemFerromagnetic(ItemStack itemStack) {
        if (itemStack.isIn(KlaxonItemTags.FERROMAGNETIC_ITEM_BLACKLIST)) return false;
        return itemStack.isIn(KlaxonItemTags.FERROMAGNETIC_ITEMS) || dynamicItemFerromagnetismCheck(itemStack);
    }

    public static boolean isBlockFerromagnetic(BlockState blockState) {

        // i'll do this later when i actually need it - it's late
        // just wanted to get this out of my head and into the repo lol
        return false;
    }

    private static boolean dynamicItemFerromagnetismCheck(ItemStack itemStack) {
        RegistryEntry<Item> registryEntry = itemStack.getRegistryEntry();

        Optional<Boolean> cachedValue = Optional.ofNullable(DYNAMIC_FERROMAGNETIC_ITEMS_CACHE.get(registryEntry));
        // if cached value is empty, compute it and save it for later
        if (cachedValue.isEmpty()) {
            String itemPath = Registries.ITEM.getId(registryEntry.value()).getPath();

            DYNAMIC_FERROMAGNETIC_ITEMS_CACHE.put(registryEntry, testStringForFerromagneticKeywords(itemPath));
        }
        return cachedValue.get();
    }

    private static boolean testStringForFerromagneticKeywords(String path) {

        boolean isFerromagnetic = false;
        for (String testKeyword : FERROMAGNETIC_KEYWORDS) {
            if (path.contains(testKeyword)) {
                isFerromagnetic = true;
                break;
            }
        }

        return isFerromagnetic;
    }
}
