package net.myriantics.klaxon.registry.item;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.component.ability.KnockbackHitModifierComponent;
import net.myriantics.klaxon.component.ability.ShieldBreachingComponent;
import net.myriantics.klaxon.component.ability.WalljumpAbilityComponent;
import net.myriantics.klaxon.item.equipment.ammo.ExplosiveDeepslateChunkItem;
import net.myriantics.klaxon.item.equipment.ammo.GrappleClawItem;
import net.myriantics.klaxon.item.equipment.armor.SteelArmorItem;
import net.myriantics.klaxon.item.equipment.tools.*;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.dynamic.KlaxonDamageTypes;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;

import java.util.ArrayList;
import java.util.Optional;

public abstract class KlaxonItems extends KlaxonBlockItems {

    public static ArrayList<Item> simpleItems = new ArrayList<>();

    // tools
    public static final Holder<Item> STEEL_HAMMER = registerSimpleItem("steel_hammer",
            new HammerItem(KlaxonToolMaterials.STEEL_INGOT, new KlaxonItemProperties()
                    .attributeModifiers(HammerItem.createAttributes(KlaxonToolMaterials.STEEL_INGOT, 5.0F, -3.1F))
                    .component(KlaxonDataComponentTypes.WALLJUMP_ABILITY, new WalljumpAbilityComponent(1.0f, true))
                    .damageTypeOverride(KlaxonDamageTypes.HAMMER_BONKING)
                    .component(KlaxonDataComponentTypes.KNOCKBACK_HIT_MODIFIER, new KnockbackHitModifierComponent(2.0f, KlaxonDamageTypes.HAMMER_WALLOPING))
                    .component(KlaxonDataComponentTypes.SHIELD_BREACHING, new ShieldBreachingComponent(Optional.empty(), ShieldBreachingComponent.Condition.KNOCKBACK))
                    .with3dHandModel()
                    .getProperties()
            ));
    public static final Holder<Item> STEEL_CABLE_SHEARS = registerSimpleItem("steel_cable_shears",
            new CableShearsItem(KlaxonToolMaterials.STEEL_PLATE, new KlaxonItemProperties()
                    .attributeModifiers(CableShearsItem.createAttributeModifiers(KlaxonToolMaterials.STEEL_PLATE, 1.0f, -2.8f))
                    .getProperties()
            ));
    public static final Holder<Item> STEEL_CLEAVER = registerItem("steel_cleaver",
            new CleaverItem(KlaxonToolMaterials.STEEL_PLATE, new KlaxonItemProperties()
                    .attributeModifiers(CleaverItem.createAttributes(KlaxonToolMaterials.STEEL_PLATE, 6.0f, -3.2f))
                    .damageTypeOverride(KlaxonDamageTypes.CLEAVING)
                    .component(KlaxonDataComponentTypes.SHIELD_BREACHING, new ShieldBreachingComponent(Optional.empty(), ShieldBreachingComponent.Condition.CRITICAL))
                    .getProperties()
            ));
    public static final Holder<Item> STEEL_WRENCH = registerItem("steel_wrench",
            new WrenchItem(KlaxonToolMaterials.STEEL_INGOT, new KlaxonItemProperties()
                    .attributeModifiers(WrenchItem.createAttributes(KlaxonToolMaterials.STEEL_INGOT, 0f, -2.6f))
                    .damageTypeOverride(KlaxonDamageTypes.WRENCH_OVERTUNING)
                    .component(KlaxonDataComponentTypes.KNOCKBACK_HIT_MODIFIER, new KnockbackHitModifierComponent(0.0f))
                    .getProperties()
            ));
    public static final Holder<Item> STEEL_LIGHTER = registerItem("steel_lighter",
            new LighterItem(new KlaxonItemProperties()
                    .maxDamage(KlaxonToolMaterials.STEEL_NUGGET.getUses())
                    .getProperties()
            )
    );
    public static final Holder<Item> STEEL_GRAPPLE_CLAW = registerSimpleItem("steel_grapple_claw",
            new GrappleClawItem(new KlaxonItemProperties()
                    .maxCount(16)
                    .grappleClaw(4.0f, 6.0f, 67)
                    .getProperties()
            ));
    public static final Holder<Item> GRAPPLE_WINCH = registerItem("grapple_winch",
            new GrappleWinchItem(new KlaxonItemProperties()
                    .component(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY)
                    .maxCount(1)
                    .damageTypeOverride(KlaxonDamageTypes.BLUDGEONING)
                    .attributeModifiers(GrappleWinchItem.createAttributeModifiers(KlaxonToolMaterials.STEEL_PLATE, 0, -3.2f))
                    .with3dHandModel()
                    .withMirroredLeftHandModel()
                    .getProperties()
            ));
    public static final Holder<Item> EXPLOSIVE_DEEPSLATE_CHUNK = registerItem("explosive_deepslate_chunk",
            new ExplosiveDeepslateChunkItem(new KlaxonItemProperties()
                    .catalystData(ExplosiveCatalystData.ZERO)
                    .maxCount(16)
                    .getProperties()
            )
    );

    // armor
    public static final Holder<Item> CRESTED_STEEL_HELMET = registerItem("crested_steel_helmet", new SteelArmorItem(KlaxonArmorMaterials.STEEL_PLATE, ArmorItem.Type.HELMET, new KlaxonItemProperties()
            .helmetCrest()
            .rarity(Rarity.UNCOMMON)
            .getProperties()
    ));
    public static final Holder<Item> STEEL_HELMET = registerItem("steel_helmet", new SteelArmorItem(KlaxonArmorMaterials.STEEL_PLATE, ArmorItem.Type.HELMET, new KlaxonItemProperties()
            .getProperties()
    ));
    public static final Holder<Item> STEEL_CHESTPLATE = registerItem("steel_chestplate", new SteelArmorItem(KlaxonArmorMaterials.STEEL_PLATE, ArmorItem.Type.CHESTPLATE, new KlaxonItemProperties()
            .getProperties()
    ));
    public static final Holder<Item> STEEL_LEGGINGS = registerItem("steel_leggings", new SteelArmorItem(KlaxonArmorMaterials.STEEL_PLATE, ArmorItem.Type.LEGGINGS, new KlaxonItemProperties()
            .getProperties()
    ));
    public static final Holder<Item> STEEL_BOOTS = registerItem("steel_boots", new SteelArmorItem(KlaxonArmorMaterials.STEEL_PLATE, ArmorItem.Type.BOOTS, new KlaxonItemProperties()
            .getProperties()
    ));

    // fractured materials
    public static final Holder<Item> FRACTURED_RAW_IRON = registerReallySimpleItem("fractured_raw_iron");
    public static final Holder<Item> FRACTURED_RAW_COPPER = registerReallySimpleItem("fractured_raw_copper");
    public static final Holder<Item> FRACTURED_RAW_GOLD = registerReallySimpleItem("fractured_raw_gold");
    public static final Holder<Item> FRACTURED_IRON = registerReallySimpleItem("fractured_iron");
    public static final Holder<Item> FRACTURED_COPPER = registerReallySimpleItem("fractured_copper");
    public static final Holder<Item> FRACTURED_GOLD = registerReallySimpleItem("fractured_gold");
    public static final Holder<Item> FRACTURED_COAL = registerReallySimpleItem("fractured_coal");
    public static final Holder<Item> FRACTURED_CHARCOAL = registerReallySimpleItem("fractured_charcoal");

    // alloy blends
    public static final Holder<Item> CRUDE_STEEL_MIXTURE = registerReallySimpleItem("crude_steel_mixture");

    // spawn eggs
    public static final Holder<Item> OMINOUS_DEEPSLATE_BLAST_PROCESSOR_SPAWN_EGG = registerItem("ominous_deepslate_blast_processor_spawn_egg", new SpawnEggItem(KlaxonEntityTypes.OMINOUS_DEEPSLATE_BLAST_PROCESSOR.value(), 0x242424, 0xd5c3c3, new Item.Properties()));

    // raw materials
    public static final Holder<Item> STEEL_INGOT = registerReallySimpleItem("steel_ingot");
    public static final Holder<Item> STEEL_NUGGET = registerReallySimpleItem("steel_nugget");
    public static final Holder<Item> CRUDE_STEEL_INGOT = registerReallySimpleItem("crude_steel_ingot");
    public static final Holder<Item> CRUDE_STEEL_NUGGET = registerReallySimpleItem("crude_steel_nugget");
    public static final Holder<Item> RUBBER_GLOB = registerReallySimpleItem("rubber_glob");
    public static final Holder<Item> COPPER_NUGGET = registerReallySimpleItem("copper_nugget");

    // plates / sheets
    public static final Holder<Item> STEEL_PLATE = registerReallySimpleItem("steel_plate");
    public static final Holder<Item> CRUDE_STEEL_PLATE = registerReallySimpleItem("crude_steel_plate");
    public static final Holder<Item> IRON_PLATE = registerReallySimpleItem("iron_plate");
    public static final Holder<Item> GOLD_PLATE = registerReallySimpleItem("gold_plate");
    public static final Holder<Item> COPPER_PLATE = registerReallySimpleItem("copper_plate");
    public static final Holder<Item> RUBBER_SHEET = registerReallySimpleItem("rubber_sheet");

    // wires
    public static final Holder<Item> STEEL_WIRE = registerReallySimpleItem("steel_wire");
    public static final Holder<Item> IRON_WIRE = registerReallySimpleItem("iron_wire");
    public static final Holder<Item> GOLD_WIRE = registerReallySimpleItem("gold_wire");
    public static final Holder<Item> COPPER_WIRE = registerReallySimpleItem("copper_wire");

    private static Holder<Item> registerItem(String name, Item item) {
        return Registry.registerForHolder(BuiltInRegistries.ITEM, KlaxonCommon.locate(name), item);
    }

    private static Holder<Item> registerReallySimpleItem(String name) {
        return registerSimpleItem(name, new Item(new Item.Properties()));
    }

    private static Holder<Item> registerSimpleItem(String name, Item item) {
        simpleItems.add(item);
        return Registry.registerForHolder(BuiltInRegistries.ITEM, KlaxonCommon.locate(name), item);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Items!");
    }
}
