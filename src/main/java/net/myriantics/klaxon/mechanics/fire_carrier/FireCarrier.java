package net.myriantics.klaxon.mechanics.fire_carrier;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

public interface FireCarrier {

    BlockApiLookup<FireCarrier, FireCarrierInteractionContext> SIDED = BlockApiLookup.get(
            KlaxonCommon.locate("fire_carrier"),
            FireCarrier.class,
            FireCarrierInteractionContext.class
    );

    /**
     * Attempts to ignite the fire carrier
     */
    void ignite();

    /**
     * Attempts to extinguish the fire carrier.
     */
    void extinguish();

    default boolean mayIgnite() {
        return true;
    }

    default boolean mayExtinguish() {
        return true;
    }

    boolean isIgnited();

    static void init() {
        FireCarrier.SIDED.registerFallback((level, pos, state, blockEntity, context) -> {
            if (state.getBlock() instanceof SimpleWorldlyFireCarrier simpleFireCarrier && !state.is(KlaxonBlockTags.SIMPLE_WORLDLY_FIRE_CARRIER_DENYLIST)) {
                return new FireCarrier() {
                    @Override
                    public void ignite() {
                        try {
                            simpleFireCarrier.klaxon$ignite(level, pos, context, state, blockEntity);
                        } catch (Exception e) {
                            throw new RuntimeException(SimpleWorldlyFireCarrier.getExceptionMessage(state, "ignite"), e);
                        }
                    }

                    @Override
                    public void extinguish() {
                        try {
                            simpleFireCarrier.klaxon$extinguish(level, pos, context, state, blockEntity);
                        } catch (Exception e) {
                            throw new RuntimeException(SimpleWorldlyFireCarrier.getExceptionMessage(state, "extinguish"), e);
                        }
                    }

                    @Override
                    public boolean isIgnited() {
                        try {
                            return simpleFireCarrier.klaxon$isIgnited(level, pos, context, state, blockEntity);
                        } catch (Exception e) {
                            throw new RuntimeException(SimpleWorldlyFireCarrier.getExceptionMessage(state, "check the ignition status of"), e);
                        }
                    }

                    @Override
                    public boolean mayExtinguish() {
                        try {
                            return simpleFireCarrier.klaxon$mayExtinguish(level, pos, context, state, blockEntity);
                        } catch (Exception e) {
                            throw new RuntimeException(SimpleWorldlyFireCarrier.getExceptionMessage(state, "check if we can extinguish"), e);
                        }
                    }

                    @Override
                    public boolean mayIgnite() {
                        try {
                            return simpleFireCarrier.klaxon$mayIgnite(level, pos, context, state, blockEntity);
                        } catch (Exception e) {
                            throw new RuntimeException(SimpleWorldlyFireCarrier.getExceptionMessage(state, "check if we can ignite"), e);
                        }
                    }
                };
            }
            return null;
        });

        FireCarrier.SIDED.registerFallback((level, pos, state, blockEntity, context) -> {
            if (state.isAir()) {
                return new FireCarrier() {

                    @Override
                    public boolean mayIgnite() {
                        return BaseFireBlock.canBePlacedAt(level, pos, context.interactedFace());
                    }

                    @Override
                    public boolean mayExtinguish() {
                        return false;
                    }

                    @Override
                    public void ignite() {
                        BlockState stateToPlace = BaseFireBlock.getState(level, pos);
                        level.setBlock(pos, stateToPlace, (Block.UPDATE_ALL_IMMEDIATE));
                    }

                    @Override
                    public void extinguish() {
                    }

                    @Override
                    public boolean isIgnited() {
                        return false;
                    }
                };
            }
            return null;
        });

        FireCarrier.SIDED.registerFallback((level, pos, state, blockEntity, context) -> {
            if (state.is(BlockTags.FIRE)) {
                return new FireCarrier() {
                    @Override
                    public boolean mayIgnite() {
                        return false;
                    }

                    @Override
                    public void ignite() {
                    }

                    @Override
                    public void extinguish() {
                        level.destroyBlock(pos, false);
                    }

                    @Override
                    public boolean isIgnited() {
                        return true;
                    }
                };
            }
            return null;
        });
    }
}
