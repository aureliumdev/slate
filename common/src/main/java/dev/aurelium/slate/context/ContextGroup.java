package dev.aurelium.slate.context;

import dev.aurelium.slate.inv.content.SlotPos;

public class ContextGroup {

    private final SlotPos start;
    private final SlotPos end;
    private final GroupAlign align;

    public ContextGroup(SlotPos start, SlotPos end, GroupAlign align) {
        this.start = start;
        this.end = end;
        this.align = align;
    }

    public SlotPos getStart() {
        return start;
    }

    public SlotPos getEnd() {
        return end;
    }

    public GroupAlign getAlign() {
        return align;
    }
}
