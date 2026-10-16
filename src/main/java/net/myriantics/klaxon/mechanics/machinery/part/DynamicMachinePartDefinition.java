package net.myriantics.klaxon.mechanics.machinery.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

public record DynamicMachinePartDefinition(Ingredient ingredient, DataComponentPatch patch) {
    public static final Codec<DynamicMachinePartDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(DynamicMachinePartDefinition::ingredient),
            DataComponentPatch.CODEC.fieldOf("components").forGetter(DynamicMachinePartDefinition::patch)
    ).apply(instance, DynamicMachinePartDefinition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DynamicMachinePartDefinition> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, DynamicMachinePartDefinition::ingredient,
            DataComponentPatch.STREAM_CODEC, DynamicMachinePartDefinition::patch,
            DynamicMachinePartDefinition::new
    );
}
