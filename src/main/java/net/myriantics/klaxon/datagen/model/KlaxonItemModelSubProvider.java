package net.myriantics.klaxon.datagen.model;

import net.minecraft.data.client.*;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.item.KlaxonItems;
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

    protected void registerFancyShit() {

    }

    protected void register2DGrappleWinch() {
        Identifier rawId = Registries.ITEM.getId(KlaxonItems.GRAPPLE_WINCH);
        Identifier unchargedTextureId = rawId.withPath((path) -> "item/" + path + "_unloaded");
        Identifier chargedTextureId = rawId.withPath((path) -> "item/" + path + "_loaded");
        Identifier chargedModelId = rawId.withPath((path) -> "item/" + path + "_loaded");

        FancyFuckinModelBuilder.of(Models.GENERATED)
                .id(getItemId(rawId.getPath()))
                .textureMap(TextureMap.layer0(unchargedTextureId))
                .override(Map.of(KlaxonCommon.locate("charged"), 1), chargedModelId)
                .build(generator.writer);
        FancyFuckinModelBuilder.of(Models.GENERATED)
                .id(chargedModelId)
                .textureMap(TextureMap.layer0(chargedTextureId))
                .build(generator);
    }

    protected void register3DGrappleWinch() {
        Identifier rawId = Registries.ITEM.getId(KlaxonItems.GRAPPLE_WINCH);
        Identifier raw3dId = rawId.withPath((path) -> path + "_3d");
        Identifier modelId = getItemId(raw3dId);
        Identifier cableStage1 = getSubModelId(raw3dId.withPath((path) -> path + "_cablestage_1"), "grapple_winch");
        Identifier cableStage2 = getSubModelId(raw3dId.withPath((path) -> path + "_cablestage_2"), "grapple_winch");
        Identifier cableStage3 = getSubModelId(raw3dId.withPath((path) -> path + "_cablestage_3"), "grapple_winch");
        Identifier cableStage4 = getSubModelId(raw3dId.withPath((path) -> path + "_cablestage_4"), "grapple_winch");

        FancyFuckinModelBuilder.of(KlaxonModels.GRAPPLE_WINCH_3D)
                .id(modelId)
                .textureMap(Map.of(
                        "structure", rawId.withPath((path) -> "item/" + path + "/grapple_winch_structure"),
                        "spool", rawId.withPath((path) -> "item/" + path + "/grapple_winch_spool_4"),
                        "claw", rawId.withPath((path) -> "item/" + path + "/grapple_winch_claw"),
                        "particle", rawId.withPath((path) -> "item/" + path + "/grapple_winch_structure")
                ))
                .override(Map.of(KlaxonCommon.locate("charged"), 1, KlaxonCommon.locate("winch_cable"), 1), cableStage1)
                .override(Map.of(KlaxonCommon.locate("charged"), 1, KlaxonCommon.locate("winch_cable"), 2), cableStage2)
                .override(Map.of(KlaxonCommon.locate("charged"), 1, KlaxonCommon.locate("winch_cable"), 3), cableStage3)
                .override(Map.of(KlaxonCommon.locate("charged"), 1, KlaxonCommon.locate("winch_cable"), 4), cableStage4)
                .build(generator);
        FancyFuckinModelBuilder.of(KlaxonModels.GRAPPLE_WINCH_3D)
                .id(cableStage1)
                .textureMap(Map.of(
                        "spool", rawId.withPath((path) -> "item/" + path + "/grapple_winch_spool_1"))
                )
                .build(generator);
        FancyFuckinModelBuilder.of(KlaxonModels.GRAPPLE_WINCH_3D)
                .id(cableStage2)
                .textureMap(Map.of(
                        "spool", rawId.withPath((path) -> "item/" + path + "/grapple_winch_spool_2"))
                )
                .build(generator);
        FancyFuckinModelBuilder.of(KlaxonModels.GRAPPLE_WINCH_3D)
                .id(cableStage3)
                .textureMap(Map.of(
                        "spool", rawId.withPath((path) -> "item/" + path + "/grapple_winch_spool_3"))
                )
                .build(generator);
        FancyFuckinModelBuilder.of(KlaxonModels.GRAPPLE_WINCH_3D)
                .id(cableStage4)
                .textureMap(Map.of(
                        "spool", rawId.withPath((path) -> "item/" + path + "/grapple_winch_spool_4"))
                )
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
