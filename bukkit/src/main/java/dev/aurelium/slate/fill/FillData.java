package dev.aurelium.slate.fill;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.builder.BuiltItem;
import dev.aurelium.slate.info.ItemInfo;
import dev.aurelium.slate.inv.ClickableItem;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.menu.MenuInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record FillData(FillItem item, SlotPos[] slots, boolean enabled) {

    public static FillData empty(Slate slate) {
        return new FillData(new FillItem(slate, new ItemStack(Material.BLACK_STAINED_GLASS_PANE)), new SlotPos[0], false);
    }

    @Override
    @Nullable
    public SlotPos[] slots() {
        return slots;
    }

    public void placeInMenu(Slate slate, Player player, MenuInventory inv) {
        FillItem fillItem = item;
        ItemStack providedFill = inv.getBuiltMenu().fillItem().modify(new ItemInfo(slate, player, inv.getActiveMenu(), fillItem.getBaseItem()));
        if (providedFill != null) {
            fillItem = new FillItem(slate, providedFill);
        }
        ItemStack itemStack = fillItem.getBaseItem();
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            String displayName = fillItem.getDisplayName();
            if (displayName != null) {
                inv.setDisplayName(meta, inv.getTextFormatter().toComponent(displayName));
            }
            List<LoreLine> loreLines = fillItem.getLore();
            if (loreLines != null) {
                inv.setLore(meta, inv.getLoreInterpreter().interpretLore(loreLines, player, inv.getActiveMenu(), BuiltItem.createEmpty(), fillItem));
            }
            itemStack.setItemMeta(meta);
        }
        if (slots == null) { // Use default fill
            inv.getContents().fill(ClickableItem.empty(itemStack));
        } else { // Use defined slot positions
            for (SlotPos slot : slots) {
                inv.getContents().set(slot, ClickableItem.empty(itemStack));
            }
        }
    }

}
