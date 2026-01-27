package dev.aurelium.slate.fabric.ref;

import dev.aurelium.slate.ref.PlayerRef;
import net.minecraft.world.entity.player.Player;

public class FabricPlayerRef implements PlayerRef {

    private final Player player;

    private FabricPlayerRef(Player player) {
        this.player = player;
    }

    public static FabricPlayerRef wrap(Player player) {
        return new FabricPlayerRef(player);
    }

    public static Player unwrap(PlayerRef ref) {
        return ((FabricPlayerRef) ref).get();
    }

    @Override
    public Player get() {
        return player;
    }

}
