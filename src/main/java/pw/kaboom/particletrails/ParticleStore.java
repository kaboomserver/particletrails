package pw.kaboom.particletrails;

import org.bukkit.Particle;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

final class ParticleStore {
    private final Set<Particle> availableParticles;

    ParticleStore() {
        final Set<Particle> excludedParticles = EnumSet.of(
                Particle.BLOCK_MARKER,
                Particle.DUST_COLOR_TRANSITION,
                Particle.FALLING_DUST,
                Particle.FLASH,
                Particle.VIBRATION
        );
        final Set<Particle> availableParticles = EnumSet.allOf(Particle.class);
        for (final Particle particle : Particle.values()) {
            if (excludedParticles.contains(particle)) {
                availableParticles.remove(particle);
            }
        }
        this.availableParticles = Set.copyOf(availableParticles);
    }

    public Set<Particle> availableParticles() {
        return availableParticles;
    }

    public boolean isAvailable(final String particleName) throws IllegalArgumentException {
        return isAvailable(Particle.valueOf(particleName));
    }

    public boolean isAvailable(final Particle particle) {
        return availableParticles.contains(particle);
    }
}
