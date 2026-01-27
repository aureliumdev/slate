package dev.aurelium.slate.scheduler;

import java.lang.reflect.Method;

public class WrappedTask {
    private Object originalTask;

    public WrappedTask(Object originalTask) {
        this.originalTask = originalTask;
    }

    public void cancel() {
        // Call cancel method using reflection
        try {
            Method method = originalTask.getClass().getMethod("cancel");
            method.setAccessible(true);
            method.invoke(originalTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
