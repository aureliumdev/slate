package dev.aurelium.slate.item.option;

public class Option<T> {

    private final String key; // The key used in the config
    private T defaultValue; // The default value of the option

    public Option(String key) {
        this.key = key;
    }

    public Option(String key, T defaultValue) {
        this(key);
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public boolean hasDefaultValue() {
        return defaultValue != null;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

}
