package net.myriantics.klaxon.registry.misc;

import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonNBTIds {

    // grapple claw
    public static final String IS_WINCH_CABLE_ATTACHED = KlaxonCommon.locateAlt("is_winch_cable_attached");
    public static final String TICKS_SINCE_DAMAGED = KlaxonCommon.locateAlt("ticks_since_damaged");
    public static final String WINCH_ATTACHED_PLAYER = KlaxonCommon.locateAlt("winch_attached_player");

    // player
    public static final String ATTACHED_GRAPPLE_CLAW = KlaxonCommon.locateAlt("attached_grapple_claw");
    public static final String CURRENT_WINCH_CABLE_LENGTH = KlaxonCommon.locateAlt("current_winch_cable_length");
}
