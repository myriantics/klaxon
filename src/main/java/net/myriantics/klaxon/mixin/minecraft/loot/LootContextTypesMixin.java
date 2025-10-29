package net.myriantics.klaxon.mixin.minecraft.loot;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.myriantics.klaxon.registry.loot.KlaxonLootContextParameters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LootContextTypes.class)
public abstract class LootContextTypesMixin {
    @ModifyExpressionValue(
            method = "method_60302",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/context/LootContextType$Builder;allow(Lnet/minecraft/loot/context/LootContextParameter;)Lnet/minecraft/loot/context/LootContextType$Builder;")
    )
    private static LootContextType.Builder klaxon$allowDamageAmountLootContextProvider(LootContextType.Builder original) {
        return original.allow(KlaxonLootContextParameters.DAMAGE_DEALT);
    }
}
