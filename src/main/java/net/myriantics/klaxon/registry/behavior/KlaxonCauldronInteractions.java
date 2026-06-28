package net.myriantics.klaxon.registry.behavior;

import net.minecraft.core.Holder;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.filing_cabinet.FilingCabinetBaseBlock;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public abstract class KlaxonCauldronInteractions {

    public static final CauldronInteraction FILING_CABINET = (blockState, level, blockPos, player, interactionHand, itemStack) -> {
        if (!(Block.byItem(itemStack.getItem()) instanceof FilingCabinetBaseBlock block) || block.getDyeColor() == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else {
            if (!level.isClientSide()) {
                ItemStack washedStack = itemStack.transmuteCopy(KlaxonItems.FILING_CABINET.value(), 1);
                player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, washedStack, false));
                // player.awardStat(); add klaxon stats
                LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
    };

    static {
        register(CauldronInteraction.WATER, KlaxonItems.CRESTED_STEEL_HELMET, CauldronInteraction.DYED_ITEM);
        register(CauldronInteraction.WATER, KlaxonItems.WHITE_FILING_CABINET, FILING_CABINET);
        register(CauldronInteraction.WATER, KlaxonItems.ORANGE_FILING_CABINET, FILING_CABINET);
        register(CauldronInteraction.WATER, KlaxonItems.MAGENTA_FILING_CABINET, FILING_CABINET);
        register(CauldronInteraction.WATER, KlaxonItems.LIGHT_BLUE_FILING_CABINET, FILING_CABINET);
        register(CauldronInteraction.WATER, KlaxonItems.YELLOW_FILING_CABINET, FILING_CABINET);
        register(CauldronInteraction.WATER, KlaxonItems.LIME_FILING_CABINET, FILING_CABINET);
        register(CauldronInteraction.WATER, KlaxonItems.PINK_FILING_CABINET, FILING_CABINET);
        register(CauldronInteraction.WATER, KlaxonItems.GRAY_FILING_CABINET, FILING_CABINET);
        register(CauldronInteraction.WATER, KlaxonItems.LIGHT_GRAY_FILING_CABINET, FILING_CABINET);
        register(CauldronInteraction.WATER, KlaxonItems.CYAN_FILING_CABINET, FILING_CABINET);
        register(CauldronInteraction.WATER, KlaxonItems.PURPLE_FILING_CABINET, FILING_CABINET);
        register(CauldronInteraction.WATER, KlaxonItems.BLUE_FILING_CABINET, FILING_CABINET);
        register(CauldronInteraction.WATER, KlaxonItems.BROWN_FILING_CABINET, FILING_CABINET);
        register(CauldronInteraction.WATER, KlaxonItems.GREEN_FILING_CABINET, FILING_CABINET);
        register(CauldronInteraction.WATER, KlaxonItems.RED_FILING_CABINET, FILING_CABINET);
        register(CauldronInteraction.WATER, KlaxonItems.BLACK_FILING_CABINET, FILING_CABINET);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Cauldron Behaviors!");
    }

    private static void register(CauldronInteraction.InteractionMap map, Holder<Item> itemHolder, CauldronInteraction interaction) {
        register(map, itemHolder.value(), interaction);
    }

    private static void register(CauldronInteraction.InteractionMap map, ItemLike item, CauldronInteraction interaction) {
        map.map().put(item.asItem(), interaction);
    }
}
