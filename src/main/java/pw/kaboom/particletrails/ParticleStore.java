package pw.kaboom.particletrails;

import org.bukkit.Particle;

import java.util.HashSet;
import java.util.Set;

final class ParticleStore {
    public static final Set<Particle> AVAILABLE_PARTICLES;

    static {
        final Set<Particle> excludedParticles = Set.of(
                Particle.BLOCK_MARKER,
                Particle.DUST_COLOR_TRANSITION,
                Particle.FALLING_DUST,
                Particle.FLASH,
                Particle.VIBRATION
        );
        final Set<Particle> availableParticles = new HashSet<>();
        for (final Particle particle : Particle.values()) {
            if (!excludedParticles.contains(particle)) {
                availableParticles.add(particle);
            }
        }
        AVAILABLE_PARTICLES = Set.copyOf(availableParticles);
    }

    @Deprecated
    private final Set<Particle> availableParticles;

    @Deprecated
    ParticleStore() {
        final Set<Particle> excludedParticles = Set.of(
                Particle.BLOCK_MARKER,
                Particle.DUST_COLOR_TRANSITION,
                Particle.FALLING_DUST,
                Particle.FLASH,
                Particle.VIBRATION
        );
        final Set<Particle> availableParticles = new HashSet<>();
        for (final Particle particle : Particle.values()) {
            if (!excludedParticles.contains(particle)) {
                availableParticles.add(particle);
            }
        }
        this.availableParticles = Set.copyOf(availableParticles);
    }

    @Deprecated
    public Set<Particle> availableParticles() {
        return availableParticles;
    }

    public static boolean isAvailable(final String particleName) throws IllegalArgumentException {
        return isAvailable(Particle.valueOf(particleName));
    }

    public static boolean isAvailable(final Particle particle) {
        return AVAILABLE_PARTICLES.contains(particle);
    }
}
