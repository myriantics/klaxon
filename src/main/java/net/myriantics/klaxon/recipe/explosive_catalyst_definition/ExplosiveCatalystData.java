package net.myriantics.klaxon.recipe.explosive_catalyst_definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.myriantics.klaxon.util.KlaxonMathHelper;

public record ExplosiveCatalystData(double explosionPower, boolean producesFire) {
    public ExplosiveCatalystData(double explosionPower, boolean producesFire) {
        this.explosionPower = KlaxonMathHelper.roundToTenth(explosionPower);
        this.producesFire = producesFire;
    }

    public static final Codec<ExplosiveCatalystData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("explosion_power").forGetter(ExplosiveCatalystData::explosionPower),
            Codec.BOOL.fieldOf("produces_fire").forGetter(ExplosiveCatalystData::producesFire)
    ).apply(instance, ExplosiveCatalystData::new));

    public static final PacketCodec<RegistryByteBuf, ExplosiveCatalystData> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.DOUBLE, ExplosiveCatalystData::explosionPower,
            PacketCodecs.BOOL, ExplosiveCatalystData::producesFire,
            ExplosiveCatalystData::new
    );
}
