package net.myriantics.klaxon.compat.jade.providers;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.BlastProcessorCatalystBehavior;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.blast_processor_behavior.BlastProcessorBehaviorRecipeLogic;
import net.myriantics.klaxon.recipe.item_explosion_power.ExplosiveCatalystRecipeInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;

import java.util.Optional;

public enum DeepslateBlastProcessorProvider implements IBlockComponentProvider, StreamServerDataProvider<BlockAccessor, Double> {
    INSTANCE;

    private DeepslateBlastProcessorProvider() {
    }

    @Override
    public boolean shouldRequestData(BlockAccessor accessor) {
        return accessor.getBlockState().get(DeepslateBlastProcessorBlock.FUELED);
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        double explosionPower = this.decodeFromData(blockAccessor).orElse(0.0);
        iTooltip.add(Text.translatable("klaxon.jade.text.tooltip.blast_processor.explosion_power", explosionPower));
    }

    @Override
    public Identifier getUid() {
        return KlaxonCommon.locate("deepslate_blast_processor");
    }

    @Override
    public @NotNull Double streamData(BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof DeepslateBlastProcessorBlockEntity blastProcessor) {
            ExplosiveCatalystRecipeInput recipeInventory = new ExplosiveCatalystRecipeInput(blastProcessor.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX));

            BlastProcessorCatalystBehavior behavior = BlastProcessorBehaviorRecipeLogic.computeBehavior(blockAccessor.getLevel(), recipeInventory);

            return behavior.getExplosionPowerData(blockAccessor.getLevel(), blockAccessor.getPosition(), blastProcessor, recipeInventory).explosionPower();
        }
        return 0.0;
    }

    @Override
    public PacketCodec<RegistryByteBuf, Double> streamCodec() {
        return PacketCodecs.DOUBLE.cast();
    }
}
