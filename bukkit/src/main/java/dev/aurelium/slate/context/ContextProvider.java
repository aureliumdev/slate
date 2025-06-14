package dev.aurelium.slate.context;

import org.jetbrains.annotations.Nullable;

public interface ContextProvider<C> {

    Class<C> getType();

    /**
     * Parses a context from an input
     *
     * @param input The string input to parse, could be something not intended to be parsed as a context
     * @return The parsed context, or null if the input does not belong to the context type
     */
    @Nullable
    C parse(String menuName, String input);

}
