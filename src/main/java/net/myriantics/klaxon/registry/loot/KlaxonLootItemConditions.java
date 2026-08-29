package net.myriantics.klaxon.registry.loot;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.loot.predicates.LootItemEntityOwnerCondition;

public class KlaxonLootItemConditions {

    public static final Holder<LootItemConditionType> ENTITY_OWNER = register("entity_owner", LootItemEntityOwnerCondition.CODEC);

    private static Holder<LootItemConditionType> register(String name, MapCodec<? extends LootItemCondition> codec) {
        return Registry.registerForHolder(BuiltInRegistries.LOOT_CONDITION_TYPE, KlaxonCommon.locate(name), new LootItemConditionType(codec));
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Loot Item Conditions!");
    }
}
