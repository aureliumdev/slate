package dev.aurelium.slate.lore;

public class ListData {

    private final String insertion;
    private final int interval;

    public ListData(String insertion, int interval) {
        this.insertion = insertion;
        this.interval = interval;
    }

    public String getInsertion() {
        return insertion;
    }

    public int getInterval() {
        return interval;
    }

}
