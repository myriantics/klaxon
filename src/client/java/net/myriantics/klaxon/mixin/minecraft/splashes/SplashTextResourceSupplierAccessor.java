package net.myriantics.klaxon.mixin.minecraft.splashes;

import net.minecraft.client.resource.SplashTextResourceSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(SplashTextResourceSupplier.class)
public interface SplashTextResourceSupplierAccessor {
    @Accessor(value = "splashTexts")
    List<String> klaxon$getSplashTexts();
}
