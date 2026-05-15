package net.myriantics.klaxon.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.myriantics.klaxon.util.container.KlaxonBaseContainerMenu;

public abstract class BaseKlaxonContainerScreen<T extends KlaxonBaseContainerMenu> extends AbstractContainerScreen<T> {
    public BaseKlaxonContainerScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    public int getOffsetX(int x) {
        return this.leftPos + x;
    }

    public int getOffsetY(int y) {
        return this.topPos + y;
    }
}
