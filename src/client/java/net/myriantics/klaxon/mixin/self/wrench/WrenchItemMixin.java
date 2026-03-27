package net.myriantics.klaxon.mixin.self.wrench;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.myriantics.klaxon.item.equipment.tools.WrenchItem;
import net.myriantics.klaxon.mechanics.wrench.WrenchInteractionOverlayManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WrenchItem.class)
public abstract class WrenchItemMixin {
    @Inject(
            method = "useOn",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/InteractionResult;FAIL:Lnet/minecraft/world/InteractionResult;", opcode = Opcodes.GETSTATIC)
    )
    private void klaxon$spawnErrorOverlay(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (context.getLevel() instanceof WrenchInteractionOverlayManager.Access access) {
            access.klaxon$get().spawnDetachedInteractionOverlay(20);
        }
    }
}
