package dev.aurelium.slate.action.trigger;

public enum MenuTrigger {

    OPEN("on_open"),
    CLOSE("on_close");

    private final String identifier;

    MenuTrigger(String id) {
        this.identifier = id;
    }

    public String getId() {
        return identifier;
    }

}
