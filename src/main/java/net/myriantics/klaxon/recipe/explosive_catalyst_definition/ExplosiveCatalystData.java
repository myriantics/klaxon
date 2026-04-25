package net.myriantics.klaxon.recipe.explosive_catalyst_definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.registry.behavior.KlaxonExplosiveCatalystBehaviors;
import net.myriantics.klaxon.util.KlaxonMathHelper;

import java.util.Optional;

public record ExplosiveCatalystData(Holder<ExplosiveCatalystBehavior> behavior, double explosionPower, boolean producesFire) {
    public ExplosiveCatalystData(Holder<ExplosiveCatalystBehavior> behavior, double explosionPower, boolean producesFire) {
        this.behavior = behavior;
        this.explosionPower = KlaxonMathHelper.roundToTenth(explosionPower);
        this.producesFire = producesFire;
    }

    public ExplosiveCatalystData(ExplosiveCatalystBehavior behavior, double explosionPower, boolean producesFire) {
        this(behavior.asHolder(), explosionPower, producesFire);
    }

    public static final ExplosiveCatalystData ZERO = new ExplosiveCatalystData(KlaxonExplosiveCatalystBehaviors.NO_OP, 0.0, false);

    public boolean matchesConditions(double explosionPowerMin, double explosionPowerMax) {
        return explosionPowerMin <= explosionPower && explosionPower <= explosionPowerMax;
    }

    public static final Codec<ExplosiveCatalystData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExplosiveCatalystBehavior.ENTRY_CODEC.optionalFieldOf("behavior").xmap(
                    (entry) -> entry.orElse(KlaxonExplosiveCatalystBehaviors.DEFAULT),
                    Optional::of
            ).forGetter(ExplosiveCatalystData::behavior),
            Codec.DOUBLE.fieldOf("explosion_power").forGetter(ExplosiveCatalystData::explosionPower),
            Codec.BOOL.fieldOf("produces_fire").forGetter(ExplosiveCatalystData::producesFire)
    ).apply(instance, ExplosiveCatalystData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ExplosiveCatalystData> PACKET_CODEC = StreamCodec.composite(
            ExplosiveCatalystBehavior.ENTRY_PACKET_CODEC, ExplosiveCatalystData::behavior,
            ByteBufCodecs.DOUBLE, ExplosiveCatalystData::explosionPower,
            ByteBufCodecs.BOOL, ExplosiveCatalystData::producesFire,
            ExplosiveCatalystData::new
    );
}
