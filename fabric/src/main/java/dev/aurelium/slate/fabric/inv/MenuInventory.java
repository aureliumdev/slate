package dev.aurelium.slate.fabric.inv;

import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.ItemActions;
import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.action.trigger.MenuTrigger;
import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.fabric.action.FabricActionExecutor;
import dev.aurelium.slate.fabric.builder.BuiltItem;
import dev.aurelium.slate.fabric.builder.BuiltMenu;
import dev.aurelium.slate.fabric.builder.BuiltTemplate;
import dev.aurelium.slate.fabric.fill.FabricFillData;
import dev.aurelium.slate.fabric.info.ItemInfo;
import dev.aurelium.slate.fabric.info.MenuInfo;
import dev.aurelium.slate.fabric.info.TemplateInfo;
import dev.aurelium.slate.fabric.inv.content.InventoryContents;
import dev.aurelium.slate.fabric.item.FabricMenuItem;
import dev.aurelium.slate.fabric.item.FabricTemplateItem;
import dev.aurelium.slate.fabric.item.ItemClick;
import dev.aurelium.slate.fabric.item.TemplateClick;
import dev.aurelium.slate.fabric.item.parser.ConfigurateItemParser;
import dev.aurelium.slate.fabric.lore.LoreInterpreter;
import dev.aurelium.slate.fabric.menu.FabricActiveMenu;
import dev.aurelium.slate.fabric.menu.FabricLoadedMenu;
import dev.aurelium.slate.fabric.text.TextFormatter;
import dev.aurelium.slate.fill.FillData;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.item.*;
import dev.aurelium.slate.item.active.ActiveItem;
import dev.aurelium.slate.item.active.ActiveSingleItem;
import dev.aurelium.slate.item.active.ActiveTemplateItem;
import dev.aurelium.slate.item.provider.PlaceholderType;
import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.menu.ActiveMenu;
import dev.aurelium.slate.menu.LoadedMenu;
import dev.aurelium.slate.position.PositionProvider;
import dev.aurelium.slate.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;

import static dev.aurelium.slate.fabric.ref.FabricItemRef.unwrap;
import static dev.aurelium.slate.fabric.ref.FabricPlayerRef.wrap;

public class MenuInventory {

    private final Slate slate;
    private final LoadedMenu loadedMenu;
    private final ActiveMenu activeMenu;
    private final BuiltMenu builtMenu;
    private final Map<String, ActiveItem> activeItems;
    private final Map<String, Object> properties;
    private final int totalPages;
    private int currentPage;
    private final ServerPlayer player;
    private final List<ActiveItem> toUpdate;
    private final MenuInfo menuInfo;
    private final FabricActionExecutor actionExecutor;
    private final TextFormatter tf = new TextFormatter();
    private final LoreInterpreter loreInterpreter;
    private InventoryContents contents;

    public MenuInventory(Slate slate, LoadedMenu loadedMenu, ServerPlayer player, Map<String, Object> properties, int currentPage) {
        this.slate = slate;
        this.loadedMenu = loadedMenu;
        this.activeMenu = new FabricActiveMenu(this);
        this.activeItems = new LinkedHashMap<>();
        this.properties = new HashMap<>(properties);
        this.builtMenu = slate.getBuiltMenu(loadedMenu.name());
        this.menuInfo = new MenuInfo(slate, player, activeMenu);
        this.totalPages = builtMenu.pageProvider().getPages(this.menuInfo);
        this.currentPage = currentPage;
        this.player = player;
        this.actionExecutor = new FabricActionExecutor(slate);
        this.loreInterpreter = new LoreInterpreter(slate);
        this.toUpdate = new ArrayList<>();
    }

    public void init(ServerPlayer player, InventoryContents contents) {
        this.contents = contents;
        for (MenuItem menuItem : loadedMenu.items().values()) {
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
        builtMenu.openListener().handle(menuInfo);
        // Execute open actions
        FabricLoadedMenu.executeActions(loadedMenu, slate, MenuTrigger.OPEN, player, this);
        // Place fill items
        FillData fillData = loadedMenu.fillData();
        if (fillData.enabled()) {
            FabricFillData fabricFillData = new FabricFillData(fillData);
            fabricFillData.placeInMenu(slate, player, this);
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

    public void update(ServerPlayer player, InventoryContents contents) {
        // Decrement item cooldowns
        for (ActiveItem activeItem : toUpdate) {
            int cooldown = activeItem.getCooldown();
            if (cooldown > 0) {
                activeItem.setCooldown(cooldown - 1);
            }
        }
        builtMenu.updateListener().handle(menuInfo);
    }

    public void close() {
        FabricLoadedMenu.executeActions(loadedMenu, slate, MenuTrigger.CLOSE, player, this);
    }

    private void addSingleItem(ActiveSingleItem activeItem, InventoryContents contents, ServerPlayer player) {
        SingleItem item = activeItem.getItem();
        ItemVariant variant = getItemVariant(item, activeMenu);

        FabricMenuItem bukkitMenuItem = new FabricMenuItem(slate, item);
        if (bukkitMenuItem.failsViewConditions(player, this)) {
            return; // Don't show item
        }

        BuiltItem builtItem = slate.getBuiltMenu(loadedMenu.name()).getBackingItem(item.getName());

        ItemStack itemStack = unwrap(item.getBaseItem()).copy();
        if (variant != null && variant.baseItem() != null) {
            itemStack = unwrap(variant.baseItem());
        }
        builtItem.initListener().handle(new MenuInfo(slate, player, activeMenu));

        replaceItemPlaceholders(itemStack);
        // Apply ItemModifier of built item
        itemStack = builtItem.modifier().modify(new ItemInfo(slate, player, activeMenu, itemStack));
        if (itemStack == null) return;

        String displayName = item.getDisplayName();
        if (variant != null && variant.displayName() != null) {
            displayName = variant.displayName();
        }
        if (displayName != null) {
            // BuiltItem replacers
            displayName = builtItem.applyReplacers(displayName, slate, player, activeMenu, PlaceholderType.DISPLAY_NAME);

            setDisplayName(itemStack, tf.toComponent(displayName));
        }
        List<LoreLine> loreLines = item.getLore();
        if (variant != null && !variant.lore().isEmpty()) {
            loreLines = variant.lore();
        }
        if (loreLines != null) {
            setLore(itemStack, loreInterpreter.interpretLore(loreLines, player, activeMenu, builtItem, item));
        }

        List<SlotPos> positions = item.getPositions();
        if (variant != null && variant.positions() != null) {
            positions = variant.positions();
        }

        // Add item to inventory
        addSingleItemToInventory(item, itemStack, positions, contents, player, builtItem);
    }

    private <C> void addTemplateItem(ActiveTemplateItem<C> activeItem, InventoryContents contents, ServerPlayer player) {
        TemplateItem<C> item = activeItem.getItem();
        BuiltTemplate<C> builtTemplate = slate.getBuiltMenu(loadedMenu.name()).getTemplate(item.getName(), item.getContextClass());

        FabricMenuItem bukkitMenuItem = new FabricMenuItem(slate, item);
        if (bukkitMenuItem.failsViewConditions(player, this)) {
            return; // Don't show item
        }

        Set<C> contexts;
        builtTemplate.initListener().handle(new MenuInfo(slate, player, activeMenu));
        Set<C> builtDefined = builtTemplate.definedContexts().get(new MenuInfo(slate, player, activeMenu));
        contexts = Objects.requireNonNullElseGet(builtDefined, () -> item.getBaseItems().keySet());

        FabricTemplateItem<C> templateItem = new FabricTemplateItem<>(slate, item);
        for (C context : contexts) {
            if (templateItem.failsContextViewConditions(context, player, this)) {
                continue;
            }

            addContextItem(contents, player, context, item, builtTemplate, contexts);
        }
    }

    private <C> void addContextItem(InventoryContents contents, ServerPlayer player, C context, TemplateItem<C> item, BuiltTemplate<C> builtTemplate, Set<C> contexts) {
        TemplateVariant<C> variant = getTemplateVariant(item, context, activeMenu);
        ItemStack itemStack = unwrap(item.getBaseItems().get(context)); // Get a context-specific base item
        if (itemStack == null) {
            itemStack = unwrap(item.getDefaultBaseItem()); // Otherwise use default base item
        }
        if (variant != null && variant.baseItem() != null) {
            itemStack = unwrap(variant.baseItem());
        }
        if (itemStack != null) {
            itemStack = itemStack.copy();
        }
        // Handle initializeContext
        builtTemplate.contextListener().handle(new TemplateInfo<>(slate, player, activeMenu, itemStack, context));

        replaceItemPlaceholders(itemStack);
        // Apply TemplateModifier of built template
        itemStack = builtTemplate.modifier().modify(new TemplateInfo<>(slate, player, activeMenu, itemStack, context));

        if (itemStack == null) return;

        setContextMeta(player, context, item, builtTemplate, itemStack, variant);
        // Add item to inventory
        PositionProvider posProvider = item.getPosition(context);
        if (variant != null && variant.position() != null) {
            posProvider = variant.position();
        }
        List<SlotPos> pos = null;
        if (posProvider != null) {
            List<PositionProvider> providers = new ArrayList<>();
            for (C cont : contexts) {
                providers.add(item.getPosition(cont));
            }
            // Parse the fixed or group position from providers
            pos = posProvider.getPosition(providers);
        } else {
            @org.jetbrains.annotations.Nullable SlotPos builtSlot = builtTemplate.slotProvider().get(new TemplateInfo<>(slate, player, activeMenu, itemStack, context));
            if (builtSlot != null) {
                pos = List.of(builtSlot);
            }
        }
        if (pos == null) {
            PositionProvider def = item.getDefaultPosition();
            if (def != null) {
                List<PositionProvider> providers = new ArrayList<>();
                for (C cont : contexts) {
                    providers.add(item.getPosition(cont));
                }
                pos = def.getPosition(providers);
            }
        }
        if (pos != null) {
            addTemplateItemToInventory(item, itemStack, pos, contents, player, builtTemplate, context);
        }
    }

    private <C> void setContextMeta(ServerPlayer player, C context, TemplateItem<C> item, BuiltTemplate<C> builtTemplate, ItemStack itemStack, @Nullable TemplateVariant<C> variant) {
        String displayName = item.getActiveDisplayName(context); // Get the context-specific display name, or default if not defined
        if (variant != null && variant.displayName() != null) {
            displayName = variant.displayName();
        }
        if (displayName != null) {
            // BuiltTemplate replacers
            displayName = builtTemplate.applyReplacers(displayName, slate, player, activeMenu, PlaceholderType.DISPLAY_NAME, context);

            if (slate.isPlaceholderAPIEnabled()) {
                displayName = slate.getPlaceholderHook().setPlaceholders(wrap(player), displayName);
            }
            setDisplayName(itemStack, tf.toComponent(displayName));
        }
        List<LoreLine> loreLines = item.getActiveLore(context);
        if (variant != null && !variant.lore().isEmpty()) {
            loreLines = variant.lore();
        }
        if (loreLines != null) {
            setLore(itemStack, loreInterpreter.interpretLore(loreLines, player, activeMenu, builtTemplate, item, context));
        }
    }

    private void addSingleItemToInventory(SingleItem singleItem, ItemStack itemStack, List<SlotPos> positions, InventoryContents contents, ServerPlayer player, BuiltItem builtItem) {
        for (SlotPos pos : positions) { // Set item for each position
            contents.set(pos.getRow(), pos.getColumn(), ClickableItem.from(itemStack, data -> {
                if (isOnCooldown(singleItem)) return;
                if (failsClickConditions(singleItem, player, data)) return;

                // Run coded click functionality
                builtItem.handleClick(data.clickTriggers(), new ItemClick(slate, player, data.item(), pos, activeMenu));

                executeClickActions(singleItem, player, data); // Run custom click actions
            }));
        }
    }

    private <C> void addTemplateItemToInventory(TemplateItem<C> templateItem, ItemStack itemStack, List<SlotPos> positions, InventoryContents contents, ServerPlayer player, BuiltTemplate<C> builtTemplate, C context) {
        for (SlotPos pos : positions) {
            contents.set(pos, ClickableItem.from(itemStack, data -> {
                if (isOnCooldown(templateItem)) return;
                if (failsClickConditions(templateItem, player, data)) return;
                if (failsContextClickConditions(context, templateItem, player, data)) return;

                // Run coded click functionality
                builtTemplate.handleClick(data.clickTriggers(), new TemplateClick<>(slate, player, data.item(), pos, activeMenu, context));

                executeClickActions(templateItem, player, data, context); // Run custom click actions
            }));
        }
    }

    private @Nullable ItemVariant getItemVariant(SingleItem item, ActiveMenu menu) {
        for (ItemVariant variant : item.getVariants()) {
            if (!variant.propertyFilters().isEmpty()) {
                if (failsPropertyFilters(variant.propertyFilters(), menu)) {
                    continue;
                }
            }

            return variant;
        }
        return null;
    }

    private <C> @Nullable TemplateVariant<C> getTemplateVariant(TemplateItem<C> item, C context, ActiveMenu menu) {
        for (TemplateVariant<C> variant : item.getVariants()) {
            if (!variant.contextFilters().isEmpty() && !variant.contextFilters().contains(context)) {
                continue;
            }

            if (!variant.propertyFilters().isEmpty()) {
                if (failsPropertyFilters(variant.propertyFilters(), menu)) {
                    continue;
                }
            }

            return variant;
        }
        return null;
    }

    private boolean failsPropertyFilters(Map<String, Object> propertyFilters, ActiveMenu menu) {
        boolean fails = false;
        for (Entry<String, Object> entry : propertyFilters.entrySet()) {
            Object propValue = menu.getProperties().get(entry.getKey());
            if (propValue == null) {
                fails = true;
                break;
            }
            if (!propValue.equals(entry.getValue())) {
                fails = true;
                break;
            }
        }
        return fails;
    }

    public void setDisplayName(ItemStack item, Component component) {
        String displayName = tf.toString(component);
        if (displayName.contains("!!REMOVE!!")) {
            return;
        }
        item.set(DataComponents.CUSTOM_NAME, slate.getAudiences().asNative(component));
    }

    public void setLore(ItemStack item, List<Component> components) {
        if (components.isEmpty()) return;
        List<net.minecraft.network.chat.Component> nativeComponents = components.stream().map(c -> slate.getAudiences().asNative(c)).toList();
        item.set(DataComponents.LORE, new ItemLore(nativeComponents));
    }

    private boolean isOnCooldown(MenuItem menuItem) {
        ActiveItem activeItem = activeItems.get(menuItem.getName());
        return activeItem != null && activeItem.getCooldown() != 0;
    }

    private boolean failsClickConditions(MenuItem menuItem, ServerPlayer player, ItemClickData data) {
        // Check click conditions
        FabricMenuItem bukkitMenuItem = new FabricMenuItem(slate, menuItem);
        for (ClickTrigger trigger : data.clickTriggers()) {
            if (bukkitMenuItem.failsClickConditions(trigger, player, this)) {
                return true;
            }
        }
        return false;
    }

    private <C> boolean failsContextClickConditions(C context, TemplateItem<C> template, ServerPlayer player, ItemClickData data) {
        // Check click conditions
        FabricTemplateItem<C> templateItem = new FabricTemplateItem<>(slate, template);
        for (ClickTrigger trigger : data.clickTriggers()) {
            if (templateItem.failsContextClickConditions(context, trigger, player, this)) {
                return true;
            }
        }
        return false;
    }

    private void executeClickActions(MenuItem menuItem, ServerPlayer player, ItemClickData clickData) {
        executeActionsForClick(menuItem.getActions(), clickData.clickTriggers(), player);
    }

    private <C> void executeClickActions(TemplateItem<C> menuItem, ServerPlayer player, ItemClickData data, C context) {
        // Execute template-level actions
        executeActionsForClick(menuItem.getActions(), data.clickTriggers(), player);

        // Execute context-level actions
        ItemActions contextActions = menuItem.getContextActions(context);
        if (contextActions != null) {
            executeActionsForClick(contextActions, data.clickTriggers(), player);
        }
    }

    private void executeActionsForClick(ItemActions itemActions, Set<ClickTrigger> clickTriggers, ServerPlayer player) {
        Map<ClickTrigger, List<Action>> actions = itemActions.actions();

        for (Map.Entry<ClickTrigger, List<Action>> entry : actions.entrySet()) {
            ClickTrigger clickTrigger = entry.getKey();
            if (clickTriggers.contains(clickTrigger)) { // Make sure click matches
                for (Action action : entry.getValue()) { // Execute each action
                    actionExecutor.executeAction(action, player, this);
                }
            }
        }
    }

    public LoadedMenu getMenu() {
        return loadedMenu;
    }

    public @Nullable ActiveItem getActiveItem(String itemName) {
        return activeItems.get(itemName);
    }

    public ActiveMenu getActiveMenu() {
        return activeMenu;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public InventoryContents getContents() {
        return contents;
    }

    public void setToUpdate(ActiveItem activeItem) {
        if (!toUpdate.contains(activeItem)) {
            toUpdate.add(activeItem);
        }
    }

    public BuiltMenu getBuiltMenu() {
        return builtMenu;
    }

    public TextFormatter getTextFormatter() {
        return tf;
    }

    public LoreInterpreter getLoreInterpreter() {
        return loreInterpreter;
    }

    private void replaceItemPlaceholders(ItemStack item) {
        if (item == null) return;
        if (!item.is(Items.PLAYER_HEAD)) {
            return;
        }
        ConfigurateItemParser.updateCustomDataTag(item, slateTag -> {
            String placeholder = slateTag.getString(ConfigurateItemParser.SKULL_PLACEHOLDER_UUID).orElse(null);
            if (placeholder == null) {
                return;
            }
            placeholder = TextUtil.replace(placeholder, "{player}", player.getUUID().toString());
            try {
                UUID uuid = UUID.fromString(placeholder);
                ResolvableProfile unresolved = ResolvableProfile.createUnresolved(uuid);
                item.set(DataComponents.PROFILE, unresolved);
            } catch (IllegalArgumentException e) {
                slate.getLogger().warning("Error while opening menu: Unable to parse UUID for skull placeholder " + placeholder);
            }
        });
    }
}
