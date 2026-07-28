package net.myriantics.klaxon.block.machines.blast_processor.steel;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.myriantics.klaxon.block.machines.blast_processor.AbstractBlastProcessorMenu;
import net.myriantics.klaxon.networking.s2c.BlastProcessorMenuPowerSyncPacket;
import net.myriantics.klaxon.registry.misc.KlaxonMenuTypes;

public class SteelBlastProcessorMenu extends AbstractBlastProcessorMenu {
    public SteelBlastProcessorMenu(int containerId, Inventory playerInventory, BlastProcessorMenuPowerSyncPacket initializer) {
        super(KlaxonMenuTypes.DEEPSLATE_BLAST_PROCESSOR.value(), containerId, playerInventory, initializer);
    }

    public SteelBlastProcessorMenu(int containerId, Inventory playerInventory, Container container, ContainerData data, ContainerLevelAccess access) {
        super(KlaxonMenuTypes.DEEPSLATE_BLAST_PROCESSOR.value(), containerId, playerInventory, container, data, access);
    }

    @Override
    protected int getIngredientStackSize() {
        return 4;
    }
}
