package dev.aurelium.slate.builder;

import dev.aurelium.slate.function.ItemReplacer;
import dev.aurelium.slate.function.LocaleProvider;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Builder for creating {@link GlobalBehavior} instances.
 */
public class GlobalBehaviorBuilder {

    private final Set<ItemReplacer> globalReplacers = new HashSet<>();
    private LocaleProvider localeProvider = p -> Locale.ENGLISH;

    private GlobalBehaviorBuilder() {

    }

    /**
     * Creates a new {@link GlobalBehaviorBuilder}.
     *
     * @return the builder
     */
    public static GlobalBehaviorBuilder builder() {
        return new GlobalBehaviorBuilder();
    }

    /**
     * Adds a global replacer, which will be applied to all placeholders in every menu.
     *
     * @param replacer the replacer
     * @return the builder
     */
    public GlobalBehaviorBuilder replacer(ItemReplacer replacer) {
        globalReplacers.add(replacer);
        return this;
    }

    public GlobalBehaviorBuilder localeProvider(LocaleProvider localeProvider) {
        this.localeProvider = localeProvider;
        return this;
    }

    public GlobalBehavior build() {
        return new GlobalBehavior(globalReplacers, localeProvider);
    }

}
