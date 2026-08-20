package net.myriantics.klaxon.registry.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.myriantics.klaxon.component.configuration.GrappleClawComponent;
import net.myriantics.klaxon.component.configuration.MeleeDamageTypeOverrideComponent;

import java.util.function.UnaryOperator;

public class KlaxonItemProperties {

    private final Item.Properties settings;

    public KlaxonItemProperties() {
        this.settings = new Item.Properties();
    }

    public KlaxonItemProperties(Item.Properties settings) {
        this.settings = new Item.Properties();
    }


    public <T> KlaxonItemProperties component(Holder<DataComponentType<T>> typeHolder, T value) {
        return this.component(typeHolder.value(), value);
    }

    public <T> KlaxonItemProperties component(DataComponentType<T> type, T value) {
        this.settings.component(type, value);
        return this;
    }

    public KlaxonItemProperties helmetCrest() {
        this.component(KlaxonDataComponentTypes.HELMET_CREST_COMPONENT, Unit.INSTANCE);
        return this;
    }

    public KlaxonItemProperties rarity(Rarity rarity) {
        this.component(DataComponents.RARITY, rarity);
        return this;
    }

    public KlaxonItemProperties food(UnaryOperator<FoodProperties.Builder> food) {
        return this.component(DataComponents.FOOD, food.apply(new FoodProperties.Builder()).build());
    }

    public KlaxonItemProperties maxDamage(int maxDamage) {
        this.settings.durability(maxDamage);
        return this;
    }

    public KlaxonItemProperties maxCount(int maxCount) {
        this.settings.stacksTo(maxCount);
        return this;
    }

    public KlaxonItemProperties grappleClaw(float baseGrapplingDamage, float baseRendingDamage, int veinmineCap) {
        this.component(KlaxonDataComponentTypes.GRAPPLE_CLAW_COMPONENT, new GrappleClawComponent(baseGrapplingDamage, baseRendingDamage, veinmineCap));
        return this;
    }

    public KlaxonItemProperties attributeModifiers(ItemAttributeModifiers attributeModifiers) {
        this.component(DataComponents.ATTRIBUTE_MODIFIERS, attributeModifiers);
        return this;
    }

    public KlaxonItemProperties damageTypeOverride(ResourceKey<DamageType> damageTypeKey) {
        this.component(KlaxonDataComponentTypes.MELEE_DAMAGE_TYPE_OVERRIDE, new MeleeDamageTypeOverrideComponent(damageTypeKey));
        return this;
    }

    public KlaxonItemProperties with3dHandModel() {
        this.component(KlaxonDataComponentTypes.ALT_HAND_MODEL, "_3d");
        return this;
    }

    public KlaxonItemProperties withMirroredLeftHandModel() {
        this.component(KlaxonDataComponentTypes.MIRRORED_LEFT_HAND_MODEL, Unit.INSTANCE);
        return this;
    }

    public Item.Properties getProperties() {
        return settings;
    }
}
