package net.myriantics.klaxon.mixin;

import net.minecraft.block.BlockSetType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockSetType.class)
public interface BlockSetTypeInvoker {

    @Invoker("register")
    public static BlockSetType klaxon$register(BlockSetType blockSetType) {
        throw new AssertionError();
    };
}
