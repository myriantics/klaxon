package net.myriantics.klaxon.datagen.model.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.mixin.minecraft.datagen.ModelAccessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class FancierModelBuilder {
    private final Identifier modelId;
    private final Model parentModel;
    private final Model model;
    private final Map<TextureKey, Identifier> defaultTextureMap;
    private final ArrayList<FancyOverride> overrides = new ArrayList<>();

    private FancierModelBuilder(Model parentModel, Identifier modelId, Map<TextureKey, Identifier> defaultTextureMap) {
        this.parentModel = parentModel;
        this.modelId = modelId;
        this.defaultTextureMap = defaultTextureMap;
        this.model = new Model(Optional.of(modelId), Optional.empty(), ((ModelAccessor) parentModel).klaxon$getRequiredTextures().toArray(new TextureKey[0]));
    }

    public static FancierModelBuilder of(Model rootModel, Identifier rootModelId, Map<TextureKey, Identifier> defaultTextureMap) {
        return new FancierModelBuilder(rootModel, rootModelId, defaultTextureMap);
    }

    public FancierModelBuilder modelOverride(Identifier predicateId, Map<Number, Model> valuesToModels) {
        return this.modelOverride(predicateId.toString(), valuesToModels);
    }

    public FancierModelBuilder modelOverride(String predicateId, Map<Number, Model> valueToModel) {
        this.validatePredicateId(predicateId);
        this.validateValues(predicateId, valueToModel.keySet());
        this.overrides.add(new FancyModelOverride(predicateId, valueToModel));
        return this;
    }

    public FancierModelBuilder textureOverride(Identifier predicateId, TextureKey textureKey, Map<Number, Identifier> valuesToTextureIds) {
        return this.textureOverride(predicateId.toString(), textureKey, valuesToTextureIds);
    }

    public FancierModelBuilder textureOverride(String predicateId, TextureKey textureKey, Map<Number, Identifier> valueToTextureIds) {
        this.validatePredicateId(predicateId);
        this.validateValues(predicateId, valueToTextureIds.keySet());
        this.overrides.add(new FancyTextureOverride(predicateId, textureKey, valueToTextureIds));
        return this;
    }

    private void validatePredicateId(String predicateId) {
        for (FancyOverride fancyOverride : overrides) {
            for (char c : fancyOverride.predicateId.toCharArray()) {
                if (!Identifier.isCharValid(c)) {
                    throw new IllegalArgumentException("Invalid character \"" + c + "\" present in predicate \"" + predicateId + "\" of: [" + this.getClass().getName() + ":" + this.modelId + "]");
                }
            }

            if (fancyOverride.predicateId.equals(predicateId)) {
                throw new IllegalArgumentException("Predicate ID \"" + predicateId + "\" already present in: [" + this.getClass().getName() + ":" + this.modelId + "]");
            }
        }
    }

    private void validateValues(String predicateId, Iterable<Number> values) {
        for (Number n : values) {
            if (n.floatValue() < 0 || n.floatValue() > 1) {
                throw new IllegalArgumentException("Value [" + n + "] of predicate \"" + predicateId + "\" out of bounds. Should be between [0, 1]. Problematic Model ID: \"" + this.modelId + "\"");
            }
        }
    }

    public void build(ItemModelGenerator generator) {
        this.build(generator.writer);
    }

    public void build(BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
        // collect the amount of overrides
        int overrideCount = 0;
        for (FancyOverride override : this.overrides) {
            if (overrideCount == 0) {
                overrideCount++;
            }
            overrideCount *= override.values.length;
        }

        JsonArray overrides = new JsonArray();

        // iterate through all the overrides and make a builder for each one
        FancierModelBuilder[] overrideBuilders = new FancierModelBuilder[overrideCount];
        for (int i = 0; i < overrideCount; i++) {
            Model overrideModel = model;
            HashMap<TextureKey, Identifier> textureMap = HashMap.newHashMap(defaultTextureMap.size());

            StringBuilder pathBuilder = new StringBuilder(modelId.getPath() + "/");

            int selector = i;

            JsonObject predicates = new JsonObject();

            // apply all the overrides
            for (FancyOverride override : this.overrides) {
                String predicateId = override.predicateId.lastIndexOf(Identifier.NAMESPACE_SEPARATOR) == -1 ? override.predicateId : override.predicateId.substring(override.predicateId.lastIndexOf(Identifier.NAMESPACE_SEPARATOR) + 1);
                int selectedIndex = selector % override.values.length;
                Number selectedValue = override.values[selectedIndex];
                if (override instanceof FancyModelOverride modelOverride) {
                    overrideModel = modelOverride.valuesToModels.get(selectedValue);
                    pathBuilder.append(predicateId).append("_").append(selectedValue).append("/");
                } else if (override instanceof FancyTextureOverride textureOverride) {
                    textureMap.put(textureOverride.textureKey, textureOverride.get(selectedValue));
                    if (pathBuilder.toString().charAt(pathBuilder.length() - 1) != '/') {
                        pathBuilder.append('_');
                    }
                    pathBuilder.append(predicateId).append('_').append(selectedValue);
                }
                selector -= selectedIndex;

                // add the property to the predicate list
                predicates.addProperty(override.predicateId, selectedValue);
            }

            // fill in any missing textures
            for (TextureKey textureKey : this.defaultTextureMap.keySet()) {
                for (TextureKey textureKey1 : textureMap.keySet().toArray(new TextureKey[]{})) {
                    if (!textureKey.toString().equals(textureKey1.toString())){
                        textureMap.put(textureKey, this.defaultTextureMap.get(textureKey));
                    }
                }
            }

            Identifier modelId = this.modelId.withPath(pathBuilder.toString());

            // add the builder to the array
            overrideBuilders[i] = FancierModelBuilder.of(
                    overrideModel,
                    modelId,
                    textureMap
            );

            // assemble the override object and add it to the array
            JsonObject override = new JsonObject();
            override.add("predicate", predicates);
            override.addProperty("model", modelId.toString());
            overrides.add(override);
        }

        // build the parent model, overrides and all
        modelCollector.accept(
                modelId,
                () -> {
                    JsonObject modelJson = parentModel.createJson(modelId, defaultTextureMap);
                    if (!overrides.isEmpty()) {
                        modelJson.add("overrides", overrides);
                    }
                    return modelJson;
                }
        );

        // build all the override model builders
        for (FancierModelBuilder builder : overrideBuilders) {
            builder.build(modelCollector);
        }
    }

    private static abstract class FancyOverride {
        protected final Number[] values;
        protected final String predicateId;

        protected FancyOverride(String predicateId, Number... values) {
            this.values = values;
            this.predicateId = predicateId;
        }

        public Number[] getValues() {
            return this.values;
        }

        public String getPredicateId() {
            return this.predicateId;
        }
    }

    private static final class FancyModelOverride extends FancyOverride {
        private final Map<Number, Model> valuesToModels;

        FancyModelOverride(String predicateId, Map<Number, Model> valuesToModels) {
            super(predicateId, valuesToModels.keySet().toArray(new Number[]{}));
            this.valuesToModels = valuesToModels;
        }
    }

    private static final class FancyTextureOverride extends FancyOverride {
        private final TextureKey textureKey;
        private final Map<Number, Identifier> valuesToTextures;

        FancyTextureOverride(String predicateId, TextureKey textureKey, Map<Number, Identifier> valuesToTextures) {
            super(predicateId, valuesToTextures.keySet().toArray(new Number[]{}));
            this.textureKey = textureKey;
            this.valuesToTextures = valuesToTextures;
        }

        private Identifier get(Number value) {
            return this.valuesToTextures.get(value);
        }
    }
}
