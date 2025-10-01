package net.myriantics.klaxon.mixin.minecraft.advanced_item_models;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.profiler.Profiler;
import net.myriantics.klaxon.mixin.minecraft.datagen.ModelAccessor;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.util.advanced_item_models.AdvancedItemModelHelper;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {

    @Shadow abstract UnbakedModel getOrLoadModel(Identifier id);

    @Shadow protected abstract void add(ModelIdentifier id, UnbakedModel model);

    @Shadow protected abstract void addModelToBake(ModelIdentifier id, UnbakedModel model);

    @Shadow @Final private Map<Identifier, UnbakedModel> unbakedModels;

    @Shadow
    @Final
    private UnbakedModel missingModel;

    // Registers the hammer model as an available resource you can pull from
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;loadItemModel(Lnet/minecraft/client/util/ModelIdentifier;)V", ordinal = 0))
    public void klaxon$loadFancyModels(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels, Map<Identifier, List<BlockStatesLoader.SourceTrackedData>> blockStates, CallbackInfo ci) {
        for (Item item : Registries.ITEM) {
            if (item.getComponents().get(KlaxonDataComponentTypes.ALT_HAND_MODEL) instanceof String suffix) {
                Identifier id = AdvancedItemModelHelper.getAlternateModelId(Registries.ITEM.getId(item), suffix);

                UnbakedModel model = getOrLoadModel(id.withPrefixedPath("item/"));
                UnbakedModel parentModel = getOrLoadModel(((JsonUnbakedModelAccessor) model).klaxon$getParentId());
                add(ModelIdentifier.ofInventoryVariant(id), model);

                if (item.getComponents().contains(KlaxonDataComponentTypes.MIRRORED_LEFT_HAND_MODEL)) {

                    if (model instanceof JsonUnbakedModel jsonUnbakedModel && jsonUnbakedModel instanceof JsonUnbakedModelAccessor accessor) {
                        ArrayList<ModelElement> newElements = new ArrayList<>();

                        Identifier mirroredModelId = AdvancedItemModelHelper.getMirroredId(id);

                        // copy + mirror model elements
                        for (ModelElement element : jsonUnbakedModel.getElements().isEmpty() ? ((JsonUnbakedModel) parentModel).getElements() : jsonUnbakedModel.getElements()) {
                            Vector3f from = new Vector3f(element.from);
                            Vector3f to = new Vector3f(element.to);

                            // invert elements on the X axis
                            Vector3f newFrom = new Vector3f(8f - (to.x - 8f), from.y, from.z);// .setComponent(0, x1);
                            Vector3f newTo = new Vector3f(8f - (from.x - 8f), to.y, to.z);// .setComponent(0, x2);

                            boolean shouldAltTextureReflection = (newFrom.x < 8f && newTo.x < 8f) || (newFrom.x > 8f && newTo.x > 8f);

                            Map<Direction, ModelElementFace> newFaces = new HashMap<>();

                            for (Direction direction : element.faces.keySet()) {
                                ModelElementFace face = element.faces.get(direction);

                                ModelElementFace usedFace = element.faces.get(direction.getAxis().equals(Direction.Axis.X) ? direction.getOpposite() : direction);

                                float[] uvs = usedFace.textureData().uvs;

                                if (shouldAltTextureReflection && !direction.getAxis().equals(Direction.Axis.X)) {
                                    uvs = new float[] {
                                            uvs[0],
                                            uvs[3],
                                            uvs[2],
                                            uvs[1]
                                    };
                                } else {
                                    uvs = new float[] {
                                            uvs[2],
                                            uvs[1],
                                            uvs[0],
                                            uvs[3]
                                    };
                                }

                                ModelElementTexture newTextureData = new ModelElementTexture(uvs, face.textureData().rotation);

                                newFaces.put(direction, new ModelElementFace(face.cullFace(), face.tintIndex(), face.textureId(), newTextureData));
                            }

                            // rotate that shi
                            ModelRotation newRotation = new ModelRotation(
                                    element.rotation.origin(),
                                    element.rotation.axis(),
                                    element.rotation.axis().equals(Direction.Axis.X) ? element.rotation.angle() : -element.rotation.angle(),
                                    element.rotation.rescale()
                            );

                            newElements.add(new ModelElement(newFrom, newTo, Map.copyOf(newFaces), newRotation, element.shade));
                        }

                        ArrayList<ModelOverride> newOverrides = new ArrayList<>();

                        // copy overrides
                        for (ModelOverride override : jsonUnbakedModel.getOverrides()) {
                            UnbakedModel overrideModel = getOrLoadModel(override.getModelId());

                            Identifier mirroredOverrideModelId = AdvancedItemModelHelper.getMirroredId(override.getModelId());

                            if (overrideModel instanceof JsonUnbakedModel overrideJsonModel && overrideJsonModel instanceof JsonUnbakedModelAccessor overrideJsonModelAccess) {
                                JsonUnbakedModel mirroredOverrideModel =  new JsonUnbakedModel(
                                        mirroredModelId,
                                        overrideJsonModel.getElements(),
                                        overrideJsonModelAccess.klaxon$getTextureMap(),
                                        overrideJsonModel.useAmbientOcclusion(),
                                        overrideJsonModel.getGuiLight(),
                                        overrideJsonModel.getTransformations(),
                                        overrideJsonModel.getOverrides()
                                );

                                addModelToBake(ModelIdentifier.ofInventoryVariant(mirroredOverrideModelId), mirroredOverrideModel);
                                unbakedModels.put(mirroredOverrideModelId, mirroredOverrideModel);
                            }

                            newOverrides.add(new ModelOverride(mirroredOverrideModelId, ((ModelOverrideAccessor) override).klaxon$getConditions()));
                        }

                        JsonUnbakedModel invertedModel = new JsonUnbakedModel(
                                accessor.klaxon$getParentId(),
                                newElements.isEmpty() ? ((JsonUnbakedModel) missingModel).getElements() : newElements,
                                accessor.klaxon$getTextureMap(),
                                jsonUnbakedModel.useAmbientOcclusion(),
                                jsonUnbakedModel.getGuiLight(),
                                jsonUnbakedModel.getTransformations(),
                                newOverrides
                        );

                        unbakedModels.put(mirroredModelId, invertedModel);
                        addModelToBake(ModelIdentifier.ofInventoryVariant(mirroredModelId), invertedModel);
                    }
                }
            }

        }
    }
}

