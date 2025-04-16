package net.myriantics.klaxon.registry.minecraft;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ShearsDispenserBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.myriantics.klaxon.KlaxonCommon;

public class KlaxonDispenserBehaviors {

    public static DispenserBehavior CABLE_SHEARS_BEHAVIOR = register(KlaxonItems.STEEL_CABLE_SHEARS, new ShearsDispenserBehavior());

    private static DispenserBehavior register(ItemConvertible item, DispenserBehavior behavior) {
        DispenserBlock.registerBehavior(item, behavior);
        return behavior;
    }

    public static void registerDispenserBehaviors() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Dispenser Behaviors!");
    }
}
