package net.myriantics.klaxon.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.mechanics.muffling.MufflerActionType;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MufflerInteractionCriterion extends SimpleCriterionTrigger<MufflerInteractionCriterion.Conditions> {

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, BlockPos muffledPos, MufflerActionType type, ItemStack appliedStack, ItemStack existingMufflerStack) {
        this.trigger(player, conditions -> conditions.test(player.serverLevel(), muffledPos, type, appliedStack, existingMufflerStack));
    }

    public record Conditions(Optional<ContextAwarePredicate> player, MufflerActionType type, Optional<ItemPredicate> appliedStackPredicate, Optional<ItemPredicate> existingMufflerStackPredicate, Optional<BlockPredicate> muffledBlockPredicate) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<MufflerInteractionCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                MufflerActionType.CODEC.fieldOf("muffler_action_type").forGetter(Conditions::type),
                ItemPredicate.CODEC.optionalFieldOf("applied_stack").forGetter(Conditions::appliedStackPredicate),
                ItemPredicate.CODEC.optionalFieldOf("existing_muffler_stack").forGetter(Conditions::existingMufflerStackPredicate),
                BlockPredicate.CODEC.optionalFieldOf("interacted_block").forGetter(Conditions::muffledBlockPredicate)
                ).apply(instance, Conditions::new
        ));

        public static Criterion<Conditions> create(MufflerActionType type, ItemPredicate appliedStackPredicate) {
            return create(null, type, appliedStackPredicate, null, null);
        }

        public static Criterion<Conditions> create(@Nullable ContextAwarePredicate player, MufflerActionType type, @Nullable ItemPredicate appliedStackPredicate, @Nullable ItemPredicate existingMufflerStackPredicate, @Nullable BlockPredicate muffledBlockPredicate) {
            return KlaxonAdvancementCriteria.MUFFLER_INTERACTION.value().createCriterion(
                    new Conditions(Optional.ofNullable(player), type, Optional.ofNullable(appliedStackPredicate), Optional.ofNullable(existingMufflerStackPredicate), Optional.ofNullable(muffledBlockPredicate))
            );
        }

        public boolean test(ServerLevel level, BlockPos muffledPos, MufflerActionType type, ItemStack appliedStack, ItemStack existingMufflerStack) {
            if (!type.equals(this.type)) {
                return false;
            }

            if (this.appliedStackPredicate.isPresent() && !this.appliedStackPredicate.get().test(appliedStack)) {
                return false;
            }

            if (this.existingMufflerStackPredicate.isPresent() && !this.existingMufflerStackPredicate.get().test(existingMufflerStack)) {
                return false;
            }

            if (this.muffledBlockPredicate.isPresent() && !this.muffledBlockPredicate.get().matches(level, muffledPos)) {
                return false;
            }

            return true;
        }
    }
}
