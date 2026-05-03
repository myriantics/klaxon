package net.myriantics.klaxon.registry.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;
import net.myriantics.klaxon.mechanics.wrench.Wrenchable;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeLogic;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipeInput;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipeLogic;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;

public abstract class KlaxonItemUsageTweaks {
    public static ArrayList<StackUseOnHandler> USE_ON_HANDLERS = new ArrayList<>();

    static {
        // world item application handler
        register((item, context) -> {
            Level level = context.getLevel();
            ItemStack stack = context.getItemInHand();
            Player player = context.getPlayer();
            boolean sneakDown = player != null && player.isShiftKeyDown();
            if (!sneakDown && WorldItemApplicationRecipeLogic.test(level, stack)) {
                BlockPos targetPos = context.getClickedPos();
                BlockState state = level.getBlockState(targetPos);

                WorldItemApplicationRecipeInput input = new WorldItemApplicationRecipeInput(stack, state);
                Optional<BlockState> newState = WorldItemApplicationRecipeLogic.getResultState(level, input);

                if (newState.isPresent()) {
                    if (level instanceof ServerLevel serverLevel) {
                        WorldItemApplicationRecipeLogic.affectWorld(serverLevel, targetPos, newState.get(), context.getClickedFace(), player, input);

                        // remainder fuckery
                        if (player != null && !player.isCreative()) {
                            ItemStack remainder = stack.getRecipeRemainder();
                            stack.shrink(1);
                            if (!player.getInventory().add(remainder)) {
                                player.drop(remainder, false);
                            }
                        }

                        // sculk sensors go brrrt
                        level.gameEvent(GameEvent.BLOCK_CHANGE, targetPos, GameEvent.Context.of(player));
                    }

                    return new InteractionResultWrapper(InteractionResult.SUCCESS);
                }
            }
            return InteractionResultWrapper.EMPTY;
        });

        register(((item, context) -> {
            if (ToolUsageRecipeLogic.test(context.getLevel(), context.getItemInHand())) {
                return switch (ToolUsageRecipeLogic.runRecipeLogic(context)) {
                    case FAIL -> InteractionResultWrapper.EMPTY;
                    case SUCCESS -> new InteractionResultWrapper(InteractionResult.SUCCESS);
                    case COSMETIC_SUCCESS -> new InteractionResultWrapper(InteractionResult.SUCCESS, true);
                };
            }

            // If the recipe process failed, call the original interaction
            return InteractionResultWrapper.EMPTY;
        }));

        // wrench handler
        register((item, context) -> {
            ItemStack stack = context.getItemInHand();
            boolean sneaking = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
            if (!stack.is(KlaxonItemTags.WRENCHABLE_INTERFACE_TRIGGERING_TOOLS) || sneaking) {
                return InteractionResultWrapper.EMPTY;
            }
            Level level = context.getLevel();
            BlockPos targetPos = context.getClickedPos();
            BlockState targetState = level.getBlockState(targetPos);
            if (targetState.getBlock() instanceof Wrenchable wrenchable) {
                Direction clickedFace = context.getClickedFace();
                Player player = context.getPlayer();
                BlockHitResult hitResult = new BlockHitResult(context.getClickLocation(), clickedFace, targetPos, false);

                WrenchActionContext.Manual manual = new WrenchActionContext.Manual(level, targetState, targetPos, stack, player, hitResult, context.getHand());
                WrenchInteractionMap interactionMap = wrenchable.getManualInteractionMap(manual);
                float x = manual.getGuiOrientation().getClickedX();
                float y = manual.getGuiOrientation().getClickedY();
                return new InteractionResultWrapper(interactionMap.select(x, y).handle(manual, interactionMap.getRotation(targetState, manual.getGuiOrientation())).orElse(null));
            } else {
                return InteractionResultWrapper.EMPTY;
            }
        });
    }

    private static void register(StackUseOnHandler handler) {
        USE_ON_HANDLERS.add(handler);
    }

    public interface StackUseOnHandler {
        InteractionResultWrapper handle(Item item, UseOnContext context);
    }

    public static class InteractionResultWrapper {
        private final @Nullable InteractionResult result;
        private final boolean isCosmetic;

        public static final InteractionResultWrapper EMPTY = new InteractionResultWrapper(null);

        public InteractionResultWrapper(@Nullable InteractionResult result) {
            this(result, false);
        }

        public InteractionResultWrapper(@Nullable InteractionResult result, boolean isCosmetic) {
            this.result = result;
            this.isCosmetic = isCosmetic;
        }

        public boolean isPresent() {
            return this.result != null;
        }

        public @Nullable InteractionResult get() {
            return this.result;
        }

        public boolean cosmeticOverride() {
            return this.isCosmetic;
        }

    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Initialized KLAXON's Item Usage Tweaks!");
    }
}
