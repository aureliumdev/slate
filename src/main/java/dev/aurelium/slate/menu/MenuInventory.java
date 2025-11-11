package dev.aurelium.slate.menu;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.ItemActions;
import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.action.trigger.MenuTrigger;
import dev.aurelium.slate.builder.BuiltItem;
import dev.aurelium.slate.builder.BuiltMenu;
import dev.aurelium.slate.builder.BuiltTemplate;
import dev.aurelium.slate.fill.FillData;
import dev.aurelium.slate.info.ItemInfo;
import dev.aurelium.slate.info.MenuInfo;
import dev.aurelium.slate.info.TemplateInfo;
import dev.aurelium.slate.inv.ClickableItem;
import dev.aurelium.slate.inv.ItemClickData;
import dev.aurelium.slate.inv.content.InventoryContents;
import dev.aurelium.slate.inv.content.InventoryProvider;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.item.*;
import dev.aurelium.slate.item.active.ActiveItem;
import dev.aurelium.slate.item.active.ActiveSingleItem;
import dev.aurelium.slate.item.active.ActiveTemplateItem;
import dev.aurelium.slate.item.provider.PlaceholderType;
import dev.aurelium.slate.lore.LoreInterpreter;
import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.position.PositionProvider;
import dev.aurelium.slate.text.TextFormatter;
import dev.aurelium.slate.util.PaperUtil;
import dev.aurelium.slate.util.TextUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;

public class MenuInventory implements InventoryProvider {

    private final Slate slate;
    private final LoreInterpreter loreInterpreter;
    private final LoadedMenu menu;
    private final ActiveMenu activeMenu;
    private final Map<String, ActiveItem> activeItems;
    private final Map<String, Object> properties;
    private final int totalPages;
    private final int currentPage;
    private final Player player;
    private InventoryContents contents;
    private final TextFormatter tf = new TextFormatter();
    private final BuiltMenu builtMenu;
    private final ArrayList<ActiveItem> toUpdate;
    private final MenuInfo menuInfo;

    public MenuInventory(Slate slate, LoadedMenu menu, Player player, Map<String, Object> properties, int currentPage) {
        this.slate = slate;
        this.loreInterpreter = new LoreInterpreter(slate);
        this.menu = menu;
        this.activeItems = new LinkedHashMap<>();
        this.activeMenu = new ActiveMenu(this);
        this.toUpdate = new ArrayList<>();
        this.properties = new HashMap<>(properties); // Make a copy in case the properties map passed in is immutable
        this.player = player;
        this.builtMenu = slate.getBuiltMenu(menu.name());
        this.totalPages = builtMenu.pageProvider().getPages(new MenuInfo(slate, player, activeMenu));
        this.currentPage = currentPage;
        this.menuInfo = new MenuInfo(slate, player, activeMenu);
    }

    public Slate getSlate() {
        return slate;
    }

    public LoadedMenu getMenu() {
        return menu;
    }

    public BuiltMenu getBuiltMenu() {
        return builtMenu;
    }

    public ActiveMenu getActiveMenu() {
        return activeMenu;
    }

    @Nullable
    public ActiveItem getActiveItem(String itemName) {
        return activeItems.get(itemName);
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public LoreInterpreter getLoreInterpreter() {
        return loreInterpreter;
    }

    public TextFormatter getTextFormatter() {
        return tf;
    }

    public void setToUpdate(ActiveItem activeItem) {
        if (!toUpdate.contains(activeItem)) {
            toUpdate.add(activeItem);
        }
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        this.contents = contents;
        // Add active items
        for (MenuItem menuItem : menu.items().values()) {
            ActiveItem activeItem = activeItems.get(menuItem.getName());
            if (activeItem != null && activeItem.isHidden()) {
                continue;
            }
            if (menuItem instanceof SingleItem) {
                activeItem = new ActiveSingleItem((SingleItem) menuItem);
            } else if (menuItem instanceof TemplateItem<?> templateItem) {
                activeItem = new ActiveTemplateItem<>(templateItem);
            } else {
                continue;
            }
            activeItems.put(menuItem.getName(), activeItem);
        }
        // Handle onOpen
        builtMenu.openListener().handle(new MenuInfo(slate, player, activeMenu));
        // Execute open actions
        menu.executeActions(MenuTrigger.OPEN, player, this);
        // Place fill items
        FillData fillData = menu.fillData();
        if (fillData.enabled()) {
            fillData.placeInMenu(slate, player, this);
        }
        // Place items
        for (ActiveItem activeItem : activeItems.values()) {
            if (activeItem instanceof ActiveSingleItem) { // Create single item
                addSingleItem((ActiveSingleItem) activeItem, contents, player);
            } else if (activeItem instanceof ActiveTemplateItem) { // Create template item
                addTemplateItem((ActiveTemplateItem<?>) activeItem, contents, player);
            }
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        // Decrement item cooldowns
        for (int i = 0; i < toUpdate.size(); i++) {
            ActiveItem activeItem = toUpdate.get(i);
            int cooldown = activeItem.getCooldown();
            if (cooldown > 0) {
                activeItem.setCooldown(cooldown- 1);
            }
        }
        builtMenu.updateListener().handle(menuInfo);
    }

    // Added to SmartInventory listeners by builder
    public void close(InventoryCloseEvent event) {
        menu.executeActions(MenuTrigger.CLOSE, player, this);
    }

    private void addSingleItem(ActiveSingleItem activeItem, InventoryContents contents, Player player) {
        SingleItem item = activeItem.getItem();

        if (item.failsViewConditions(player, this)) {
            return; // Don't show item
        }

        BuiltItem builtItem = slate.getBuiltMenu(menu.name()).getBackingItem(item.getName());

        ItemStack itemStack = item.getBaseItem().clone();
        builtItem.initListener().handle(new MenuInfo(slate, player, activeMenu));

        replaceItemPlaceholders(itemStack);
        // Apply ItemModifier of built item
        itemStack = builtItem.modifier().modify(new ItemInfo(slate, player, activeMenu, itemStack));
        if (itemStack == null) return;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            String displayName = item.getDisplayName();
            if (displayName != null) {
                // BuiltItem replacers
                displayName = builtItem.applyReplacers(displayName, slate, player, activeMenu, PlaceholderType.DISPLAY_NAME);

                if (slate.isPlaceholderAPIEnabled()) {
                    displayName = PlaceholderAPI.setPlaceholders(player, displayName);
                }
                setDisplayName(meta, tf.toComponent(displayName));
            }
            List<LoreLine> loreLines = item.getLore();
            if (loreLines != null) {
                setLore(meta, loreInterpreter.interpretLore(loreLines, player, activeMenu, builtItem, item));
            }
            itemStack.setItemMeta(meta);
        }

        // Add item to inventory
        addSingleItemToInventory(item, itemStack, item.getPositions(), contents, player, builtItem);
    }

    private <C> void addTemplateItem(ActiveTemplateItem<C> activeItem, InventoryContents contents, Player player) {
        TemplateItem<C> item = activeItem.getItem();
        BuiltTemplate<C> builtTemplate = slate.getBuiltMenu(menu.name()).getTemplate(item.getName(), item.getContextClass());

        if (item.failsViewConditions(player, this)) {
            return; // Don't show item
        }

        Set<C> contexts;
        builtTemplate.initListener().handle(new MenuInfo(slate, player, activeMenu));
        Set<C> builtDefined = builtTemplate.definedContexts().get(new MenuInfo(slate, player, activeMenu));
        contexts = Objects.requireNonNullElseGet(builtDefined, () -> item.getBaseItems().keySet());

        for (C context : contexts) {
            if (item.failsContextViewConditions(context, player, this)) {
                continue;
            }

            addContextItem(contents, player, context, item, builtTemplate, contexts);
        }
    }

    private <C> @Nullable TemplateVariant<C> getVariant(TemplateItem<C> item, C context, ActiveMenu menu) {
        for (TemplateVariant<C> variant : item.getVariants()) {
            if (!variant.contextFilters().isEmpty() && !variant.contextFilters().contains(context)) {
                continue;
            }

            if (!variant.propertyFilters().isEmpty()) {
                boolean passes = true;
                for (Entry<String, Object> entry : variant.propertyFilters().entrySet()) {
                    Object propValue = menu.getProperties().get(entry.getKey());
                    if (propValue == null) {
                        passes = false;
                        break;
                    }
                    if (!propValue.equals(entry.getValue())) {
                        passes = false;
                        break;
                    }
                }
                if (!passes) {
                    continue;
                }
            }

            return variant;
        }
        return null;
    }

    private <C> void addContextItem(InventoryContents contents, Player player, C context, TemplateItem<C> item, BuiltTemplate<C> builtTemplate, Set<C> contexts) {
        TemplateVariant<C> variant = getVariant(item, context, activeMenu);
        ItemStack itemStack = item.getBaseItems().get(context); // Get a context-specific base item
        if (itemStack == null) {
            itemStack = item.getDefaultBaseItem(); // Otherwise use default base item
        }
        if (variant != null && variant.baseItem() != null) {
            itemStack = variant.baseItem();
        }
        if (itemStack != null) {
            itemStack = itemStack.clone();
        }
        // Handle initializeContext
        builtTemplate.contextListener().handle(new TemplateInfo<>(slate, player, activeMenu, itemStack, context));

        replaceItemPlaceholders(itemStack);
        // Apply TemplateModifier of built template
        itemStack = builtTemplate.modifier().modify(new TemplateInfo<>(slate, player, activeMenu, itemStack, context));

        if (itemStack == null) return;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            setContextMeta(player, context, item, builtTemplate, meta, itemStack, variant);
        }
        // Add item to inventory
        PositionProvider posProvider = item.getPosition(context);
        if (variant != null && variant.position() != null) {
            posProvider = variant.position();
        }
        SlotPos pos = null;
        if (posProvider != null) {
            List<PositionProvider> providers = new ArrayList<>();
            for (C cont : contexts) {
                providers.add(item.getPosition(cont));
            }
            // Parse the fixed or group position from providers
            pos = posProvider.getPosition(providers);
        } else {
            @Nullable SlotPos builtSlot = builtTemplate.slotProvider().get(new TemplateInfo<>(slate, player, activeMenu, itemStack, context));
            if (builtSlot != null) {
                pos = builtSlot;
            }
        }
        if (pos == null) {
            pos = item.getDefaultPosition();
        }
        if (pos != null) {
            addTemplateItemToInventory(item, itemStack, pos, contents, player, builtTemplate, context);
        }
    }

    private <C> void setContextMeta(Player player, C context, TemplateItem<C> item, BuiltTemplate<C> builtTemplate, ItemMeta meta, ItemStack itemStack, @Nullable TemplateVariant<C> variant) {
        String displayName = item.getActiveDisplayName(context); // Get the context-specific display name, or default if not defined
        if (variant != null && variant.displayName() != null) {
            displayName = variant.displayName();
        }
        if (displayName != null) {
            // BuiltTemplate replacers
            displayName = builtTemplate.applyReplacers(displayName, slate, player, activeMenu, PlaceholderType.DISPLAY_NAME, context);

            if (slate.isPlaceholderAPIEnabled()) {
                displayName = PlaceholderAPI.setPlaceholders(player, displayName);
            }
            setDisplayName(meta, tf.toComponent(displayName));
        }
        List<LoreLine> loreLines = item.getActiveLore(context);
        if (variant != null && !variant.lore().isEmpty()) {
            loreLines = variant.lore();
        }
        if (loreLines != null) {
            setLore(meta, loreInterpreter.interpretLore(loreLines, player, activeMenu, builtTemplate, item, context));
        }
        itemStack.setItemMeta(meta);
    }

    public void setDisplayName(ItemMeta meta, Component component) {
        String displayName = tf.toString(component);
        if (displayName.contains("!!REMOVE!!")) {
            return;
        }
        PaperUtil.setDisplayName(meta, component);
    }

    public void setLore(ItemMeta meta, List<Component> components) {
        if (components.isEmpty()) return;
        PaperUtil.setLore(meta, components);
    }

    /**
     * Adds the menu item itself to the inventory menu and registers click listeners, both listeners and actions
     */
    private void addSingleItemToInventory(SingleItem singleItem, ItemStack itemStack, List<SlotPos> positions, InventoryContents contents, Player player, BuiltItem builtItem) {
        for (SlotPos pos : positions) { // Set item for each position
            contents.set(pos, ClickableItem.from(itemStack, c -> {
                if (!(c.getEvent() instanceof InventoryClickEvent event)) return;

                if (isOnCooldown(singleItem)) return;
                if (failsClickConditions(singleItem, player, event)) return;

                // Run coded click functionality
                builtItem.handleClick(getClickTriggers(event.getClick()), new ItemClick(slate, player, event, c.getItem(), pos, activeMenu));

                executeClickActions(singleItem, player, contents, c); // Run custom click actions
            }));
        }
    }

    private <C> void addTemplateItemToInventory(TemplateItem<C> templateItem, ItemStack itemStack, SlotPos pos, InventoryContents contents, Player player, BuiltTemplate<C> builtTemplate, C context) {
        contents.set(pos, ClickableItem.from(itemStack, c -> {
            if (!(c.getEvent() instanceof InventoryClickEvent event)) return;

            if (isOnCooldown(templateItem)) return;
            if (failsClickConditions(templateItem, player, event)) return;
            if (failsContextClickConditions(context, templateItem, player, event)) return;

            // Run coded click functionality
            builtTemplate.handleClick(getClickTriggers(event.getClick()), new TemplateClick<>(slate, player, event, c.getItem(), pos, activeMenu, context));

            executeClickActions(templateItem, player, contents, c, context); // Run custom click actions
        }));
    }

    private boolean isOnCooldown(MenuItem menuItem) {
        ActiveItem activeItem = activeItems.get(menuItem.getName());
        return activeItem != null && activeItem.getCooldown() != 0;
    }

    private boolean failsClickConditions(MenuItem menuItem, Player player, InventoryClickEvent event) {
        // Check click conditions
        for (ClickTrigger trigger : getClickTriggers(event.getClick())) {
            if (menuItem.failsClickConditions(trigger, player, this)) {
                return true;
            }
        }
        return false;
    }

    private <C> boolean failsContextClickConditions(C context, TemplateItem<C> template, Player player, InventoryClickEvent event) {
        // Check click conditions
        for (ClickTrigger trigger : getClickTriggers(event.getClick())) {
            if (template.failsContextClickConditions(context, trigger, player, this)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Executes the configured click actions of an item
     */
    private void executeClickActions(MenuItem menuItem, Player player, InventoryContents contents, ItemClickData clickData) {
        // Convert click data event to InventoryClickEvent
        if (!(clickData.getEvent() instanceof InventoryClickEvent event)) {
            return;
        }

        executeActionsForClick(menuItem.getActions(), event.getClick(), player, contents);
    }

    private <C> void executeClickActions(TemplateItem<C> menuItem, Player player, InventoryContents contents, ItemClickData clickData, C context) {
        if (!(clickData.getEvent() instanceof InventoryClickEvent event)) {
            return;
        }

        // Execute template-level actions
        executeActionsForClick(menuItem.getActions(), event.getClick(), player, contents);

        // Execute context-level actions
        ItemActions contextActions = menuItem.getContextActions(context);
        if (contextActions != null) {
            executeActionsForClick(contextActions, event.getClick(), player, contents);
        }
    }

    private void executeActionsForClick(ItemActions itemActions, ClickType type, Player player, InventoryContents contents) {
        Set<ClickTrigger> clickTriggers = getClickTriggers(type);
        Map<ClickTrigger, List<Action>> actions = itemActions.actions();

        for (Map.Entry<ClickTrigger, List<Action>> entry : actions.entrySet()) {
            ClickTrigger clickTrigger = entry.getKey();
            if (clickTriggers.contains(clickTrigger)) { // Make sure click matches
                for (Action action : entry.getValue()) { // Execute each action
                    action.execute(player, this, contents);
                }
            }
        }
    }

    private Set<ClickTrigger> getClickTriggers(ClickType clickType) {
        Set<ClickTrigger> clickTriggers = new HashSet<>();
        clickTriggers.add(ClickTrigger.ANY);
        switch (clickType) {
            case LEFT:
            case SHIFT_LEFT:
                clickTriggers.add(ClickTrigger.LEFT);
                break;
            case RIGHT:
            case SHIFT_RIGHT:
                clickTriggers.add(ClickTrigger.RIGHT);
                break;
            case MIDDLE:
                clickTriggers.add(ClickTrigger.MIDDLE);
                break;
            case DROP:
            case CONTROL_DROP:
                clickTriggers.add(ClickTrigger.DROP);
                break;
        }
        return clickTriggers;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    private void replaceItemPlaceholders(ItemStack item) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof SkullMeta skullMeta) {
            PersistentDataContainer container = skullMeta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(slate.getPlugin(), "skull_placeholder_uuid");
            String placeholder = container.get(key, PersistentDataType.STRING);
            if (placeholder != null) {
                placeholder = TextUtil.replace(placeholder, "{player}", player.getUniqueId().toString());
                if (slate.isPlaceholderAPIEnabled()) { // Replace PlaceholderAPI placeholders inside skull_placeholder_uuid
                    placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                }
                try {
                    UUID uuid = UUID.fromString(placeholder);
                    skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
                } catch (IllegalArgumentException e) {
                    slate.getPlugin().getLogger().warning("Error while opening menu: Unable to parse UUID for skull placeholder " + placeholder);
                }
            }
            item.setItemMeta(skullMeta);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public InventoryContents getContents() {
        return contents;
    }

}
