package dev.aurelium.slate.fabric.fill;

import dev.aurelium.slate.SlateLibrary;
import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.fabric.builder.BuiltItem;
import dev.aurelium.slate.fabric.info.ItemInfo;
import dev.aurelium.slate.fabric.inv.ClickableItem;
import dev.aurelium.slate.fabric.inv.MenuInventory;
import dev.aurelium.slate.fill.FillData;
import dev.aurelium.slate.fill.FillItem;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.lore.LoreLine;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

import static dev.aurelium.slate.fabric.ref.FabricItemRef.unwrap;
import static dev.aurelium.slate.fabric.ref.FabricItemRef.wrap;

public class FabricFillData {

    private final FillData fillData;

    public FabricFillData(FillData fillData) {
        this.fillData = fillData;
    }

    public static FillItem getDefault(SlateLibrary slate) {
        return new FillItem(slate, wrap(new ItemStack(Items.BLACK_STAINED_GLASS_PANE)));
    }

    public static FillData empty(Slate slate) {
        return new FillData(new FillItem(slate, wrap(new ItemStack(Items.BLACK_STAINED_GLASS_PANE))), new SlotPos[0], false);
    }

    public void placeInMenu(Slate slate, ServerPlayer player, MenuInventory inv) {
        FillItem fillItem = fillData.item();
        ItemStack providedFill = inv.getBuiltMenu().fillItem().modify(new ItemInfo(slate, player, inv.getActiveMenu(), unwrap(fillItem.getBaseItem())));
        if (providedFill != null) {
            fillItem = new FillItem(slate, wrap(providedFill));
        }
        ItemStack itemStack = unwrap(fillItem.getBaseItem());

        String displayName = fillItem.getDisplayName();
        if (displayName != null) {
            inv.setDisplayName(itemStack, inv.getTextFormatter().toComponent(displayName));
        }
        List<LoreLine> loreLines = fillItem.getLore();
        if (loreLines != null) {
            inv.setLore(itemStack, inv.getLoreInterpreter().interpretLore(loreLines, player, inv.getActiveMenu(), BuiltItem.createEmpty(), fillItem));
        }

        if (fillData.slots() == null) { // Use default fill
            inv.getContents().fill(ClickableItem.empty(itemStack));
        } else { // Use defined slot positions
            for (SlotPos slot : fillData.slots()) {
                if (slot == null) continue;
                inv.getContents().set(slot, ClickableItem.empty(itemStack));
            }
        }
    }
}
