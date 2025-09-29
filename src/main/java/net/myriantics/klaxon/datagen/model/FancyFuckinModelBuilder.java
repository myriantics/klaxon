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
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mixin.minecraft.datagen.ModelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class FancyFuckinModelBuilder {
    private final Model model;
    private final JsonArray overrides;
    private Identifier modelId;
    private Map<TextureKey, Identifier> textureMap;
    private TextureMap uncoolTextureMap;
    private @Nullable ModelAwareJsonFactory jsonFactory;

    private static final String validationString = "item/";

    private FancyFuckinModelBuilder(Model model) {
        this.model = model;
        this.overrides = new JsonArray();
    }

    public static FancyFuckinModelBuilder of(Model model) {
        return new FancyFuckinModelBuilder(model);
    }

    public FancyFuckinModelBuilder id(String id) {
        this.id(KlaxonItemModelSubProvider.getItemId(id));
        return this;
    }

    public FancyFuckinModelBuilder id(Identifier id) {
        String substring = id.getPath().substring(0, 5);

        if (!substring.equals(validationString)) {
            throw new IllegalArgumentException("Got: \"" + substring + "\", expected: \"" + validationString + "\"");
        }

        this.modelId = id;
        return this;
    }

    public FancyFuckinModelBuilder textureMap(TextureMap textureMap) {
        this.textureMap = createTextureMap(textureMap);
        return this;
    }

    public FancyFuckinModelBuilder textureMap(Map<String, Identifier> map) {
        HashMap<TextureKey, Identifier> textureMap = HashMap.newHashMap(map.size());

        TextureKey[] requiredTextures = ((ModelAccessor) this.model).klaxon$getRequiredTextures().toArray(new TextureKey[] {});

        for (TextureKey textureKey : requiredTextures) {
            String string = textureKey.toString().substring(1);

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

    public FancyFuckinModelBuilder override(Map<Identifier, Number> predicates, Identifier overrideModelId) {
        JsonObject object = new JsonObject();
        JsonObject predicate = new JsonObject();

        for (Identifier id : predicates.keySet()) {
            predicate.addProperty(id.toString(), predicates.get(id));
        }

        object.add("predicate", predicate);
        object.addProperty("model", overrideModelId.toString());

        this.overrides.add(object);
        return this;
    }

    public void build(ItemModelGenerator generator) {
        this.build(generator.writer);
    }

    public void build(BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
        JsonObject builtObject = model.createJson(modelId, textureMap);

        builtObject.add("overrides", overrides);

        modelCollector.accept(
                modelId,
                () -> builtObject
        );
    }

    private Map<TextureKey, Identifier> createTextureMap(TextureMap textures) {
        return Streams.concat(((ModelAccessor) model).klaxon$getRequiredTextures().stream(), textures.getInherited())
                .collect(ImmutableMap.toImmutableMap(Function.identity(), textures::getTexture));
    }

    public interface ModelAwareJsonFactory {
        JsonObject create(Identifier id, Map<TextureKey, Identifier> textures, Model model);
    }
}
