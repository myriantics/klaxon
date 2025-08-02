package net.myriantics.klaxon.client.particle;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.fabricmc.fabric.impl.client.particle.FabricSpriteProviderImpl;
import net.minecraft.client.particle.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class HallnoxDripParticle extends SpriteBillboardParticle {
    protected HallnoxDripParticle(ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(clientWorld, x, y, z, velocityX, velocityY, velocityZ);
        this.setColor(255f/255, 234f/255, 185f/255);
        this.gravityStrength = 0.01f;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    protected int getBrightness(float tint) {
        return 240;
    }

    @Override
    public void tick() {

        if (this.onGround || this.age++ > this.maxAge) {
            this.markDead();
        }

        if (!this.dead) {
            this.velocityY = this.velocityY - this.gravityStrength;

            this.velocityX *= 0.02;
            this.velocityY *= 0.02;
            this.velocityZ *= 0.02;

            this.move(this.velocityX, this.velocityY, this.velocityZ);
        }
    }

    public static class HallnoxDripParticleFactory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public HallnoxDripParticleFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            HallnoxDripParticle particle = new HallnoxDripParticle(world, x, y, z, velocityX, velocityY, velocityZ);

            particle.setSprite(spriteProvider);

            return particle;
        }
    }
}
