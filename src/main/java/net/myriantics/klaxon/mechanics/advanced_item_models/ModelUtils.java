package net.myriantics.klaxon.mechanics.advanced_item_models;

import net.minecraft.client.render.model.json.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.myriantics.klaxon.mixin.minecraft.advanced_item_models.JsonUnbakedModelAccessor;
import net.myriantics.klaxon.mixin.minecraft.advanced_item_models.ModelOverrideAccessor;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class ModelUtils {

    public static JsonUnbakedModel generateInvertedModel(JsonUnbakedModel model) {
        ArrayList<ModelElement> newElements = new ArrayList<>();

        JsonUnbakedModelAccessor accessor = (JsonUnbakedModelAccessor) model;

        // copy + mirror model elements
        for (ModelElement element : model.getElements()) {
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
                    uvs = new float[]{
                            uvs[0],
                            uvs[3],
                            uvs[2],
                            uvs[1]
                    };
                } else {
                    uvs = new float[]{
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
        for (ModelOverride override : model.getOverrides()) {
            Identifier mirroredOverrideModelId = AdvancedItemModelHelper.getMirroredId(override.getModelId());

            newOverrides.add(new ModelOverride(
                    mirroredOverrideModelId,
                    ((ModelOverrideAccessor) override).klaxon$getConditions()
            ));
        }

        @Nullable Identifier parentId = accessor.klaxon$getParentId();

        return new JsonUnbakedModel(
                parentId == null ? null : AdvancedItemModelHelper.getMirroredId(accessor.klaxon$getParentId()),
                newElements,
                accessor.klaxon$getTextureMap(),
                model.useAmbientOcclusion(),
                model.getGuiLight(),
                model.getTransformations(),
                newOverrides
        );
    }
}
