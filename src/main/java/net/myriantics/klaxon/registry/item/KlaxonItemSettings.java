package net.myriantics.klaxon.registry.item;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.myriantics.klaxon.component.configuration.GrappleClawComponent;
import net.myriantics.klaxon.component.configuration.MeleeDamageTypeOverrideComponent;

public class KlaxonItemSettings {

    private final Item.Properties settings;

    public KlaxonItemSettings() {
        this.settings = new Item.Properties();
    }

    public KlaxonItemSettings(Item.Properties settings) {
        this.settings = new Item.Properties();
    }

    public <T> KlaxonItemSettings component(DataComponentType<T> type, T value) {
        this.settings.component(type, value);
        return this;
    }

    public KlaxonItemSettings helmetCrest() {
        this.settings.component(KlaxonDataComponentTypes.HELMET_CREST_COMPONENT, Unit.INSTANCE);
        return this;
    }

    public KlaxonItemSettings rarity(Rarity rarity) {
        this.settings.component(DataComponents.RARITY, rarity);
        return this;
    }

    public KlaxonItemSettings maxCountMaxDamage(int maxCount, int maxDamage) {
        this.settings.component(KlaxonDataComponentTypes.DAMAGEABLE_AND_STACKABLE, Unit.INSTANCE);
        this.settings.component(DataComponents.MAX_DAMAGE, maxDamage);
        this.settings.component(DataComponents.DAMAGE, 0);
        this.settings.component(DataComponents.MAX_STACK_SIZE, maxCount);
        return this;
    }

    public KlaxonItemSettings maxDamage(int maxDamage) {
        this.settings.durability(maxDamage);
        return this;
    }

    public KlaxonItemSettings maxCount(int maxCount) {
        this.settings.stacksTo(maxCount);
        return this;
    }

    public KlaxonItemSettings grappleClaw(float baseGrapplingDamage, float baseRendingDamage, int veinmineCap) {
        this.component(KlaxonDataComponentTypes.GRAPPLE_CLAW_COMPONENT, new GrappleClawComponent(baseGrapplingDamage, baseRendingDamage, veinmineCap));
        return this;
    }

    public KlaxonItemSettings attributeModifiers(ItemAttributeModifiers attributeModifiers) {
        this.settings.component(DataComponents.ATTRIBUTE_MODIFIERS, attributeModifiers);
        return this;
    }

    public KlaxonItemSettings damageTypeOverride(ResourceKey<DamageType> damageTypeKey) {
        this.settings.component(KlaxonDataComponentTypes.MELEE_DAMAGE_TYPE_OVERRIDE, new MeleeDamageTypeOverrideComponent(damageTypeKey));
        return this;
    }

    public KlaxonItemSettings with3dHandModel() {
        this.settings.component(KlaxonDataComponentTypes.ALT_HAND_MODEL, "_3d");
        return this;
    }

    public KlaxonItemSettings withMirroredLeftHandModel() {
        this.settings.component(KlaxonDataComponentTypes.MIRRORED_LEFT_HAND_MODEL, Unit.INSTANCE);
        return this;
    }

    public Item.Properties getSettings() {
        return settings;
    }
}
