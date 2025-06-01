package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(SplashTextResourceSupplier.class)
public abstract class SplashTextResourceSupplierMixin {

    @ModifyReturnValue(
            method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Ljava/util/List;",
            at = @At(value = "RETURN")
    )
    private List<String> klaxon$appendSplashTexts(List<String> original) {
        // original.add("We Love The Company!");
        original.add("Klaxons blaring!");
        // original.add("Bioluminescent");
        original.add("The gang's all here and the mood is electric!");
        original.add("Steel, Iron, Nuts and Bolts");
        return original;
    }
}
