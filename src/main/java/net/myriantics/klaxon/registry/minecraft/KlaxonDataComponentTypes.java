package net.myriantics.klaxon.registry.minecraft;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.component.ability.InstabreakingToolComponent;
import net.myriantics.klaxon.component.ability.KnockbackModifierComponent;
import net.myriantics.klaxon.component.ability.ShieldBreachingComponent;
import net.myriantics.klaxon.component.configuration.*;
import net.myriantics.klaxon.component.ability.WalljumpAbilityComponent;

import java.util.function.UnaryOperator;

public class KlaxonDataComponentTypes {

    public static final ComponentType<WalljumpAbilityComponent> WALLJUMP_ABILITY = register("walljump_ability",
            builder -> {
                builder.codec(WalljumpAbilityComponent.CODEC);
                builder.packetCodec(WalljumpAbilityComponent.PACKET_CODEC);
                return builder;
            });

    public static final ComponentType<ShieldBreachingComponent> SHIELD_BREACHING = register("shield_breaching",
            builder -> {
                builder.codec(ShieldBreachingComponent.CODEC);
                builder.packetCodec(ShieldBreachingComponent.PACKET_CODEC);
                return builder;
            });

    public static final ComponentType<KnockbackModifierComponent> KNOCKBACK_MODIFIER = register("knockback_modifier",
            builder -> {
                builder.codec(KnockbackModifierComponent.CODEC);
                builder.packetCodec(KnockbackModifierComponent.PACKET_CODEC);
                return builder;
            });

    public static final ComponentType<MeleeDamageTypeOverrideComponent> DAMAGE_TYPE_OVERRIDE = register("melee_damage_type_override",
            builder -> {
                builder.codec(MeleeDamageTypeOverrideComponent.CODEC);
                builder.packetCodec(MeleeDamageTypeOverrideComponent.PACKET_CODEC);
                return builder;
            });

    public static final ComponentType<RepairIngredientOverrideComponent> REPAIR_INGREDIENT_OVERRIDE = register("repair_ingredient_override",
            builder -> {
                builder.codec(RepairIngredientOverrideComponent.CODEC);
                builder.packetCodec(RepairIngredientOverrideComponent.PACKET_CODEC);
                return builder;
            });

    public static final ComponentType<InnateItemEnchantmentsComponent> INNATE_ENCHANTMENTS = register("innate_enchantments",
            builder -> {
                builder.codec(InnateItemEnchantmentsComponent.CODEC);
                builder.packetCodec(InnateItemEnchantmentsComponent.PACKET_CODEC);
                return builder;
            });

    public static final ComponentType<PrebakedInnateItemEnchantmentsComponent> PREBAKED_INNATE_ENCHANTMENTS = register("prebaked_innate_enchantments",
            builder -> {
                builder.codec(PrebakedInnateItemEnchantmentsComponent.CODEC);
                builder.packetCodec(PrebakedInnateItemEnchantmentsComponent.PACKET_CODEC);
                return builder;
            });

    public static final ComponentType<ToolUseRecipeConfigComponent> TOOL_USE_RECIPE_CONFIG = register("tool_usage_config",
            builder -> {
                builder.codec(ToolUseRecipeConfigComponent.CODEC);
                builder.packetCodec(ToolUseRecipeConfigComponent.PACKET_CODEC);
                return builder;
            });

    public static final ComponentType<InstabreakingToolComponent> INSTABREAK_TOOL_COMPONENT = register("instabreaking_tool",
            builder -> {
                builder.codec(InstabreakingToolComponent.CODEC);
                builder.packetCodec(InstabreakingToolComponent.PACKET_CODEC);
                return builder;
            });

    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, KlaxonCommon.locate(name), builderOperator.apply(ComponentType.builder()).build());
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Data Component Types!");
    }
}
