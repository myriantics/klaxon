package net.myriantics.klaxon.datagen.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.client.*;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public abstract class KlaxonItemModelSubProvider {
    public final KlaxonModelProvider provider;
    public final ItemModelGenerator generator;

    public KlaxonItemModelSubProvider(KlaxonModelProvider provider, ItemModelGenerator generator) {
        this.provider = provider;
        this.generator = generator;
    }

    public abstract void generateModels();

    protected void registerSimpleItem(Item item) {
        generator.register(item, Models.GENERATED);
    }

    protected void register2DGrappleWinch() {
        Identifier rawId = Registries.ITEM.getId(KlaxonItems.GRAPPLE_WINCH);
        Identifier unchargedTextureId = rawId.withPath((path) -> "item/" + path + "_unloaded");
        Identifier chargedTextureId = rawId.withPath((path) -> "item/" + path + "_loaded");
        Identifier chargedModelId = rawId.withPath((path) -> "item/" + path + "_loaded");
        Models.GENERATED.upload(
                rawId.withPath((path) -> "item/" + path),
                TextureMap.layer0(unchargedTextureId),
                generator.writer,
                ((id, textures) -> {
                    JsonObject jsonObject = Models.GENERATED.createJson(id, textures);
                    JsonArray jsonArray = new JsonArray();

                    JsonObject loadedObject = new JsonObject();

                    JsonObject chargedPredicate = new JsonObject();
                    chargedPredicate.addProperty(KlaxonCommon.locate("charged").toString(), 1);

                    loadedObject.add("predicate", chargedPredicate);
                    loadedObject.addProperty("model", chargedModelId.toString());
                    jsonArray.add(loadedObject);

                    jsonObject.add("overrides", jsonArray);
                    return jsonObject;
                })
        );
        Models.GENERATED.upload(
                chargedModelId,
                TextureMap.layer0(chargedTextureId),
                generator.writer
        );
    }

    protected void registerArmor(Item armor) {
        if (armor instanceof ArmorItem armorItem) {
            generator.registerArmor(armorItem);
        } else {
            throw new IllegalArgumentException(armor + "is not an ArmorItem. Fix.");
        }
    }
}
