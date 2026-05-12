package net.myriantics.klaxon.registry.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.TooltipProvider;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.item.equipment.tools.GrappleWinchItem;
import net.myriantics.klaxon.mechanics.grapple_winch.ClientGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.ClientGrappleWinchConnectionManager;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import net.myriantics.klaxon.util.KlaxonMathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class KlaxonItemTooltipModifications {
    public static ArrayList<TooltipModifier> MODIFIERS = new ArrayList<>();

    static {
        // grapple winch cable length
        register((stack, context, player, flag, consumer) -> {
            if (!(stack.getItem() instanceof GrappleWinchItem)) {
                return;
            }

            ChargedProjectiles chargedProjectilesComponent = stack.get(DataComponents.CHARGED_PROJECTILES);
            if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
                ItemStack itemStack = chargedProjectilesComponent.getItems().get(0);
                consumer.accept(Component.translatable("klaxon.text.tooltip.grapple_winch.projectile").append(CommonComponents.SPACE).append(itemStack.getDisplayName()));
            } else {
                MutableComponent inner = Component.translatable("klaxon.text.tooltip.grapple_winch.cable_length.display", "--", "--");
                if (player != null) {
                    // yoink the connection
                    ClientGrappleWinchConnectionManager manager = ClientGrappleWinchConnectionManager.get(Minecraft.getInstance().level);
                    @Nullable ClientGrappleWinchConnection connection = manager.fromPlayer(player);

                    // initialize max cable length
                    double maxCableLength = connection == null ? -1 : connection.getMaxCableLength();

                    // only render live numbers if cable length is greater than 0
                    // ensures no divide by 0
                    // connection not being present also
                    if (maxCableLength > 0) {
                        double truncatedCableLength = KlaxonMathHelper.roundToTenth(connection.getCableLength());
                        double ratio = truncatedCableLength / maxCableLength;

                        MutableComponent populatedCableDisplay = Component.translatable("klaxon.text.tooltip.grapple_winch.cable_length.display", truncatedCableLength, maxCableLength);

                        // format text according to cable ratio
                        if (ratio >= 1.0) {
                            inner = populatedCableDisplay.withStyle(ChatFormatting.RED);
                        } else if (ratio >= 0.75) {
                            inner = populatedCableDisplay.withStyle(ChatFormatting.YELLOW);
                        } else {
                            inner = populatedCableDisplay.withStyle(ChatFormatting.GREEN);
                        }
                    }
                }

                consumer.accept(
                        Component.translatable("klaxon.text.tooltip.grapple_winch.cable_length.prefix")
                                .withStyle(ChatFormatting.GRAY)
                                .append(inner)
                );
            }


        });

        // recipe output chance lore
        register(((stack, context, player, flag, consumer) -> {
            if (stack.get(KlaxonDataComponentTypes.RECIPE_OUTPUT_CHANCE_LORE.value()) instanceof Double chance) {
                ChatFormatting color;
                if (chance >= 0.75) {
                    color = ChatFormatting.GREEN;
                } else if (chance >= 0.5) {
                    color = ChatFormatting.YELLOW;
                } else if (chance >= 0.25) {
                    color = ChatFormatting.RED;
                } else {
                    color = ChatFormatting.DARK_RED;
                }

                consumer.accept(
                        Component.translatable("klaxon.text.tooltip.recipe_output_lore.chance",
                                KlaxonMathHelper.roundToDecimalPlace(chance * 100, 4) + "%"
                        ).withStyle(color)
                );
            }
        }));
        // heavy equipment
        register((stack, context, player, flag, consumer) -> {
            if (stack.is(KlaxonItemTags.HEAVY_EQUIPMENT)) {
                consumer.accept(Component.translatable("klaxon.text.tooltip.heavy_equipment").withStyle(ChatFormatting.GRAY));
            }
        });
        // modular explosive
        register(KlaxonDataComponentTypes.MODULAR_EXPLOSIVE_BLOCK_CONFIG);
        // explosive catalyst data
        register(KlaxonDataComponentTypes.EXPLOSIVE_CATALYST_DATA);
    }

    private static <T extends TooltipProvider> void register(Holder<DataComponentType<T>> typeHolder) {
        register(typeHolder.value());
    }

    private static <T extends TooltipProvider> void register(DataComponentType<T> type) {
        register((stack, context, player, flag, consumer) -> {
            if (stack.get(type) instanceof T component) {
                component.addToTooltip(context, consumer, flag);
            }
        });
    }

    private static void register(TooltipModifier modifier) {
        KlaxonItemTooltipModifications.MODIFIERS.add(modifier);
    }

    public interface TooltipModifier {
        void modify(ItemStack stack, Item.TooltipContext context, @Nullable Player player, TooltipFlag flag, Consumer<Component> consumer);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Initialized KLAXON's Item Tooltip Modifications!");
    }
}
