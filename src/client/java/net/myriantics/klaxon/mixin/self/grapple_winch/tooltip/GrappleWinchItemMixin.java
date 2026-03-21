package net.myriantics.klaxon.mixin.self.grapple_winch.tooltip;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.MutableComponent;
import net.myriantics.klaxon.item.equipment.tools.GrappleWinchItem;
import net.myriantics.klaxon.mechanics.grapple_winch.ClientGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.ClientGrappleWinchConnectionManager;
import net.myriantics.klaxon.util.KlaxonMathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(
        value = GrappleWinchItem.class
)
public abstract class GrappleWinchItemMixin {

    @WrapOperation(
            method = "createCableLengthDisplayText",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;")
    )
    private static MutableComponent klaxon$addCableLengthDataToTooltip(String key, Object[] args, Operation<MutableComponent> original) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return original.call(key, args);
        }

        // yoink the connection
        ClientGrappleWinchConnectionManager manager = ClientGrappleWinchConnectionManager.get(Minecraft.getInstance().level);
        @Nullable ClientGrappleWinchConnection connection = manager.fromPlayer(player);

        // initialize max cable length
        double maxCableLength = connection == null ? -1 : connection.getMaxCableLength();

        // only render live numbers if cable length is greater than 0
        // ensures no divide by 0
        // connection not being present also
        if (maxCableLength > 0) {
            double truncatedCableLength = KlaxonMathHelper.roundToTenth(connection.getCableLength());
            double ratio = truncatedCableLength / maxCableLength;

            MutableComponent populatedCableDisplay = original.call(key, new Object[]{truncatedCableLength, maxCableLength});

            // format text according to cable ratio
            if (ratio >= 1.0) {
                return populatedCableDisplay.withStyle(ChatFormatting.RED);
            } else if (ratio >= 0.75) {
                return populatedCableDisplay.withStyle(ChatFormatting.YELLOW);
            } else {
                return populatedCableDisplay.withStyle(ChatFormatting.GREEN);
            }
        }

        return original.call(key, args);
    }
}
