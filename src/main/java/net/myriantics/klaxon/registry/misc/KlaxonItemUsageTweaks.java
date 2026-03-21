package net.myriantics.klaxon.registry.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.Wrenchable;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipeInput;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipeLogic;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalItemTags;

import java.util.ArrayList;
import java.util.Optional;

public abstract class KlaxonItemUsageTweaks {
    public static ArrayList<StackUseOnHandler> USE_ON_HANDLERS = new ArrayList<>();

    static {
        // world item application handler
        register((item, context) -> {
            Level level = context.getLevel();
            ItemStack stack = context.getItemInHand();
            if (WorldItemApplicationRecipeLogic.test(level, stack)) {
                BlockPos targetPos = context.getClickedPos();
                BlockState state = level.getBlockState(targetPos);

                WorldItemApplicationRecipeInput input = new WorldItemApplicationRecipeInput(stack, state);
                Optional<BlockState> newState = WorldItemApplicationRecipeLogic.getResultState(level, input);

                if (newState.isPresent()) {
                    Player player = context.getPlayer();
                    if (level instanceof ServerLevel serverLevel) {
                        WorldItemApplicationRecipeLogic.affectWorld(serverLevel, targetPos, newState.get(), context.getClickedFace(), player, input);

                        // remainder fuckery
                        if (!player.isCreative()) {
                            ItemStack remainder = stack.getRecipeRemainder();
                            stack.shrink(1);
                            if (!player.getInventory().add(remainder)) {
                                player.drop(remainder, false);
                            }
                        }

                        // sculk sensors go brrrt
                        level.gameEvent(GameEvent.BLOCK_CHANGE, targetPos, GameEvent.Context.of(player));
                    }

                    return Optional.of(InteractionResult.SUCCESS);
                }
            }
            return Optional.empty();
        });

        // wrench handler
        register((item, context) -> {
            ItemStack stack = context.getItemInHand();
            boolean sneaking = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
            if (!stack.is(KlaxonConventionalItemTags.WRENCHES) || sneaking) {
                return Optional.empty();
            }
            Level level = context.getLevel();
            BlockPos targetPos = context.getClickedPos();
            BlockState targetState = level.getBlockState(targetPos);
            if (targetState.getBlock() instanceof Wrenchable wrenchable) {
                return Optional.ofNullable(wrenchable.onManualWrenchInteraction(new ManualWrenchInteractionContext(targetState, stack, level, context.getPlayer(), context.getHand(), new BlockHitResult(context.getClickLocation(), context.getClickedFace(), targetPos, false))));
            } else {
                return Optional.empty();
            }
        });
    }

    private static void register(StackUseOnHandler handler) {
        USE_ON_HANDLERS.add(handler);
    }

    public interface StackUseOnHandler {
        Optional<InteractionResult> handle(Item item, UseOnContext context);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Initialized KLAXON's Item Usage Tweaks");
    }
}
