package dev.aurelium.slate.fabric;

import dev.aurelium.slate.SlateLibrary;
import dev.aurelium.slate.hooks.PlaceholderHook;
import dev.aurelium.slate.menu.LoadedMenu;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;
import java.util.logging.Logger;

public class Slate extends SlateLibrary {

    @Override
    public int loadMenus() {
        return 0;
    }

    @Override
    public void generateFiles() {

    }

    @Override
    public void unregisterMenus() {

    }

    @Override
    public int getLoreWrappingWidth() {
        return 0;
    }

    @Override
    public void addMergeDirectory(File mergeDir) {

    }

    @Override
    public void removeMergeDirectory(File mergeDir) {

    }

    @Override
    public Logger getLogger() {
        return null;
    }

    @Override
    public boolean isPlaceholderAPIEnabled() {
        return false;
    }

    @Override
    public File getDataFolder() {
        return null;
    }

    @Override
    public void addLoadedMenu(LoadedMenu menu) {

    }

    @Override
    public PlaceholderHook getPlaceholderHook() {
        return null;
    }

    @Nullable
    @Override
    public LoadedMenu getLoadedMenu(String name) {
        return null;
    }

    @Override
    public Map<String, LoadedMenu> getLoadedMenus() {
        return Map.of();
    }
}
