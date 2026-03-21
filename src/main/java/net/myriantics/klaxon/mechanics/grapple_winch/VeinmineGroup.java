package net.myriantics.klaxon.mechanics.grapple_winch;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.myriantics.klaxon.recipe.BlockIngredient;

public record VeinmineGroup(BlockIngredient ingredient) {
    public static final Codec<VeinmineGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockIngredient.DISALLOW_EMPTY_CODEC.fieldOf("block_ingredient").forGetter(VeinmineGroup::ingredient)
    ).apply(instance, VeinmineGroup::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, VeinmineGroup> PACKET_CODEC = StreamCodec.composite(
            BlockIngredient.PACKET_CODEC, VeinmineGroup::ingredient,
            VeinmineGroup::new
    );
}
