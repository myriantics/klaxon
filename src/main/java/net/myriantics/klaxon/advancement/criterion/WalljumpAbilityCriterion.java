package net.myriantics.klaxon.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;

import java.util.Optional;

public class WalljumpAbilityCriterion extends AbstractCriterion<WalljumpAbilityCriterion.Conditions> {

    @Override
    public Codec<WalljumpAbilityCriterion.Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, HammerItem.UsageType usageType) {
        this.trigger(player, conditions -> conditions.matches(usageType));
    }

    public static record Conditions(Optional<LootContextPredicate> player, HammerItem.UsageType usageType) implements AbstractCriterion.Conditions {
        public static final Codec<WalljumpAbilityCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(WalljumpAbilityCriterion.Conditions::player),
                        HammerItem.UsageType.getCodec().fieldOf("usageType").forGetter(WalljumpAbilityCriterion.Conditions::usageType)
                )
                .apply(instance, WalljumpAbilityCriterion.Conditions::new)
        );

        public static AdvancementCriterion<WalljumpAbilityCriterion.Conditions> createWalljump(boolean succeeded) {
            return KlaxonAdvancementCriteria.WALLJUMP_ABILITY_CRITERION.create(new WalljumpAbilityCriterion.Conditions(Optional.empty(), succeeded ? HammerItem.UsageType.WALLJUMP_SUCCEEDED : HammerItem.UsageType.WALLJUMP_FAILED));
        }

        public static AdvancementCriterion<WalljumpAbilityCriterion.Conditions> createStrengthWalljump() {
            return KlaxonAdvancementCriteria.WALLJUMP_ABILITY_CRITERION.create(new WalljumpAbilityCriterion.Conditions(Optional.empty(), HammerItem.UsageType.STRENGTH_WALLJUMP_SUCCEEDED));
        }

        public static AdvancementCriterion<WalljumpAbilityCriterion.Conditions> createMinecartWalljump() {
            return KlaxonAdvancementCriteria.WALLJUMP_ABILITY_CRITERION.create(new WalljumpAbilityCriterion.Conditions(Optional.empty(), HammerItem.UsageType.MINECART_WALLJUMP_SUCCESS));
        }

        boolean matches(HammerItem.UsageType usageType) {
            return usageType.equals(this.usageType());
        }
    }
}
