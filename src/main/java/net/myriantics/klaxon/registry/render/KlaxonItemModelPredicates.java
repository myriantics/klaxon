package net.myriantics.klaxon.registry.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.GrappleWinchItem;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;

public abstract class KlaxonItemModelPredicates {

    public static final Identifier GRAPPLE_WINCH_CABLE = register(KlaxonItems.GRAPPLE_WINCH, KlaxonItemModelPredicateIds.WINCH_CABLE_LENGTH, ((stack, world, entity, seed) -> {
        boolean supportsCable = stack.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(stack);

        if (entity instanceof PlayerEntity player && supportsCable) {
            AttributeContainer attributes = player.getAttributes();
            PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) player;

            if (access.klaxon$hasActiveConnection() && attributes.hasAttribute(KlaxonEntityAttributes.WINCH_CABLE_LENGTH)) {
                return  (float) (access.klaxon$getCurrentWinchCableLength() / entity.getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH));
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
