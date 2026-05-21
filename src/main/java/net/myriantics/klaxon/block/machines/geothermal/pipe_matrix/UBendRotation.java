package net.myriantics.klaxon.block.machines.geothermal.pipe_matrix;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.myriantics.klaxon.util.KlaxonCodecUtils;
import org.jetbrains.annotations.Nullable;

import java.time.Year;
import java.util.Map;
import java.util.Objects;

public enum UBendRotation implements StringRepresentable {
    DOWN_BENT_AROUND_X(Direction.DOWN, Direction.Axis.X),
    DOWN_BENT_AROUND_Z(Direction.DOWN, Direction.Axis.Z),
    UP_BENT_AROUND_X(Direction.UP, Direction.Axis.X),
    UP_BENT_AROUND_Z(Direction.UP, Direction.Axis.Z),
    NORTH_BENT_AROUND_X(Direction.NORTH, Direction.Axis.X),
    NORTH_BENT_AROUND_Y(Direction.NORTH, Direction.Axis.Y),
    SOUTH_BENT_AROUND_X(Direction.SOUTH, Direction.Axis.X),
    SOUTH_BENT_AROUND_Y(Direction.SOUTH, Direction.Axis.Y),
    WEST_BENT_AROUND_Y(Direction.WEST, Direction.Axis.Y),
    WEST_BENT_AROUND_Z(Direction.WEST, Direction.Axis.Z),
    EAST_BENT_AROUND_Y(Direction.EAST, Direction.Axis.Y),
    EAST_BENT_AROUND_Z(Direction.EAST, Direction.Axis.Z);

    public static final Codec<UBendRotation> CODEC = StringRepresentable.fromEnum(UBendRotation::values);

    private static final Int2ObjectMap<UBendRotation> LOOKUP_FACING_AXIS = Util.make(new Int2ObjectOpenHashMap<>(values().length), map -> {
        for (UBendRotation rotation : values()) {
            map.put(lookupKey(rotation.exposedFace, rotation.bentAroundAxis), rotation);
        }
    });

    private final Direction exposedFace;
    private final Direction.Axis bentAroundAxis;

    UBendRotation(Direction exposedFace, Direction.Axis bentAroundAxis) {
        this.exposedFace = exposedFace;
        this.bentAroundAxis = bentAroundAxis;
    }

    private static int lookupKey(Direction exposedFace, Direction.Axis bentAround) {
        return exposedFace.ordinal() << 2 | bentAround.ordinal();
    }

    public static @Nullable UBendRotation from(Direction exposedFace, Direction.Axis bentAround) {
        return LOOKUP_FACING_AXIS.get(lookupKey(exposedFace, bentAround));
    }

    public static UBendRotation firstMatchingDirection(Direction exposedFace) {
        return values()[exposedFace.ordinal() * 2];
    }

    public Direction getExposedFace() {
        return this.exposedFace;
    }

    public Direction.Axis getBentAroundAxis() {
        return this.bentAroundAxis;
    }

    @Override
    public String getSerializedName() {
        return switch (this) {
            case DOWN_BENT_AROUND_X -> "down_bent_around_x";
            case DOWN_BENT_AROUND_Z -> "down_bent_around_z";
            case UP_BENT_AROUND_X -> "up_bent_around_x";
            case UP_BENT_AROUND_Z -> "up_bent_around_z";
            case NORTH_BENT_AROUND_X -> "north_bent_around_x";
            case NORTH_BENT_AROUND_Y -> "north_bent_around_y";
            case SOUTH_BENT_AROUND_X -> "south_bent_around_x";
            case SOUTH_BENT_AROUND_Y -> "south_bent_around_y";
            case WEST_BENT_AROUND_Y -> "west_bent_around_y";
            case WEST_BENT_AROUND_Z -> "west_bent_around_z";
            case EAST_BENT_AROUND_Y -> "east_bent_around_y";
            case EAST_BENT_AROUND_Z -> "east_bent_around_z";
        };
    }

    public UBendRotation cycleBendAxis() {
        return values()[(this.exposedFace.ordinal() * 2) + ((this.ordinal() % 2 == 0) ? 1 : 0)];
    }

    public UBendRotation rotate(Rotation rotation) {
        if (this.exposedFace.getAxis() == Direction.Axis.Y) {
            return switch (rotation) {
                case NONE, CLOCKWISE_180 -> this;
                case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> this.cycleBendAxis();
            };
        } else {
            Direction newExposedFace = rotation.rotate(this.exposedFace);
            // we can do this epicness because i organized this in the same order as the direction enum is ordered
            // basically imagine each bend state is a subgroup of 2
            // and theres a subgroup for each direction
            // so im preserving the index of the axis within that subgroup
            return Objects.requireNonNullElseGet(
                    from(newExposedFace, this.bentAroundAxis),
                    () -> Objects.requireNonNullElseGet(
                            from(newExposedFace, this.exposedFace.getAxis()),
                            () -> values()[(newExposedFace.ordinal() * 2) + (this.ordinal() % 2)]
                    )
            );
        }
    }
}
