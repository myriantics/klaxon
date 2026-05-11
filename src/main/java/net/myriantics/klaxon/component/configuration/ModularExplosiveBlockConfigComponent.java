package net.myriantics.klaxon.component.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.myriantics.klaxon.block.machines.modular_explosive.ModularExplosiveBlock;

public record ModularExplosiveBlockConfigComponent(
        int maxFuseTime,
        int ignitionTicks,
        boolean modifyWorld,
        boolean exposeCatalystData
) {

    public static final ModularExplosiveBlockConfigComponent DEFAULT = new ModularExplosiveBlockConfigComponent(0, ModularExplosiveBlock.DEFAULT_IGNITION_TICKS, true, true);

    public static final Codec<ModularExplosiveBlockConfigComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PrimitiveCodec.INT.fieldOf("max_fuse_time").forGetter(ModularExplosiveBlockConfigComponent::maxFuseTime),
            Codec.intRange(-1, 255).lenientOptionalFieldOf("ignition_ticks", ModularExplosiveBlock.DEFAULT_IGNITION_TICKS).forGetter(ModularExplosiveBlockConfigComponent::ignitionTicks),
            PrimitiveCodec.BOOL.fieldOf("modify_world").forGetter(ModularExplosiveBlockConfigComponent::modifyWorld),
            PrimitiveCodec.BOOL.fieldOf("expose_catalyst_data").forGetter(ModularExplosiveBlockConfigComponent::exposeCatalystData)
    ).apply(instance, ModularExplosiveBlockConfigComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ModularExplosiveBlockConfigComponent> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ModularExplosiveBlockConfigComponent::maxFuseTime,
            ByteBufCodecs.INT, ModularExplosiveBlockConfigComponent::ignitionTicks,
            ByteBufCodecs.BOOL, ModularExplosiveBlockConfigComponent::modifyWorld,
            ByteBufCodecs.BOOL, ModularExplosiveBlockConfigComponent::exposeCatalystData,
            ModularExplosiveBlockConfigComponent::new
    );
}
