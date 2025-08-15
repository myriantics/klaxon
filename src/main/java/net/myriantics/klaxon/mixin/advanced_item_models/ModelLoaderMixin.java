package net.myriantics.klaxon.mixin.advanced_item_models;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.data.client.ModelIds;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.profiler.Profiler;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.util.advanced_item_models.AdvancedItemModelHelper;
import org.joml.Vector3f;
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

    // Registers the hammer model as an available resource you can pull from
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;loadItemModel(Lnet/minecraft/client/util/ModelIdentifier;)V", ordinal = 1))
    public void klaxon$loadFancyModels(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels, Map<Identifier, List<BlockStatesLoader.SourceTrackedData>> blockStates, CallbackInfo ci) {
        for (Item item : Registries.ITEM) {
            if (item.getComponents().get(KlaxonDataComponentTypes.ALT_HAND_MODEL) instanceof String suffix) {
                Identifier id = AdvancedItemModelHelper.getAlternateModelId(Registries.ITEM.getId(item), suffix);

                UnbakedModel model = getOrLoadModel(id.withPrefixedPath("item/"));
                add(ModelIdentifier.ofInventoryVariant(id), model);

                if (item.getComponents().contains(KlaxonDataComponentTypes.MIRRORED_LEFT_HAND_MODEL)) {

                    if (model instanceof JsonUnbakedModel jsonUnbakedModel && jsonUnbakedModel instanceof JsonUnbakedModelAccessor accessor) {
                        ArrayList<ModelElement> newElements = new ArrayList<>();


                        Identifier mirroredId = AdvancedItemModelHelper.getMirroredId(id);

                        // copy + mirror model elements
                        for (ModelElement element : jsonUnbakedModel.getElements()) {
                            // float x1 = -element.from.get(0);
                            // float x2 = -element.to.get(0);

                            Vector3f newFrom = new Vector3f(element.to);// .setComponent(0, x1);
                            Vector3f newTo = new Vector3f(element.from);// .setComponent(0, x2);

                            Map<Direction, ModelElementFace> newFaces = new HashMap<>();

                            for (Direction direction : element.faces.keySet()) {
                                ModelElementFace face = element.faces.get(direction);

                                float[] oldUvs = element.faces.get(direction.getOpposite()).textureData().uvs;

                                switch (direction.getAxis()) {
                                    case X -> {
                                    }
                                    case Y -> {
                                        //oldUvs = new float[] {oldUvs[2], oldUvs[3], oldUvs[0], oldUvs[1]};
                                    }
                                    case Z -> {
                                        //oldUvs = new float[] {oldUvs[0], oldUvs[3], oldUvs[2], oldUvs[1]};
                                    }
                                }

                                ModelElementTexture newTextureData = new ModelElementTexture(new float[] {
                                        oldUvs[0],
                                        oldUvs[1],
                                        oldUvs[2],
                                        oldUvs[3]
                                }, face.textureData().rotation);

                                newFaces.put(direction, new ModelElementFace(face.cullFace(), face.tintIndex(), face.textureId(), newTextureData));
                            }

                            newElements.add(new ModelElement(newFrom, newTo, Map.copyOf(newFaces), element.rotation, element.shade));
                        }

                        ArrayList<ModelOverride> newOverrides = new ArrayList<>();

                        // copy overrides
                        for (ModelOverride override : jsonUnbakedModel.getOverrides()) {
                            UnbakedModel overrideModel = getOrLoadModel(override.getModelId());

                            Identifier newOverrideModelId = AdvancedItemModelHelper.getMirroredId(override.getModelId());

                            if (overrideModel instanceof JsonUnbakedModel jayson && jayson instanceof JsonUnbakedModelAccessor jaysonAccess) {
                                JsonUnbakedModel invertedOverrideModel =  new JsonUnbakedModel(
                                        jaysonAccess.klaxon$getParentId(),
                                        jayson.getElements(),
                                        jaysonAccess.klaxon$getTextureMap(),
                                        jayson.useAmbientOcclusion(),
                                        jayson.getGuiLight(),
                                        jsonUnbakedModel.getTransformations(),
                                        jsonUnbakedModel.getOverrides()
                                );

                                invertedOverrideModel.setParents(this::getOrLoadModel);

                                add(ModelIdentifier.ofInventoryVariant(newOverrideModelId), invertedOverrideModel);
                            }

                            newOverrides.add(new ModelOverride(newOverrideModelId, ((ModelOverrideAccessor) override).klaxon$getConditions()
                            ));
                        }

                        JsonUnbakedModel invertedModel = new JsonUnbakedModel(
                                accessor.klaxon$getParentId(),
                                newElements,
                                accessor.klaxon$getTextureMap(),
                                jsonUnbakedModel.useAmbientOcclusion(),
                                jsonUnbakedModel.getGuiLight(),
                                jsonUnbakedModel.getTransformations(),
                                newOverrides
                        );

                        invertedModel.setParents(this::getOrLoadModel);

                        add(new ModelIdentifier(mirroredId, "inventory"), invertedModel);
                    }
                }
            }


        }
    }
}

