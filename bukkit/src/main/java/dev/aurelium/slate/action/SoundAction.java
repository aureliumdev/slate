package dev.aurelium.slate.action;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.inv.content.InventoryContents;
import dev.aurelium.slate.menu.MenuInventory;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class SoundAction extends Action {

    private final String sound;
    private final SoundCategory category;
    private final float volume;
    private final float pitch;

    public SoundAction(Slate slate, String sound, SoundCategory category, float volume, float pitch) {
        super(slate);
        this.sound = sound;
        this.category = category;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void execute(Player player, MenuInventory menuInventory, InventoryContents contents) {
        player.playSound(player.getLocation(), sound, category, volume, pitch);
    }
}
