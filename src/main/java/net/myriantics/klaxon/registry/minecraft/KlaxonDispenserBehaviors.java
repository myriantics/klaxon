package net.myriantics.klaxon.registry.minecraft;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ShearsDispenserBehavior;
import net.minecraft.item.ItemConvertible;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.behavior.dispenser.ManualItemApplicationDispenserBehavior;
import net.myriantics.klaxon.api.behavior.dispenser.WrenchDispenserBehavior;

public class KlaxonDispenserBehaviors {

    // used in DispenserBlockMixin
    public static ManualItemApplicationDispenserBehavior MANUAL_ITEM_APPLICATION_BEHAVIOR = new ManualItemApplicationDispenserBehavior();

    public static DispenserBehavior CABLE_SHEARS_BEHAVIOR = register(KlaxonItems.STEEL_CABLE_SHEARS, new ShearsDispenserBehavior());
    public static DispenserBehavior WRENCH_BEHAVIOR = register(KlaxonItems.STEEL_WRENCH, new WrenchDispenserBehavior());

    private static DispenserBehavior register(ItemConvertible item, DispenserBehavior behavior) {
        DispenserBlock.registerBehavior(item, behavior);
        return behavior;
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Dispenser Behaviors!");
    }
}
