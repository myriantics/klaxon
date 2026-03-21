package net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_claw;

import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnderDragon.class)
public interface EnderDragonEntityAccessor {
    @Accessor("body")
    EnderDragonPart getBody();
}
