package net.myriantics.klaxon.registry.misc;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;

import java.util.function.Consumer;

public abstract class KlaxonAttachmentTypes {

    public static final AttachmentType<Integer> STEEL_LIGHTER_FIRE_PLACEMENT_TRACKER = register(
            "steel_lighter_fire_placement_tracker",
            builder -> builder
                    .initializer(() -> 0)
    );
    public static final AttachmentType<ExplosiveCatalystData> EXPLOSIVE_CATALYST_DATA = register(
            "explosive_catalyst_data",
            builder -> builder
                    .initializer(() -> ExplosiveCatalystData.ZERO)
                    .persistent(ExplosiveCatalystData.CODEC)
                    .syncWith(ExplosiveCatalystData.STREAM_CODEC, AttachmentSyncPredicate.all())
    );
    public static final AttachmentType<ItemStack> MUFFLER_STACK = register(
            "muffler_stack",
            builder -> builder
                    .initializer(() -> ItemStack.EMPTY)
                    .persistent(ItemStack.CODEC)
    );

    private static <T> AttachmentType<T> register(String name, Consumer<AttachmentRegistry.Builder<T>> consumer) {
        return AttachmentRegistry.create(KlaxonCommon.locate(name), consumer);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Attachment Types!");
    }
}
