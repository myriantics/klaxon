package net.myriantics.klaxon.mixin;

import net.minecraft.datafixer.fix.ItemInstanceSpawnEggFix;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AnvilScreenHandler.class)
public interface AnvilScreenHandlerInvoker {
    @Invoker("onTakeOutput")
    void klaxon$invokeOnTakeOutput(PlayerEntity player, ItemStack stack);

    @Invoker("canTakeOutput")
    boolean klaxon$invokeCanTakeOutput(PlayerEntity player, boolean present);
}
