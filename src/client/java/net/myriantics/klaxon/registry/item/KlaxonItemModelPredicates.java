package net.myriantics.klaxon.registry.item;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.item.equipment.tools.GrappleWinchItem;
import net.myriantics.klaxon.mechanics.grapple_winch.ClientGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.ClientGrappleWinchConnectionManager;
import net.myriantics.klaxon.registry.render.KlaxonItemModelPredicateIds;
import org.jetbrains.annotations.Nullable;

public abstract class KlaxonItemModelPredicates {

    static {
        register(KlaxonItems.GRAPPLE_WINCH, KlaxonItemModelPredicateIds.WINCH_CABLE_LENGTH, ((stack, world, entity, seed) -> {
            boolean supportsCable = stack.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(stack);

            if (entity instanceof AbstractClientPlayer player && supportsCable) {
                ClientGrappleWinchConnectionManager manager = ClientGrappleWinchConnectionManager.get(world);
                @Nullable ClientGrappleWinchConnection connection = manager.fromPlayer(player);

                if (connection != null) {
                    if (connection.getMaxCableLength() > 0) {
                        return  (float) (connection.getCableLength() / connection.getMaxCableLength());
                    }
                }
            }

            // default to displaying a full spool
            return 0f;
        }));
        register(KlaxonItems.GRAPPLE_WINCH, KlaxonItemModelPredicateIds.CHARGED, ((stack, world, entity, seed) -> {
            ChargedProjectiles component = stack.get(DataComponents.CHARGED_PROJECTILES);
            if (component == null || component.isEmpty()) {
                return 0;
            } else {
                return 1;
            }
        }));
        register(KlaxonItems.GRAPPLE_WINCH, KlaxonItemModelPredicateIds.RETRACTING, (((stack, world, entity, seed) -> {
            ClientGrappleWinchConnectionManager manager = ClientGrappleWinchConnectionManager.get(world);
        if (entity instanceof Player player && manager.fromPlayer(player) instanceof ClientGrappleWinchConnection connection && connection.isRetracting()) {
            return 1.0f;
        }
            return 0;
        })));
        register(KlaxonItems.STEEL_LIGHTER, KlaxonItemModelPredicateIds.USE_RATIO, (itemStack, clientLevel, livingEntity, i) ->  {
            if (itemStack.get(KlaxonDataComponentTypes.USE_ACTION_MODEL_PREDICATE_OVERRIDE.value()) instanceof Float f) {
                return f;
            } else if (livingEntity != null && itemStack == livingEntity.getUseItem()) {
                int maxUseTime = itemStack.getUseDuration(livingEntity);
                return (float) (maxUseTime - livingEntity.getUseItemRemainingTicks()) / maxUseTime;
            }
            return 0f;
        });
    }

    private static void register(Holder<Item> itemHolder, ResourceLocation id, ClampedItemPropertyFunction function) {
        register(itemHolder.value(), id, function);
    }

    private static void register(Item item, ResourceLocation id, ClampedItemPropertyFunction function) {
        ItemProperties.register(item, id, function);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Item Model Predicates!");
    }
}
