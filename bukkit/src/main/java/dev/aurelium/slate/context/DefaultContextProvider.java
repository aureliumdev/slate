package dev.aurelium.slate.context;

import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class DefaultContextProvider<C> implements ContextProvider<C> {

    private final Class<C> type;
    private final BiFunction<String, String, C> parser;

    public DefaultContextProvider(Class<C> type, BiFunction<String, String, C> parser) {
        this.type = type;
        this.parser = parser;
    }

    @Override
    public Class<C> getType() {
        return type;
    }

    @Override
    public @Nullable C parse(String menuName, String input) {
        return parser.apply(menuName, input);
    }
}
