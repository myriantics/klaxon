package net.myriantics.klaxon.registry.entity;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.PacketCodecs;
import net.myriantics.klaxon.KlaxonCommon;

import java.util.function.Consumer;

public abstract class KlaxonDataAttachments {
    public static AttachmentType<Boolean> HEAVY_EQUIPMENT = register("heavy_equipment", Boolean.class, builder -> {
        builder
                .persistent(Codec.BOOL)
                .syncWith(PacketCodecs.BOOL, AttachmentSyncPredicate.all())
                .initializer(() -> false);
    });

    private static <C extends Object, T extends C> AttachmentType<T> register(String name, Class<C> clazz, Consumer<AttachmentRegistry.Builder<T>> builderConsumer) {
        return AttachmentRegistry.create(KlaxonCommon.locate(name), builderConsumer);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Initialized KLAXON's Data Attachments!");
    }
}
