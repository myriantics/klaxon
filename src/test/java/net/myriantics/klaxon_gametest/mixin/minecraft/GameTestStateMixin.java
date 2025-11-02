package net.myriantics.klaxon_gametest.mixin.minecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import net.myriantics.klaxon_gametest.util.KlaxonTestContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameTestState.class)
public abstract class GameTestStateMixin {
    @WrapOperation(
            method = "start",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/test/TestFunction;start(Lnet/minecraft/test/TestContext;)V")
    )
    public void klaxon$useMyTextContextBecauseItsCooler(TestFunction instance, TestContext context, Operation<Void> original) {
        original.call(instance, new KlaxonTestContext((GameTestState) (Object) this));
    }
}
