package net.myriantics.klaxon.mixin.dual_wielding;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.myriantics.klaxon.util.LivingEntityMixinAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Shadow private float prevEquipProgressOffHand;

    @Shadow private float equipProgressOffHand;

    @Shadow private float prevEquipProgressMainHand;

    @Shadow private float equipProgressMainHand;

    @WrapOperation(
            method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 0)
    )
    public void klaxon$mainHandItemAnimationOverride(HeldItemRenderer instance, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Operation<Void> original, @Local Hand preferredHand) {

        // only run this if player is dual wielding and hand to be rendered isn't preferred hand
        if (player instanceof LivingEntityMixinAccess access && access.klaxon$isDualWielding() && !preferredHand.equals(hand)) {
            original.call(instance, player, tickDelta, pitch, hand, player.getHandSwingProgress(tickDelta), item, 1.0f - MathHelper.lerp(tickDelta, prevEquipProgressOffHand, equipProgressOffHand), matrices, vertexConsumers, light);
        } else {
            original.call(instance, player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
        }
    }

    @WrapOperation(
            method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 1)
    )
    public void klaxon$offHandItemAnimationOverride(HeldItemRenderer instance, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Operation<Void> original, @Local Hand preferredHand) {

        // only run this if player is dual wielding and hand to be rendered isn't preferred hand
        if (player instanceof LivingEntityMixinAccess access && access.klaxon$isDualWielding() && !preferredHand.equals(hand)) {
            original.call(instance, player, tickDelta, pitch, hand, player.getHandSwingProgress(tickDelta), item, 1.0f - MathHelper.lerp(tickDelta, prevEquipProgressMainHand, equipProgressMainHand), matrices, vertexConsumers, light);
        } else {
            original.call(instance, player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
        }
    }
}
