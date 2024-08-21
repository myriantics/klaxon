package net.myriantics.klaxon.networking;

import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonMain;

// networking layout yoinked from spectrum github
public class KlaxonS2CPackets {
    public static final Identifier BLAST_PROCESSOR_SCREEN_DATA_SYNC_S2C = new Identifier(KlaxonMain.MOD_ID, "blast_processor_screen_data_sync_s2c");
    public static final Identifier FAST_INPUT_SYNC_S2C = new Identifier(KlaxonMain.MOD_ID, "fast_input_sync_s2c");
}
