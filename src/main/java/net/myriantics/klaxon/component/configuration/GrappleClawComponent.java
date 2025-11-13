package net.myriantics.klaxon.component.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record GrappleClawComponent(float baseGrappling, float baseRending) {
    public static final Codec<GrappleClawComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("base_grappling_damage").forGetter(GrappleClawComponent::baseGrappling),
            Codec.FLOAT.fieldOf("base_rending_damage").forGetter(GrappleClawComponent::baseRending)
    ).apply(instance, GrappleClawComponent::new));

    public static final PacketCodec<RegistryByteBuf, GrappleClawComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, GrappleClawComponent::baseGrappling,
            PacketCodecs.FLOAT, GrappleClawComponent::baseRending,
            GrappleClawComponent::new
    );

    public static final GrappleClawComponent DEFAULT = new GrappleClawComponent(4.0f, 6.0f);

    public float computeGrappling(ItemStack stack) {
        return this.baseGrappling;
    }

    public float computeRending(ItemStack stack) {
        return this.baseRending;
    }
}
