package dev.aurelium.slate.action;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.builder.BuiltMenu;
import dev.aurelium.slate.info.MenuInfo;
import dev.aurelium.slate.inv.content.InventoryContents;
import dev.aurelium.slate.menu.MenuInventory;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MenuAction extends Action {

    private final ActionType actionType;
    private final String menuName;
    private final Map<String, Object> properties;

    public MenuAction(Slate slate, ActionType actionType, String menuName, Map<String, Object> properties) {
        super(slate);
        this.actionType = actionType;
        this.menuName = menuName;
        this.properties = properties;
    }

    @Override
    public void execute(Player player, MenuInventory menuInventory, InventoryContents contents) {
        switch (actionType) {
            case OPEN:
                slate.openMenu(player, menuName, getProperties(menuInventory));
                break;
            case CLOSE:
                player.closeInventory();
                break;
            case NEXT_PAGE:
                int nextPage = menuInventory.getCurrentPage() + 1;
                if (nextPage < menuInventory.getTotalPages()) {
                    slate.openMenu(player, menuInventory.getMenu().name(), getProperties(menuInventory), nextPage);
                }
                break;
            case PREVIOUS_PAGE:
                int previousPage = menuInventory.getCurrentPage() - 1;
                if (previousPage >= 0) {
                    slate.openMenu(player, menuInventory.getMenu().name(), getProperties(menuInventory), previousPage);
                }
                break;
        }

    }

    private Map<String, Object> getProperties(MenuInventory inventory) {
        // Add BuiltMenu properties from PropertyProvider
        BuiltMenu builtMenu = slate.getBuiltMenu(menuName);
        MenuInfo info = new MenuInfo(slate, inventory.getPlayer(), inventory.getActiveMenu());
        Map<String, Object> base = new HashMap<>(builtMenu.propertyProvider().get(info));
        // Otherwise fallback to current menu properties
        if (base.isEmpty()) {
            base.putAll(inventory.getProperties());
        }
        // Override with action-defined properties
        base.putAll(properties);
        return base;
    }

    public enum ActionType {

        OPEN,
        CLOSE,
        NEXT_PAGE,
        PREVIOUS_PAGE

    }

}
