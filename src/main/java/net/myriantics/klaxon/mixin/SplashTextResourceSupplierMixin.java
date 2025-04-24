package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextResourceSupplierMixin {

    @ModifyReturnValue(
            method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Ljava/util/List;",
            at = @At(value = "RETURN")
    )
    private List<String> klaxon$appendSplashTexts(List<String> original) {
        original.add("We Love The Company!");
        original.add("Not a One-Trick Pony!");
        original.add("Klaxons blaring!");
        original.add("Bioluminescent");

        return original;
    }
}
