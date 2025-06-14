package dev.aurelium.slate.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperScheduler extends Scheduler {
    public PaperScheduler(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public WrappedTask run(Player player, Runnable runnable) {
        ScheduledTask task = player.getScheduler().run(plugin, (t) -> runnable.run(), null);
        return new WrappedTask(task);
    }

    @Override
    public WrappedTask runGlobal(Runnable runnable) {
        ScheduledTask task = Bukkit.getGlobalRegionScheduler().run(plugin, (t) -> runnable.run());
        return new WrappedTask(task);
    }

    @Override
    public WrappedTask runLater(Player player, Runnable runnable, long delay) {
        ScheduledTask task = player.getScheduler().runDelayed(plugin, (t) -> runnable.run(), null, delay);
        return new WrappedTask(task);
    }

    @Override
    public WrappedTask runTimer(Player player, Runnable runnable, long delay, long period) {
        ScheduledTask task = player.getScheduler().runAtFixedRate(plugin, (t) -> runnable.run(), null, delay, period);
        return new WrappedTask(task);
    }
}
