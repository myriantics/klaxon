package net.myriantics.klaxon.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;
import net.myriantics.klaxon.registry.minecraft.KlaxonAdvancementCriteria;

import java.util.Optional;

public class HammerUseCriterion extends AbstractCriterion<HammerUseCriterion.Conditions> {

    @Override
    public Codec<HammerUseCriterion.Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, HammerItem.UsageType usageType) {
        this.trigger(player, conditions -> conditions.matches(usageType));
    }

    public static record Conditions(Optional<LootContextPredicate> player, HammerItem.UsageType usageType) implements AbstractCriterion.Conditions {
        public static final Codec<HammerUseCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(HammerUseCriterion.Conditions::player),
                        HammerItem.UsageType.getCodec().fieldOf("usageType").forGetter(HammerUseCriterion.Conditions::usageType)
                )
                .apply(instance, HammerUseCriterion.Conditions::new)
        );

        public static AdvancementCriterion<HammerUseCriterion.Conditions> createRecipeSuccess() {
            return KlaxonAdvancementCriteria.HAMMER_USE_CRITERION.create(new HammerUseCriterion.Conditions(Optional.empty(), HammerItem.UsageType.RECIPE_SUCCESS));
        }

        public static AdvancementCriterion<HammerUseCriterion.Conditions> createWalljump(boolean succeeded) {
            return KlaxonAdvancementCriteria.HAMMER_USE_CRITERION.create(new HammerUseCriterion.Conditions(Optional.empty(), succeeded ? HammerItem.UsageType.WALLJUMP_SUCCEEDED : HammerItem.UsageType.WALLJUMP_FAILED));
        }

        public static AdvancementCriterion<HammerUseCriterion.Conditions> createStrengthWalljump() {
            return KlaxonAdvancementCriteria.HAMMER_USE_CRITERION.create(new HammerUseCriterion.Conditions(Optional.empty(), HammerItem.UsageType.STRENGTH_WALLJUMP_SUCCEEDED));
        }

        boolean matches(HammerItem.UsageType usageType) {
            return usageType.equals(this.usageType());
        }
    }
}
