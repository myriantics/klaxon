package net.myriantics.klaxon.item.equipment.tools;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.Orientation;
import net.minecraft.block.enums.RailShape;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.data.client.VariantSettings;
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
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.myriantics.klaxon.component.ability.InstabreakingToolComponent;
import net.myriantics.klaxon.registry.minecraft.KlaxonDataComponentTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.util.EquipmentSlotHelper;
import org.jetbrains.annotations.Nullable;

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

            ItemStack wrenchStack = context.getStack();

            if (player.isSneaking() && targetState.isIn(KlaxonBlockTags.WRENCH_PICKUPABLE)) {

                if (world instanceof ServerWorld serverWorld) {
                    ItemStack blockStack = new ItemStack(targetState.getBlock().asItem());

                    if (!blockStack.isEmpty()) {
                        if (player.getInventory().insertStack(blockStack)) serverWorld.removeBlock(targetPos, false);
                    }
                }
                world.breakBlock(targetPos, true, player);

                return ActionResult.SUCCESS;
            }

            if (targetState.isIn(KlaxonBlockTags.WRENCH_ROTATABLE)) {
                ActionResult result = rotateBlock(world, targetPos, targetState, context.getSide(), context.getHorizontalPlayerFacing(), context.getPlayerYaw());
                if (result.isAccepted()) {
                    Vec3d cords = targetPos.toCenterPos();
                    world.playSound(cords.getX(), cords.getY(), cords.getZ(), targetState.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 0.7f + 0.3f * world.getRandom().nextFloat(), 1.0f, true);
                    wrenchStack.damage(1, player, EquipmentSlotHelper.convert(context.getHand()));
                    return ActionResult.SUCCESS;
                }
            }
        }

        return super.useOnBlock(context);
    }

    public static ActionResult rotateBlock(World world, BlockPos targetPos, BlockState targetState, Direction clickedFace, @Nullable Direction playerFacing, @Nullable float playerYaw) {
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

            if (facing.equals(clickedFace)) {
                return ActionResult.FAIL;
            } else if (facing.getAxis().equals(clickedAxis)) {
                newState = targetState.with(Properties.FACING, clickedFace);
            } else {
                newState = targetState.with(Properties.FACING, facing.rotateClockwise(clickedFace.getAxis()));
            }
        }

        // Rotate blocks that have the Horizontal Facing block state
        if (newState == null && optionalHorizontalFacing.isPresent()) {
            Direction facing = optionalHorizontalFacing.get();
            Direction newDirection;

            if (facing.equals(clickedFace)) {
                return ActionResult.FAIL;
            } else if (facing.getAxis().equals(clickedAxis)) {
                newDirection = clickedFace;
            } else {
                newDirection = facing.rotateClockwise(clickedFace.getAxis());
                if (newDirection.getAxis().equals(Direction.Axis.Y)) return ActionResult.FAIL;
            }

            newState = targetState.with(Properties.HORIZONTAL_FACING, newDirection);
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
                RailShape toggled = null;
                // swap it if it's a dispenser to allow tracks to be routed on top of dispenser - more useful than setting the dispenser at the bottom of the track, blocking any carts.
                Direction.AxisDirection ascensionDirection = hasPlayer ? direction.getDirection() : direction.getDirection().getOpposite();
                // check to see if block can support ascending rails before doing it - won't stop you from correcting a wrongly ascending one, though
                if (railShape.isAscending() || AbstractRailBlock.hasTopRim(world, targetPos.offset(Direction.from(direction.getAxis(), ascensionDirection))))  {
                    toggled = toggleAscent(railShape, ascensionDirection);
                }

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
            RailShape railShape = optionalStraightRailShape.get();
            Direction.Axis railAxis = railShapeToAxis(railShape);
            int rotation = hasPlayer ? RotationPropertyHelper.fromYaw(playerYaw) : -1;
            RailShape newShape = null;

            if (railAxis == null) {
                // rail can't be curved here, but we do this to filter any out as good practice
                return ActionResult.FAIL;
            } else if (direction.getAxis().equals(railAxis)) {
                // if we're coming from the same direction that the rail's already facing, try to switch between ascending and flat rails
                RailShape toggled = null;
                // swap it if it's a dispenser to allow tracks to be routed on top of dispenser - more useful than setting the dispenser at the bottom of the track, blocking any carts.
                Direction.AxisDirection ascensionDirection = hasPlayer ? direction.getDirection() : direction.getDirection().getOpposite();
                // check to see if block can support ascending rails before doing it - won't stop you from correcting a wrongly ascending one, though
                if (railShape.isAscending() || AbstractRailBlock.hasTopRim(world, targetPos.offset(Direction.from(direction.getAxis(), ascensionDirection))))  {
                    toggled = toggleAscent(railShape, ascensionDirection);
                }

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

        if (newState != null) {
            if (!world.isClient() && !newState.equals(targetState)) {
                world.setBlockState(targetPos, newState);
                world.updateComparators(targetPos, targetState.getBlock());
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
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

    private static @Nullable RailShape yawToRailShape(RailShape shape, float yaw) {
        int rotation = RotationPropertyHelper.fromYaw(yaw);
        if (rotation == 0 || rotation == 8) return axisToRailShape(Direction.Axis.Z);
        if (rotation == 4 || rotation == 12) return axisToRailShape(Direction.Axis.X);
        // facing northeast
        if (rotation > 8 && rotation < 12) {
            switch (shape) {
                case EAST_WEST : return RailShape.NORTH_WEST;
                case NORTH_SOUTH : return RailShape.SOUTH_EAST;
            }
        }
        // facing southeast
        if (rotation > 12) {
            switch (shape) {
                case EAST_WEST : return RailShape.SOUTH_WEST;
                case ;
            }
        }

        return null;
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
