package net.myriantics.klaxon.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.registry.minecraft.KlaxonAdvancementCriteria;
import net.myriantics.klaxon.registry.minecraft.KlaxonItems;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.Optional;

public class ToolUsageRecipeCraftCriterion extends AbstractCriterion<ToolUsageRecipeCraftCriterion.Conditions> {

    @Override
    public Codec<ToolUsageRecipeCraftCriterion.Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, ItemStack toolStack, ItemStack resultStack) {
        this.trigger(player, conditions -> conditions.matches(toolStack, resultStack));
    }

    public static record Conditions(Optional<LootContextPredicate> player, Ingredient requiredTool, Ingredient resultIngredient) implements AbstractCriterion.Conditions {
        public static final Codec<ToolUsageRecipeCraftCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> {
            return instance.group(
                    EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(ToolUsageRecipeCraftCriterion.Conditions::player),
                    Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("required_tool").forGetter(ToolUsageRecipeCraftCriterion.Conditions::requiredTool),
                    Ingredient.ALLOW_EMPTY_CODEC.fieldOf("result_ingredient").forGetter(ToolUsageRecipeCraftCriterion.Conditions::resultIngredient)
            ).apply(instance, ToolUsageRecipeCraftCriterion.Conditions::new);
        });

        public static AdvancementCriterion<ToolUsageRecipeCraftCriterion.Conditions> createHammering(Ingredient resultIngredient) {
            return KlaxonAdvancementCriteria.TOOL_USAGE_RECIPE_CRITERION.create(new Conditions(Optional.empty(), Ingredient.fromTag(KlaxonItemTags.RECIPE_PROCESSING_HAMMERS), resultIngredient));
        }

        public static AdvancementCriterion<ToolUsageRecipeCraftCriterion.Conditions> createWirecutting(Ingredient resultIngredient) {
            return KlaxonAdvancementCriteria.TOOL_USAGE_RECIPE_CRITERION.create(new Conditions(Optional.empty(), Ingredient.fromTag(KlaxonItemTags.RECIPE_PROCESSING_WIRECUTTERS), resultIngredient));
        }

        public static AdvancementCriterion<ToolUsageRecipeCraftCriterion.Conditions> createShearing(Ingredient resultIngredient) {
            return KlaxonAdvancementCriteria.TOOL_USAGE_RECIPE_CRITERION.create(new Conditions(Optional.empty(), Ingredient.fromTag(KlaxonItemTags.RECIPE_PROCESSING_SHEARS), resultIngredient));
        }

        boolean matches(ItemStack toolStack, ItemStack resultStack) {
            if (this.resultIngredient.isEmpty()) {
                return requiredTool.test(toolStack);
            } else {
                return requiredTool.test(toolStack) && resultIngredient.test(resultStack);
            }
        }
    }
}
