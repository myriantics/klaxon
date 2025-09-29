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
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;

public abstract class KlaxonItemModelPredicates {

    public static final Identifier GRAPPLE_WINCH_CABLE = register(KlaxonItems.GRAPPLE_WINCH, KlaxonItemModelPredicateIds.WINCH_CABLE_LENGTH, ((stack, world, entity, seed) -> {
        if (entity instanceof PlayerEntity player) {
            AttributeContainer attributes = player.getAttributes();
            PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) player;

            if (access.klaxon$hasActiveConnection() && attributes.hasAttribute(KlaxonEntityAttributes.WINCH_CABLE_LENGTH)) {
                // returns numbers 0 thru 4 indicating the depletion stage of the grapple winch cable
                // 0 = 100%
                // 1 = 70% to 99%
                // 2 = 30% to 69%
                // 3 = 10% to 29%
                // 4 = 0% to 9%
                double remainingCableProportion = access.klaxon$getCurrentWinchCableLength() / entity.getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH);
                if (remainingCableProportion >= 1.00) {
                    return 0;
                } else if (remainingCableProportion >= 0.70) {
                    return 1;
                } else if (remainingCableProportion >= 0.30) {
                    return 2;
                } else if (remainingCableProportion >= 0.10) {
                    return 3;
                } else {
                    return 4;
                }
            }
        }

        // default to displaying a full spool
        return 0;
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
