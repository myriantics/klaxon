package net.myriantics.klaxon.recipe.explosive_catalyst;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystBehaviors;
import net.myriantics.klaxon.util.KlaxonMathHelper;

import java.util.Optional;

public record ExplosiveCatalystData(ResourceKey<ExplosiveCatalystBehavior> behavior, double explosionPower, boolean producesFire) {
    public ExplosiveCatalystData(ResourceKey<ExplosiveCatalystBehavior> behavior, double explosionPower, boolean producesFire) {
        this.behavior = behavior;
        this.explosionPower = KlaxonMathHelper.roundToTenth(explosionPower);
        this.producesFire = producesFire;
    }

    public ExplosiveCatalystData(double explosionPower, boolean producesFire) {
        this(KlaxonExplosiveCatalystBehaviors.DEFAULT, explosionPower, producesFire);
    }

    public static final ExplosiveCatalystData ZERO = new ExplosiveCatalystData(KlaxonExplosiveCatalystBehaviors.NO_OP, 0.0, false);

    public boolean matchesConditions(double explosionPowerMin, double explosionPowerMax) {
        return explosionPowerMin <= explosionPower && explosionPower <= explosionPowerMax;
    }

    public ExplosiveCatalystData copyWithPower(double explosionPower) {
        return new ExplosiveCatalystData(this.behavior, explosionPower, this.producesFire);
    }

    public ExplosiveCatalystData copyWithFiery() {
        return new ExplosiveCatalystData(this.behavior, this.explosionPower, true);
    }

    public Holder<ExplosiveCatalystBehavior> get(Level level) {
        Registry<ExplosiveCatalystBehavior> reg = level.registryAccess().registryOrThrow(KlaxonRegistryKeys.EXPLOSIVE_CATALYST_BEHAVIOR);
        Optional<Holder.Reference<ExplosiveCatalystBehavior>> selected = reg.getHolder(this.behavior);
        return selected.orElse(reg.getHolderOrThrow(KlaxonExplosiveCatalystBehaviors.NO_OP));
    }

    public static final Codec<ExplosiveCatalystData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(KlaxonRegistryKeys.EXPLOSIVE_CATALYST_BEHAVIOR).fieldOf("behavior").forGetter(ExplosiveCatalystData::behavior),
            Codec.DOUBLE.fieldOf("explosion_power").forGetter(ExplosiveCatalystData::explosionPower),
            Codec.BOOL.fieldOf("produces_fire").forGetter(ExplosiveCatalystData::producesFire)
    ).apply(instance, ExplosiveCatalystData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ExplosiveCatalystData> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(KlaxonRegistryKeys.EXPLOSIVE_CATALYST_BEHAVIOR), ExplosiveCatalystData::behavior,
            ByteBufCodecs.DOUBLE, ExplosiveCatalystData::explosionPower,
            ByteBufCodecs.BOOL, ExplosiveCatalystData::producesFire,
            ExplosiveCatalystData::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<ExplosiveCatalystData>> OPTIONAL_STREAM_CODEC = ByteBufCodecs.optional(STREAM_CODEC);
}
