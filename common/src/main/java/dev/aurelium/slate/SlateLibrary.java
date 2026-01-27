package dev.aurelium.slate;

import dev.aurelium.slate.action.ActionManager;
import dev.aurelium.slate.context.ContextManager;
import dev.aurelium.slate.hooks.PlaceholderHook;
import dev.aurelium.slate.menu.LoadedMenu;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;
import java.util.logging.Logger;

public abstract class SlateLibrary {

    private final ContextManager contextManager = new ContextManager();
    private final ActionManager actionManager = new ActionManager(this);

    public abstract int loadMenus();

    public abstract void generateFiles();

    public abstract void unregisterMenus();

    public abstract int getLoreWrappingWidth();

    public abstract void addMergeDirectory(File mergeDir);

    public abstract void removeMergeDirectory(File mergeDir);

    public abstract Logger getLogger();

    public abstract boolean isPlaceholderAPIEnabled();

    public abstract File getDataFolder();

    public abstract void addLoadedMenu(LoadedMenu menu);

    public abstract PlaceholderHook getPlaceholderHook();

    @Nullable
    public abstract LoadedMenu getLoadedMenu(String name);

    public abstract Map<String, LoadedMenu> getLoadedMenus();

    public ContextManager getContextManager() {
        return contextManager;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

}
