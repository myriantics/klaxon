package net.myriantics.klaxon.api;

import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Unit;
import net.myriantics.klaxon.component.configuration.DefaultInnateItemEnchantmentsComponent;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;

import java.util.Map;

public class KlaxonItemSettings {

    private final Item.Settings settings;

    public KlaxonItemSettings() {
        this.settings = new Item.Settings();
    }

    public KlaxonItemSettings(Item.Settings settings) {
        this.settings = new Item.Settings();
    }

    public <T> KlaxonItemSettings component(ComponentType<T> type, T value) {
        this.settings.component(type, value);
        return this;
    }

    public KlaxonItemSettings maxCountMaxDamage(int maxCount, int maxDamage) {
        this.settings.component(KlaxonDataComponentTypes.DAMAGEABLE_AND_STACKABLE, Unit.INSTANCE);
        this.settings.component(DataComponentTypes.MAX_DAMAGE, maxDamage);
        this.settings.component(DataComponentTypes.DAMAGE, 0);
        this.settings.component(DataComponentTypes.MAX_STACK_SIZE, maxCount);
        return this;
    }

    public KlaxonItemSettings maxDamage(int maxDamage) {
        this.settings.maxDamage(maxDamage);
        return this;
    }

    public KlaxonItemSettings maxCount(int maxCount) {
        this.settings.maxCount(maxCount);
        return this;
    }

    public KlaxonItemSettings attributeModifiers(AttributeModifiersComponent attributeModifiers) {
        this.settings.component(DataComponentTypes.ATTRIBUTE_MODIFIERS, attributeModifiers);
        return this;
    }

    public KlaxonItemSettings innateEnchantments(Map<RegistryKey<Enchantment>, Integer> enchantments) {
        this.settings.component(KlaxonDataComponentTypes.DEFAULT_INNATE_ENCHANTMENTS, new DefaultInnateItemEnchantmentsComponent(enchantments));
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

    public Item.Settings getSettings() {
        return settings;
    }
}
