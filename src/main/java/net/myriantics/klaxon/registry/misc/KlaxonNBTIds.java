package net.myriantics.klaxon.registry.misc;

import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonNBTIds {

    // grapple claw
    public static final String TICKS_SINCE_DAMAGED = KlaxonCommon.locateAlt("ticks_since_damaged");

    // player
    public static final String CURRENT_WINCH_CABLE_LENGTH = KlaxonCommon.locateAlt("current_winch_cable_length");

    // sniffer
    public static final String GERALD_SNIFFER_STATE = KlaxonCommon.locateAlt("gerald_sniffer_state");

    // grapple winch connection
    public static final String GRAPPLE_WINCH_CONNECTIONS = KlaxonCommon.locateAlt("grapple_winch_connections");
    public static final String GRAPPLING_HOOK = KlaxonCommon.locateAlt("grappling_hook");
    public static final String PLAYER_UUID = KlaxonCommon.locateAlt("player_uuid");
    public static final String CABLE_LENGTH = KlaxonCommon.locateAlt("cable_length");
    public static final String MAX_CABLE_LENGTH = KlaxonCommon.locateAlt("max_cable_length");
    public static final String HOOK_ANCHORED = KlaxonCommon.locateAlt("hook_anchored");

    // modular explosive
    public static final String MAX_FUSE_TIME = KlaxonCommon.locateAlt("max_fuse_time");
    public static final String FUSE_TIME = KlaxonCommon.locateAlt("fuse_time");
    public static final String MODIFY_WORLD = KlaxonCommon.locateAlt("modify_world");
    public static final String EXPLOSIVE_CATALYST_DATA = KlaxonCommon.locateAlt("explosive_catalyst_data");

    // blast processor
    public static final String CATALYST_STACK = KlaxonCommon.locateAlt("catalyst_stack");
    public static final String INGREDIENT_STACK = KlaxonCommon.locateAlt("ingredient_stack");

    // muffling
    public static final String MUFFLER_STACK = KlaxonCommon.locateAlt("muffler_stack");

    // general
    public static final String CUSTOM_NAME = KlaxonCommon.locateAlt("custom_name");

    // duct payload
    public static final String DUCT_PAYLOAD = KlaxonCommon.locateAlt("duct_payload");
    public static final String DUCT_PAYLOAD_SIZE = KlaxonCommon.locateAlt("size");
    public static final String DUCT_PAYLOAD_STACKS = KlaxonCommon.locateAlt("stacks");

    // contact charger
    public static final String CHARGING_STACK = KlaxonCommon.locateAlt("charging_stack");
    public static final String PREFERRED_REPLACEMENT_SLOT = KlaxonCommon.locateAlt("preferred_replacement_slot");
    public static final String KEEP_ALIVE_TICKS = KlaxonCommon.locateAlt("keep_alive_ticks");
    public static final String USER_UUID = KlaxonCommon.locateAlt("user_uuid");

    // energy sink
    public static final String REMAINING_POWERED_TICKS = KlaxonCommon.locateAlt("remaining_powered_ticks");
}
