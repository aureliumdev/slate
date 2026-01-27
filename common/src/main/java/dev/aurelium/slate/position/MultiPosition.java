package dev.aurelium.slate.position;

import dev.aurelium.slate.inv.content.SlotPos;

import java.util.Collection;
import java.util.List;

public record MultiPosition(List<SlotPos> positions) implements PositionProvider {

    @Override
    public List<SlotPos> getPosition(Collection<PositionProvider> positionData) {
        return positions;
    }
}
