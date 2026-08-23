package net.myriantics.klaxon.item.block;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class ExplosiveCatalystVesselBlockItem extends BlockItem {
    public ExplosiveCatalystVesselBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        Fireworks fireworks = stack.get(DataComponents.FIREWORKS);
        if (fireworks != null) {
            fireworks.addToTooltip(context, tooltipComponents::add, tooltipFlag);
        }
        FireworkExplosion fireworkExplosion = stack.get(DataComponents.FIREWORK_EXPLOSION);
        if (fireworkExplosion != null) {
            fireworkExplosion.addToTooltip(context, tooltipComponents::add, tooltipFlag);
        }
    }
}
