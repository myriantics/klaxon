package net.myriantics.klaxon.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.Optional;

public record DimensionTypePredicate(
        Optional<TagKey<DimensionType>> tagKey,
        Optional<Boolean> ultraWarm,
        Optional<Boolean> bedWorks,
        Optional<Boolean> respawnAnchorWorks
) {

    public static final Codec<DimensionTypePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.DIMENSION_TYPE).optionalFieldOf("dimension_type").forGetter(DimensionTypePredicate::tagKey),
            Codec.BOOL.optionalFieldOf("ultrawarm").forGetter(DimensionTypePredicate::ultraWarm),
            Codec.BOOL.optionalFieldOf("bed_works").forGetter(DimensionTypePredicate::bedWorks),
            Codec.BOOL.optionalFieldOf("respawn_anchor_works").forGetter(DimensionTypePredicate::respawnAnchorWorks)
            ).apply(instance, DimensionTypePredicate::new)
    );

    public boolean test(Level level) {
        DimensionType type = level.dimensionType();

        // check tag
        if (this.tagKey.isPresent()) {
            Optional<Registry<DimensionType>> reg = level.registryAccess().registry(Registries.DIMENSION_TYPE);
            if (reg.isPresent() && !reg.get().wrapAsHolder(type).is(tagKey.get())) {
                return false;
            }
        }

        // check ultrawarm matches
        if (this.ultraWarm.isPresent() && type.ultraWarm() != this.ultraWarm.get()) {
            return false;
        }

        // check bed works matches
        if (this.bedWorks.isPresent() && type.bedWorks() != this.bedWorks.get()) {
            return false;
        }

        // check respawn anchor works matches
        if (this.respawnAnchorWorks.isPresent() && type.respawnAnchorWorks() != this.respawnAnchorWorks.get()) {
            return false;
        }

        return true;
    }
}
