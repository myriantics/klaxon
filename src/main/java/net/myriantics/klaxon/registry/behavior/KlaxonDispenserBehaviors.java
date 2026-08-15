package net.myriantics.klaxon.registry.behavior;

import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.modular_explosive.ModularExplosiveBlockDispenserBehavior;
import net.myriantics.klaxon.mechanics.wrench.WrenchDispenserBehavior;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationDispenserBehavior;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public abstract class KlaxonDispenserBehaviors {

    // used in DispenserBlockMixin
    public static WorldItemApplicationDispenserBehavior MANUAL_ITEM_APPLICATION_BEHAVIOR = new WorldItemApplicationDispenserBehavior();

    static {
        register(KlaxonItems.STEEL_CABLE_SHEARS, new ShearsDispenseItemBehavior());
        register(KlaxonItems.STEEL_WRENCH, new WrenchDispenserBehavior());
        register(KlaxonItems.STEEL_GRAPPLE_CLAW, new ProjectileDispenseBehavior(KlaxonItems.STEEL_GRAPPLE_CLAW.value()));
        register(KlaxonItems.STEEL_LIGHTER, DispenserBlock.DISPENSER_REGISTRY.get(Items.FLINT_AND_STEEL));
        register(KlaxonItems.MODULAR_EXPLOSIVE_BLOCK, new ModularExplosiveBlockDispenserBehavior());

    }

    private static void register(Holder<Item> itemHolder, DispenseItemBehavior behavior) {
        register(itemHolder.value(), behavior);
    }

    private static void register(ItemLike item, DispenseItemBehavior behavior) {
        DispenserBlock.registerBehavior(item, behavior);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Dispenser Behaviors!");
    }
}
