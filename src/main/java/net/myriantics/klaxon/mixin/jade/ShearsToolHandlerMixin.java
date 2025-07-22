package net.myriantics.klaxon.mixin.jade;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.compat.jade.KlaxonJadePlugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import snownee.jade.addon.harvest.ShearsToolHandler;

import java.util.List;

@Pseudo
@Mixin(ShearsToolHandler.class)
public abstract class ShearsToolHandlerMixin {
    @ModifyExpressionValue(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Ljava/util/List;of(Ljava/lang/Object;)Ljava/util/List;"),
            remap = false
    )
    private static List<ItemStack> klaxon$cableShearsOverride(List<ItemStack> original) {
        try {
            original = KlaxonJadePlugin.appendCableShears(original);
        } catch (Exception e) {
            KlaxonCommon.LOGGER.error(e.getMessage());
        }

        return original;
    }
}
