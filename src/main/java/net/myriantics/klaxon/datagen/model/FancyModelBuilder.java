package net.myriantics.klaxon.datagen.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.mixin.minecraft.datagen.ModelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class FancyModelBuilder {
    private final Model parentModel;
    private final JsonArray overrides;
    private Identifier modelId;
    private Map<TextureKey, Identifier> textureMap;
    private final ArrayList<FancyModelBuilder> overrideModelBuilder = new ArrayList<>();

    private static final String validationString = "item/";

    private FancyModelBuilder(Model parentModel) {
        this.parentModel = parentModel;
        this.overrides = new JsonArray();
    }

    public static FancyModelBuilder of(Model parent) {
        return new FancyModelBuilder(parent);
    }

    public FancyModelBuilder id(String id) {
        this.id(KlaxonItemModelSubProvider.getItemId(id));
        return this;
    }

    public FancyModelBuilder id(Identifier id) {
        String substring = id.getPath().substring(0, 5);

        if (!substring.equals(validationString)) {
            throw new IllegalArgumentException("Got: \"" + substring + "\", expected: \"" + validationString + "\"");
        }

        this.modelId = id;
        return this;
    }

    public FancyModelBuilder textureMap(TextureMap textureMap) {
        this.textureMap = createTextureMap(textureMap);
        return this;
    }

    public FancyModelBuilder textureMap(Map<String, Identifier> map) {
        HashMap<TextureKey, Identifier> textureMap = HashMap.newHashMap(map.size());

        TextureKey[] requiredTextures = ((ModelAccessor) this.parentModel).klaxon$getRequiredTextures().toArray(new TextureKey[] {});

        for (TextureKey textureKey : requiredTextures) {
            String string = textureKey.toString();

            if (map.containsKey(string)) {
                textureMap.put(textureKey, map.get(string));
            }
        }

        if (textureMap.isEmpty()) {
            throw new IllegalArgumentException("No keys provided in " + map + " matched up with keys in " + Arrays.toString(requiredTextures));
        }

        this.textureMap = textureMap;
        return this;
    }

    public FancyOverrideBuilder override(Identifier baseModelId, Identifier... predicateIds) {
        return new FancyOverrideBuilder(this, baseModelId, predicateIds);
    }

    public void build(ItemModelGenerator generator) {
        this.build(generator.writer);
    }

    public void build(BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
        JsonObject builtObject = parentModel.createJson(modelId, textureMap);

        // don't bother adding an empty overrides list if there are none
        if (!overrides.isEmpty()) {
            builtObject.add("overrides", overrides);
        }

        modelCollector.accept(
                modelId,
                () -> builtObject
        );

        // build all the models that we assembled when doing override crap
        for (FancyModelBuilder builder : this.overrideModelBuilder) {
            builder.build(modelCollector);
        }
    }

    private Map<TextureKey, Identifier> createTextureMap(TextureMap textures) {
        return Streams.concat(((ModelAccessor) parentModel).klaxon$getRequiredTextures().stream(), textures.getInherited())
                .collect(ImmutableMap.toImmutableMap(Function.identity(), textures::getTexture));
    }

    public static class FancyOverrideBuilder {

        private final FancyModelBuilder builder;
        private final Identifier baseModelId;
        private final Identifier[] predicateIds;
        private final ArrayList<Map<Identifier, Number>> predicates = new ArrayList<>();
        private final Map<Identifier, Map<Number, TextureKey>> overrideTextureAssociations;
        private final Map<Identifier, Map<Number, Model>> overrideModelAssociations;

        private FancyOverrideBuilder(FancyModelBuilder builder, Identifier baseModelId, Identifier... predicateIds) {
            this.builder = builder;
            this.baseModelId = baseModelId;
            this.predicateIds = predicateIds;
            this.overrideTextureAssociations = HashMap.newHashMap(predicateIds.length);
            this.overrideModelAssociations = HashMap.newHashMap(predicateIds.length);
        }

        public FancyOverrideBuilder associateTexture(Identifier predicateId, Number value, TextureKey textureKey) {
            @Nullable Map<Number, TextureKey> valueToKeyMap = overrideTextureAssociations.get(predicateId);

            // if a map hasn't been created yet
            if (valueToKeyMap == null) {
                valueToKeyMap = new HashMap<>();
                this.overrideTextureAssociations.put(predicateId, valueToKeyMap);
            }

            if (valueToKeyMap.containsKey(value)) {
                throw new IllegalArgumentException("FancyOverrideBuilder of model [" + baseModelId + "] already has associated [\"" + predicateId + "\": " + value + "] with: \"" + valueToKeyMap.get(value));
            }

            valueToKeyMap.put(value, textureKey);

            return this;
        }

        public FancyOverrideBuilder associateModel(Identifier predicateId, Number value, Model model) {
            @Nullable Map<Number, Model> valueToModelMap = this.overrideModelAssociations.get(predicateId);

            // make sure predicate id is valid
            boolean invalid = true;
            for (Identifier id : predicateIds) {
                if (id.equals(predicateId)) {
                    invalid = false;
                    break;
                }
            }
            if (invalid) {
                throw new IllegalArgumentException("Invalid predicate associated! Predicate [" + predicateId + "]" + "not found in [" + Arrays.toString(this.predicateIds) + "]");
            }

            // if a map hasn't been created yet
            if (valueToModelMap == null) {
                valueToModelMap = new HashMap<>();
                this.overrideModelAssociations.put(predicateId, valueToModelMap);
            }

            if (valueToModelMap.containsKey(value)) {
                throw new IllegalArgumentException("FancyOverrideBuilder of model [" + baseModelId + "] already has associated [\"" + predicateId + "\": " + value + "] with: \"" + valueToModelMap.get(value));
            }

            valueToModelMap.put(value, model);

            return this;
        }

        public FancyOverrideBuilder add(Number... values) {
            if (values.length != predicateIds.length) {
                throw new IllegalArgumentException("Number of values doesn't match number of predicates! Expected [" + predicateIds.length + "], got [" + values.length + "]");
            }

            HashMap<Identifier, Number> working = HashMap.newHashMap(predicateIds.length);

            // map out the numeric values to the predicate ids
            for (int i = 0; i < predicateIds.length; i++) {
                Identifier predicateId = predicateIds[i];
                Number value = values[i];

                working.put(predicateId, value);
            }

            // add the new map to the larger list of them
            this.predicates.add(working);

            return this;
        }

        public FancyModelBuilder buildOverrides() {
            String basePath = baseModelId.getPath();

            for (Map<Identifier, Number> predicate : this.predicates) {
                // set up the parent model and texture overrides - ready for overriding
                Model parentModel = this.builder.parentModel;
                HashMap<String, Identifier> textureOverrides = HashMap.newHashMap(predicate.size());

                // prep the override and predicate objects
                JsonObject overrideObject = new JsonObject();
                JsonObject predicateObject = new JsonObject();

                StringBuilder suffixBuilder = new StringBuilder();
                for (Identifier conditionId : predicate.keySet()) {
                    Number value = predicate.get(conditionId);

                    String predicateSuffix = conditionId.getPath() + "_" + value;
                    boolean overrideActionPerformed = false;

                    // if the parent model hasn't already been overwritten, and an override exists for it, apply that override
                    if (this.overrideModelAssociations.containsKey(conditionId) && parentModel == builder.parentModel) {
                        @Nullable Model overrideModel = this.overrideModelAssociations.get(conditionId).get(value);

                        // make sure we don't overwrite the parent model with a null value
                        if (overrideModel != null) {
                            parentModel = overrideModel;
                            overrideActionPerformed = true;
                        }
                    }

                    if (this.overrideTextureAssociations.containsKey(conditionId)) {
                        @Nullable TextureKey textureKey = this.overrideTextureAssociations.get(conditionId).get(value);

                        // add the texture into the specialized directory
                        if (textureKey != null) {
                            textureOverrides.put(
                                    textureKey.toString(),
                                    baseModelId.withPath(basePath + "/" + predicateSuffix)
                            );
                            overrideActionPerformed = true;
                        }
                    }

                    // add onto the json object
                    predicateObject.addProperty(conditionId.toString(), value);

                    // add the formatted suffix to the builder - only if an override was performed
                    if (overrideActionPerformed) {
                        if (!suffixBuilder.isEmpty()) {
                            suffixBuilder.append('_');
                        }
                        suffixBuilder.append(predicateSuffix);
                    }
                }

                // you gotta do at least one modification if you're doing overrides
                if (suffixBuilder.isEmpty()) {
                    throw new IllegalArgumentException("Overrides must apply at least one transformation. Overrides missing associations: " + Arrays.toString(predicateIds));
                }

                Identifier overrideModelId = this.baseModelId.withPath(basePath + '/' + suffixBuilder);

                overrideObject.add("predicate", predicateObject);
                overrideObject.addProperty("model", overrideModelId.toString());

                this.builder.overrides.add(overrideObject);

                // add newly made override model to the last fancy builder for it to build when it builds
                // bob the builder
                if (parentModel != this.builder.parentModel || !textureOverrides.isEmpty()) {
                    this.builder.overrideModelBuilder.add(FancyModelBuilder.of(parentModel).id(overrideModelId).textureMap(textureOverrides));
                }
            }

            return this.builder;
        }
    }
}
