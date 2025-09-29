package net.myriantics.klaxon.datagen.model;

import net.minecraft.data.client.*;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.render.KlaxonItemModelPredicateIds;
import net.myriantics.klaxon.registry.render.KlaxonItemModelPredicates;
import net.myriantics.klaxon.registry.render.KlaxonModels;

import java.util.Map;

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
        Identifier modelId = getItemId(Registries.ITEM.getId(KlaxonItems.GRAPPLE_WINCH));
        Identifier unchargedTextureId = modelId.withPath((path) -> path + "_unloaded");

        Identifier chargedPredicate = KlaxonItemModelPredicateIds.CHARGED;

        FancyModelBuilder.of(Models.GENERATED)
                .id(getItemId(modelId.getPath()))
                .textureMap(TextureMap.layer0(unchargedTextureId))
                .override(modelId, KlaxonItemModelPredicateIds.CHARGED)
                .associateTexture(chargedPredicate, 1, TextureKey.of("layer0"))
                .add(1)
                .buildOverrides()
                .build(generator);
    }

    protected void register3DGrappleWinch() {
        Identifier raw3dId = Registries.ITEM.getId(KlaxonItems.GRAPPLE_WINCH).withPath((path) -> path + "_3d");
        Identifier modelId = getItemId(raw3dId);

        Identifier chargedPredicate = KlaxonItemModelPredicateIds.CHARGED;
        Identifier winchCableLengthPredicate = KlaxonItemModelPredicateIds.WINCH_CABLE_LENGTH;

        FancyModelBuilder.of(KlaxonModels.GRAPPLE_WINCH_3D)
                .id(modelId)
                .textureMap(Map.of(
                        "#structure", raw3dId.withPath((path) -> "item/" + path + "/grapple_winch_structure"),
                        "#spool", raw3dId.withPath((path) -> "item/" + path + "/cable_length_0"),
                        "#claw", raw3dId.withPath((path) -> "item/" + path + "/grapple_winch_claw"),
                        "#particle", raw3dId.withPath((path) -> "item/" + path + "/grapple_winch_structure")
                ))
                .override(modelId, chargedPredicate, winchCableLengthPredicate)
                .associateTexture(winchCableLengthPredicate, 1, TextureKey.of("spool"))
                .associateTexture(winchCableLengthPredicate, 2, TextureKey.of("spool"))
                .associateTexture(winchCableLengthPredicate, 3, TextureKey.of("spool"))
                .associateTexture(winchCableLengthPredicate, 4, TextureKey.of("spool"))
                .add(1, 1)
                .add(1, 2)
                .add(1, 3)
                .add(1, 4)
                .buildOverrides()
                .build(generator);
    }

    protected void registerArmor(Item armor) {
        if (armor instanceof ArmorItem armorItem) {
            generator.registerArmor(armorItem);
        } else {
            throw new IllegalArgumentException(armor + "is not an ArmorItem. Fix.");
        }
    }

    public static Identifier getItemId(String name) {
        return KlaxonCommon.locate("item/" + name);
    }

    public static Identifier getItemId(Identifier id) {
        return id.withPath((path) -> "item/" + path);
    }

    public static Identifier getSubModelId(Identifier id, String prefix) {
        return id.withPath((path) -> "item/" + prefix + "/" + path);
    }

    public static Identifier getSimpleModelId(String name) {
        return KlaxonCommon.locate("item/simple/" + name);
    }

    public static Identifier getAdvancedModelId(String name) {
        return KlaxonCommon.locate("item/advanced/" + name);
    }
}
