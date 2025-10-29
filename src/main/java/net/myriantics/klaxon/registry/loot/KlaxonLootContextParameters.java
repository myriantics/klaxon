package net.myriantics.klaxon.registry.loot;

import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonLootContextParameters {

    public static LootContextParameter<Float> DAMAGE_DEALT = register("damage_dealt");

    private static <T> LootContextParameter<T> register(String name) {
        return new LootContextParameter<>(KlaxonCommon.locate(name));
    }
}
