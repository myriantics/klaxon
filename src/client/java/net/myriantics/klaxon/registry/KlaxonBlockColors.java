package net.myriantics.klaxon.registry;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.Holder;
import net.minecraft.util.CommonColors;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.modular_explosive.ModularExplosiveBlockEntity;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;

public abstract class KlaxonBlockColors {

    static {
        register((blockState, blockAndTintGetter, blockPos, tintIndex) -> {
            Level level = Minecraft.getInstance().level;
            if (tintIndex > 0) {
                return -1;
            }
            if (level != null && level.getBlockEntity(blockPos) instanceof ModularExplosiveBlockEntity modularExplosiveBlockEntity) {
                return modularExplosiveBlockEntity.getRawData().get(level).value().color;
            }
            return CommonColors.WHITE;
        }, KlaxonBlocks.MODULAR_EXPLOSIVE_BLOCK);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Initialized KLAXON's Block Colors!");
    }

    private static void register(BlockColor provider, Holder<Block> block) {
        register(provider, block.value());
    }

    private static void register(BlockColor provider, Block... blocks) {
        ColorProviderRegistry.BLOCK.register(provider, blocks);
    }
}
