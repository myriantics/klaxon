package net.myriantics.klaxon.component.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.myriantics.klaxon.block.machines.modular_explosive.ModularExplosiveBlock;

import java.util.function.Consumer;

public record ModularExplosiveBlockConfigComponent(
        int maxFuseTime,
        int ignitionTicks,
        boolean modifyWorld,
        boolean exposeCatalystData
) implements TooltipProvider {

    private static final Style GREY = Style.EMPTY.withColor(CommonColors.LIGHT_GRAY);

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

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        tooltipAdder.accept(Component.translatable("klaxon.text.tooltip.modular_explosive_block_config.fuse_ticks", this.maxFuseTime).withStyle(GREY));
        if (tooltipFlag.isCreative()) {
            tooltipAdder.accept(Component.translatable("klaxon.text.tooltip.modular_explosive_block_config.ignition_ticks", this.ignitionTicks).withStyle(GREY));
            if (this.modifyWorld) {
                tooltipAdder.accept(Component.translatable("klaxon.text.tooltip.modular_explosive_block_config.modify_world.true"));
            } else {
                tooltipAdder.accept(Component.translatable("klaxon.text.tooltip.modular_explosive_block_config.modify_world.false"));
            }
        }
    }
}
