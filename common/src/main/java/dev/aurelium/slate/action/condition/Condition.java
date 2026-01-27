package dev.aurelium.slate.action.condition;

import dev.aurelium.slate.SlateLibrary;

public abstract class Condition {

    protected final SlateLibrary slate;

    public Condition(SlateLibrary slate) {
        this.slate = slate;
    }
}
