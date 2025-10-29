package net.myriantics.klaxon.api.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.myriantics.klaxon.registry.loot.KlaxonLootContextParameters;
import net.myriantics.klaxon.registry.loot.KlaxonLootNumberProviders;

public class LootDamageAmountProvider implements LootNumberProvider {

    private LootDamageAmountProvider() {
    }

    public static final LootDamageAmountProvider INSTANCE = new LootDamageAmountProvider();

    public static final MapCodec<LootDamageAmountProvider> CODEC = MapCodec.unit(() -> INSTANCE);

    @Override
    public float nextFloat(LootContext context) {
        Float damageDealt = context.get(KlaxonLootContextParameters.DAMAGE_DEALT);
        return damageDealt == null ? -1.0f : damageDealt;
    }

    @Override
    public LootNumberProviderType getType() {
        return KlaxonLootNumberProviders.DAMAGE_AMOUNT;
    }
}
