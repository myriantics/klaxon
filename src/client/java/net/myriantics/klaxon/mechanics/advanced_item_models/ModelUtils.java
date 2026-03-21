package net.myriantics.klaxon.mechanics.advanced_item_models;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.klaxon.mixin.minecraft.advanced_item_models.JsonUnbakedModelAccessor;
import net.myriantics.klaxon.mixin.minecraft.advanced_item_models.ModelOverrideAccessor;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class ModelUtils {

    public static BlockModel generateInvertedModel(BlockModel model) {
        ArrayList<BlockElement> newElements = new ArrayList<>();

        JsonUnbakedModelAccessor accessor = (JsonUnbakedModelAccessor) model;

        // copy + mirror model elements
        for (BlockElement element : model.getElements()) {
            Vector3f from = new Vector3f(element.from);
            Vector3f to = new Vector3f(element.to);

            // invert elements on the X axis
            Vector3f newFrom = new Vector3f(8f - (to.x - 8f), from.y, from.z);// .setComponent(0, x1);
            Vector3f newTo = new Vector3f(8f - (from.x - 8f), to.y, to.z);// .setComponent(0, x2);

            boolean shouldAltTextureReflection = (newFrom.x < 8f && newTo.x < 8f) || (newFrom.x > 8f && newTo.x > 8f);

            Map<Direction, BlockElementFace> newFaces = new HashMap<>();

            for (Direction direction : element.faces.keySet()) {
                BlockElementFace face = element.faces.get(direction);

                BlockElementFace usedFace = element.faces.get(direction.getAxis().equals(Direction.Axis.X) ? direction.getOpposite() : direction);

                float[] uvs = usedFace.uv().uvs;

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

                BlockFaceUV newTextureData = new BlockFaceUV(uvs, face.uv().rotation);

                newFaces.put(direction, new BlockElementFace(face.cullForDirection(), face.tintIndex(), face.texture(), newTextureData));
            }

            // rotate that shi
            BlockElementRotation newRotation = new BlockElementRotation(
                    element.rotation.origin(),
                    element.rotation.axis(),
                    element.rotation.axis().equals(Direction.Axis.X) ? element.rotation.angle() : -element.rotation.angle(),
                    element.rotation.rescale()
            );

            newElements.add(new BlockElement(newFrom, newTo, Map.copyOf(newFaces), newRotation, element.shade));
        }

        ArrayList<ItemOverride> newOverrides = new ArrayList<>();

        // copy overrides
        for (ItemOverride override : model.getOverrides()) {
            ResourceLocation mirroredOverrideModelId = AdvancedItemModelHelper.getMirroredId(override.getModel());

            newOverrides.add(new ItemOverride(
                    mirroredOverrideModelId,
                    ((ModelOverrideAccessor) override).klaxon$getConditions()
            ));
        }

        @Nullable ResourceLocation parentId = accessor.klaxon$getParentId();

        return new BlockModel(
                parentId == null ? null : AdvancedItemModelHelper.getMirroredId(accessor.klaxon$getParentId()),
                newElements,
                accessor.klaxon$getTextureMap(),
                model.hasAmbientOcclusion(),
                model.getGuiLight(),
                model.getTransforms(),
                newOverrides
        );
    }
}
