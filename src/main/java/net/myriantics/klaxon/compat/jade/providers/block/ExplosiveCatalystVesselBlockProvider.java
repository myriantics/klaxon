package net.myriantics.klaxon.compat.jade.providers.block;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.compat.jade.KlaxonJadePlugin;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystVessel;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.misc.KlaxonColors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;

public enum ExplosiveCatalystVesselBlockProvider implements IBlockComponentProvider, StreamServerDataProvider<BlockAccessor, ExplosiveCatalystData> {
    INSTANCE;

    public static final ResourceLocation ID = KlaxonCommon.locate("explosive_catalyst_vessel");
    public static final String DATA_HIDDEN = KlaxonJadePlugin.textTranslationKey(ID, "data_hidden");
    public static final String EXPLOSION_POWER = KlaxonJadePlugin.textTranslationKey(ID, "explosion_power");
    public static final String CONFIG = KlaxonJadePlugin.configTranslationKey(ID);

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        @Nullable ExplosiveCatalystData data = this.decodeFromData(blockAccessor).orElse(null);

        MutableComponent explosionPowerComponent;
        if (data == null) {
            explosionPowerComponent = Component.translatable(DATA_HIDDEN);
        } else {
            explosionPowerComponent = Component.literal("" + data.explosionPower());
            if (data.producesFire()) {
                explosionPowerComponent = explosionPowerComponent.withColor(KlaxonColors.ORANGE.getRGB());
            }
        }
        iTooltip.add(Component.translatable(EXPLOSION_POWER, explosionPowerComponent));
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public @NotNull ExplosiveCatalystData streamData(BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof ExplosiveCatalystVessel vessel) {
            return vessel.getEffectiveData();
        } else {
            return ExplosiveCatalystData.ZERO;
        }
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ExplosiveCatalystData> streamCodec() {
        return ExplosiveCatalystData.PACKET_CODEC;
    }
}
