package net.myriantics.klaxon.item.equipment.tools;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.Orientation;
import net.minecraft.block.enums.RailShape;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.myriantics.klaxon.component.ability.InstabreakingToolComponent;
import net.myriantics.klaxon.registry.minecraft.KlaxonDataComponentTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.util.EquipmentSlotHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class WrenchItem extends MiningToolItem {
    public WrenchItem(ToolMaterial material, Settings settings) {
        super(material, KlaxonBlockTags.WRENCH_MINEABLE, settings
                .component(KlaxonDataComponentTypes.INSTABREAK_TOOL_COMPONENT, new InstabreakingToolComponent(KlaxonBlockTags.WRENCH_INSTABREAKABLE))
        );
    }

    public static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material, float baseAttackDamage, float attackSpeed) {
        return AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, material.getAttackDamage() + baseAttackDamage, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, attackSpeed, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                ).build();
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos targetPos = context.getBlockPos();
        BlockState targetState = world.getBlockState(targetPos);
        PlayerEntity player = context.getPlayer();

        if (player != null && !targetState.isIn(KlaxonBlockTags.WRENCH_INTERACTION_DENYLIST)) {

            if (player.isSneaking() && targetState.isIn(KlaxonBlockTags.WRENCH_PICKUPABLE)) {

                if (world instanceof ServerWorld serverWorld) {
                    List<ItemStack> outputStacks = Block.getDroppedStacks(targetState, serverWorld, targetPos, serverWorld.getBlockEntity(targetPos));
                    if (!outputStacks.isEmpty()) {
                        for (ItemStack stack : outputStacks) {
                            // don't insert the stack if player is already creative - unless it's valuable, then do
                            if (!player.isCreative() || stack.contains(DataComponentTypes.CONTAINER) || stack.contains(DataComponentTypes.CONTAINER_LOOT)) {

                                // dump the rest of the stack into the world if it doesn't fit into player's inventory
                                if (!player.getInventory().insertStack(stack) && !stack.isEmpty()) Block.dropStack(serverWorld, targetPos, stack);
                            }
                        }
                    }

                    // drop is false here because we already handled the drops
                    // only break on server because sound plays twice on client otherwise
                    world.breakBlock(targetPos, false, player);
                }

                return ActionResult.SUCCESS;
            }

            if (targetState.isIn(KlaxonBlockTags.WRENCH_ROTATABLE)) {
                ActionResult result = rotateBlock(world, targetPos, targetState, context.getSide(), context.getHorizontalPlayerFacing(), context.getHitPos());
                if (result.isAccepted()) {
                    Vec3d cords = targetPos.toCenterPos();
                    world.playSound(cords.getX(), cords.getY(), cords.getZ(), targetState.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 0.7f + 0.3f * world.getRandom().nextFloat(), 1.0f, true);
                    return ActionResult.SUCCESS;
                }
            }
        }

        return super.useOnBlock(context);
    }

    public static ActionResult rotateBlock(World world, BlockPos targetPos, BlockState targetState, Direction clickedFace, @Nullable Direction playerFacing, @Nullable Vec3d clickedPos) {
        Optional<Boolean> optionalExtended = targetState.getOrEmpty(Properties.EXTENDED);
        Optional<ChestType> optionalChestType = targetState.getOrEmpty(Properties.CHEST_TYPE);

        // no rotating extended pistons or other bs
        if (optionalChestType.isPresent() && !optionalChestType.get().equals(ChestType.SINGLE)) return ActionResult.FAIL;
        if (optionalExtended.isPresent() && optionalExtended.get()) return ActionResult.FAIL;
        if (targetState.contains(Properties.SHORT)) return ActionResult.FAIL;

        Optional<Direction> optionalFacing = targetState.getOrEmpty(Properties.FACING);
        Optional<Direction> optionalHorizontalFacing = targetState.getOrEmpty(Properties.HORIZONTAL_FACING);
        Optional<Direction.Axis> optionalAxis = targetState.getOrEmpty(Properties.AXIS);
        Optional<Direction.Axis> optionalHorizontalAxis = targetState.getOrEmpty(Properties.HORIZONTAL_AXIS);
        Optional<Orientation> optionalOrientation = targetState.getOrEmpty(Properties.ORIENTATION);
        Optional<RailShape> optionalRailShape = targetState.getOrEmpty(Properties.RAIL_SHAPE);
        Optional<RailShape> optionalStraightRailShape = targetState.getOrEmpty(Properties.STRAIGHT_RAIL_SHAPE);


        boolean hasPlayer = playerFacing != null;

        Direction.Axis clickedAxis = clickedFace.getAxis();

        BlockState newState = null;

        if (optionalOrientation.isPresent()) {
            Orientation orientation = optionalOrientation.get();
            Direction facing = orientation.getFacing();
            Direction rotation = orientation.getRotation();

            Orientation newOrientation;

            if (facing.getAxis().equals(clickedAxis)) {
                newOrientation = Orientation.byDirections(facing, rotation.rotateClockwise(clickedAxis));
            } else {
                newOrientation = Orientation.byDirections(facing.rotateClockwise(clickedAxis), rotation);
            }

            if (newOrientation != null) newState = targetState.with(Properties.ORIENTATION, newOrientation);
        }

        if (newState == null && optionalFacing.isPresent()) {
            Direction facing = optionalFacing.get();
            Direction newFacing = null;

            if (facing.getAxis().equals(clickedAxis)) {
                newFacing = facing.getOpposite();
            } else {
                newFacing = facing.rotateClockwise(clickedFace.getAxis());
            }

            if (newFacing != null) newState = targetState.with(Properties.FACING, newFacing);
        }

        // Rotate blocks that have the Horizontal Facing block state
        if (newState == null && optionalHorizontalFacing.isPresent()) {
            Direction horizontalFacing = optionalHorizontalFacing.get();
            Direction newHorizontalFacing;

            if (horizontalFacing.getAxis().equals(clickedAxis)) {
                newHorizontalFacing = horizontalFacing.getOpposite();
            } else {
                newHorizontalFacing = horizontalFacing.rotateClockwise(clickedFace.getAxis());
                if (newHorizontalFacing.getAxis().equals(Direction.Axis.Y)) return ActionResult.FAIL;
            }

            newState = targetState.with(Properties.HORIZONTAL_FACING, newHorizontalFacing);
        }

        // Rotate blocks with the Axis block state
        if (newState == null && optionalAxis.isPresent()) {
            Direction.Axis axis = optionalAxis.get();

            if (axis.equals(clickedAxis)) {
                return ActionResult.FAIL;
            }

            newState = targetState.with(Properties.AXIS, Direction.from(axis, Direction.AxisDirection.NEGATIVE).rotateClockwise(clickedAxis).getAxis());
        }

        // Rotate blocks with the Horizontal Axis block state
        if (newState == null && optionalHorizontalAxis.isPresent()) {
            Direction.Axis axis = optionalHorizontalAxis.get();
            Direction.Axis newAxis;

            if (axis.equals(clickedAxis)) {
                return ActionResult.FAIL;
            } else {
                newAxis = Direction.from(axis, Direction.AxisDirection.NEGATIVE).rotateClockwise(clickedAxis).getAxis();
                if (newAxis.equals(Direction.Axis.Y)) return ActionResult.FAIL;
            }

            newState = targetState.with(Properties.HORIZONTAL_AXIS, newAxis);
        }

        // Rotate non-curving rails
        if (newState == null && optionalStraightRailShape.isPresent()) {
            Direction direction = hasPlayer ? playerFacing : clickedFace;
            RailShape railShape = optionalStraightRailShape.get();
            Direction.Axis railAxis = railShapeToAxis(railShape);
            RailShape newShape = null;

            if (railAxis == null) {
                // rail can't be curved here, but we do this to filter any out as good practice
                return ActionResult.FAIL;
            } else if (direction.getAxis().equals(railAxis)) {
                // if we're coming from the same direction that the rail's already facing, try to switch between ascending and flat rails
                Direction.AxisDirection ascensionDirection = hasPlayer ? direction.getDirection() : direction.getDirection().getOpposite();
                // swap it if it's a dispenser to allow tracks to be routed on top of dispenser - more useful than setting the dispenser at the bottom of the track, blocking any carts.
                RailShape toggled = tryAscend(world, railShape, targetPos, ascensionDirection);

                if (toggled != null) newShape = toggled;
            } else {
                // if the rail is being rotated from a horizontal axis, rotate rail to be on that axis
                RailShape rotated = axisToRailShape(direction.getAxis());

                // if a dispenser is wrenching from top or bottom, flip the rail's orientation
                if (rotated == null) {
                    Direction.Axis axis = flipHorizontalAxis(railAxis);
                    if(axis != null) rotated = axisToRailShape(axis);
                }

                if (rotated != null) newShape = rotated;
            }

            if (newShape != null) newState = targetState.with(Properties.STRAIGHT_RAIL_SHAPE, newShape);
        }

        // Rotate curving rails
        if (newState == null && optionalRailShape.isPresent()) {
            Direction direction = hasPlayer ? playerFacing : clickedFace;
            RailShape railShape = optionalRailShape.get();
            Direction.Axis railAxis = railShapeToAxis(railShape);
            RailShape newShape = null;

            // run player logic on rails
            if (hasPlayer && clickedPos != null) {
                newShape = playerRotateCurvingRail(world, railShape, playerFacing, targetPos, clickedPos);
            } else {

            }

            /*
            if (railAxis == null) {
                // process curved rails
                // try to straighten any curved rails to the axis player is facing
                RailShape straightened = axisToRailShape(direction.getAxis());
                if (straightened != null) {
                    // commit player interaction
                    newShape = straightened;
                } else {
                    // don't bother doing dispenser logic if there's a player
                    if (hasPlayer) return ActionResult.FAIL;
                    // compute then commit dispenser interaction
                    // todo: add dispenser curved rail rotation code
                }
            } else if (direction.getAxis().equals(railAxis)) {
                // if our direction is the same direction as a straight rail, try to make it ascending

                // swap it if it's a dispenser to allow tracks to be routed on top of dispenser - more useful than setting the dispenser at the bottom of the track, blocking any carts.
                Direction.AxisDirection ascensionDirection = hasPlayer ? direction.getDirection() : direction.getDirection().getOpposite();

                RailShape toggled = tryAscend(world, railShape, targetPos, ascensionDirection);

                if (toggled != null) newShape = toggled;
            } else {
                // try to rotate or flip a rail
                RailShape rotated = null;
                if (hasPlayer) {
                    // if we've got a player, try to rotate the rail based on their look direction
                    rotated = playerRotateCurvingRail(railShape, direction, targetPos, clickedPos);
                } else {
                    // if we've got a dispenser, just flip the orientation of the rail
                }

                if (rotated != null) newShape = rotated;
            }
             */

            if (newShape != null) newState = targetState.with(Properties.RAIL_SHAPE, newShape);
        }

        if (newState != null) {
            if (!world.isClient() && !newState.equals(targetState)) {
                world.setBlockState(targetPos, newState);
                if (hasPlayer) world.updateComparators(targetPos.offset(clickedFace), newState.getBlock());
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private static @Nullable RailShape tryAscend(World world, RailShape railShape, BlockPos railPos, Direction.AxisDirection ascensionDirection) {
        Direction.Axis railAxis = railShapeToAxis(railShape);
        if (railAxis == null) return null;
        // check to see if block can support ascending rails before doing it - won't stop you from correcting a wrongly ascending one, though
        if (railShape.isAscending() || AbstractRailBlock.hasTopRim(world, railPos.offset(Direction.from(railAxis, ascensionDirection)))) {
            return toggleAscent(railShape, ascensionDirection);
        }

        return null;
    }

    private static Direction.Axis flipHorizontalAxis(Direction.Axis axis) {
        switch (axis) {
            case X -> {
                return Direction.Axis.Z;
            }
            case Z -> {
                return Direction.Axis.X;
            }
        }

        return null;
    }

    private static @Nullable RailShape toggleAscent(RailShape railShape, Direction.AxisDirection direction) {
        switch (railShape) {
            case NORTH_SOUTH -> {
                return direction.equals(Direction.AxisDirection.POSITIVE) ? RailShape.ASCENDING_SOUTH : RailShape.ASCENDING_NORTH;
            }
            case EAST_WEST -> {
                return direction.equals(Direction.AxisDirection.POSITIVE) ? RailShape.ASCENDING_EAST : RailShape.ASCENDING_WEST;
            }
            case ASCENDING_EAST, ASCENDING_WEST -> {
                return RailShape.EAST_WEST;
            }
            case ASCENDING_NORTH, ASCENDING_SOUTH -> {
                return RailShape.NORTH_SOUTH;
            }
        }

        return null;
    }


    private static @Nullable RailShape playerRotateCurvingRail(World world, RailShape railShape, Direction lookDirection, BlockPos railPos, Vec3d hitPos) {
        Vec3d railCenterPos = railPos.toCenterPos();

        Direction.Axis railAxis = railShapeToAxis(railShape);
        Direction.Axis lookAxis = lookDirection.getAxis();

        RailShape newShape = null;

        // try to toggle ascension / descension first
        if (railShape.isAscending() || lookAxis.equals(railAxis)) {
            RailShape toggled = tryAscend(world, railShape, railPos, lookDirection.getDirection());
            // make sure that we've changed something before committing
            if (toggled != null && !toggled.equals(railShape)) newShape = toggled;
        }

        // if the rail is curved, straighten it out
        if (newShape == null && (!lookAxis.equals(railAxis))) {
            RailShape straightened = axisToRailShape(lookAxis);
            // make sure that we've changed something before committing
            if (straightened != null && !straightened.equals(newShape)) newShape = straightened;
        }

        // only try to rotate straight rails when no other operations have been performed
        if (newShape == null && railAxis != null) {
            // we're already going to rotate it, so broaden search to either side of the rail
            Direction clickDirection =
                    Direction.getFacing(railAxis.equals(Direction.Axis.X)
                    ? new Vec3d(0, 0, hitPos.getZ() - railCenterPos.getZ())
                    : new Vec3d(hitPos.getX() - railCenterPos.getX(), 0, 0)
            );

            // if player clicks on the axis perpendicular to looking axis, rotate rail.
            switch (clickDirection) {
                case NORTH -> {
                    newShape = lookDirection.equals(Direction.WEST) ? RailShape.NORTH_EAST : RailShape.NORTH_WEST;
                }
                case SOUTH -> {
                    newShape = lookDirection.equals(Direction.WEST) ? RailShape.SOUTH_EAST : RailShape.SOUTH_WEST;
                }
                case WEST -> {
                    newShape = lookDirection.equals(Direction.NORTH) ? RailShape.SOUTH_WEST : RailShape.NORTH_WEST;
                }
                case EAST -> {
                    newShape = lookDirection.equals(Direction.NORTH) ? RailShape.SOUTH_EAST : RailShape.NORTH_EAST;
                }
            }
        }

        return newShape;
    }

    private static @Nullable RailShape axisToRailShape(Direction.Axis axis) {
        switch (axis) {
            case X -> {
                return RailShape.EAST_WEST;
            }
            case Z -> {
                return RailShape.NORTH_SOUTH;
            }
        }

        return null;
    }

    private static @Nullable Direction.Axis railShapeToAxis(RailShape railShape) {
        switch (railShape) {
            case NORTH_SOUTH, ASCENDING_NORTH, ASCENDING_SOUTH -> {
                return Direction.Axis.Z;
            }
            case EAST_WEST, ASCENDING_EAST, ASCENDING_WEST -> {
                return Direction.Axis.X;
            }
        }

        return null;
    }
}
