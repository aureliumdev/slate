package dev.aurelium.slate.fabric.inv;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ClickableItem {

    private final ItemStack item;
    private final Consumer<ItemClickData> consumer;
    private Predicate<ServerPlayer> canSee;
    private Predicate<ServerPlayer> canClick;

    ClickableItem(ItemStack item, Consumer<ItemClickData> consumer) {
        this.item = item;
        this.consumer = consumer;
        this.canSee = null;
        this.canClick = null;
    }

    ClickableItem(ItemStack item, Consumer<ItemClickData> consumer, Predicate<ServerPlayer> canSee, Predicate<ServerPlayer> canClick) {
        this.item = item;
        this.consumer = consumer;
        this.canSee = canSee;
        this.canClick = canClick;
    }

    public static ClickableItem from(ItemStack item, Consumer<ItemClickData> consumer) {
        return new ClickableItem(item, consumer);
    }

    public static ClickableItem empty(ItemStack item) {
        return from(item, data -> {});
    }

    public ItemStack getItem() {
        return item;
    }

    public ItemStack getItemIfVisible(ServerPlayer player) {
        if (canSee(player)) {
            return item;
        }
        return ItemStack.EMPTY;
    }

    public Consumer<ItemClickData> getConsumer() {
        return consumer;
    }

    public boolean canSee(ServerPlayer player) {
        if (canSee == null) return true;
        return canSee.test(player);
    }

    public boolean canClick(ServerPlayer player) {
        if (canClick == null) return true;
        return canClick.test(player);
    }

    public void setCanClick(Predicate<ServerPlayer> canClick) {
        this.canClick = canClick;
    }

    public void setCanSee(Predicate<ServerPlayer> canSee) {
        this.canSee = canSee;
    }

    public void run(ItemClickData data) {
        if (!canSee(data.player()) || !canClick(data.player())) return;

        consumer.accept(data);
    }
}
