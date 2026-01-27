package dev.aurelium.slate.context;

import com.google.common.primitives.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ContextManager {

    private final Map<Class<?>, ContextProvider<?>> contexts;
    private final Map<String, ContextProvider<?>> keyedContexts;

    public ContextManager() {
        this.contexts = new HashMap<>();
        this.keyedContexts = new HashMap<>();
        registerDefaults();
    }

    /**
     * Registers a context provider to the given context type
     *
     * @param provider The context provider
     * @param <C> The context type
     */
    public <C> void registerContext(ContextProvider<C> provider) {
        contexts.put(provider.getType(), provider);
    }

    public <C> void registerContext(String key, ContextProvider<C> provider) {
        registerContext(provider);
        keyedContexts.put(key, provider);
    }

    @Nullable
    public ContextProvider<?> getContextProvider(Class<?> contextClass) {
        return contexts.get(contextClass);
    }

    @Nullable
    public ContextProvider<?> getContextProvider(String key) {
        return keyedContexts.get(key);
    }

    @Nullable
    public Class<?> getContextClass(String key) {
        ContextProvider<?> provider = getContextProvider(key);
        if (provider != null) {
            for (Map.Entry<Class<?>, ContextProvider<?>> entry : contexts.entrySet()) {
                if (provider.equals(entry.getValue())) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    private void registerDefaults() {
        registerContext(new DefaultContextProvider<>(Integer.class, (menuName, input) -> Ints.tryParse(input)));
        registerContext(new DefaultContextProvider<>(Long.class, (menuName, input) -> Longs.tryParse(input)));
        registerContext(new DefaultContextProvider<>(Float.class, (menuName, input) -> Floats.tryParse(input)));
        registerContext(new DefaultContextProvider<>(Double.class, (menuName, input) -> Doubles.tryParse(input)));
        registerContext(new DefaultContextProvider<>(Short.class, (menuName, input) -> {
            Integer integer = Ints.tryParse(input);
            if (integer != null) {
                return (short) integer.intValue();
            }
            return null;
        }));
        registerContext(new DefaultContextProvider<>(Byte.class, (menuName, input) -> {
            Integer integer = Ints.tryParse(input);
            if (integer != null) {
                return (byte) integer.intValue();
            }
            return null;
        }));
        registerContext(new DefaultContextProvider<>(Boolean.class, (menuName, input) -> input.equalsIgnoreCase("true")));
        registerContext(new DefaultContextProvider<>(Character.class, (menuName, input) -> {
            if (input.length() == 1) {
                return input.toCharArray()[0];
            }
            return null;
        }));
        registerContext(new DefaultContextProvider<>(String.class, (menuName, input) -> input));
    }

}
