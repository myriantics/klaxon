package net.myriantics.klaxon.mixin.minecraft.gerald_sniffer;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import net.myriantics.klaxon.mechanics.gerald_sniffer.GeraldSnifferState;
import net.myriantics.klaxon.mechanics.gerald_sniffer.SnifferEntityMixinAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NameTagItem.class)
public abstract class NameTagItemMixin {
    @Inject(
            method = "interactLivingEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setCustomName(Lnet/minecraft/network/chat/Component;)V")
    )
    private void klaxon$overrideGeraldSnifferState(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        // due to setCustomName being used as the general setter for the names of entities, including reading from NBT, we can't just set the gerald state to tracking every time
        // because that would mean that it always starts tracking whenever the nbt is set
        // so that's why i added the FINISHED_TRACKING state
        // this mixin allows you to override that state with a name tag for ease of use, if you wanted to farm crested steel helmets / other gerald drops if someone adds them
        if (entity instanceof SnifferEntityMixinAccess access && access.klaxon$getGeraldSnifferState().equals(GeraldSnifferState.TRACKING_FINISHED)) {
            access.klaxon$setGeraldSnifferState(GeraldSnifferState.TRACKING_UNSUPPORTED);
        }
    }
}
