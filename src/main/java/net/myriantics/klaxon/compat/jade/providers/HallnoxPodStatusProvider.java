package net.myriantics.klaxon.compat.jade.providers;

import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.customblocks.functional.HallnoxPodBlock;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum HallnoxPodStatusProvider implements IBlockComponentProvider {
    INSTANCE;

    private HallnoxPodStatusProvider() {
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        BlockState podState = blockAccessor.getBlockState();
        boolean growthDisabled = podState.contains(KlaxonBlockStateProperties.GROWTH_DISABLED) && podState.get(KlaxonBlockStateProperties.GROWTH_DISABLED);
        if (growthDisabled) {
            iTooltip.add(Text.translatable("klaxon.jade.text.tooltip.crop_growth_disabled").withColor(Colors.RED));
        } else {
            World world = blockAccessor.getLevel();
            Direction podFacing = podState.get(HallnoxPodBlock.FACING);
            BlockPos supportingPos = blockAccessor.getPosition().offset(podFacing);
            BlockState supportingState = world.getBlockState(supportingPos);

            if (supportingState.isIn(KlaxonBlockTags.HALLNOX_POD_NATURAL_GROWTH_INHIBITING)) {
                iTooltip.add(Text.translatable("klaxon.jade.text.tooltip.natural_crop_growth_inhibited").withColor(Colors.YELLOW));
            }
        }
    }

    @Override
    public Identifier getUid() {
        return KlaxonCommon.locate("crop_growth_disabled");
    }
}
