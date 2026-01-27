package dev.aurelium.slate.action;

import java.util.Map;

public class MenuAction extends Action {

    private final ActionType actionType;
    private final String menuName;
    private final Map<String, Object> properties;

    public MenuAction(ActionType actionType, String menuName, Map<String, Object> properties) {
        this.actionType = actionType;
        this.menuName = menuName;
        this.properties = properties;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public String getMenuName() {
        return menuName;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public enum ActionType {

        OPEN,
        CLOSE,
        NEXT_PAGE,
        PREVIOUS_PAGE

    }

}
