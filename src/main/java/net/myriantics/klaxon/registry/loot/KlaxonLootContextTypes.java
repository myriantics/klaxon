package net.myriantics.klaxon.registry.loot;

import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mixin.minecraft.loot.LootContextTypesAccessor;

import java.util.function.Consumer;

public abstract class KlaxonLootContextTypes {

    private static LootContextType register(String name, Consumer<LootContextType.Builder> type) {
        LootContextType.Builder builder = new LootContextType.Builder();
        type.accept(builder);
        LootContextType lootContextType = builder.build();
        Identifier identifier = KlaxonCommon.locate(name);
        LootContextType lootContextType2 = LootContextTypesAccessor.klaxon$getLootContextTypeMap().put(identifier, lootContextType);
        if (lootContextType2 != null) {
            throw new IllegalStateException("Loot table parameter set " + identifier + " is already registered");
        } else {
            return lootContextType;
        }
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Loot Context Types!");
    }
}
