package dev.aurelium.slate.fabric.menu;

import dev.aurelium.slate.component.MenuComponent;
import dev.aurelium.slate.context.ContextGroup;
import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.fabric.inv.MenuInventory;
import dev.aurelium.slate.item.MenuItem;
import dev.aurelium.slate.item.TemplateItem;
import dev.aurelium.slate.item.active.ActiveItem;
import dev.aurelium.slate.menu.ActiveMenu;
import dev.aurelium.slate.position.PositionProvider;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FabricActiveMenu implements ActiveMenu {

    private final MenuInventory menuInventory;

    public FabricActiveMenu(MenuInventory menuInventory) {
        this.menuInventory = menuInventory;
    }

    public static ActiveMenu empty(Slate slate, ServerPlayer player) {
        return new EmptyActiveMenu(slate, player);
    }

    @Override
    public String getName() {
        return menuInventory.getMenu().name();
    }

    @Override
    public void setHidden(String itemName, boolean hidden) {
        ActiveItem activeItem = menuInventory.getActiveItem(itemName);
        if (activeItem != null) {
            activeItem.setHidden(hidden);
        }
    }

    @Override
    public int getCurrentPage() {
        return menuInventory.getCurrentPage();
    }

    @Override
    public int getTotalPages() {
        return menuInventory.getTotalPages();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T property(String name) {
        try {
            return (T) menuInventory.getProperties().get(name);
        } catch (ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T property(String name, T def) {
        try {
            return (T) menuInventory.getProperties().getOrDefault(name, def);
        } catch (ClassCastException e) {
            e.printStackTrace();
            return def;
        }
    }

    @Override
    public Object getProperty(String name) {
        return menuInventory.getProperties().get(name);
    }

    @Override
    public Object getProperty(String name, Object def) {
        Object value = menuInventory.getProperties().get(name);
        if (value != null) {
            return value;
        } else {
            return def;
        }
    }

    @Override
    public Map<String, Object> getProperties() {
        return menuInventory.getProperties();
    }

    @Override
    public void setProperty(String name, Object value) {
        menuInventory.getProperties().put(name, value);
    }

    @Override
    public void defaultProperty(String name, Object value) {
        if (!menuInventory.getProperties().containsKey(name)) {
            menuInventory.getProperties().put(name, value);
        }
    }

    @Override
    public void reload() {
        menuInventory.init(menuInventory.getPlayer(), menuInventory.getContents());
    }

    @Override
    public void setCooldown(String itemName, int cooldown) {
        ActiveItem activeItem = menuInventory.getActiveItem(itemName);
        if (activeItem != null) {
            activeItem.setCooldown(cooldown);
            menuInventory.setToUpdate(activeItem);
        }
    }

    @Nullable
    @Override
    public Object getOption(String key) {
        return menuInventory.getMenu().options().get(key);
    }

    @Nullable
    @Override
    public <T> T getOption(Class<T> clazz, String key) {
        try {
            return clazz.cast(menuInventory.getMenu().options().get(key));
        } catch (ClassCastException e) {
            return null;
        }
    }

    @Override
    public Object getOption(String key, Object def) {
        Object obj = menuInventory.getMenu().options().get(key);
        if (obj != null) {
            return obj;
        } else {
            return def;
        }
    }

    @Override
    public <T> T getOption(Class<T> clazz, String key, T def) {
        try {
            T result = clazz.cast(menuInventory.getMenu().options().get(key));
            if (result != null) {
                return result;
            } else {
                return def;
            }
        } catch (ClassCastException e) {
            return def;
        }
    }

    @Nullable
    @Override
    public Object getItemOption(String itemName, String key) {
        MenuItem menuItem = menuInventory.getMenu().items().get(itemName);
        if (menuItem != null) {
            return menuItem.getOptions().get(key);
        }
        return null;
    }

    @Override
    public Object getItemOption(String itemName, String key, Object def) {
        MenuItem menuItem = menuInventory.getMenu().items().get(itemName);
        if (menuItem != null) {
            Object obj = menuItem.getOptions().get(key);
            if (obj != null) {
                return obj;
            }
        }
        return def;
    }

    @Override
    public Map<String, MenuComponent> getComponents() {
        return menuInventory.getMenu().components();
    }

    @Override
    public Map<String, String> getFormats() {
        return menuInventory.getMenu().formats();
    }

    @NotNull
    @Override
    public String getFormat(String key) {
        return menuInventory.getMenu().formats().getOrDefault(key, key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void setPositionProvider(String templateName, T context, PositionProvider provider) {
        MenuItem menuItem = menuInventory.getMenu().items().get(templateName);
        if (menuItem instanceof TemplateItem) {
            TemplateItem<T> templateItem = (TemplateItem<T>) menuItem;
            templateItem.getPositionsMap().put(context, provider);
        }
    }

    @Override
    public Map<String, ContextGroup> getContextGroups(String templateName) {
        MenuItem menuItem = menuInventory.getMenu().items().get(templateName);
        if (menuItem instanceof TemplateItem<?> templateItem) {
            return templateItem.getContextGroups();
        }
        return new HashMap<>();
    }

}
