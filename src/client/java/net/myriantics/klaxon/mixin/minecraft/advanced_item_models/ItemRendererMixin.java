package net.myriantics.klaxon.mixin.minecraft.advanced_item_models;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.mechanics.advanced_item_models.AdvancedItemModelHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow public abstract ItemModels getModels();

    @WrapOperation(
            method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V")
    )
    private void klaxon$overrideItemModel(ItemRenderer instance, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, Operation<Void> original, @Local(argsOnly = true) World world, @Local(argsOnly = true) LivingEntity entity, @Local(argsOnly = true, ordinal = 2) int seed) {
        if (stack.get(KlaxonDataComponentTypes.ALT_HAND_MODEL) instanceof String suffix && renderMode != ModelTransformationMode.GUI && renderMode != ModelTransformationMode.FIXED && renderMode != ModelTransformationMode.GROUND) {
            Identifier id = AdvancedItemModelHelper.getAlternateModelId(Registries.ITEM.getId(stack.getItem()), suffix);

            if (leftHanded && stack.contains(KlaxonDataComponentTypes.MIRRORED_LEFT_HAND_MODEL)) {
                id = AdvancedItemModelHelper.getMirroredId(id);
            }

            BakedModel newModel = getModels().getModelManager().getModel(ModelIdentifier.ofInventoryVariant(id));

            newModel = newModel.getOverrides().apply(newModel, stack, (ClientWorld) world, entity, seed);

            // apply while keeping overrides
            original.call(instance, stack, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, newModel);
        } else {
            original.call(instance, stack, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, model);
        }
    }
}
