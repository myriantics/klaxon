package net.myriantics.klaxon.mixin.minecraft.advanced_item_models;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.data.client.Model;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.profiler.Profiler;
import net.myriantics.klaxon.mechanics.advanced_item_models.ModelUtils;
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

import java.util.*;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {

    @Shadow abstract UnbakedModel getOrLoadModel(Identifier id);

    @Shadow protected abstract void add(ModelIdentifier id, UnbakedModel model);

    @Shadow protected abstract void addModelToBake(ModelIdentifier id, UnbakedModel model);

    @Shadow @Final private Map<Identifier, UnbakedModel> unbakedModels;

    // Registers alt models and generates inverted models
    @Inject(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/model/ModelLoader;loadInventoryVariantItemModel(Lnet/minecraft/util/Identifier;)V"
            )
    )
    public void klaxon$loadFancyModels(
            BlockColors blockColors,
            Profiler profiler,
            Map<Identifier, JsonUnbakedModel> jsonUnbakedModels,
            Map<Identifier, List<BlockStatesLoader.SourceTrackedData>> blockStates,
            CallbackInfo ci,
            @Local Identifier itemId
    ) {
        Item item = Registries.ITEM.get(itemId);

        if (item.getComponents().get(KlaxonDataComponentTypes.ALT_HAND_MODEL) instanceof String suffix) {
            Identifier modelId = AdvancedItemModelHelper.getAlternateModelId(Registries.ITEM.getId(item), suffix);

            JsonUnbakedModel model = (JsonUnbakedModel) getOrLoadModel(modelId.withPrefixedPath("item/"));
            Collection<Identifier> modelDependencies = model.getModelDependencies();

            ArrayList<Identifier> processedIds = new ArrayList<>();
            while (modelDependencies != null && !modelDependencies.isEmpty()) {
                Collection<Identifier> newDependencies = new ArrayList<>();

                modelDependencies.forEach((identifier -> {
                    // protection against an infinite loop - it will exhaust itself eventually
                    if (processedIds.contains(identifier)) {
                        return;
                    }

                    Identifier mirroredId = AdvancedItemModelHelper.getMirroredId(identifier);

                    JsonUnbakedModel selected = (JsonUnbakedModel) this.getOrLoadModel(identifier);
                    JsonUnbakedModel mirrored = ModelUtils.generateInvertedModel(selected);

                    // add selected model and mirrored variant to baking
                    add(ModelIdentifier.ofInventoryVariant(identifier), selected);
                    this.addModelToBake(
                            ModelIdentifier.ofInventoryVariant(mirroredId),
                            mirrored
                    );
                    unbakedModels.put(mirroredId, mirrored);
                    newDependencies.addAll(selected.getModelDependencies());

                    // make sure we don't process the same id twice.
                    processedIds.add(identifier);
                }));
                // update the model dependencies with the next list to process
                modelDependencies = newDependencies;
            }

            ModelIdentifier mirroredModelId = ModelIdentifier.ofInventoryVariant(AdvancedItemModelHelper.getMirroredId(modelId));
            JsonUnbakedModel invertedModel = ModelUtils.generateInvertedModel(model);

            add(ModelIdentifier.ofInventoryVariant(modelId), model);
            this.addModelToBake(mirroredModelId, invertedModel);

            unbakedModels.put(mirroredModelId.id(), invertedModel);
            addModelToBake(mirroredModelId, invertedModel);
        }
    }
}

