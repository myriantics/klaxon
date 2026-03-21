package net.myriantics.klaxon.mixin.minecraft.gerald_sniffer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.myriantics.klaxon.mechanics.gerald_sniffer.GeraldSnifferHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(
            method = "setCustomName",
            at = @At(value = "HEAD")
    )
    private void klaxon$checkForGeraldSnifferRenameIfPossible(Component name, CallbackInfo ci) {
        if ((Object) this instanceof Sniffer snifferEntity) {
            GeraldSnifferHelper.onCustomNameSet(snifferEntity, name);
        }
    }
}
