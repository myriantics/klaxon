package net.myriantics.klaxon.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class NetherReactionExplosionLargeParticle extends HugeExplosionParticle {
    protected NetherReactionExplosionLargeParticle(ClientLevel world, double x, double y, double z, double d, SpriteSet spriteProvider) {
        super(world, x, y, z, d, spriteProvider);
        this.setSprite(spriteProvider.get(world.getRandom()));
        this.scale((this.quadSize * 0.3f) + (this.quadSize * 0.4f * world.getRandom().nextFloat()));
    }

    @Override
    public void setSpriteFromAge(SpriteSet spriteProvider) {
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Factory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            return new NetherReactionExplosionLargeParticle(clientWorld, d, e, f, g, this.spriteProvider);
        }
    }
}
