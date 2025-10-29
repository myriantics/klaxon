package net.myriantics.klaxon.mixin.minecraft.loot;

import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.loot.provider.number.LootNumberProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BoundedIntUnaryOperator.class)
public interface BoundedIntUnaryOperatorInvoker {
    @Invoker(value = "<init>")
    static BoundedIntUnaryOperator klaxon$init(@Nullable LootNumberProvider min, @Nullable LootNumberProvider max) {
        throw new AssertionError();
    }
}
