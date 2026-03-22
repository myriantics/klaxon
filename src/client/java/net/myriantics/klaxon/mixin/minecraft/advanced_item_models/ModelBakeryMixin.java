package net.myriantics.klaxon.mixin.minecraft.advanced_item_models;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.myriantics.klaxon.mechanics.advanced_item_models.ModelUtils;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.mechanics.advanced_item_models.AdvancedItemModelHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

    @Shadow abstract UnbakedModel getModel(ResourceLocation id);

    @Shadow protected abstract void registerModelAndLoadDependencies(ModelResourceLocation id, UnbakedModel model);

    @Shadow protected abstract void registerModel(ModelResourceLocation id, UnbakedModel model);

    @Shadow @Final private Map<ResourceLocation, UnbakedModel> unbakedCache;

    // Registers alt models and generates inverted models
    @Inject(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/ModelBakery;loadItemModelAndDependencies(Lnet/minecraft/resources/ResourceLocation;)V"
            )
    )
    public void klaxon$loadFancyModels(
            BlockColors blockColors,
            ProfilerFiller profiler,
            Map<ResourceLocation, BlockModel> jsonUnbakedModels,
            Map<ResourceLocation, List<BlockStateModelLoader.LoadedJson>> blockStates,
            CallbackInfo ci,
            @Local ResourceLocation itemId
    ) {
        Item item = BuiltInRegistries.ITEM.get(itemId);

        if (item.components().get(KlaxonDataComponentTypes.ALT_HAND_MODEL.value()) instanceof String suffix) {
            ResourceLocation modelId = AdvancedItemModelHelper.getAlternateModelId(BuiltInRegistries.ITEM.getKey(item), suffix);

            if (!(getModel(modelId.withPrefix("item/")) instanceof BlockModel model)) {
                return;
            }

            Collection<ResourceLocation> modelDependencies = model.getDependencies();

            ArrayList<ResourceLocation> processedIds = new ArrayList<>();
            while (modelDependencies != null && !modelDependencies.isEmpty()) {
                Collection<ResourceLocation> newDependencies = new ArrayList<>();

                modelDependencies.forEach((identifier -> {
                    // protection against an infinite loop - it will exhaust itself eventually
                    if (processedIds.contains(identifier)) {
                        return;
                    }

                    ResourceLocation mirroredId = AdvancedItemModelHelper.getMirroredId(identifier);

                    BlockModel selected = (BlockModel) this.getModel(identifier);
                    BlockModel mirrored = ModelUtils.generateInvertedModel(selected);

                    // add selected model and mirrored variant to baking
                    registerModelAndLoadDependencies(ModelResourceLocation.inventory(identifier), selected);
                    this.registerModel(
                            ModelResourceLocation.inventory(mirroredId),
                            mirrored
                    );
                    unbakedCache.put(mirroredId, mirrored);
                    newDependencies.addAll(selected.getDependencies());

                    // make sure we don't process the same id twice.
                    processedIds.add(identifier);
                }));
                // update the model dependencies with the next list to process
                modelDependencies = newDependencies;
            }

            ModelResourceLocation mirroredModelId = ModelResourceLocation.inventory(AdvancedItemModelHelper.getMirroredId(modelId));
            BlockModel invertedModel = ModelUtils.generateInvertedModel(model);

            registerModelAndLoadDependencies(ModelResourceLocation.inventory(modelId), model);
            this.registerModel(mirroredModelId, invertedModel);

            unbakedCache.put(mirroredModelId.id(), invertedModel);
            registerModel(mirroredModelId, invertedModel);
        }
    }
}

