package net.myriantics.klaxon.mixin.minecraft.nether_reaction;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.recipe.nether_reaction.NetherReactionRecipeLogic;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.misc.KlaxonParticleTypes;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockEntityTypeTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiConsumer;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;

    @Mutable
    @Shadow
    @Final
    private ParticleEffect particle;

    @Mutable
    @Shadow
    @Final
    private RegistryEntry<SoundEvent> soundEvent;

    @Mutable
    @Shadow
    @Final
    private ParticleEffect emitterParticle;
    @Unique
    private boolean klaxon$isNetherReactionExplosion = false;

    @Inject(
            method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/explosion/Explosion$DestructionType;Lnet/minecraft/particle/ParticleEffect;Lnet/minecraft/particle/ParticleEffect;Lnet/minecraft/registry/entry/RegistryEntry;)V",
            at = @At(value = "TAIL")
    )
    private void klaxon$setOriginState(World world, Entity entity, DamageSource damageSource, ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType, ParticleEffect particle, ParticleEffect emitterParticle, RegistryEntry<SoundEvent> soundEvent, CallbackInfo ci) {
        BlockState explosionOriginBlockState = world.getBlockState(BlockPos.ofFloored(x, y, z));

        // if the blockstate at the explosion's origin is a valid conversion catalyst, update the stored variable to say so.
        if (NetherReactionRecipeLogic.test(explosionOriginBlockState)) {
            this.klaxon$isNetherReactionExplosion = true;

            // proc block activation advancement
            for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, Box.of(new Vec3d(x, y, z), 24, 24, 24))) {
                KlaxonAdvancementTriggers.triggerBlockActivation(serverPlayerEntity, explosionOriginBlockState);
            }

            this.particle = KlaxonParticleTypes.NETHER_REACTION_EXPLOSION;
            this.emitterParticle = KlaxonParticleTypes.NETHER_REACTION_EXPLOSION_EMITTER;
            this.soundEvent = KlaxonSoundEvents.NETHER_REACTION_EXPLOSION;
        }
    }

    @WrapOperation(
            method = "collectBlocksAndDamageEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/explosion/ExplosionBehavior;getBlastResistance(Lnet/minecraft/world/explosion/Explosion;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/fluid/FluidState;)Ljava/util/Optional;")
    )
    private Optional<Float> klaxon$ignoreOriginConversionCatalyst(ExplosionBehavior instance, Explosion explosion, BlockView world, BlockPos targetPos, BlockState targetState, FluidState fluidState, Operation<Optional<Float>> original) {
        // this is to prevent high blast resistance catalysts completely bonking the strength of the conversion reaction.
        if (klaxon$isNetherReactionExplosion && targetPos.equals(BlockPos.ofFloored(x, y, z))) {
            return Optional.of(0.0f);
        }

        // if the target state is anything but the origin conversion catalyst, return the original value.
        return original.call(instance, explosion, world, targetPos, targetState, fluidState);
    }

    @WrapMethod(
            method = "affectWorld"
    )
    private void klaxon$alwaysEnableParticlesForNetherReactionExplosions(boolean particles, Operation<Void> original) {
        original.call(particles);
    }

    @WrapOperation(
            method = "affectWorld",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onExploded(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/explosion/Explosion;Ljava/util/function/BiConsumer;)V")
    )
    private void klaxon$hijackBlockDestruction(
            BlockState instance,
            World world,
            BlockPos pos,
            Explosion explosion,
            BiConsumer<ItemStack, BlockPos> biConsumer,
            Operation<Void> original,
            @Share("klaxon$netherReactionOutputs") LocalRef<HashMap<Block, Block>> netherReactionOutputs
    ) {
        // if it's not a nether reaction explosion, call original method and break out of this one
        if (!klaxon$isNetherReactionExplosion) {
            original.call(instance, world, pos, explosion, biConsumer);
            return;
        }

        // if the target state is immune to nether reaction, skip operating on it
        if (instance.isIn(KlaxonBlockTags.NETHER_REACTION_IMMUNE)) {
            return;
        }

        // don't convert any nether reactor cores aside from the origin one
        if (instance.isIn(KlaxonBlockTags.NETHER_REACTOR_CORES) && !pos.equals(BlockPos.ofFloored(x, y, z))) {
            return;
        }

        // sign text preserved for reapplication later
        Pair<SignText, SignText> signText = null;

        // yonk the blockentity from the target position
        BlockEntity targetBlockEntity = world.getBlockEntity(pos);
        if (targetBlockEntity != null) {

            // if there is a blockentity, make sure it's tagged as overwritable before replacing it
            RegistryEntry<BlockEntityType<?>> entry = targetBlockEntity.getType().getRegistryEntry();
            if (entry != null && !entry.isIn(KlaxonBlockEntityTypeTags.NETHER_REACTION_OVERWRITABLE) && !(targetBlockEntity instanceof SignBlockEntity)) {
                // if it's not overwritable, skip further operations
                return;
            }

            // preserve the text from sign block entities
            if (targetBlockEntity instanceof SignBlockEntity signBlockEntity) {
                signText = new Pair<>(signBlockEntity.getFrontText(), signBlockEntity.getBackText());
            }
        }

        // override for door blocks because i cant think of a better way to handle them
        if (instance.getBlock() instanceof DoorBlock && instance.get(DoorBlock.HALF).equals(DoubleBlockHalf.UPPER)) {
            pos = pos.down();
            instance = world.getBlockState(pos);
        }

        if (world instanceof ServerWorld serverWorld) {
            BlockState newState = NetherReactionRecipeLogic.getOutputState(instance, pos, serverWorld, explosion);
            // make sure we've changed something before setting the blockstate
            if (!instance.equals(newState)) {
                serverWorld.setBlockState(pos, newState);
            }
        }

        // apply the preserved sign text if present
        if (signText != null && world.getBlockEntity(pos) instanceof SignBlockEntity signBlockEntity) {
            signBlockEntity.setText(signText.getLeft(), true);
            signBlockEntity.setText(signText.getRight(), false);
        }
    }
}
