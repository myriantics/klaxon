package net.myriantics.klaxon.registry.item;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.component.ability.KnockbackHitModifierComponent;
import net.myriantics.klaxon.component.ability.ShieldBreachingComponent;
import net.myriantics.klaxon.component.configuration.DefaultInnateItemEnchantmentsComponent;
import net.myriantics.klaxon.component.configuration.MeleeDamageTypeOverrideComponent;
import net.myriantics.klaxon.component.ability.WalljumpAbilityComponent;
import net.myriantics.klaxon.item.equipment.armor.SteelArmorItem;
import net.myriantics.klaxon.item.equipment.tools.CleaverItem;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;
import net.myriantics.klaxon.item.equipment.tools.CableShearsItem;
import net.myriantics.klaxon.item.equipment.tools.WrenchItem;
import net.myriantics.klaxon.registry.entity.KlaxonDamageTypes;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public abstract class KlaxonItems extends KlaxonBlockItems {

    public static ArrayList<Item> simpleItems = new ArrayList<>();

    // tools
    public static final Item STEEL_HAMMER = registerSimpleItem("steel_hammer",
            new HammerItem(KlaxonToolMaterials.STEEL, new Item.Settings()
                    .attributeModifiers(HammerItem.createAttributeModifiers(KlaxonToolMaterials.STEEL, 5.0F, -3.1F))
                    .component(KlaxonDataComponentTypes.WALLJUMP_ABILITY, new WalljumpAbilityComponent(1.0f, true))
                    .component(KlaxonDataComponentTypes.MELEE_DAMAGE_TYPE_OVERRIDE, new MeleeDamageTypeOverrideComponent(KlaxonDamageTypes.HAMMER_BONKING))
                    .component(KlaxonDataComponentTypes.KNOCKBACK_HIT_MODIFIER, new KnockbackHitModifierComponent(2.0f, KlaxonDamageTypes.HAMMER_WALLOPING))
                    .component(KlaxonDataComponentTypes.SHIELD_BREACHING, new ShieldBreachingComponent(Optional.empty(), ShieldBreachingComponent.Condition.KNOCKBACK))
                    .component(KlaxonDataComponentTypes.DEFAULT_INNATE_ENCHANTMENTS, new DefaultInnateItemEnchantmentsComponent(Map.of(Enchantments.UNBREAKING, 4)))
            ));
    public static final Item STEEL_CABLE_SHEARS = registerItem("steel_cable_shears",
            new CableShearsItem(KlaxonToolMaterials.STEEL_PLATE, new Item.Settings()
                    .attributeModifiers(CableShearsItem.createAttributeModifiers(KlaxonToolMaterials.STEEL_PLATE, 1.0f, -2.8f))
                    .component(KlaxonDataComponentTypes.DEFAULT_INNATE_ENCHANTMENTS, new DefaultInnateItemEnchantmentsComponent(Map.of(Enchantments.UNBREAKING, 4)))
            ));
    public static final Item STEEL_CLEAVER = registerItem("steel_cleaver",
            new CleaverItem(KlaxonToolMaterials.STEEL_PLATE, new Item.Settings()
                    .attributeModifiers(CleaverItem.createAttributeModifiers(KlaxonToolMaterials.STEEL_PLATE, 6.0f, -3.2f))
                    .component(KlaxonDataComponentTypes.MELEE_DAMAGE_TYPE_OVERRIDE, new MeleeDamageTypeOverrideComponent(KlaxonDamageTypes.CLEAVING))
                    .component(KlaxonDataComponentTypes.SHIELD_BREACHING, new ShieldBreachingComponent(Optional.empty(), ShieldBreachingComponent.Condition.CRITICAL))
                    .component(KlaxonDataComponentTypes.DEFAULT_INNATE_ENCHANTMENTS, new DefaultInnateItemEnchantmentsComponent(Map.of(Enchantments.LOOTING, 1, Enchantments.UNBREAKING, 4)))
            ));
    public static final Item STEEL_WRENCH = registerItem("steel_wrench",
            new WrenchItem(KlaxonToolMaterials.STEEL, new Item.Settings()
                    .attributeModifiers(WrenchItem.createAttributeModifiers(KlaxonToolMaterials.STEEL, 0f, -2.6f))
                    .component(KlaxonDataComponentTypes.MELEE_DAMAGE_TYPE_OVERRIDE, new MeleeDamageTypeOverrideComponent(KlaxonDamageTypes.WRENCH_OVERTUNING))
                    .component(KlaxonDataComponentTypes.KNOCKBACK_HIT_MODIFIER, new KnockbackHitModifierComponent(0.0f))
                    .component(KlaxonDataComponentTypes.DEFAULT_INNATE_ENCHANTMENTS, new DefaultInnateItemEnchantmentsComponent(Map.of(Enchantments.UNBREAKING, 4)))
            ));

    // armor
    public static final Item STEEL_HELMET = registerSimpleItem("steel_helmet",
            new SteelArmorItem(KlaxonArmorMaterials.STEEL_PLATE, ArmorItem.Type.HELMET, new Item.Settings()
                    .component(KlaxonDataComponentTypes.DEFAULT_INNATE_ENCHANTMENTS, new DefaultInnateItemEnchantmentsComponent(Map.of(Enchantments.UNBREAKING, 4)))
            ));
    public static final Item STEEL_CHESTPLATE = registerSimpleItem("steel_chestplate",
            new SteelArmorItem(KlaxonArmorMaterials.STEEL_PLATE, ArmorItem.Type.CHESTPLATE, new Item.Settings()
                    .component(KlaxonDataComponentTypes.DEFAULT_INNATE_ENCHANTMENTS, new DefaultInnateItemEnchantmentsComponent(Map.of(Enchantments.UNBREAKING, 4)))
            ));
    public static final Item STEEL_LEGGINGS = registerSimpleItem("steel_leggings",
            new SteelArmorItem(KlaxonArmorMaterials.STEEL_PLATE, ArmorItem.Type.LEGGINGS, new Item.Settings()
                    .component(KlaxonDataComponentTypes.DEFAULT_INNATE_ENCHANTMENTS, new DefaultInnateItemEnchantmentsComponent(Map.of(Enchantments.UNBREAKING, 4)))
            ));
    public static final Item STEEL_BOOTS = registerSimpleItem("steel_boots",
            new SteelArmorItem(KlaxonArmorMaterials.STEEL_PLATE, ArmorItem.Type.BOOTS, new Item.Settings()
                    .component(KlaxonDataComponentTypes.DEFAULT_INNATE_ENCHANTMENTS, new DefaultInnateItemEnchantmentsComponent(Map.of(Enchantments.UNBREAKING, 4)))
            ));

    // fractured materials
    public static final Item FRACTURED_RAW_IRON = registerReallySimpleItem("fractured_raw_iron");
    public static final Item FRACTURED_RAW_COPPER = registerReallySimpleItem("fractured_raw_copper");
    public static final Item FRACTURED_RAW_GOLD = registerReallySimpleItem("fractured_raw_gold");
    public static final Item FRACTURED_IRON = registerReallySimpleItem("fractured_iron");
    public static final Item FRACTURED_COPPER = registerReallySimpleItem("fractured_copper");
    public static final Item FRACTURED_GOLD = registerReallySimpleItem("fractured_gold");
    public static final Item FRACTURED_COAL = registerReallySimpleItem("fractured_coal");
    public static final Item FRACTURED_CHARCOAL = registerReallySimpleItem("fractured_charcoal");

    // alloy blends
    public static final Item CRUDE_STEEL_MIXTURE = registerReallySimpleItem("crude_steel_mixture");

    // raw materials
    public static final Item STEEL_INGOT = registerReallySimpleItem("steel_ingot");
    public static final Item STEEL_NUGGET = registerReallySimpleItem("steel_nugget");
    public static final Item CRUDE_STEEL_INGOT = registerReallySimpleItem("crude_steel_ingot");
    public static final Item CRUDE_STEEL_NUGGET = registerReallySimpleItem("crude_steel_nugget");
    public static final Item RUBBER_GLOB = registerReallySimpleItem("rubber_glob");
    public static final Item MOLTEN_RUBBER_GLOB = registerReallySimpleItem("molten_rubber_glob");

    // plates / sheets
    public static final Item STEEL_PLATE = registerReallySimpleItem("steel_plate");
    public static final Item CRUDE_STEEL_PLATE = registerReallySimpleItem("crude_steel_plate");
    public static final Item IRON_PLATE = registerReallySimpleItem("iron_plate");
    public static final Item GOLD_PLATE = registerReallySimpleItem("gold_plate");
    public static final Item COPPER_PLATE = registerReallySimpleItem("copper_plate");
    public static final Item RUBBER_SHEET = registerReallySimpleItem("rubber_sheet");
    public static final Item MOLTEN_RUBBER_SHEET = registerReallySimpleItem("molten_rubber_sheet");

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, KlaxonCommon.locate(name), item);
    }

    private static Item registerReallySimpleItem(String name) {
        return registerSimpleItem(name, new Item(new Item.Settings()));
    }

    private static Item registerSimpleItem(String name, Item item) {
        simpleItems.add(item);
        return Registry.register(Registries.ITEM, KlaxonCommon.locate(name), item);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Items!");
    }
}
