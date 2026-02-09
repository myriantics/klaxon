package net.myriantics.klaxon.datagen.model;

import net.minecraft.data.client.*;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.datagen.model.item.FancierItemModelBuilder;
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

        FancierItemModelBuilder.of(Models.GENERATED, modelId, Map.of(
                layer0, KlaxonTextures.GRAPPLE_WINCH_2D_UNLOADED
                ))
                .textureOverride(chargedPredicate, "layer0", 2)
                .add(0, KlaxonTextures.GRAPPLE_WINCH_2D_UNLOADED)
                .add(1, KlaxonTextures.GRAPPLE_WINCH_2D_LOADED)
                .build()
                .build(generator);
    }

    protected void register3DGrappleWinch() {
        Identifier raw3dId = Registries.ITEM.getId(KlaxonItems.GRAPPLE_WINCH).withPath((path) -> path + "_3d");
        Identifier modelId = getItemId(raw3dId);

        // predicates
        Identifier chargedPredicate = KlaxonItemModelPredicateIds.CHARGED;
        Identifier retractingPredicate = KlaxonItemModelPredicateIds.RETRACTING;
        Identifier winchCableLengthPredicate = KlaxonItemModelPredicateIds.WINCH_CABLE_LENGTH;

        // texture keys
        TextureKey structure = TextureKey.of("5");
        TextureKey claw = TextureKey.of("3");
        TextureKey spool = TextureKey.of("4");

        FancierItemModelBuilder.of(KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_4, modelId, Map.of(
                        structure, KlaxonTextures.GRAPPLE_WINCH_3D_STRUCTURE,
                        claw, KlaxonTextures.EMPTY,
                        spool, KlaxonTextures.GRAPPLE_WINCH_3D_SPOOL_RETRACTING
                ))
                .modelOverride(winchCableLengthPredicate, 7)
                .add(0f/6f, KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_6)
                .add(1f/6f, KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_5)
                .add(2f/6f, KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_4)
                .add(3f/6f, KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_3)
                .add(4f/6f, KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_2)
                .add(5f/6f, KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_1)
                .add(6f/6f, KlaxonModels.GRAPPLE_WINCH_3D_SPOOL_0)
                .build()
                .textureOverride(chargedPredicate, claw.getName(), 2)
                .add(0, KlaxonTextures.EMPTY)
                .add(1, KlaxonTextures.GRAPPLE_WINCH_STEEL_GRAPPLE_CLAW)
                .build()
                .textureOverride(retractingPredicate, spool.getName(), 2)
                .add(0, KlaxonTextures.GRAPPLE_WINCH_3D_SPOOL)
                .add(1, KlaxonTextures.GRAPPLE_WINCH_3D_SPOOL_RETRACTING)
                .build()
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
