package net.myriantics.klaxon.compat.jade.providers;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.compat.jade.KlaxonJadePlugin;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystVessel;
import net.myriantics.klaxon.registry.misc.KlaxonColors;
import org.jetbrains.annotations.NotNull;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

import java.util.Optional;

public abstract sealed class ExplosiveCatalystVesselProvider implements IJadeProvider permits ExplosiveCatalystVesselProvider.Block, ExplosiveCatalystVesselProvider.Entity {

    public static final ResourceLocation ID = KlaxonCommon.locate("explosive_catalyst_vessel");
    public static final String DATA_HIDDEN = KlaxonJadePlugin.textTranslationKey(ID, "data_hidden");
    public static final String EXPLOSION_POWER = KlaxonJadePlugin.textTranslationKey(ID, "explosion_power");
    public static final String CONFIG = KlaxonJadePlugin.configTranslationKey(ID);

    private static final Style OBFUSCATED = Style.EMPTY.withObfuscated(true);

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    MutableComponent createComponent(ExplosiveCatalystData data) {
        MutableComponent explosionPowerComponent;
        explosionPowerComponent = Component.literal(String.valueOf(data.explosionPower()));
        if (data.producesFire()) {
            explosionPowerComponent = explosionPowerComponent.withColor(KlaxonColors.ORANGE.getRGB());
        }
        return Component.translatable(EXPLOSION_POWER, explosionPowerComponent);
    }

    public static final class Block extends ExplosiveCatalystVesselProvider implements IBlockComponentProvider, StreamServerDataProvider<BlockAccessor, Optional<ExplosiveCatalystData>> {

        public static final Block INSTANCE = new Block();

        private Block() {
        }

        @Override
        public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
            this.decodeFromData(blockAccessor).ifPresent(
                    (data) -> iTooltip.add(this.createComponent(data.orElse(ExplosiveCatalystData.ZERO)))
            );
        }

        @Override
        public Optional<ExplosiveCatalystData> streamData(BlockAccessor blockAccessor) {
            if (blockAccessor.getBlockEntity() instanceof ExplosiveCatalystVessel vessel && vessel.hasDataReady()) {
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

    public static final class Entity extends ExplosiveCatalystVesselProvider implements IEntityComponentProvider, StreamServerDataProvider<EntityAccessor, Optional<ExplosiveCatalystData>> {

        public static final Entity INSTANCE = new Entity();

        private Entity() {
        }

        @Override
        public @NotNull Optional<ExplosiveCatalystData> streamData(EntityAccessor entityAccessor) {
            return entityAccessor.getEntity() instanceof ExplosiveCatalystVessel vessel && vessel.hasDataReady() ? Optional.ofNullable(vessel.getEffectiveCatalystData()) : Optional.empty();
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, Optional<ExplosiveCatalystData>> streamCodec() {
            return ExplosiveCatalystData.OPTIONAL_STREAM_CODEC;
        }

        @Override
        public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
            this.decodeFromData(entityAccessor).ifPresent(
                    (data) -> iTooltip.add(this.createComponent(data.orElse(ExplosiveCatalystData.ZERO)))
            );
        }
    }
}
