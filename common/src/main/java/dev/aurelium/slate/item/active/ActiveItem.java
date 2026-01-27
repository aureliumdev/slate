package dev.aurelium.slate.item.active;

public abstract class ActiveItem {

    private boolean hidden;
    private int cooldown;

    public ActiveItem() {
        this.hidden = false;
        this.cooldown = 0;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getCooldown() {
        return cooldown;
    }

}
