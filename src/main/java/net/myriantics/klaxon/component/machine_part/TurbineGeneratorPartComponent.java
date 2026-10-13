package net.myriantics.klaxon.component.machine_part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.myriantics.klaxon.mechanics.machinery.part.data.MachinePartData;
import net.myriantics.klaxon.mechanics.machinery.part.data.StackingStrategy;
import org.jetbrains.annotations.NotNull;

public record TurbineGeneratorPartComponent(float accelerationFactor) implements MachinePartData {
    public static final Codec<TurbineGeneratorPartComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("acceleration_factor").forGetter(TurbineGeneratorPartComponent::accelerationFactor)
    ).apply(instance, TurbineGeneratorPartComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TurbineGeneratorPartComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, TurbineGeneratorPartComponent::accelerationFactor,
            TurbineGeneratorPartComponent::new
    );

    public static final float DEFAULT_ACCELERATION_FACTOR = 0.4f;

    @Override
    public MachinePartData add(MachinePartData otherData) {
        if (otherData instanceof TurbineGeneratorPartComponent otherComponent) {
            return new TurbineGeneratorPartComponent(
                    this.accelerationFactor + otherComponent.accelerationFactor
            );
        }
        throw new IllegalArgumentException();
    }

    @Override
    public int compareTo(@NotNull MachinePartData other) {
        if (other instanceof TurbineGeneratorPartComponent component) {
            return Float.compare(this.accelerationFactor, component.accelerationFactor);
        }
        return 0;
    }

}
