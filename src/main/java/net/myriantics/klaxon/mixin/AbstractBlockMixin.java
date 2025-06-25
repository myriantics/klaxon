package net.myriantics.klaxon.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.util.ExplosionMixinAccess;
import net.myriantics.klaxon.util.PermissionsHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.BiConsumer;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {
    @Inject(
            method = "onExploded",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    public void klaxon$blockConversionChecker(BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger, CallbackInfo ci) {
        if (!world.isClient() || (explosion.getCausingEntity() instanceof PlayerEntity player && PermissionsHelper.canModifyWorld(player))) return;

        // yoink origin state from the explosion
        Optional<BlockState> originState = Optional.ofNullable(((ExplosionMixinAccess) explosion).klaxon$getOriginState());

        if (originState.isPresent() && originState.get().isOf(KlaxonBlocks.HALLNOX_NETHER_REACTOR_CORE)) {

            world.setBlockState(pos, KlaxonBlocks.STEEL_BLOCK.getDefaultState());
            // we don't need to do any more processing if this is run.
            ci.cancel();
        }
    }
}
