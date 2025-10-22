package net.myriantics.klaxon.compat.jade.providers;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeLogic;
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

    private static final Identifier ID = KlaxonCommon.locate("deepslate_blast_processor");

    @Override
    public boolean shouldRequestData(BlockAccessor accessor) {
        return accessor.getBlockState().get(DeepslateBlastProcessorBlock.FUELED);
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        double explosionPower = this.decodeFromData(blockAccessor).orElse(0.0);
        iTooltip.add(Text.translatable("klaxon.jade.text.blast_processor.explosion_power", explosionPower));
    }

    @Override
    public Identifier getUid() {
        return ID;
    }

    @Override
    public @NotNull Double streamData(BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof DeepslateBlastProcessorBlockEntity blastProcessor) {
            ExplosiveCatalystDefinitionRecipeInput recipeInventory = new ExplosiveCatalystDefinitionRecipeInput(blastProcessor.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX));


            return ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(blockAccessor.getLevel(), blockAccessor.getPosition(), blastProcessor, recipeInventory).explosionPower();
        }
        return 0.0;
    }

    @Override
    public PacketCodec<RegistryByteBuf, Double> streamCodec() {
        return PacketCodecs.DOUBLE.cast();
    }
}
