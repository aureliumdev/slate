package dev.aurelium.slate.scheduler;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class BukkitScheduler extends Scheduler {
    public BukkitScheduler(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public WrappedTask run(Player player, Runnable runnable) {
        BukkitTask task = plugin.getServer().getScheduler().runTask(plugin, runnable);
        return new WrappedTask(task);
    }

    @Override
    public WrappedTask runGlobal(Runnable runnable) {
        BukkitTask task = plugin.getServer().getScheduler().runTask(plugin, runnable);
        return new WrappedTask(task);
    }

    @Override
    public WrappedTask runLater(Player player, Runnable runnable, long delay) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
        return new WrappedTask(task);
    }

    @Override
    public WrappedTask runTimer(Player player, Runnable runnable, long delay, long period) {
        BukkitTask task =  plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, period);
        return new WrappedTask(task);
    }
}
