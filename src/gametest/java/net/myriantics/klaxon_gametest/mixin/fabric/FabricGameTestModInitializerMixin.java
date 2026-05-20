package net.myriantics.klaxon_gametest.mixin.fabric;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.fabric.impl.gametest.FabricGameTestModInitializer;
import net.myriantics.klaxon_gametest.KlaxonGameTester;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FabricGameTestModInitializer.class)
public class FabricGameTestModInitializerMixin {
    @WrapMethod(
            method = "getModIdForTestClass",
            remap = false
    )
    private static String klaxon$allminehahahaha(Class<?> testClass, Operation<String> original) {
        return KlaxonGameTester.MOD_ID;
    }
}
