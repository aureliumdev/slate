package dev.aurelium.slate.fabric.scheduler;

import dev.aurelium.slate.scheduler.WrappedTask;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.jspecify.annotations.NonNull;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class FabricScheduler implements ServerTickEvents.EndTick {

    private static final long MSPT = 50;
    private final Queue<ScheduledSyncTask> syncTasks = new ConcurrentLinkedQueue<>();

    public FabricScheduler() {
        ServerTickEvents.END_SERVER_TICK.register(this);
    }

    public WrappedTask executeSync(Runnable runnable) {
        ScheduledSyncTask task = new ScheduledSyncTask(runnable, 0, 0);
        syncTasks.add(task);
        return new WrappedTask(task);
    }

    public WrappedTask scheduleSync(Runnable runnable, long delay, TimeUnit timeUnit) {
        ScheduledSyncTask task = new ScheduledSyncTask(runnable, timeUnit.toMillis(delay) / MSPT, 0);
        syncTasks.add(task);
        return new WrappedTask(task);
    }

    public WrappedTask timerSync(Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        ScheduledSyncTask task = new ScheduledSyncTask(runnable, timeUnit.toMillis(delay) / MSPT, timeUnit.toMillis(period) / MSPT);
        syncTasks.add(task);
        return new WrappedTask(task);
    }

    @Override
    public void onEndTick(@NonNull MinecraftServer server) {
        Iterator<ScheduledSyncTask> iterator = syncTasks.iterator();
        while (iterator.hasNext()) {
            ScheduledSyncTask task = iterator.next();
            if (task.getStatus() == TaskStatus.STOPPED) { // Remove canceled tasks
                iterator.remove();
                continue;
            }
            if (task.getTicksUntilExecute() <= 0) {
                task.getRunnable().run();
                if (task.getPeriodTicks() > 0) { // Repeating tasks
                    task.setTicksUntilExecute(task.getPeriodTicks());
                } else { // Remove task
                    task.setStatus(TaskStatus.STOPPED);
                    iterator.remove();
                }
            }
            task.decrementTicksUntilExecute();
        }
    }

    public static class ScheduledSyncTask {

        private final Runnable runnable;
        private final long periodTicks;
        private long ticksUntilExecute;
        private TaskStatus status;

        public ScheduledSyncTask(Runnable runnable, long delayTicks, long periodTicks) {
            this.runnable = runnable;
            this.periodTicks = periodTicks;
            this.ticksUntilExecute = delayTicks;
            this.status = TaskStatus.SCHEDULED;
        }

        public Runnable getRunnable() {
            return runnable;
        }

        public long getPeriodTicks() {
            return periodTicks;
        }

        public long getTicksUntilExecute() {
            return ticksUntilExecute;
        }

        public void setTicksUntilExecute(long ticksUntilExecute) {
            this.ticksUntilExecute = ticksUntilExecute;
        }

        public void decrementTicksUntilExecute() {
            ticksUntilExecute--;
        }

        public TaskStatus getStatus() {
            return status;
        }

        public void setStatus(TaskStatus status) {
            this.status = status;
        }

        public void cancel() {
            setStatus(TaskStatus.STOPPED);
        }
    }

    public enum TaskStatus {

        SCHEDULED,
        RUNNING,
        STOPPED

    }
}
