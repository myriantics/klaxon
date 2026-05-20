package net.myriantics.klaxon_gametest.mixin.minecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.TestFunction;
import net.myriantics.klaxon_gametest.util.KlaxonGameTestHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameTestInfo.class)
public abstract class GameTestStateMixin {
    @WrapOperation(
            method = "startTest",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/gametest/framework/TestFunction;run(Lnet/minecraft/gametest/framework/GameTestHelper;)V")
    )
    public void klaxon$useMyTextContextBecauseItsCooler(TestFunction instance, GameTestHelper context, Operation<Void> original) {
        original.call(instance, new KlaxonGameTestHelper((GameTestInfo) (Object) this));
    }
}
