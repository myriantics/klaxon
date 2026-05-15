package net.myriantics.klaxon.compat.jade.providers.block;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.compat.jade.KlaxonJadePlugin;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystVessel;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.misc.KlaxonColors;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;

import java.util.Optional;

public enum ExplosiveCatalystVesselBlockProvider implements IBlockComponentProvider, StreamServerDataProvider<BlockAccessor, Optional<ExplosiveCatalystData>> {
    INSTANCE;

    public static final ResourceLocation ID = KlaxonCommon.locate("explosive_catalyst_vessel");
    public static final String DATA_HIDDEN = KlaxonJadePlugin.textTranslationKey(ID, "data_hidden");
    public static final String EXPLOSION_POWER = KlaxonJadePlugin.textTranslationKey(ID, "explosion_power");
    public static final String CONFIG = KlaxonJadePlugin.configTranslationKey(ID);

    private static final Style OBFUSCATED = Style.EMPTY.withObfuscated(true);

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        ExplosiveCatalystData data = this.decodeFromData(blockAccessor).get().orElse(ExplosiveCatalystData.ZERO);

        MutableComponent explosionPowerComponent;
        explosionPowerComponent = Component.literal("" + data.explosionPower());
        if (data.producesFire()) {
            explosionPowerComponent = explosionPowerComponent.withColor(KlaxonColors.ORANGE.getRGB());
        }
        iTooltip.add(Component.translatable(EXPLOSION_POWER, explosionPowerComponent));
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Optional<ExplosiveCatalystData> streamData(BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof ExplosiveCatalystVessel vessel) {
            return Optional.ofNullable(vessel.getEffectiveCatalystData());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Optional<ExplosiveCatalystData>> streamCodec() {
        return ExplosiveCatalystData.OPTIONAL_STREAM_CODEC;
    }
}
