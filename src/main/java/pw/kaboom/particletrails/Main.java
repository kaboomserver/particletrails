package pw.kaboom.particletrails;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.List;

public final class Main extends JavaPlugin {
    private @MonotonicNonNull ParticleStore store;
    private @MonotonicNonNull ParticleCreator creator;

    ParticleStore store() {
        return store;
    }

    ParticleCreator creator() {
        return creator;
    }

    @Override
    public void onLoad() {
        this.store = new ParticleStore();
    }

    @Override
    public void onEnable() {
        this.creator = new ParticleCreator(this);

        final CommandParticleTrails command = new CommandParticleTrails(this);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final LiteralArgumentBuilder<CommandSourceStack> builder
                    = Commands.literal(command.getLabel());
            command.build(builder);
            event.registrar().register(
                    builder.build(), command.getDescription(), List.of()
            );
        });
    }
}