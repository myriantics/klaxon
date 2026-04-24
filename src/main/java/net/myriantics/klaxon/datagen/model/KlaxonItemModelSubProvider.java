package net.myriantics.klaxon.datagen.model;

import net.minecraft.core.Holder;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.datagen.model.item.FancierItemModelBuilder;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.render.KlaxonItemModelPredicateIds;
import net.myriantics.klaxon.registry.render.KlaxonModelTemplates;
import net.myriantics.klaxon.registry.render.KlaxonTextures;

import java.util.Map;

public abstract class KlaxonItemModelSubProvider {
    public final KlaxonModelProvider provider;
    public final ItemModelGenerators generator;

    public KlaxonItemModelSubProvider(KlaxonModelProvider provider, ItemModelGenerators generator) {
        this.provider = provider;
        this.generator = generator;
    }

    public abstract void generateModels();

    protected void registerSimpleItem(Item item) {
        generator.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
    }

    protected void register2DGrappleWinch() {
        ResourceLocation modelId = getItemId(KlaxonItems.GRAPPLE_WINCH.unwrapKey().get().location());

        // predicate
        ResourceLocation chargedPredicate = KlaxonItemModelPredicateIds.CHARGED;

        // texture key
        TextureSlot layer0 = TextureSlot.LAYER0;

        FancierItemModelBuilder.of(ModelTemplates.FLAT_ITEM, modelId, Map.of(
                layer0, KlaxonTextures.GRAPPLE_WINCH_2D_UNLOADED
                ))
                .textureOverride(chargedPredicate, "layer0", 2)
                .add(0, KlaxonTextures.GRAPPLE_WINCH_2D_UNLOADED)
                .add(1, KlaxonTextures.GRAPPLE_WINCH_2D_LOADED)
                .build()
                .build(generator);
    }

    protected void register3DGrappleWinch() {
        ResourceLocation raw3dId = KlaxonItems.GRAPPLE_WINCH.unwrapKey().get().location().withPath((path) -> path + "_3d");
        ResourceLocation modelId = getItemId(raw3dId);

        // predicates
        ResourceLocation chargedPredicate = KlaxonItemModelPredicateIds.CHARGED;
        ResourceLocation retractingPredicate = KlaxonItemModelPredicateIds.RETRACTING;
        ResourceLocation winchCableLengthPredicate = KlaxonItemModelPredicateIds.WINCH_CABLE_LENGTH;

        // texture keys
        TextureSlot structure = TextureSlot.create("5");
        TextureSlot claw = TextureSlot.create("3");
        TextureSlot spool = TextureSlot.create("4");

        FancierItemModelBuilder.of(KlaxonModelTemplates.GRAPPLE_WINCH_3D_SPOOL_4, modelId, Map.of(
                        structure, KlaxonTextures.GRAPPLE_WINCH_3D_STRUCTURE,
                        claw, KlaxonTextures.EMPTY,
                        spool, KlaxonTextures.GRAPPLE_WINCH_3D_SPOOL_RETRACTING
                ))
                .modelOverride(winchCableLengthPredicate, 7)
                .add(0f/6f, KlaxonModelTemplates.GRAPPLE_WINCH_3D_SPOOL_6)
                .add(1f/6f, KlaxonModelTemplates.GRAPPLE_WINCH_3D_SPOOL_5)
                .add(2f/6f, KlaxonModelTemplates.GRAPPLE_WINCH_3D_SPOOL_4)
                .add(3f/6f, KlaxonModelTemplates.GRAPPLE_WINCH_3D_SPOOL_3)
                .add(4f/6f, KlaxonModelTemplates.GRAPPLE_WINCH_3D_SPOOL_2)
                .add(5f/6f, KlaxonModelTemplates.GRAPPLE_WINCH_3D_SPOOL_1)
                .add(6f/6f, KlaxonModelTemplates.GRAPPLE_WINCH_3D_SPOOL_0)
                .build()
                .textureOverride(chargedPredicate, claw.getId(), 2)
                .add(0, KlaxonTextures.EMPTY)
                .add(1, KlaxonTextures.GRAPPLE_WINCH_STEEL_GRAPPLE_CLAW)
                .build()
                .textureOverride(retractingPredicate, spool.getId(), 2)
                .add(0, KlaxonTextures.GRAPPLE_WINCH_3D_SPOOL)
                .add(1, KlaxonTextures.GRAPPLE_WINCH_3D_SPOOL_RETRACTING)
                .build()
                .build(generator);
    }

    protected void registerArmor(Holder<Item> armor) {
        registerArmor(armor.value());
    }

    protected void registerArmor(Item armor) {
        if (armor instanceof ArmorItem armorItem) {
            generator.generateArmorTrims(armorItem);
        } else {
            throw new IllegalArgumentException(armor + "is not an ArmorItem. Fix.");
        }
    }

    public static ResourceLocation getItemId(String name) {
        return KlaxonCommon.locate("item/" + name);
    }

    public static ResourceLocation getItemId(ResourceLocation id) {
        return id.withPath((path) -> "item/" + path);
    }

    public static ResourceLocation getSubModelId(ResourceLocation id, String prefix) {
        return id.withPath((path) -> "item/" + prefix + "/" + path);
    }

    public static ResourceLocation getSimpleModelId(String name) {
        return KlaxonCommon.locate("item/simple/" + name);
    }

    public static ResourceLocation getAdvancedModelId(String name) {
        return KlaxonCommon.locate("item/advanced/" + name);
    }
}
