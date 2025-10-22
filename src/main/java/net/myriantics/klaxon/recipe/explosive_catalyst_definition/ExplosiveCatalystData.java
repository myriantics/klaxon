package net.myriantics.klaxon.recipe.explosive_catalyst_definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.myriantics.klaxon.api.behavior.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.registry.behavior.KlaxonExplosiveCatalystBehaviors;
import net.myriantics.klaxon.util.KlaxonMathHelper;

import java.util.Optional;

public record ExplosiveCatalystData(RegistryEntry<ExplosiveCatalystBehavior> behavior, double explosionPower, boolean producesFire) {
    public ExplosiveCatalystData(RegistryEntry<ExplosiveCatalystBehavior> behavior, double explosionPower, boolean producesFire) {
        this.behavior = behavior;
        this.explosionPower = KlaxonMathHelper.roundToTenth(explosionPower);
        this.producesFire = producesFire;
    }

    public ExplosiveCatalystData(ExplosiveCatalystBehavior behavior, double explosionPower, boolean producesFire) {
        this(behavior.getRegistryEntry(), explosionPower, producesFire);
    }

    public static final ExplosiveCatalystData ZERO = new ExplosiveCatalystData(KlaxonExplosiveCatalystBehaviors.DEFAULT.getRegistryEntry(), 0.0, false);

    public boolean matchesConditions(double explosionPowerMin, double explosionPowerMax) {
        return explosionPowerMin <= explosionPower && explosionPower <= explosionPowerMax;
    }

    public static final Codec<ExplosiveCatalystData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExplosiveCatalystBehavior.ENTRY_CODEC.optionalFieldOf("behavior").xmap(
                    (entry) -> entry.orElse(KlaxonExplosiveCatalystBehaviors.DEFAULT.getRegistryEntry()),
                    Optional::of
            ).forGetter(ExplosiveCatalystData::behavior),
            Codec.DOUBLE.fieldOf("explosion_power").forGetter(ExplosiveCatalystData::explosionPower),
            Codec.BOOL.fieldOf("produces_fire").forGetter(ExplosiveCatalystData::producesFire)
    ).apply(instance, ExplosiveCatalystData::new));

    public static final PacketCodec<RegistryByteBuf, ExplosiveCatalystData> PACKET_CODEC = PacketCodec.tuple(
            ExplosiveCatalystBehavior.ENTRY_PACKET_CODEC, ExplosiveCatalystData::behavior,
            PacketCodecs.DOUBLE, ExplosiveCatalystData::explosionPower,
            PacketCodecs.BOOL, ExplosiveCatalystData::producesFire,
            ExplosiveCatalystData::new
    );
}
