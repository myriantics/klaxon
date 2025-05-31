package net.myriantics.klaxon.item.equipment.tools;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.Orientation;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.myriantics.klaxon.component.ability.InstabreakingToolComponent;
import net.myriantics.klaxon.registry.minecraft.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.minecraft.KlaxonDataComponentTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.util.KlaxonRailHelper;
import net.myriantics.klaxon.util.PermissionsHelper;
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
        ItemStack wrenchStack = context.getStack();

        if (player != null) {
            // Wrench pickup ability requires both CAN_PLACE_ON and CAN_DESTROY components to work in Adventure Mode
            if (player.isSneaking() && targetState.isIn(KlaxonBlockTags.WRENCH_PICKUP_ALLOWLIST) && !targetState.isIn(KlaxonBlockTags.WRENCH_PICKUP_DENYLIST) && (PermissionsHelper.canModifyWorld(player) || wrenchStack.canBreak(new CachedBlockPosition(world, targetPos, false)))) {

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
                    KlaxonAdvancementTriggers.triggerWrenchUsage((ServerPlayerEntity) player, UsageType.PICKUP, targetState);
                }

                return ActionResult.SUCCESS;
            }

            // Only requires CAN_PLACE_ON in adventure mode
            if (targetState.isIn(KlaxonBlockTags.WRENCH_ROTATION_ALLOWLIST) && !targetState.isIn(KlaxonBlockTags.WRENCH_ROTATION_DENYLIST)) {
                Optional<BlockState> rotatedState = getRotatedState(world, targetPos, targetState, context.getSide(), context.getHorizontalPlayerFacing(), context.getHitPos());

                if (rotatedState.isPresent()) {
                    Vec3d cords = targetPos.toCenterPos();
                    world.playSound(cords.getX(), cords.getY(), cords.getZ(), targetState.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 0.7f + 0.3f * world.getRandom().nextFloat(), 1.0f, true);
                    if (player instanceof ServerPlayerEntity serverPlayer) {
                        KlaxonAdvancementTriggers.triggerWrenchUsage(serverPlayer, UsageType.ROTATION, targetState);
                        world.setBlockState(targetPos, rotatedState.get());
                        world.updateNeighbor(targetPos, rotatedState.get().getBlock(), targetPos);
                        world.updateComparators(targetPos, rotatedState.get().getBlock());
                    }
                    return ActionResult.SUCCESS;
                }
            }
        }

        return super.useOnBlock(context);
    }

    public static Optional<BlockState> getRotatedState(World world, BlockPos targetPos, BlockState targetState, Direction clickedFace, @Nullable Direction playerFacing, @Nullable Vec3d clickedPos) {
        Optional<Boolean> optionalExtended = targetState.getOrEmpty(Properties.EXTENDED);
        Optional<ChestType> optionalChestType = targetState.getOrEmpty(Properties.CHEST_TYPE);

        // no rotating extended pistons or other bs
        if (optionalChestType.isPresent() && !optionalChestType.get().equals(ChestType.SINGLE)) return Optional.empty();
        if (optionalExtended.isPresent() && optionalExtended.get()) return Optional.empty();
        if (targetState.contains(Properties.SHORT)) return Optional.empty();

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
                if (newHorizontalFacing.getAxis().equals(Direction.Axis.Y)) return Optional.empty();
            }

            newState = targetState.with(Properties.HORIZONTAL_FACING, newHorizontalFacing);
        }

        // Rotate blocks with the Axis block state
        if (newState == null && optionalAxis.isPresent()) {
            Direction.Axis axis = optionalAxis.get();

            if (axis.equals(clickedAxis)) {
                return Optional.empty();
            }

            newState = targetState.with(Properties.AXIS, Direction.from(axis, Direction.AxisDirection.NEGATIVE).rotateClockwise(clickedAxis).getAxis());
        }

        // Rotate blocks with the Horizontal Axis block state
        if (newState == null && optionalHorizontalAxis.isPresent()) {
            Direction.Axis axis = optionalHorizontalAxis.get();
            Direction.Axis newAxis;

            if (axis.equals(clickedAxis)) {
                return Optional.empty();
            } else {
                newAxis = Direction.from(axis, Direction.AxisDirection.NEGATIVE).rotateClockwise(clickedAxis).getAxis();
                if (newAxis.equals(Direction.Axis.Y)) return Optional.empty();
            }

            newState = targetState.with(Properties.HORIZONTAL_AXIS, newAxis);
        }

        // Rotate non-curving rails
        if (newState == null && optionalStraightRailShape.isPresent()) {
            Direction direction = hasPlayer ? playerFacing : clickedFace;
            RailShape railShape = optionalStraightRailShape.get();
            Direction.Axis railAxis = KlaxonRailHelper.railShapeToAxis(railShape);
            RailShape newShape = null;

            if (railAxis == null) {
                // rail can't be curved here, but we do this to filter any out as good practice
                return Optional.empty();
            } else if (direction.getAxis().equals(railAxis)) {
                // if we're coming from the same direction that the rail's already facing, try to switch between ascending and flat rails
                Direction.AxisDirection ascensionDirection = hasPlayer ? direction.getDirection() : direction.getDirection().getOpposite();
                // swap it if it's a dispenser to allow tracks to be routed on top of dispenser - more useful than setting the dispenser at the bottom of the track, blocking any carts.
                RailShape toggled = KlaxonRailHelper.tryToggleAscending(world, railShape, targetPos, ascensionDirection);

                if (toggled != null) newShape = toggled;
            } else {
                // if the rail is being rotated from a horizontal axis, rotate rail to be on that axis
                RailShape rotated = KlaxonRailHelper.axisToRailShape(direction.getAxis());

                // if a dispenser is wrenching from top or bottom, flip the rail's orientation
                if (rotated == null) {
                    Direction.Axis axis = KlaxonRailHelper.flipHorizontalAxis(railAxis);
                    if(axis != null) rotated = KlaxonRailHelper.axisToRailShape(axis);
                }

                if (rotated != null) newShape = rotated;
            }

            if (newShape != null) newState = targetState.with(Properties.STRAIGHT_RAIL_SHAPE, newShape);
        }

        // Rotate curving rails
        if (newState == null && optionalRailShape.isPresent()) {
            Direction direction = hasPlayer ? playerFacing : clickedFace;
            RailShape railShape = optionalRailShape.get();
            RailShape newShape;

            if (hasPlayer && clickedPos != null) {
                // run player logic on rails
                newShape = playerRotateCurvingRail(world, railShape, playerFacing, targetPos, clickedPos);
            } else {
                // run dispenser logic on rails
                newShape = dispenserRotateCurvingRails(world, targetPos, railShape, direction);
            }

            if (newShape != null) newState = targetState.with(Properties.RAIL_SHAPE, newShape);
        }

        // make sure that we've actually changed something
        if (newState != null && !newState.equals(targetState)) {
            return Optional.of(newState);
        }

        return Optional.empty();
    }

    private static @Nullable RailShape dispenserRotateCurvingRails(World world, BlockPos railPos , RailShape railShape, Direction dispenserFacing) {
        Direction.Axis railAxis = KlaxonRailHelper.railShapeToAxis(railShape);
        Direction.Axis perpendicularRailAxis = railAxis != null ? (railAxis.equals(Direction.Axis.Z) ? Direction.Axis.X : Direction.Axis.Z ) : null;
        Direction.Axis dispenserAxis = dispenserFacing.getAxis();

        RailShape newShape = null;

        switch (dispenserFacing) {
            case DOWN, UP -> {
                // start off with making the rail flat if needed
                if (railShape.isAscending()) {
                    RailShape toggled = KlaxonRailHelper.tryToggleAscending(world, railShape, railPos, dispenserFacing.getOpposite().getDirection());
                    if (toggled != null && !toggled.equals(railShape)) newShape = toggled;
                }

                if (newShape == null) {
                    RailShape rotated = KlaxonRailHelper.rotateCurvingRail(railShape, dispenserFacing, railAxis);

                    if (rotated != null) newShape = rotated;
                }
            }
            case NORTH, SOUTH, WEST, EAST -> {
                // start off with trying to make it ascending
                if (dispenserAxis.equals(railAxis)) {
                    RailShape toggled = KlaxonRailHelper.tryToggleAscending(world, railShape, railPos, dispenserFacing.getOpposite().getDirection());
                    if (toggled != null && !toggled.equals(railShape)) newShape = toggled;
                }

                // then try to correct rail to dispenser direction
                if (newShape == null && !dispenserAxis.equals(railAxis)) {
                    RailShape straightened = KlaxonRailHelper.axisToRailShape(dispenserAxis);
                    if (straightened != null && !straightened.equals(railShape)) newShape = straightened;
                }
            }
        }

        return newShape;
    }


    private static @Nullable RailShape playerRotateCurvingRail(World world, RailShape railShape, Direction lookDirection, BlockPos railPos, Vec3d hitPos) {
        Vec3d railCenterPos = railPos.toCenterPos();

        Direction.Axis railAxis = KlaxonRailHelper.railShapeToAxis(railShape);
        Direction.Axis lookAxis = lookDirection.getAxis();

        RailShape newShape = null;

        // try to toggle ascension / descension first
        if (railShape.isAscending() || lookAxis.equals(railAxis)) {
            RailShape toggled = KlaxonRailHelper.tryToggleAscending(world, railShape, railPos, lookDirection.getDirection());
            // make sure that we've changed something before committing
            if (toggled != null && !toggled.equals(railShape)) newShape = toggled;
        }

        // if the rail is curved, straighten it out
        if (newShape == null && (!lookAxis.equals(railAxis))) {
            RailShape straightened = KlaxonRailHelper.axisToRailShape(lookAxis);
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

    public enum UsageType implements StringIdentifiable {
        ROTATION,
        PICKUP;


        public static Codec<UsageType> CODEC = StringIdentifiable.createCodec(UsageType::values);

        @Override
        public String asString() {
            return toString().toLowerCase();
        }
    }
}
