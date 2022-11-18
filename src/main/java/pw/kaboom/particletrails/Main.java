package pw.kaboom.particletrails;

import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class Main extends JavaPlugin implements CommandExecutor, Listener {
    private FileConfiguration config;
    final private Set<String> availableParticles = new HashSet<>();
    final private Set<String> excludedParticles = new HashSet<>();
    final int numParticles = 12;
    final double offsetX = 0.5;
    final double offsetY = 1;
    final double offsetZ = 0.5;

    class Tick extends BukkitRunnable {
        @Override
        public void run() {
            for (Player player: Bukkit.getOnlinePlayers()) {
                final String playerUuid = player.getUniqueId().toString();

                if (!config.contains(playerUuid)) {
                    continue;
                }

                final String particleName = config.getString(playerUuid);

                if (!availableParticles.contains(particleName)) {
                    continue;
                }

                final Particle particle = Particle.valueOf(particleName.toUpperCase());
                final Location location = player.getLocation();
                location.add(location.getDirection().normalize().multiply(-1.5));
                final World world = location.getWorld();
                double data = 0;

                if (particle.equals(Particle.NOTE)) {
                    // Colored notes
                    data = ThreadLocalRandom.current().nextInt(0, 16);
                }

                world.spawnParticle(particle, location, numParticles,
                                    offsetX, offsetY, offsetZ, data);
            }
        }
    }

    @Override
    public void onLoad() {
        /* Fill lists */
        Collections.addAll(
            excludedParticles,
            "block_crack",
            "block_dust",
            "block_marker",
            "dust_color_transition",
            "explosion_huge",
            "falling_dust",
            "flash",
            "item_crack",
            "legacy_block_crack",
            "legacy_block_dust",
            "legacy_falling_dust",
            "mob_appearance",
            "redstone",
            "vibration"
        );

        for (Particle particle : Particle.values()) {
            final String particleName = particle.toString().toLowerCase();

            if (!excludedParticles.contains(particleName)) {
                availableParticles.add(particleName);
            }
        }
    }

    @Override
    public void onEnable() {
        config = getConfig();
        new Tick().runTaskTimer(this, 0, 2);
        this.getCommand("particletrails").setExecutor(this);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label,
                             final String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Component.text("Command has to be run by a player"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Component
                .text("Usage: /" + label + " <particle ..>", NamedTextColor.RED));
            return true;
        }

        final Player player = (Player) sender;

        if ("off".equalsIgnoreCase(args[0])) {
            config.set(player.getUniqueId().toString(), null);
            saveConfig();
            player.sendMessage(Component.text("You no longer have a particle trail"));
            return true;
        }

        final String particleName = args[0].toLowerCase();

        if (availableParticles.contains(particleName)) {
            final Particle particle = Particle.valueOf(particleName.toUpperCase());

            config.set(player.getUniqueId().toString(), particleName);
            saveConfig();
            player.sendMessage(Component.text("You now have a particle trail"));
            return true;
        }

        sender.sendMessage(
            Component.text("Invalid particle name ", NamedTextColor.RED)
                .append(Component.text(particleName))
         );
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command cmd,
                                      final String label, final String[] args){
        if (args.length == 1) {
            String partialName = args[0].toLowerCase();
            List<String> completions = new ArrayList<>();

            for (String particleName : availableParticles) {
                if (particleName.startsWith(partialName)) {
                    completions.add(particleName);
                }
            }
            return completions;
        }
        return null;
    }
}
