package net.myriantics.klaxon.registry.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.muffling.Mufflable;
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

            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof Mufflable mufflable) {
                switch (type) {
                    case MUFFLER_APPLY -> {
                        if (!mufflable.hasMuffler()) {
                            mufflable.setMuffler(stack.split(1));
                            type.playSuccess(level, pos, state, player);
                        } else {
                            type.playFail(level, pos, state);
                        }
                    }
                    case MUFFLER_REMOVE -> {
                        ItemStack oldMuffler = mufflable.removeMuffler();
                        if (!oldMuffler.isEmpty()) {
                            stack.hurtAndBreak(1, player, EquipmentSlotHelper.convert(hand));
                            type.playSuccess(level, pos, state, player);
                        } else {
                            type.playFail(level, pos, state);
                        }
                    }
                }
            }

            return Optional.of(ItemInteractionResult.SUCCESS);
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
