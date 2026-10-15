package net.myriantics.klaxon.component.machine_part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.myriantics.klaxon.mechanics.machinery.part.data.MachinePartData;
import org.jetbrains.annotations.NotNull;

public record GeneratorTurbinePartComponent(float accelerationFactor) implements MachinePartData {
    public static final Codec<GeneratorTurbinePartComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("acceleration_factor").forGetter(GeneratorTurbinePartComponent::accelerationFactor)
    ).apply(instance, GeneratorTurbinePartComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GeneratorTurbinePartComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, GeneratorTurbinePartComponent::accelerationFactor,
            GeneratorTurbinePartComponent::new
    );

    public static final float DEFAULT_ACCELERATION_FACTOR = 0.4f;

    @Override
    public MachinePartData add(MachinePartData otherData) {
        if (otherData instanceof GeneratorTurbinePartComponent otherComponent) {
            return new GeneratorTurbinePartComponent(
                    this.accelerationFactor + otherComponent.accelerationFactor
            );
        }
        throw new IllegalArgumentException();
    }

    @Override
    public int compareTo(@NotNull MachinePartData other) {
        if (other instanceof GeneratorTurbinePartComponent component) {
            return Float.compare(this.accelerationFactor, component.accelerationFactor);
        }
        return 0;
    }

}
