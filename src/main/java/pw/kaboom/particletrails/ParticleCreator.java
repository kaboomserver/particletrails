package pw.kaboom.particletrails;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

final class ParticleCreator {
    private static final int NUM_PARTICLES = 12;
    private static final double OFFSET_X = 0.5;
    private static final double OFFSET_Y = 1;
    private static final double OFFSET_Z = 0.5;
    private final Main plugin;

    ParticleCreator(final Main plugin) {
        this.plugin = plugin;
        schedule(plugin);
    }

    public void setParticle(final Player player, final Particle particle) {
        plugin.getConfig().set(player.getUniqueId().toString(), particle);
        plugin.saveConfig();
    }

    public void removeParticle(final Player player) {
        plugin.getConfig().set(player.getUniqueId().toString(), null);
        plugin.saveConfig();
    }

    private static void schedule(final Main plugin) {
        plugin.getServer().getScheduler().runTaskTimer(
                plugin,
                () -> {
                    for (final Player player : Bukkit.getOnlinePlayers()) {
                        if (player.isInvisible()) {
                            continue;
                        }

                        final GameMode gameMode = player.getGameMode();
                        if (gameMode.equals(GameMode.SPECTATOR)) {
                            continue;
                        }

                        final String playerUuid = player.getUniqueId().toString();
                        if (!plugin.getConfig().contains(playerUuid)) {
                            continue;
                        }

                        final String particleName = plugin.getConfig().getString(playerUuid);
                        if (!plugin.store().isAvailable(particleName)) {
                            continue;
                        }

                        final Particle particle = Particle.valueOf(particleName);
                        final Location location = player.getLocation();
                        location.add(location.getDirection().normalize().multiply(-1.5));
                        final World world = location.getWorld();
                        double data = 0;

                        if (particle.equals(Particle.NOTE)) {
                            // Colored notes
                            data = ThreadLocalRandom.current().nextInt(0, 16);
                        }

                        try {
                            world.spawnParticle(particle, location, NUM_PARTICLES,
                                    OFFSET_X, OFFSET_Y, OFFSET_Z, data);
                        } catch (final Exception e) {
                            plugin.getConfig().set(playerUuid, null);

                            player.sendMessage(
                                    Component.text(
                                            "Something went wrong while displaying particle trail" +
                                                    ". Please check console",
                                            NamedTextColor.RED
                                    )
                            );
                            plugin.getSLF4JLogger().error(
                                    "Failed to spawn particle of type {}", particleName, e
                            );

                            plugin.saveConfig();
                        }
                    }
                },
                0,
                2
        );
    }
}
