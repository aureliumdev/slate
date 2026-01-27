package dev.aurelium.slate.position;

import dev.aurelium.slate.inv.content.SlotPos;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public record FixedPosition(@NotNull SlotPos pos) implements PositionProvider {

    @Override
    public List<SlotPos> getPosition(Collection<PositionProvider> positionData) {
        return List.of(pos);
    }
}
