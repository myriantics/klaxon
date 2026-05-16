package net.myriantics.klaxon.screen.container.blast_processor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.myriantics.klaxon.block.machines.blast_processor.steel.SteelBlastProcessorMenu;

public class Rizz extends DeepslateBlastProcessorScreen<SteelBlastProcessorMenu> {
    public Rizz(SteelBlastProcessorMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }
}
