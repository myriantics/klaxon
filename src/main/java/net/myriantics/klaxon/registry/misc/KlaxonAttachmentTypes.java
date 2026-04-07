package net.myriantics.klaxon.registry.misc;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.myriantics.klaxon.KlaxonCommon;

import java.util.function.Consumer;

public abstract class KlaxonAttachmentTypes {

    public static final AttachmentType<Integer> STEEL_LIGHTER_FIRE_PLACEMENT_TRACKER = register(
            "steel_lighter_fire_placement_tracker",
            builder -> builder
                    .initializer(() -> 0)
    );

    private static <T> AttachmentType<T> register(String name, Consumer<AttachmentRegistry.Builder<T>> consumer) {
        return AttachmentRegistry.create(KlaxonCommon.locate(name), consumer);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Attachment Types!");
    }
}
