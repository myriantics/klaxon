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
        boolean modifyWorld
) implements TooltipProvider {

    private static final Style GREY = Style.EMPTY.withColor(CommonColors.LIGHT_GRAY);

    public static final ModularExplosiveBlockConfigComponent DEFAULT = new ModularExplosiveBlockConfigComponent(0, true);

    public static final Codec<ModularExplosiveBlockConfigComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PrimitiveCodec.INT.fieldOf("max_fuse_time").forGetter(ModularExplosiveBlockConfigComponent::maxFuseTime),
            PrimitiveCodec.BOOL.fieldOf("modify_world").forGetter(ModularExplosiveBlockConfigComponent::modifyWorld)
    ).apply(instance, ModularExplosiveBlockConfigComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ModularExplosiveBlockConfigComponent> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ModularExplosiveBlockConfigComponent::maxFuseTime,
            ByteBufCodecs.BOOL, ModularExplosiveBlockConfigComponent::modifyWorld,
            ModularExplosiveBlockConfigComponent::new
    );

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        if (this.maxFuseTime == -1) {
            tooltipAdder.accept(Component.translatable("klaxon.text.tooltip.modular_explosive_block_config.detonation_disabled").withStyle(GREY));
        } else {
            tooltipAdder.accept(Component.translatable("klaxon.text.tooltip.modular_explosive_block_config.fuse_ticks", this.maxFuseTime).withStyle(GREY));
            if (tooltipFlag.isAdvanced()) {
                if (this.modifyWorld) {
                    tooltipAdder.accept(Component.translatable("klaxon.text.tooltip.modular_explosive_block_config.modify_world.true").withStyle(GREY));
                } else {
                    tooltipAdder.accept(Component.translatable("klaxon.text.tooltip.modular_explosive_block_config.modify_world.false").withStyle(GREY));
                }
            }
        }
    }
}
