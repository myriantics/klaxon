package net.myriantics.klaxon.registry.render;

import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.item.equipment.tools.GrappleWinchItem;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ClientGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ClientGrappleWinchConnectionManager;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import org.jetbrains.annotations.Nullable;

public abstract class KlaxonItemModelPredicates {

    public static final Identifier GRAPPLE_WINCH_CABLE = register(KlaxonItems.GRAPPLE_WINCH, KlaxonItemModelPredicateIds.WINCH_CABLE_LENGTH, ((stack, world, entity, seed) -> {
        boolean supportsCable = stack.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(stack);

        if (entity instanceof AbstractClientPlayerEntity player && supportsCable) {
            ClientGrappleWinchConnectionManager manager = ((ClientGrappleWinchConnectionManager.Access) player.getWorld()).klaxon$get();
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

    public static final Identifier GRAPPLE_WINCH_CHARGED = register(KlaxonItems.GRAPPLE_WINCH, KlaxonItemModelPredicateIds.CHARGED, ((stack, world, entity, seed) -> {
        ChargedProjectilesComponent component = stack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (component == null || component.isEmpty()) {
            return 0;
        } else {
            return 1;
        }
    }));

    private static Identifier register(Item item, Identifier id, ClampedModelPredicateProvider provider) {
        ModelPredicateProviderRegistry.register(item, id, provider);
        return id;
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Item Model Predicates!");
    }
}
