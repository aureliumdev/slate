package dev.aurelium.slate.item.provider;

import dev.aurelium.slate.lore.ListData;
import dev.aurelium.slate.util.TextUtil;

public class ListBuilder {

    private final ListData listData;
    private String list;
    private int index = 0;

    public ListBuilder(ListData listData) {
        this.listData = listData;
        this.list = "";
    }

    public ListBuilder append(String text, String... rep) {
        if (rep != null) {
            text = replace(text, rep);
        }
        if (list == null || list.isEmpty()) {
            list = text;
        } else if (index % (listData.getInterval() > 0 ? listData.getInterval() : 1) == 0) {
            list += listData.getInsertion() + text;
        } else {
            list += text;
        }
        index++;
        return this;
    }

    public String build() {
        return list;
    }

    private String replace(String source, String... rep) {
        if (source == null) {
            return null;
        }
        if (rep.length % 2 != 0) {
            throw new IllegalArgumentException("The number of arguments must be even!");
        }
        for (int i = 0; i < rep.length; i += 2) {
            source = TextUtil.replace(source, rep[i], rep[i + 1]);
        }
        return source;
    }

}
