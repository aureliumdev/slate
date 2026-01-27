package dev.aurelium.slate.action;

public class CommandAction extends Action {

    private final String command;
    private final Executor executor;

    public CommandAction(String command, Executor executor) {
        this.command = command;
        this.executor = executor;
    }

    public String getCommand() {
        return command;
    }

    public Executor getExecutor() {
        return executor;
    }

    public enum Executor {

        CONSOLE,
        PLAYER

    }

}
