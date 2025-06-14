package dev.aurelium.slate.item.provider;

import dev.aurelium.slate.lore.ListData;

public record PlaceholderData(PlaceholderType type, String style, ListData listData) {

    public boolean isList() {
        return listData != null && listData.getInterval() > 0;
    }

    @Override
    public ListData listData() {
        if (isList()) {
            return listData;
        } else {
            return new ListData("", 0);
        }
    }
}
