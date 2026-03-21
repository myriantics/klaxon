package net.myriantics.klaxon.component.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record GrappleClawComponent(float baseGrappling, float baseRending, int veinmineCap) {
    public static final Codec<GrappleClawComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("base_grappling_damage").forGetter(GrappleClawComponent::baseGrappling),
            Codec.FLOAT.fieldOf("base_rending_damage").forGetter(GrappleClawComponent::baseRending),
            Codec.INT.fieldOf("veinmine_cap").forGetter(GrappleClawComponent::veinmineCap)
    ).apply(instance, GrappleClawComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GrappleClawComponent> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, GrappleClawComponent::baseGrappling,
            ByteBufCodecs.FLOAT, GrappleClawComponent::baseRending,
            ByteBufCodecs.VAR_INT, GrappleClawComponent::veinmineCap,
            GrappleClawComponent::new
    );

    public static final GrappleClawComponent DEFAULT = new GrappleClawComponent(4.0f, 6.0f, 128);

    public float computeGrappling(ItemStack stack) {
        return this.baseGrappling;
    }

    public float computeRending(ItemStack stack) {
        return this.baseRending;
    }
}
