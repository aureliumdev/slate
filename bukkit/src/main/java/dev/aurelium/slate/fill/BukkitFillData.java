package dev.aurelium.slate.fill;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.SlateLibrary;
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

import java.util.List;

import static dev.aurelium.slate.bukkit.ref.BukkitItemRef.unwrap;
import static dev.aurelium.slate.bukkit.ref.BukkitItemRef.wrap;

public class BukkitFillData {

    private final FillData fillData;

    public BukkitFillData(FillData fillData) {
        this.fillData = fillData;
    }

    public static FillItem getDefault(SlateLibrary slate) {
        return new FillItem(slate, wrap(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)));
    }

    public static FillData empty(Slate slate) {
        return new FillData(new FillItem(slate, wrap(new ItemStack(Material.BLACK_STAINED_GLASS_PANE))), new SlotPos[0], false);
    }

    public void placeInMenu(Slate slate, Player player, MenuInventory inv) {
        FillItem fillItem = fillData.item();
        ItemStack providedFill = inv.getBuiltMenu().fillItem().modify(new ItemInfo(slate, player, inv.getActiveMenu(), unwrap(fillItem.getBaseItem())));
        if (providedFill != null) {
            fillItem = new FillItem(slate, wrap(providedFill));
        }
        ItemStack itemStack = unwrap(fillItem.getBaseItem());
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
        if (fillData.slots() == null) { // Use default fill
            inv.getContents().fill(ClickableItem.empty(itemStack));
        } else { // Use defined slot positions
            for (SlotPos slot : fillData.slots()) {
                inv.getContents().set(slot, ClickableItem.empty(itemStack));
            }
        }
    }
}
