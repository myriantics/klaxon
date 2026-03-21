package net.myriantics.klaxon.mechanics.ferromagnetism;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.Map;
import java.util.Optional;

public abstract class FerromagnetismHelper {
    private static final Map<Holder<Item>, Boolean> DYNAMIC_FERROMAGNETIC_ITEMS_CACHE = Map.of();
    private static final Map<Holder<Block>, Boolean> DYNAMIC_FERROMAGNETIC_BLOCKS_CACHE = Map.of();
    private static final Map<Holder<EntityType<?>>, Boolean> DYNAMIC_FERROMAGNETIC_ENTITIES_CACHE = Map.of();
    private static final String[] FERROMAGNETIC_KEYWORDS = new String[] {
            "iron",
            "steel",
            "ferro",
            "magnet",
            "netherite"
    };

    public static boolean isItemFerromagnetic(ItemStack itemStack) {
        if (itemStack.is(KlaxonItemTags.FERROMAGNETIC_ITEM_BLACKLIST)) return false;
        return itemStack.is(KlaxonItemTags.FERROMAGNETIC_ITEMS) || dynamicItemFerromagnetismCheck(itemStack);
    }

    public static boolean isBlockFerromagnetic(BlockState blockState) {

        // i'll do this later when i actually need it - it's late
        // just wanted to get this out of my head and into the repo lol
        return false;
    }

    private static boolean dynamicItemFerromagnetismCheck(ItemStack itemStack) {
        Holder<Item> registryEntry = itemStack.getItemHolder();

        Optional<Boolean> cachedValue = Optional.ofNullable(DYNAMIC_FERROMAGNETIC_ITEMS_CACHE.get(registryEntry));
        // if cached value is empty, compute it and save it for later
        if (cachedValue.isEmpty()) {
            String itemPath = BuiltInRegistries.ITEM.getKey(registryEntry.value()).getPath();

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
