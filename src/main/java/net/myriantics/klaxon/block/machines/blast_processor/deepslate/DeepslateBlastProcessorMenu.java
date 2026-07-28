package net.myriantics.klaxon.block.machines.blast_processor.deepslate;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.myriantics.klaxon.block.machines.blast_processor.AbstractBlastProcessorMenu;
import net.myriantics.klaxon.networking.s2c.BlastProcessorMenuPowerSyncPacket;
import net.myriantics.klaxon.registry.misc.KlaxonMenuTypes;

public class DeepslateBlastProcessorMenu extends AbstractBlastProcessorMenu {
    public DeepslateBlastProcessorMenu(int containerId, Inventory playerInventory, BlastProcessorMenuPowerSyncPacket initializer) {
        super(KlaxonMenuTypes.DEEPSLATE_BLAST_PROCESSOR.value(), containerId, playerInventory, initializer);
    }

    public DeepslateBlastProcessorMenu(int containerId, Inventory playerInventory, Container container, ContainerData data, ContainerLevelAccess access) {
        super(KlaxonMenuTypes.DEEPSLATE_BLAST_PROCESSOR.value(), containerId, playerInventory, container, data, access);
    }
}
