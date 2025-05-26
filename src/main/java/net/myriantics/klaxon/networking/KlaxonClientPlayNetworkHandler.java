package net.myriantics.klaxon.networking;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.registry.custom.KlaxonWorldEvents;

public abstract class KlaxonClientPlayNetworkHandler {
    public static void processKlaxonWorldEvent(int eventId, BlockPos pos, int data) {
        ClientWorld clientWorld = MinecraftClient.getInstance().world;
        if (clientWorld == null) return;

        WorldRenderer renderer = MinecraftClient.getInstance().worldRenderer;

        Random random = clientWorld.getRandom();

        switch (eventId) {
            case KlaxonWorldEvents.DRAGONS_BREATH_EXPLOSIVE_CATALYST_CLOUD_SPAWNS -> {
                BlockEntity entity = clientWorld.getBlockEntity(pos);
                if (entity instanceof DeepslateBlastProcessorBlockEntity deepslateBlastProcessorBlockEntity) {
                    Position outputPos = deepslateBlastProcessorBlockEntity.getExplosionOutputLocation(clientWorld.getBlockState(pos).get(DeepslateBlastProcessorBlock.HORIZONTAL_FACING));

                    for (int m = 0; m < 200; m++) {
                        float ab = random.nextFloat() * 4.0F;
                        float ag = random.nextFloat() * (float) (Math.PI * 2);
                        double n = MathHelper.cos(ag) * ab;
                        double o = 0.01 + random.nextDouble() * 0.5;
                        double p = MathHelper.sin(ag) * ab;
                        renderer.addParticle(ParticleTypes.DRAGON_BREATH, false, outputPos.getX() + n * 0.1, outputPos.getY() + 0.3, outputPos.getZ() + p * 0.1, n, o, p);
                    }

                    if (data == 1) {
                        clientWorld.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
                    }
                }
            }
        }
    }
}
