package net.myriantics.klaxon.registry.minecraft;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.component.ability.InstabreakingToolComponent;
import net.myriantics.klaxon.component.ability.KnockbackHitModifierComponent;
import net.myriantics.klaxon.component.ability.ShieldBreachingComponent;
import net.myriantics.klaxon.component.configuration.*;
import net.myriantics.klaxon.component.ability.WalljumpAbilityComponent;

import java.util.function.UnaryOperator;

public class KlaxonDataComponentTypes {

    // Items with this component will propel the given entity backwards from their look direction if they attack a block with positive Y velocity. Also restricts the given item from being able to break blocks in Creative.
    public static final ComponentType<WalljumpAbilityComponent> WALLJUMP_ABILITY = register("walljump_ability",
            builder -> {
                builder.codec(WalljumpAbilityComponent.CODEC);
                builder.packetCodec(WalljumpAbilityComponent.PACKET_CODEC);
                return builder;
            });

    // Items with this component can disable shields and deal damage in the same hit, given the defined conditions are met.
    public static final ComponentType<ShieldBreachingComponent> SHIELD_BREACHING = register("shield_breaching",
            builder -> {
                builder.codec(ShieldBreachingComponent.CODEC);
                builder.packetCodec(ShieldBreachingComponent.PACKET_CODEC);
                return builder;
            });

    // Modifies the knockback strength of a knockback hit when using the given item.
    public static final ComponentType<KnockbackHitModifierComponent> KNOCKBACK_HIT_MODIFIER = register("knockback_hit_modifier",
            builder -> {
                builder.codec(KnockbackHitModifierComponent.CODEC);
                builder.packetCodec(KnockbackHitModifierComponent.PACKET_CODEC);
                return builder;
            });

    // Determines what damage type a weapon will use on melee strike
    public static final ComponentType<MeleeDamageTypeOverrideComponent> MELEE_DAMAGE_TYPE_OVERRIDE = register("melee_damage_type_override",
            builder -> {
                builder.codec(MeleeDamageTypeOverrideComponent.CODEC);
                builder.packetCodec(MeleeDamageTypeOverrideComponent.PACKET_CODEC);
                return builder;
            });

    // The given item will now use this repair item in lieu of a code-defined one
    public static final ComponentType<RepairIngredientOverrideComponent> REPAIR_INGREDIENT_OVERRIDE = register("repair_ingredient_override",
            builder -> {
                builder.codec(RepairIngredientOverrideComponent.CODEC);
                builder.packetCodec(RepairIngredientOverrideComponent.PACKET_CODEC);
                return builder;
            });

    // Stores enchantments independently from other enchantments. Non-transferable and cannot be removed via traditional disenchanting. Enchantments function as normal and stack with their non-innate counterparts.
    public static final ComponentType<InnateItemEnchantmentsComponent> INNATE_ENCHANTMENTS = register("innate_enchantments",
            builder -> {
                builder.codec(InnateItemEnchantmentsComponent.CODEC);
                builder.packetCodec(InnateItemEnchantmentsComponent.PACKET_CODEC);
                return builder;
            });

    // Used instead of Innate Enchantments Component when assigning default components to an item.
    public static final ComponentType<DefaultInnateItemEnchantmentsComponent> DEFAULT_INNATE_ENCHANTMENTS = register("default_innate_enchantments",
            builder -> {
                builder.codec(DefaultInnateItemEnchantmentsComponent.CODEC);
                builder.packetCodec(DefaultInnateItemEnchantmentsComponent.PACKET_CODEC);
                return builder;
            });

    // Determines the default sound used for a given ToolUsageRecipe. Also determines if you can cosmetically use the tool - i.e. hammering items to no effect, just to make the noise.
    public static final ComponentType<ToolUseRecipeConfigComponent> TOOL_USE_RECIPE_CONFIG = register("tool_usage_config",
            builder -> {
                builder.codec(ToolUseRecipeConfigComponent.CODEC);
                builder.packetCodec(ToolUseRecipeConfigComponent.PACKET_CODEC);
                return builder;
            });

    // Defines a block tag that the given ItemStack can instantly break. Requires ToolComponent that boosts mining speed of given block.
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
