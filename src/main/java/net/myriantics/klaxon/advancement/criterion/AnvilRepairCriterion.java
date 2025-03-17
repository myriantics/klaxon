package net.myriantics.klaxon.advancement.criterion;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.KlaxonAdvancementCriteria;

import java.util.Optional;

public class AnvilRepairCriterion extends AbstractCriterion<AnvilRepairCriterion.Conditions> {
    static final Identifier ID = KlaxonCommon.locate("anvil_repair");

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack) {
        this.trigger(player, conditions -> conditions.matches(stack));
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject jsonObject, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("accepted_items"));
        NumberRange.FloatRange floatRange = NumberRange.FloatRange.fromJson(jsonObject.get("max_damage_proportion"));

        return Conditions.create(playerPredicate, itemPredicate, floatRange);
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final ItemPredicate acceptedItems;
        private final NumberRange.FloatRange advancementMaxDamageProportion;

        public Conditions(LootContextPredicate player, ItemPredicate acceptedItems, NumberRange.FloatRange advancementMaxDamageProportion) {
            super(AnvilRepairCriterion.ID, player);
            this.acceptedItems = acceptedItems;
            this.advancementMaxDamageProportion = advancementMaxDamageProportion;
        }

        public static AnvilRepairCriterion.Conditions createFromTag(TagKey<Item> itemTag, double durabilityMinProportion) {
            return create(LootContextPredicate.EMPTY, ItemPredicate.Builder.create().tag(itemTag).build(), NumberRange.FloatRange.atMost(durabilityMinProportion));
        }

        public static AnvilRepairCriterion.Conditions create(LootContextPredicate player, ItemPredicate acceptedItems, NumberRange.FloatRange advancementMaxDamageProportion) {
            return new AnvilRepairCriterion.Conditions(player, acceptedItems, advancementMaxDamageProportion);
        }

        boolean matches(ItemStack stack) {
            double testStackDamageProportion = (double) stack.getDamage() / stack.getMaxDamage();

            if (advancementMaxDamageProportion.getMax() != null) {
                return acceptedItems.test(stack) && testStackDamageProportion <= advancementMaxDamageProportion.getMax();
            }

            return false;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("accepted_items", this.acceptedItems.toJson());
            jsonObject.add("max_damage_proportion", this.advancementMaxDamageProportion.toJson());
            return jsonObject;
        }
    }
}
