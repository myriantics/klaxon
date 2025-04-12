package net.myriantics.klaxon.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.TagKey;

public abstract class InstabreakMiningToolItem extends MiningToolItem {
    public InstabreakMiningToolItem(ToolMaterial material, TagKey<Block> effectiveBlocks, Settings settings) {
        super(material, effectiveBlocks, settings);
    }

    public abstract boolean isCorrectForInstabreak(ItemStack stack, BlockState state);
}
