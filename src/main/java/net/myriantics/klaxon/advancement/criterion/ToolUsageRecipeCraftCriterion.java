package net.myriantics.klaxon.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.Optional;

public class ToolUsageRecipeCraftCriterion extends SimpleCriterionTrigger<ToolUsageRecipeCraftCriterion.Conditions> {

    @Override
    public Codec<ToolUsageRecipeCraftCriterion.Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack toolStack, ItemStack resultStack) {
        this.trigger(player, conditions -> conditions.matches(toolStack, resultStack));
    }

    public static record Conditions(Optional<ContextAwarePredicate> player, Ingredient requiredTool, Ingredient resultIngredient) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<ToolUsageRecipeCraftCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> {
            return instance.group(
                    EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(ToolUsageRecipeCraftCriterion.Conditions::player),
                    Ingredient.CODEC_NONEMPTY.fieldOf("required_tool").forGetter(ToolUsageRecipeCraftCriterion.Conditions::requiredTool),
                    Ingredient.CODEC.fieldOf("result_ingredient").forGetter(ToolUsageRecipeCraftCriterion.Conditions::resultIngredient)
            ).apply(instance, ToolUsageRecipeCraftCriterion.Conditions::new);
        });

        public static Criterion<ToolUsageRecipeCraftCriterion.Conditions> createHammering(Ingredient resultIngredient) {
            return KlaxonAdvancementCriteria.TOOL_USAGE_RECIPE_CRITERION.value().createCriterion(new Conditions(Optional.empty(), Ingredient.of(KlaxonItemTags.RECIPE_PROCESSING_HAMMERS), resultIngredient));
        }

        public static Criterion<ToolUsageRecipeCraftCriterion.Conditions> createWirecutting(Ingredient resultIngredient) {
            return KlaxonAdvancementCriteria.TOOL_USAGE_RECIPE_CRITERION.value().createCriterion(new Conditions(Optional.empty(), Ingredient.of(KlaxonItemTags.RECIPE_PROCESSING_WIRECUTTERS), resultIngredient));
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
