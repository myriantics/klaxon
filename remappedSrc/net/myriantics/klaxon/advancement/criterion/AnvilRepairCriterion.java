package net.myriantics.klaxon.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.registry.KlaxonAdvancementCriteria;

import java.util.Optional;

public class AnvilRepairCriterion extends AbstractCriterion<AnvilRepairCriterion.Conditions> {

    @Override
    public Codec<AnvilRepairCriterion.Conditions> getConditionsCodec() {
        return AnvilRepairCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack) {
        this.trigger(player, conditions -> conditions.matches(stack));
    }

    public static record Conditions(Optional<LootContextPredicate> player, Ingredient acceptedItems, double advancementMaxDamageProportion) implements AbstractCriterion.Conditions {
        public static final Codec<AnvilRepairCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(AnvilRepairCriterion.Conditions::player),
                        Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("accepted_items").forGetter(AnvilRepairCriterion.Conditions::acceptedItems),
                        Codec.DOUBLE.fieldOf("max_damage_proportion").forGetter(AnvilRepairCriterion.Conditions::advancementMaxDamageProportion)
                        )
                        .apply(instance, AnvilRepairCriterion.Conditions::new)
        );

        public static AdvancementCriterion<AnvilRepairCriterion.Conditions> createFromTag(TagKey<Item> itemTag, double durabilityMinProportion) {
            return KlaxonAdvancementCriteria.ANVIL_REPAIR_CRITERION.create(new AnvilRepairCriterion.Conditions(Optional.empty(), Ingredient.fromTag(itemTag), durabilityMinProportion));
        }

        boolean matches(ItemStack stack) {
            double testStackDamageProportion = (double) stack.getDamage() / stack.getMaxDamage();

            return acceptedItems.test(stack) && testStackDamageProportion <= advancementMaxDamageProportion;
        }
    }
}
