package net.myriantics.klaxon.registry.item;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.item.equipment.tools.GrappleWinchItem;
import net.myriantics.klaxon.mechanics.grapple_winch.ClientGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.ClientGrappleWinchConnectionManager;
import net.myriantics.klaxon.registry.render.KlaxonItemModelPredicateIds;
import org.jetbrains.annotations.Nullable;

public abstract class KlaxonItemModelPredicates {

    public static final ResourceLocation GRAPPLE_WINCH_CABLE = register(KlaxonItems.GRAPPLE_WINCH, KlaxonItemModelPredicateIds.WINCH_CABLE_LENGTH, ((stack, world, entity, seed) -> {
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

    public static final ResourceLocation GRAPPLE_WINCH_CHARGED = register(KlaxonItems.GRAPPLE_WINCH, KlaxonItemModelPredicateIds.CHARGED, ((stack, world, entity, seed) -> {
        ChargedProjectiles component = stack.get(DataComponents.CHARGED_PROJECTILES);
        if (component == null || component.isEmpty()) {
            return 0;
        } else {
            return 1;
        }
    }));

    public static final ResourceLocation GRAPPLE_WINCH_RETRACTING = register(KlaxonItems.GRAPPLE_WINCH, KlaxonItemModelPredicateIds.RETRACTING, (((stack, world, entity, seed) -> {
        ClientGrappleWinchConnectionManager manager = ClientGrappleWinchConnectionManager.get(world);
        /*if (entity instanceof PlayerEntity player && manager.fromPlayer(player) instanceof ClientGrappleWinchConnection connection && connection.isRetracting()) {
            return 1.0f;
        }*/
        return 0;
    })));

    private static ResourceLocation register(Item item, ResourceLocation id, ClampedItemPropertyFunction provider) {
        ItemProperties.register(item, id, provider);
        return id;
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Item Model Predicates!");
    }
}
