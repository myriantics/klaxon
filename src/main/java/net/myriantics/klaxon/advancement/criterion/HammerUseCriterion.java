package net.myriantics.klaxon.advancement.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;
import net.myriantics.klaxon.registry.KlaxonAdvancementCriteria;

import java.util.Optional;

public class HammerUseCriterion extends AbstractCriterion<HammerUseCriterion.Conditions> {
    static final Identifier ID = KlaxonCommon.locate("hammer_use");

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, HammerItem.UsageType usageType) {
        this.trigger(player, conditions -> conditions.matches(usageType));
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject jsonObject, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        HammerItem.UsageType usageType = HammerItem.UsageType.valueOf(jsonObject.get("usage_type").getAsString().toUpperCase());
        return Conditions.create(playerPredicate, usageType);
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final HammerItem.UsageType usageType;

        public Conditions(LootContextPredicate player, HammerItem.UsageType usageType) {
            super(HammerUseCriterion.ID, player);
            this.usageType = usageType;
        }

        public static Conditions createRecipeSuccess() {
            return create(LootContextPredicate.EMPTY, HammerItem.UsageType.RECIPE_SUCCESS);
        }

        public static Conditions createWalljump(boolean succeeded) {
            return create(LootContextPredicate.EMPTY, succeeded ? HammerItem.UsageType.WALLJUMP_SUCCEEDED : HammerItem.UsageType.WALLJUMP_FAILED);
        }

        public static Conditions createStrengthWalljump() {
            return create(LootContextPredicate.EMPTY, HammerItem.UsageType.STRENGTH_WALLJUMP_SUCCEEDED);
        }

        public static Conditions create(LootContextPredicate player, HammerItem.UsageType usageType) {
            return new Conditions(player, usageType);
        }

        boolean matches(HammerItem.UsageType usageType) {
            return usageType.equals(this.usageType);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("usage_type", new JsonPrimitive(usageType.asString()));
            return jsonObject;
        }
    }
}
