package net.myriantics.klaxon.registry.item;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.component.ability.InstabreakingToolComponent;
import net.myriantics.klaxon.component.ability.KnockbackHitModifierComponent;
import net.myriantics.klaxon.component.ability.ShieldBreachingComponent;
import net.myriantics.klaxon.component.ability.WalljumpAbilityComponent;
import net.myriantics.klaxon.component.configuration.*;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;

import java.util.function.UnaryOperator;

public abstract class KlaxonDataComponentTypes {

    // Items with this component will propel the given entity backwards from their look direction if they attack a block with positive Y velocity. Also restricts the given item from being able to break blocks in Creative.
    public static final Holder<DataComponentType<WalljumpAbilityComponent>> WALLJUMP_ABILITY = register("walljump_ability",
            builder -> {
                builder.persistent(WalljumpAbilityComponent.CODEC);
                builder.networkSynchronized(WalljumpAbilityComponent.PACKET_CODEC);
                return builder;
            });

    // Items with this component can disable shields and deal damage in the same hit, given the defined conditions are met.
    public static final Holder<DataComponentType<ShieldBreachingComponent>> SHIELD_BREACHING = register("shield_breaching",
            builder -> {
                builder.persistent(ShieldBreachingComponent.CODEC);
                builder.networkSynchronized(ShieldBreachingComponent.PACKET_CODEC);
                return builder;
            });

    // Modifies the knockback strength of a knockback hit when using the given item.
    public static final Holder<DataComponentType<KnockbackHitModifierComponent>> KNOCKBACK_HIT_MODIFIER = register("knockback_hit_modifier",
            builder -> {
                builder.persistent(KnockbackHitModifierComponent.CODEC);
                builder.networkSynchronized(KnockbackHitModifierComponent.PACKET_CODEC);
                return builder;
            });

    // Determines what damage type a weapon will use on melee strike
    public static final Holder<DataComponentType<MeleeDamageTypeOverrideComponent>> MELEE_DAMAGE_TYPE_OVERRIDE = register("melee_damage_type_override",
            builder -> {
                builder.persistent(MeleeDamageTypeOverrideComponent.CODEC);
                builder.networkSynchronized(MeleeDamageTypeOverrideComponent.PACKET_CODEC);
                return builder;
            });

    // The given item will now use this repair item in lieu of a code-defined one
    public static final Holder<DataComponentType<RepairIngredientOverrideComponent>> REPAIR_INGREDIENT_OVERRIDE = register("repair_ingredient_override",
            builder -> {
                builder.persistent(RepairIngredientOverrideComponent.CODEC);
                builder.networkSynchronized(RepairIngredientOverrideComponent.PACKET_CODEC);
                return builder;
            });

    // Determines the default sound used for a given ToolUsageRecipe. Also determines if you can cosmetically use the tool - i.e. hammering items to no effect, just to make the noise.
    public static final Holder<DataComponentType<ToolUseRecipeConfigComponent>> TOOL_USE_RECIPE_CONFIG = register("tool_usage_config",
            builder -> {
                builder.persistent(ToolUseRecipeConfigComponent.CODEC);
                builder.networkSynchronized(ToolUseRecipeConfigComponent.PACKET_CODEC);
                return builder;
            });

    // Defines a block tag that the given ItemStack can instantly break. Requires ToolComponent that boosts mining speed of given block.
    public static final Holder<DataComponentType<InstabreakingToolComponent>> INSTABREAK_TOOL_COMPONENT = register("instabreaking_tool",
            builder -> {
                builder.persistent(InstabreakingToolComponent.CODEC);
                builder.networkSynchronized(InstabreakingToolComponent.PACKET_CODEC);
                return builder;
            });

    public static final Holder<DataComponentType<GrappleClawComponent>> GRAPPLE_CLAW_COMPONENT = register("grapple_claw", builder -> builder
            .persistent(GrappleClawComponent.CODEC)
            .networkSynchronized(GrappleClawComponent.PACKET_CODEC)
    );

    public static final Holder<DataComponentType<ExplosiveCatalystData>> EXPLOSIVE_CATALYST_DATA = register("explosive_catalyst_data", builder -> builder
            .persistent(ExplosiveCatalystData.CODEC)
            .networkSynchronized(ExplosiveCatalystData.PACKET_CODEC)
    );

    // Items with this component override the check that disallows both damage and stacking components coexisting.
    public static final Holder<DataComponentType<Unit>> DAMAGEABLE_AND_STACKABLE = registerUnit("damageable_and_stackable");

    // Items with this component replace their held item model "x:example_model" with "x:example_model_[YOUR_STRING_HERE]" under certain conditions
    public static final Holder<DataComponentType<String>> ALT_HAND_MODEL = register("alt_hand_model",
            builder ->  {
                builder.persistent(Codec.STRING);
                builder.networkSynchronized(ByteBufCodecs.STRING_UTF8);
                return builder;
            });

    public static final Holder<DataComponentType<ModularExplosiveBlockConfigComponent>> MODULAR_EXPLOSIVE_BLOCK_CONFIG = register("modular_explosive_block_config", builder -> {
        builder.persistent(ModularExplosiveBlockConfigComponent.CODEC);
        builder.networkSynchronized(ModularExplosiveBlockConfigComponent.PACKET_CODEC);
        return builder;
    });

    // Items with this component flip their held item model when held in the left hand
    public static final Holder<DataComponentType<Unit>> MIRRORED_LEFT_HAND_MODEL = registerUnit("mirrored_left_hand_model");

    public static final Holder<DataComponentType<Double>> RECIPE_OUTPUT_CHANCE_LORE = register("recipe_output_chance_lore",
            builder -> {
        builder.persistent(Codec.DOUBLE);
        builder.networkSynchronized(ByteBufCodecs.DOUBLE);
        return builder;
    });

    public static final Holder<DataComponentType<Unit>> HELMET_CREST_COMPONENT = registerUnit("helmet_crest");

    @SuppressWarnings("unchecked")
    private static <T> Holder<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return (Holder<DataComponentType<T>>) (Object) Registry.registerForHolder(BuiltInRegistries.DATA_COMPONENT_TYPE, KlaxonCommon.locate(name), builderOperator.apply(DataComponentType.builder()).build());
    }

    private static Holder<DataComponentType<Unit>> registerUnit(String name) {
        return register(name, unitBuilder -> unitBuilder.persistent(Codec.unit(Unit.INSTANCE)).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Data Component Types!");
    }
}
