package net.myriantics.klaxon.mixin.self.grapple_winch.tooltip;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.myriantics.klaxon.item.equipment.tools.GrappleWinchItem;
import net.myriantics.klaxon.mechanics.grapple_winch.ClientGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.ClientGrappleWinchConnectionManager;
import net.myriantics.klaxon.util.KlaxonMathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(
        value = GrappleWinchItem.class
)
public abstract class GrappleWinchItemMixin {

    @WrapOperation(
            method = "createCableLengthDisplayText",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/text/MutableText;")
    )
    private static MutableText klaxon$addCableLengthDataToTooltip(String key, Object[] args, Operation<MutableText> original) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return original.call(key, args);
        }

        // yoink the connection
        ClientGrappleWinchConnectionManager manager = ((ClientGrappleWinchConnectionManager.Access) player.getWorld()).klaxon$get();
        @Nullable ClientGrappleWinchConnection connection = manager.fromPlayer(player);

        // initialize max cable length
        double maxCableLength = connection == null ? -1 : connection.getMaxCableLength();

        // only render live numbers if cable length is greater than 0
        // ensures no divide by 0
        // connection not being present also
        if (maxCableLength > 0) {
            double truncatedCableLength = KlaxonMathHelper.roundToTenth(connection.getCableLength());
            double ratio = truncatedCableLength / maxCableLength;

            MutableText populatedCableDisplay = original.call(key, new Object[]{truncatedCableLength, maxCableLength});

            // format text according to cable ratio
            if (ratio >= 1.0) {
                return populatedCableDisplay.formatted(Formatting.RED);
            } else if (ratio >= 0.75) {
                return populatedCableDisplay.formatted(Formatting.YELLOW);
            } else {
                return populatedCableDisplay.formatted(Formatting.GREEN);
            }
        }

        return original.call(key, args);
    }
}
