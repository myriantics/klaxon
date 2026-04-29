package net.myriantics.klaxon.compat.jade.providers.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.functional.hallnox_pod.HallnoxPodBlock;
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

    private static final ResourceLocation ID = KlaxonCommon.locate("crop_growth_disabled");

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        BlockState podState = blockAccessor.getBlockState();
        boolean growthDisabled = podState.hasProperty(KlaxonBlockStateProperties.GROWTH_DISABLED) && podState.getValue(KlaxonBlockStateProperties.GROWTH_DISABLED);
        if (growthDisabled) {
            iTooltip.add(Component.translatable("klaxon.jade.text.crop_growth_disabled").withColor(CommonColors.RED));
        } else {
            Level world = blockAccessor.getLevel();
            Direction podFacing = podState.getValue(HallnoxPodBlock.FACING);
            BlockPos supportingPos = blockAccessor.getPosition().relative(podFacing);
            BlockState supportingState = world.getBlockState(supportingPos);

            if (supportingState.is(KlaxonBlockTags.HALLNOX_POD_NATURAL_GROWTH_INHIBITING)) {
                iTooltip.add(Component.translatable("klaxon.jade.text.natural_crop_growth_inhibited").withColor(CommonColors.YELLOW));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }
}
