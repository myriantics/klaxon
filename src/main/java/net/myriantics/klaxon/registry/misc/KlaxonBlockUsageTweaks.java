package net.myriantics.klaxon.registry.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.muffling.MufflableBlock;
import net.myriantics.klaxon.mechanics.muffling.MufflerActionType;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import net.myriantics.klaxon.util.EquipmentSlotHelper;

import java.util.ArrayList;
import java.util.Optional;

public abstract class KlaxonBlockUsageTweaks {
    public static ArrayList<UseItemOnHandler> USE_ITEM_ON_HANDLERS = new ArrayList<>();

    static {
        // muffler handling
        register((block, stack, state, level, pos, player, hand, hitResult) -> {
            MufflerActionType type;
            if (stack.is(KlaxonItemTags.MUFFLERS)) {
                type = MufflerActionType.MUFFLER_APPLY;
            } else if (stack.is(KlaxonItemTags.MUFFLER_REMOVERS)) {
                type = MufflerActionType.MUFFLER_REMOVE;
            } else {
                return Optional.empty();
            }

            if (state.getBlock() instanceof MufflableBlock mufflableBlock) {
                switch (type) {
                    case MUFFLER_APPLY -> {
                        if (!mufflableBlock.hasMuffler(level, pos)) {
                            if (!level.isClientSide()) {
                                mufflableBlock.setMuffler(level, pos, player.hasInfiniteMaterials() ? stack.copyWithCount(1) : stack.split(1));
                            }
                            type.playSuccessSound(level, pos, state, player);
                            return Optional.of(ItemInteractionResult.SUCCESS);
                        } else {
                            return Optional.empty();
                        }
                    }
                    case MUFFLER_REMOVE -> {
                        if (mufflableBlock.hasMuffler(level, pos)) {
                            if (!level.isClientSide()) {
                                ItemStack oldMuffler = mufflableBlock.removeMuffler(level, pos);
                                if (!player.hasInfiniteMaterials()) {
                                    stack.hurtAndBreak(1, player, EquipmentSlotHelper.convert(hand));
                                    Block.popResource(level, pos, oldMuffler.copy()); // no cloggage in creative mode
                                }
                            }
                            type.playSuccessSound(level, pos, state, player);
                            return Optional.of(ItemInteractionResult.SUCCESS);
                        } else {
                            return Optional.empty();
                        }
                    }
                }
            }

            return Optional.of(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
        });
    }

    public interface UseItemOnHandler {
        Optional<ItemInteractionResult> handle(Block block, ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult);
    }

    private static void register(UseItemOnHandler handler) {
        USE_ITEM_ON_HANDLERS.add(handler);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Initialized KLAXON's Block Usage Tweaks!");
    }
}
