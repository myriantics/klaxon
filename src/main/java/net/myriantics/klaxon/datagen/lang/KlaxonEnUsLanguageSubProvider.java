package net.myriantics.klaxon.datagen.lang;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.compat.emi.KlaxonEmiRecipeCategories;
import net.myriantics.klaxon.datagen.advancement.providers.KlaxonStageOneAdvancementProvider;
import org.jetbrains.annotations.Nullable;

public abstract class KlaxonEnUsLanguageSubProvider {
    public final KlaxonEnUsLanguageProvider provider;
    public final FabricLanguageProvider.TranslationBuilder builder;

    public KlaxonEnUsLanguageSubProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        this.provider = provider;
        this.builder = builder;
    }

    public abstract void generate();

    protected void addItem(ItemConvertible itemConvertible, String name) {
        builder.add(itemConvertible.asItem(), name);
    }

    protected void addBlock(Block block, String name) {
        builder.add(block, name);
    }

    protected void addEntityType(EntityType<? extends Entity> type, String name) {
        builder.add(type, name);
    }

    protected void addTag(TagKey<?> tagKey, String name) {
        builder.add(tagKey, name);
    }

    protected void addItemGroup(RegistryKey<ItemGroup> group, String name) {
        builder.add(group, name);
    }

    protected void addEntityAttribute(RegistryEntry<EntityAttribute> attribute, String name) {
        builder.add(attribute, name);
    }

    protected void addEmiRecipeCategory(String id, String name) {
        builder.add("container." + KlaxonCommon.MOD_ID + '.' + id + ".title", name);
    }

    protected void addEmiText(String key, String name) {
        builder.add(KlaxonCommon.MOD_ID + ".emi.text." + key, name);
    }

    protected void addJadeTooltipText(String key, String name) {
        builder.add(KlaxonCommon.MOD_ID + ".jade.text." + key, name);
    }

    protected void addJadeConfigText(String key, String name) {
        builder.add("config.jade.plugin_" + KlaxonCommon.MOD_ID + "." + key, name);
    }

    protected void addText(String key, String name) {
        builder.add(KlaxonCommon.MOD_ID + ".text." + key, name);
    }

    protected void addTooltipText(String key, String name) {
        addText("tooltip." + key, name);
    }

    protected void addEmiExplosionPowerInfo(String key, String name) {
        addEmiText("explosion_power_info." + key, name);
    }

    private void addAdvancement(String advancementName, String stage, String title, String description) {
        String baseId = "advancements." + KlaxonCommon.MOD_ID + "." + stage + "." + advancementName;
        builder.add(baseId + ".title", title);
        builder.add(baseId + ".description", description);
    }

    protected void addStageOneAdvancement(String advancementName, String title, String description) {
        addAdvancement(advancementName, KlaxonStageOneAdvancementProvider.STAGE, title, description);
    }

    protected void addDeathMessage(RegistryKey<DamageType> type, String value, @Nullable String itemValue) {
        String key = "death.attack." + type.getValue().getPath();

        builder.add(key, value);
        if (itemValue != null) {
            builder.add(key + ".item", itemValue);
        }
    }
}
