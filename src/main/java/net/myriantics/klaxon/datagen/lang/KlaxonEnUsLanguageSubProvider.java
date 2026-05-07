package net.myriantics.klaxon.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.compat.jade.KlaxonJadePlugin;
import net.myriantics.klaxon.datagen.advancement.providers.KlaxonStageOneAdvancementProvider;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import org.jetbrains.annotations.Nullable;

public abstract class KlaxonEnUsLanguageSubProvider {
    public final KlaxonEnUsLanguageProvider provider;
    public final FabricLanguageProvider.TranslationBuilder builder;

    public KlaxonEnUsLanguageSubProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        this.provider = provider;
        this.builder = builder;
    }

    public abstract void generate();

    protected void addItem(Holder<Item> itemHolder, String name) {
        addItem(itemHolder.value(), name);
    }

    protected void addItem(ItemLike itemConvertible, String name) {
        builder.add(itemConvertible.asItem(), name);
    }

    protected void addEnchantment(ResourceKey<Enchantment> enchantment, String name) {
        builder.addEnchantment(enchantment, name);
    }

    protected void addTrimMaterial(ResourceKey<TrimMaterial> material, String name) {
        builder.add("trim_material." + material.location().getNamespace() + "." + material.location().getPath(), name);
    }

    protected void addExplosiveCatalystBehavior(ResourceKey<ExplosiveCatalystBehavior> key, String name) {
        builder.add(key.location().toLanguageKey(), name);
    }

    protected void addBlock(Holder<Block> holder, String name) {
        this.addBlock(holder.value(), name);
    }

    protected void addBlock(Block block, String name) {
        builder.add(block, name);
    }

    protected void addEntityType(Holder<EntityType<?>> typeHolder, String name) {
        addEntityType(typeHolder.value(), name);
    }

    protected void addEntityType(EntityType<?> type, String name) {
        builder.add(type, name);
    }

    protected void addTag(TagKey<?> tagKey, String name) {
        builder.add(tagKey, name);
    }

    protected void addItemGroup(ResourceKey<CreativeModeTab> group, String name) {
        builder.add(group, name);
    }

    protected void addEntityAttribute(Holder<Attribute> attribute, String name) {
        builder.add(attribute, name);
    }

    protected void addEmiRecipeCategory(String id, String name) {
        builder.add("container." + KlaxonCommon.MOD_ID + '.' + id + ".title", name);
    }

    protected void addEmiText(String key, String name) {
        builder.add(KlaxonCommon.MOD_ID + ".emi.text." + key, name);
    }

    protected void addJadeTooltipText(ResourceLocation location, String suffix, String name) {
        builder.add(KlaxonJadePlugin.textTranslationKey(location), name);
    }

    protected void addJadeTooltipText(String key, String name) {
        builder.add(KlaxonCommon.MOD_ID + ".jade.text." + key, name);
    }

    protected void addJadeConfigText(ResourceLocation location, String suffix, String name) {
        builder.add(KlaxonJadePlugin.configTranslationKey(location), name);
    }

    protected void addJadeConfigText(String key, String name) {
        builder.add("config.jade.plugin_" + KlaxonCommon.MOD_ID + "." + key, name);
    }

    protected void addRawText(String key, String name) {
        builder.add(key, name);
    }

    protected void addText(String key, String name) {
        addRawText(KlaxonCommon.MOD_ID + ".text." + key, name);
    }

    protected void addTooltipText(String key, String name) {
        addText("tooltip." + key, name);
    }

    protected void addChatText(String key, String name) {
        addText("chat." + key, name);
    }

    protected void addActionBarText(String key, String name) {
        addText("actionbar." + key, name);
    }

    protected void addEmiExplosionPowerInfo(String key, String name) {
        addEmiText("explosive_catalyst_definition." + key, name);
    }

    private void addAdvancement(String advancementName, String stage, String title, String description) {
        String baseId = "advancements." + KlaxonCommon.MOD_ID + "." + stage + "." + advancementName;
        builder.add(baseId + ".title", title);
        builder.add(baseId + ".description", description);
    }

    protected void addStageOneAdvancement(String advancementName, String title, String description) {
        addAdvancement(advancementName, KlaxonStageOneAdvancementProvider.STAGE, title, description);
    }

    protected void addDeathMessage(ResourceKey<DamageType> type, String value, @Nullable String itemValue) {
        String key = "death.attack." + type.location().getNamespace() + "." + type.location().getPath();

        builder.add(key, value);
        if (itemValue != null) {
            builder.add(key + ".item", itemValue);
        }
    }
}
