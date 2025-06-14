package dev.aurelium.slate.builder;

import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.function.*;
import dev.aurelium.slate.info.TemplateInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TemplateBuilder<T> {

    private final Class<T> contextType;
    private final Map<String, TemplateReplacer<T>> replacers = new HashMap<>();
    private TemplateReplacer<T> anyReplacer = p -> null;
    private final Map<ClickTrigger, TemplateClicker<T>> clickers = new HashMap<>();
    private TemplateModifier<T> modifier = TemplateInfo::item;
    private DefinedContexts<T> definedContexts = m -> new HashSet<>();
    private TemplateSlot<T> slotProvider = t -> null;
    private MenuListener initListener = m -> {};
    private ContextListener<T> contextListener = t -> {};

    public TemplateBuilder(Class<T> contextType) {
        this.contextType = contextType;
    }

    public static <T> TemplateBuilder<T> builder(Class<T> type) {
        return new TemplateBuilder<>(type);
    }

    public TemplateBuilder<T> replace(String from, TemplateReplacer<T> replacer) {
        replacers.put(from, replacer);
        return this;
    }

    public TemplateBuilder<T> replaceAny(TemplateReplacer<T> replacer) {
        anyReplacer = replacer;
        return this;
    }

    public TemplateBuilder<T> onClick(TemplateClicker<T> clicker) {
        clickers.put(ClickTrigger.ANY, clicker);
        return this;
    }

    public TemplateBuilder<T> onClick(ClickTrigger action, TemplateClicker<T> clicker) {
        clickers.put(action, clicker);
        return this;
    }

    public TemplateBuilder<T> modify(TemplateModifier<T> modifier) {
        this.modifier = modifier;
        return this;
    }

    public TemplateBuilder<T> definedContexts(DefinedContexts<T> context) {
        this.definedContexts = context;
        return this;
    }

    public TemplateBuilder<T> slotPos(TemplateSlot<T> slot) {
        this.slotProvider = slot;
        return this;
    }

    public TemplateBuilder<T> initialize(MenuListener listener) {
        this.initListener = listener;
        return this;
    }

    public TemplateBuilder<T> initializeContext(ContextListener<T> listener) {
        this.contextListener = listener;
        return this;
    }

    public BuiltTemplate<T> build() {
        return new BuiltTemplate<>(contextType, replacers, anyReplacer, clickers, modifier, definedContexts,
                slotProvider, initListener, contextListener);
    }
    
}
