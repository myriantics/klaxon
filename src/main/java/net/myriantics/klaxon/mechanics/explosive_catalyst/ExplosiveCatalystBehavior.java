package net.myriantics.klaxon.mechanics.explosive_catalyst;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;

public abstract class ExplosiveCatalystBehavior {

    public abstract void createExplosion(ExplosiveCatalystContext context, Position detonationPosition, ExplosiveCatalystData data, boolean modifyWorld);

    public ExplosiveCatalystData transformExplosiveCatalystData(ExplosiveCatalystContext context, ExplosiveCatalystData original) {
        return original;
    }

    public boolean isNoOp() {
        return false;
    }

    public final boolean is(TagKey<ExplosiveCatalystBehavior> tagKey) {
        return this.asHolder().is(tagKey);
    }

    public final boolean is(ExplosiveCatalystBehavior behavior) {
        return this == behavior;
    }

    public final boolean is(Holder<ExplosiveCatalystBehavior> behaviorHolder) {
        return this.is(behaviorHolder.value());
    }

    public static final Codec<Holder<ExplosiveCatalystBehavior>> ENTRY_CODEC = KlaxonRegistries.EXPLOSIVE_CATALYST_BEHAVIORS.holderByNameCodec();

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ExplosiveCatalystBehavior>> ENTRY_PACKET_CODEC = ByteBufCodecs.holderRegistry(KlaxonRegistryKeys.EXPLOSIVE_CATALYST_BEHAVIOR);

    public final Holder<ExplosiveCatalystBehavior> asHolder() {
        return KlaxonRegistries.EXPLOSIVE_CATALYST_BEHAVIORS.wrapAsHolder(this);
    }
}