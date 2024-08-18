package net.myriantics.klaxon.mixin.blast_processor;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityScreenHandlerMixin {

    @Inject(method = "openHandledScreen", at = @At(value = "RETURN", ordinal = 2))
    public void onScreenHandlerOpened(NamedScreenHandlerFactory factory, CallbackInfoReturnable<OptionalInt> cir, @Local ScreenHandler screenHandler) {
        if (screenHandler instanceof BlastProcessorScreenHandler blastProcessorScreenHandler) {
            // i just want it to update inventory on open
            // theres probably a way better method of doing this but idk so we're doing this
            blastProcessorScreenHandler.onContentChanged(blastProcessorScreenHandler.getIngredientInventory());
        }
    }
}
