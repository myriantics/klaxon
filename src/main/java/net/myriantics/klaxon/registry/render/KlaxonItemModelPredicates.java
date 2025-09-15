package net.myriantics.klaxon.registry.render;

import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;

public abstract class KlaxonItemModelPredicates {

    public static ClampedModelPredicateProvider GRAPPLE_WINCH_CABLE = register(KlaxonItems.GRAPPLE_WINCH, "winch_cable", ((stack, world, entity, seed) -> {
        if (
                entity instanceof PlayerEntityGrappleAccess access
                        && access.klaxon$hasActiveConnection()
                        && entity.getAttributes() != null
                        && entity.getAttributes().hasAttribute(KlaxonEntityAttributes.WINCH_CABLE_LENGTH)
        ) {
            // returns numbers 0 thru 4 indicating the depletion stage of the grapple winch cable
            // 0 = 0% to 9%
            // 1 = 10% to 29%
            // 2 = 30% to 69%
            // 3 = 70% to 99%
            // 4 = 100%
            double remainingCableProportion = access.klaxon$getCurrentWinchCableLength() / entity.getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH);
            if (remainingCableProportion == 1.00) {
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

        // default to displaying a full spool
        return 0;
    }));

    private static ClampedModelPredicateProvider register(String name, ClampedModelPredicateProvider provider) {
        return ModelPredicateProviderRegistry.register(KlaxonCommon.locate(name), provider);
    }

    private static ClampedModelPredicateProvider register(Item item, String name, ClampedModelPredicateProvider provider) {
        ModelPredicateProviderRegistry.register(item, KlaxonCommon.locate(name), provider);
        return provider;
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Item Model Predicates!");
    }
}
