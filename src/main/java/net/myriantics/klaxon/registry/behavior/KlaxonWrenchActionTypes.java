package net.myriantics.klaxon.registry.behavior;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionType;
import net.myriantics.klaxon.registry.KlaxonRegistries;

import java.util.function.Supplier;

public abstract class KlaxonWrenchActionTypes {

    public static final Holder<WrenchActionType> PASS = register("pass", WrenchActionType::colorless);
    public static final Holder<WrenchActionType> PICKUP = register("pickup", WrenchActionType::orange);
    public static final Holder<WrenchActionType> FAIL = register("fail", WrenchActionType::red);
    public static final Holder<WrenchActionType> CURVE_RIGHT = register("curve_right");
    public static final Holder<WrenchActionType> CURVE_LEFT = register("curve_left");
    public static final Holder<WrenchActionType> ROTATE_CLOCKWISE = register("rotate_clockwise");
    public static final Holder<WrenchActionType> ROTATE_COUNTERCLOCKWISE = register("rotate_counterclockwise");
    public static final Holder<WrenchActionType> ROTATE_FORWARD = register("rotate_forward");
    public static final Holder<WrenchActionType> ROTATE_BACKWARD = register("rotate_backward");
    public static final Holder<WrenchActionType> ROTATE_LEFT = register("rotate_left");
    public static final Holder<WrenchActionType> ROTATE_RIGHT = register("rotate_right");
    public static final Holder<WrenchActionType> ROTATE_UP = register("rotate_up");
    public static final Holder<WrenchActionType> ROTATE_DOWN = register("rotate_down");
    public static final Holder<WrenchActionType> EXTRUDE = register("extrude");
    public static final Holder<WrenchActionType> RETRACT = register("retract");
    public static final Holder<WrenchActionType> CONNECT = register("connect");
    public static final Holder<WrenchActionType> DISCONNECT = register("disconnect");
    public static final Holder<WrenchActionType> FLIP = register("flip");
    public static final Holder<WrenchActionType> CYCLE = register("cycle");
    public static final Holder<WrenchActionType> ALIGN = register("align");

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Wrench Action Types!");
    }

    private static Holder<WrenchActionType> register(String name) {
        return register(name, WrenchActionType::green);
    }

    private static Holder<WrenchActionType> register(String name, Supplier<WrenchActionType> supplier) {
        return register(name, supplier.get());
    }

    private static Holder<WrenchActionType> register(String name, WrenchActionType type) {
        ResourceLocation id = KlaxonCommon.locate(name);
        return Registry.registerForHolder(KlaxonRegistries.WRENCH_ACTION_TYPE, id, type);
    }
}
