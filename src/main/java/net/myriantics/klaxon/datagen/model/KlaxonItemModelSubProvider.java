package net.myriantics.klaxon.datagen.model;

import net.minecraft.data.client.*;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.datagen.model.item.FancierModelBuilder;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.render.KlaxonItemModelPredicateIds;
import net.myriantics.klaxon.registry.render.KlaxonModels;
import net.myriantics.klaxon.registry.render.KlaxonTextures;

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

        // predicate
        Identifier chargedPredicate = KlaxonItemModelPredicateIds.CHARGED;

        // texture key
        TextureKey layer0 = TextureKey.LAYER0;

        FancierModelBuilder.of(Models.GENERATED, modelId, Map.of(
                layer0, KlaxonTextures.GRAPPLE_WINCH_2D_UNLOADED
                ))
                .textureOverride(chargedPredicate, TextureKey.of("layer0"), Map.of(
                        0, KlaxonTextures.GRAPPLE_WINCH_2D_UNLOADED,
                        1, KlaxonTextures.GRAPPLE_WINCH_2D_LOADED
                ))
                .build(generator);
    }

    protected void register3DGrappleWinch() {
        Identifier raw3dId = Registries.ITEM.getId(KlaxonItems.GRAPPLE_WINCH).withPath((path) -> path + "_3d");
        Identifier modelId = getItemId(raw3dId);

        // predicates
        Identifier chargedPredicate = KlaxonItemModelPredicateIds.CHARGED;
        Identifier winchCableLengthPredicate = KlaxonItemModelPredicateIds.WINCH_CABLE_LENGTH;

        // texture keys
        TextureKey structure = TextureKey.of("structure");
        TextureKey claw = TextureKey.of("claw");
        TextureKey spool = TextureKey.of("spool");

        FancierModelBuilder.of(KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_4, modelId, Map.of(
                        structure, KlaxonTextures.GRAPPLE_WINCH_3D_STRUCTURE,
                        claw, KlaxonTextures.EMPTY,
                        spool, KlaxonTextures.GRAPPLE_WINCH_3D_SPOOL
                ))
                .modelOverride(winchCableLengthPredicate, Map.of(
                        0, KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_4,
                        0.25, KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_3,
                        0.5, KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_2,
                        0.75, KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_1,
                        1.0, KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_0
                ))
                .textureOverride(chargedPredicate, TextureKey.of("claw"), Map.of(
                        0, KlaxonTextures.EMPTY,
                        1, KlaxonTextures.GRAPPLE_WINCH_3D_CLAW
                ))
                .build(generator);

        /*
        FancyModelBuilder.of(KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_4, modelId)
                .textureMap(Map.of(
                        "#structure", raw3dId.withPath((path) -> "item/" + path + "/structure/structure"),
                        "#spool", raw3dId.withPath((path) -> "item/" + path + "/spool/cable_length_4"),
                        "#claw", raw3dId.withPath((path) -> "item/" + path + "/claw/charged_0")
                ))
                .override(modelId, chargedPredicate, winchCableLengthPredicate)
                .associateModel(winchCableLengthPredicate, 0, KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_4)
                .associateModel(winchCableLengthPredicate, 0.25, KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_3)
                .associateModel(winchCableLengthPredicate, 0.5, KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_2)
                .associateModel(winchCableLengthPredicate, 0.75, KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_1)
                .associateModel(winchCableLengthPredicate, 1, KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_0)
                .associateTexture(chargedPredicate, 0, TextureKey.of("claw"))
                .associateTexture(chargedPredicate, 1, TextureKey.of("claw"))
                .add(0, 0)
                .add(0, 0.25)
                .add(0, 0.5)
                .add(0, 0.75)
                .add(0, 1)
                .add(1, 0)
                .add(1, 0.25)
                .add(1, 0.5)
                .add(1, 0.75)
                .add(1, 1)
                .buildOverrides()
                .build(generator);*/
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
