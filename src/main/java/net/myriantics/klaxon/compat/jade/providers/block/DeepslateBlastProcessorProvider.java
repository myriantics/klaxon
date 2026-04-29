package net.myriantics.klaxon.compat.jade.providers.block;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystDefinitionRecipeLogic;
import org.jetbrains.annotations.NotNull;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;

public enum DeepslateBlastProcessorProvider implements IBlockComponentProvider, StreamServerDataProvider<BlockAccessor, Double> {
    INSTANCE;

    private DeepslateBlastProcessorProvider() {
    }

    private static final ResourceLocation ID = KlaxonCommon.locate("deepslate_blast_processor");

    @Override
    public boolean shouldRequestData(BlockAccessor accessor) {
        return accessor.getBlockState().getValue(DeepslateBlastProcessorBlock.LOOT_STATE).hasKnownCatalyst();
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        double explosionPower = this.decodeFromData(blockAccessor).orElse(0.0);
        iTooltip.add(Component.translatable("klaxon.jade.text.blast_processor.explosion_power", explosionPower));
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public @NotNull Double streamData(BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof DeepslateBlastProcessorBlockEntity blastProcessor && !blastProcessor.isUnlooted()) {
            return ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(blastProcessor.getContext(), blastProcessor.getCatalystStack()).explosionPower();
        }
        return 0.0;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Double> streamCodec() {
        return ByteBufCodecs.DOUBLE.cast();
    }
}
