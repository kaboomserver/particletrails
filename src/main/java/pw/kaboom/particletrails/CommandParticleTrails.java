package pw.kaboom.particletrails;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.Commands.restricted;

final class CommandParticleTrails {
    private final Main plugin;

    CommandParticleTrails(final Main plugin) {
        this.plugin = plugin;
    }

    public String getLabel() {
        return "particletrails";
    }

    public String getDescription() {
        return "Creates a particle trail behind you";
    }

    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
       builder
                    .requires(restricted(src ->
                            src.getSender().hasPermission("particletrails.command")
                    ))
                    .then(literal("off")
                            .executes(ctx -> {
                                final Player sender = ctx.getSource().getPlayerOrThrow();
                                plugin.creator().removeParticle(sender);
                                sender.sendMessage(Component.text(
                                        "You no longer have a particle trail"
                                ));
                                return Command.SINGLE_SUCCESS;
                            })
                    )

        for (final Particle particle : plugin.store().availableParticles()) {
            builder
                    .then(literal(particle.toString().toLowerCase())
                            .executes(ctx -> {
                                final Player sender = ctx.getSource().getPlayerOrThrow();
                                plugin.creator().setParticle(sender, particle);
                                sender.sendMessage(Component.text("You now have a particle trail"));
                                return Command.SINGLE_SUCCESS;
                            })
                    );
        }
    }
}
