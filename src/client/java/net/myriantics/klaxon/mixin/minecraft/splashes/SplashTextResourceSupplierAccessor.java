package net.myriantics.klaxon.mixin.minecraft.splashes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import net.minecraft.client.resources.SplashManager;

@Mixin(SplashManager.class)
public interface SplashTextResourceSupplierAccessor {
    @Accessor(value = "splashes")
    List<String> klaxon$getSplashTexts();
}
