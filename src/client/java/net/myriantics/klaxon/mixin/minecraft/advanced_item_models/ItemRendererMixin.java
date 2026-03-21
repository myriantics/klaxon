package net.myriantics.klaxon.mixin.minecraft.advanced_item_models;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.mechanics.advanced_item_models.AdvancedItemModelHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow public abstract ItemModelShaper getItemModelShaper();

    @WrapOperation(
            method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V")
    )
    private void klaxon$overrideItemModel(ItemRenderer instance, ItemStack stack, ItemDisplayContext renderMode, boolean leftHanded, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, BakedModel model, Operation<Void> original, @Local(argsOnly = true) Level world, @Local(argsOnly = true) LivingEntity entity, @Local(argsOnly = true, ordinal = 2) int seed) {
        if (stack.get(KlaxonDataComponentTypes.ALT_HAND_MODEL) instanceof String suffix && renderMode != ItemDisplayContext.GUI && renderMode != ItemDisplayContext.FIXED && renderMode != ItemDisplayContext.GROUND) {
            ResourceLocation id = AdvancedItemModelHelper.getAlternateModelId(BuiltInRegistries.ITEM.getKey(stack.getItem()), suffix);

            if (leftHanded && stack.has(KlaxonDataComponentTypes.MIRRORED_LEFT_HAND_MODEL)) {
                id = AdvancedItemModelHelper.getMirroredId(id);
            }

            BakedModel newModel = getItemModelShaper().getModelManager().getModel(ModelResourceLocation.inventory(id));

            newModel = newModel.getOverrides().resolve(newModel, stack, (ClientLevel) world, entity, seed);

            // apply while keeping overrides
            original.call(instance, stack, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, newModel);
        } else {
            original.call(instance, stack, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, model);
        }
    }
}
