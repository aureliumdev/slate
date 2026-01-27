package dev.aurelium.slate.bukkit.ref;

import dev.aurelium.slate.ref.PlayerRef;
import org.bukkit.entity.Player;

public class BukkitPlayerRef implements PlayerRef {

    private final Player player;

    private BukkitPlayerRef(Player player) {
        this.player = player;
    }

    public static BukkitPlayerRef wrap(Player player) {
        return new BukkitPlayerRef(player);
    }

    public static Player unwrap(PlayerRef ref) {
        return ((BukkitPlayerRef) ref).get();
    }

    @Override
    public Player get() {
        return player;
    }

}
