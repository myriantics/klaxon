package net.myriantics.klaxon.compat.jade.providers;

import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;

public enum CropGrowthDisabledProvider implements IBlockComponentProvider {
    INSTANCE;

    private CropGrowthDisabledProvider() {
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        BlockState state = blockAccessor.getBlockState();
        boolean growthDisabled = state.contains(KlaxonBlockStateProperties.GROWTH_DISABLED) && state.get(KlaxonBlockStateProperties.GROWTH_DISABLED);
        if (growthDisabled) iTooltip.add(Text.translatable("klaxon.jade.text.tooltip.crop_growth_disabled").withColor(Colors.RED));
    }

    @Override
    public Identifier getUid() {
        return KlaxonCommon.locate("crop_growth_disabled");
    }
}
