package net.myriantics.klaxon.mechanics.muffling;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

public class MufflerSlot extends Slot {

    private final Player player;
    private final ContainerLevelAccess access;

    public MufflerSlot(Player player, ContainerLevelAccess access, Container container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.player = player;
        this.access = access;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.is(KlaxonItemTags.MUFFLERS);
    }

    @Override
    public void set(ItemStack newStack) {
        ItemStack oldStack = this.getItem();
        super.set(newStack);
        if (ItemStack.isSameItemSameComponents(newStack, this.getItem())) {
            this.triggerAdvancements(oldStack, newStack);
        }
    }

    @Override
    public void setByPlayer(ItemStack newStack, ItemStack oldStack) {
        super.setByPlayer(newStack, oldStack);
        this.triggerAdvancements(oldStack, newStack);
    }

    private void triggerAdvancements(ItemStack oldStack, ItemStack newStack) {
        if (this.player instanceof ServerPlayer serverPlayer && this.access != null) {
            this.access.execute((level, pos) -> {
                if (!oldStack.isEmpty()) {
                    KlaxonAdvancementTriggers.triggerMufflerInteraction(serverPlayer, pos, MufflerActionType.REMOVE, ItemStack.EMPTY, oldStack);
                }
                if (!newStack.isEmpty()) {
                    KlaxonAdvancementTriggers.triggerMufflerInteraction(serverPlayer, pos, MufflerActionType.APPLY, newStack, ItemStack.EMPTY);
                }
            });
        }
    }
}
