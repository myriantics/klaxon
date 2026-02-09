package net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_claw;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnderDragonEntity.class)
public interface EnderDragonEntityAccessor {
    @Accessor("body")
    EnderDragonPart getBody();
}
