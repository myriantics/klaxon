package net.myriantics.klaxon.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;

import java.util.Optional;

public class WalljumpAbilityCriterion extends SimpleCriterionTrigger<WalljumpAbilityCriterion.Conditions> {

    @Override
    public Codec<WalljumpAbilityCriterion.Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, HammerItem.UsageType usageType) {
        this.trigger(player, conditions -> conditions.matches(usageType));
    }

    public static record Conditions(Optional<ContextAwarePredicate> player, HammerItem.UsageType usageType) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<WalljumpAbilityCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(WalljumpAbilityCriterion.Conditions::player),
                        HammerItem.UsageType.getCodec().fieldOf("usageType").forGetter(WalljumpAbilityCriterion.Conditions::usageType)
                )
                .apply(instance, WalljumpAbilityCriterion.Conditions::new)
        );

        public static Criterion<WalljumpAbilityCriterion.Conditions> createNormalWalljump() {
            return KlaxonAdvancementCriteria.WALLJUMP_ABILITY_CRITERION.createCriterion(new WalljumpAbilityCriterion.Conditions(Optional.empty(), HammerItem.UsageType.NORMAL_WALLJUMP));
        }

        public static Criterion<WalljumpAbilityCriterion.Conditions> createStrengthWalljump() {
            return KlaxonAdvancementCriteria.WALLJUMP_ABILITY_CRITERION.createCriterion(new WalljumpAbilityCriterion.Conditions(Optional.empty(), HammerItem.UsageType.BOOSTED_WALLJUMP));
        }

        public static Criterion<WalljumpAbilityCriterion.Conditions> createMinecartWalljump() {
            return KlaxonAdvancementCriteria.WALLJUMP_ABILITY_CRITERION.createCriterion(new WalljumpAbilityCriterion.Conditions(Optional.empty(), HammerItem.UsageType.MINECART_WALLJUMP));
        }

        boolean matches(HammerItem.UsageType usageType) {
            return usageType.equals(this.usageType());
        }
    }
}
