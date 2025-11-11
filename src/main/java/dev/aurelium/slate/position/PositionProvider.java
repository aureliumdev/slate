package dev.aurelium.slate.position;

import dev.aurelium.slate.inv.content.SlotPos;

import java.util.Collection;
import java.util.List;

public interface PositionProvider {

    List<SlotPos> getPosition(Collection<PositionProvider> positionData);

}
