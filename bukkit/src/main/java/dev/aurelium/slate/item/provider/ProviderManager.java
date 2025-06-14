package dev.aurelium.slate.item.provider;

import dev.aurelium.slate.context.ContextProvider;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ProviderManager {

    private final Map<String, ContextProvider<?>> templateContextProviders;
    private KeyedItemProvider keyedItemProvider;

    public ProviderManager() {
        this.templateContextProviders = new HashMap<>();
    }

    public void unregisterAll() {
        templateContextProviders.clear();
    }

    @Nullable
    public ContextProvider<?> getContextProvider(String itemName) {
        return templateContextProviders.get(itemName);
    }

    @Nullable
    public KeyedItemProvider getKeyedItemProvider() {
        return keyedItemProvider;
    }

    public void registerKeyedItemProvider(KeyedItemProvider provider) {
        keyedItemProvider = provider;
    }

}
