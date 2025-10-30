package net.myriantics.klaxon.mixin.minecraft.gerald_sniffer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.myriantics.klaxon.mechanics.gerald_sniffer.GeraldSnifferHelper;
import net.myriantics.klaxon.mechanics.gerald_sniffer.SnifferEntityMixinAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(
            method = "setCustomName",
            at = @At(value = "HEAD")
    )
    private void klaxon$checkForGeraldSnifferRenameIfPossible(Text name, CallbackInfo ci) {
        if ((Object) this instanceof SnifferEntity snifferEntity) {
            GeraldSnifferHelper.onCustomNameSet(snifferEntity, name);
        }
    }
}
