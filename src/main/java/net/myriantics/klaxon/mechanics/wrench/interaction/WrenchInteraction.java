package net.myriantics.klaxon.mechanics.wrench.interaction;

import net.minecraft.core.Holder;
import net.minecraft.world.InteractionResult;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionType;
import net.myriantics.klaxon.registry.behavior.KlaxonWrenchActionTypes;
import net.myriantics.klaxon.util.BlockFaceRegion;

import java.util.Optional;

public class WrenchInteraction {
    private final WrenchActionType type;
    private final WrenchActionHandler handler;
    private final WrenchInteractionMap singletonMap;

    public static final WrenchInteraction NO_OP = of(KlaxonWrenchActionTypes.PASS, (context, rotation) -> Optional.empty());
    public static final WrenchInteraction FAIL = of(KlaxonWrenchActionTypes.FAIL, (context, rotation) -> Optional.of(InteractionResult.FAIL));

    protected WrenchInteraction(WrenchActionType type, WrenchActionHandler handler) {
        this.type = type;
        this.handler = handler;
        this.singletonMap = WrenchInteractionMap.fullBlock(this);
    }

    public static WrenchInteraction of(Holder<WrenchActionType> typeHolder, WrenchActionHandler handler) {
        return of(typeHolder.value(), handler);
    }

    public static WrenchInteraction of(WrenchActionType type, WrenchActionHandler handler) {
        return new WrenchInteraction(type, handler);
    }

    public WrenchInteractionMap toSingletonMap() {
        return this.singletonMap;
    }

    public WrenchActionType getType() {
        return this.type;
    }

    public Optional<InteractionResult> handle(WrenchActionContext context, BlockFaceRegion.Rotation rotation) {
        return this.handler.handle(context, rotation);
    }
}
