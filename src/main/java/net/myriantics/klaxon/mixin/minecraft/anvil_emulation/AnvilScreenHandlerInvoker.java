package net.myriantics.klaxon.mixin.minecraft.anvil_emulation;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AnvilMenu.class)
public interface AnvilScreenHandlerInvoker {
    @Invoker("onTake")
    void klaxon$invokeOnTakeOutput(Player player, ItemStack stack);

    @Invoker("mayPickup")
    boolean klaxon$invokeCanTakeOutput(Player player, boolean present);
}
