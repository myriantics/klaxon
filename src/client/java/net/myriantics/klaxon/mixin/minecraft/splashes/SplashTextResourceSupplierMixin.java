package net.myriantics.klaxon.mixin.minecraft.splashes;

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
        // mod name
        original.add("Klaxons blaring!");
        // hallnox fungus / "i'm heading out of my mind" - dan bull
        original.add("Bioluminescent");
        // mabel - gravity falls
        original.add("GRAPPLING HOOK!");
        // "the production line" the stupendium
        original.add("Steel, Iron, Nuts and Bolts");
        // my signature catchphrase
        original.add("But um yeah.");
        // minecraft manga book funny moments
        original.add("THIS IS MY CRAFT!!!");
        original.add("OH MY BLOCKS!!!");
        return original;
    }
}
