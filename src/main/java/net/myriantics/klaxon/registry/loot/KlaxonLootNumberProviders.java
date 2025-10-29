package net.myriantics.klaxon.registry.loot;

import com.mojang.serialization.MapCodec;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.loot.LootDamageAmountProvider;

public abstract class KlaxonLootNumberProviders {
    public static LootNumberProviderType DAMAGE_AMOUNT = register("damage_amount", LootDamageAmountProvider.CODEC);

    private static LootNumberProviderType register(String name, MapCodec<? extends LootNumberProvider> codec) {
        return Registry.register(Registries.LOOT_NUMBER_PROVIDER_TYPE, KlaxonCommon.locate(name), new LootNumberProviderType(codec));
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Loot Number Providers!");
    }
}
