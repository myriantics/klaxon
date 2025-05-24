package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.compat.jade.KlaxonJadePlugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import snownee.jade.addon.harvest.ShearsToolHandler;

import java.util.List;

@Pseudo
@Mixin(ShearsToolHandler.class)
public class ShearsToolHandlerMixin {
    @ModifyExpressionValue(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Ljava/util/List;of(Ljava/lang/Object;)Ljava/util/List;")
    )
    private static List<ItemStack> klaxon$cableShearsOverride(List<ItemStack> original) {
        return KlaxonJadePlugin.appendCableShears(original);
    }
}
