package dev.aurelium.slate.action;

import net.kyori.adventure.sound.Sound.Source;

public class SoundAction extends Action {

    private final String sound;
    private final Source category;
    private final float volume;
    private final float pitch;

    public SoundAction(String sound, Source category, float volume, float pitch) {
        this.sound = sound;
        this.category = category;
        this.volume = volume;
        this.pitch = pitch;
    }

    public String getSound() {
        return sound;
    }

    public Source getCategory() {
        return category;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }
}
