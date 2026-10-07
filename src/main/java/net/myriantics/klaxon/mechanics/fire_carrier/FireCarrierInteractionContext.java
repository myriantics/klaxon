package net.myriantics.klaxon.mechanics.fire_carrier;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public record FireCarrierInteractionContext(@Nullable Entity entity, Direction interactedFace) {

    public static final FireCarrierInteractionContext UP = new FireCarrierInteractionContext(Direction.UP);
    public static final FireCarrierInteractionContext DOWN = new FireCarrierInteractionContext(Direction.DOWN);
    public static final FireCarrierInteractionContext NORTH = new FireCarrierInteractionContext(Direction.NORTH);
    public static final FireCarrierInteractionContext EAST = new FireCarrierInteractionContext(Direction.EAST);
    public static final FireCarrierInteractionContext SOUTH = new FireCarrierInteractionContext(Direction.SOUTH);
    public static final FireCarrierInteractionContext WEST = new FireCarrierInteractionContext(Direction.WEST);

    private FireCarrierInteractionContext(Direction interactedFace) {
        this(null, interactedFace);
    }

    public static FireCarrierInteractionContext of(Direction direction) {
        return switch (direction) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }
}
