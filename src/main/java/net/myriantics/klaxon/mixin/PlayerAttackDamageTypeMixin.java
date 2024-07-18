package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DamageSources.class)
public abstract class PlayerAttackDamageTypeMixin {

    @Shadow public abstract DamageSource playerAttack(PlayerEntity attacker);

    @ModifyReturnValue(method = "playerAttack", at = @At(value = "RETURN"))
    private DamageSource checkHeldItem(DamageSource original) {
        Entity attacker = original.getAttacker();
        if (attacker instanceof PlayerEntity player) {
            player.sendMessage(Text.literal("cooldown: " + player.getAttackCooldownProgress(0.5f)));
            player.sendMessage(Text.literal("cooldown: " +
                    ((player.getAttackCooldownProgress(0.5f)
                    * player.getAttackCooldownProgressPerTick())
                    - 0.5f)
            ));
            if (player.)
        }
        return original;
    }
}
