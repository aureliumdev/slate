package dev.aurelium.slate.item.parser;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.function.ItemMetaParser;
import dev.aurelium.slate.item.provider.KeyedItemProvider;
import dev.aurelium.slate.lore.LoreFactory;
import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.util.NumberUtil;
import dev.aurelium.slate.util.SkullCreator;
import dev.aurelium.slate.util.Validate;
import dev.aurelium.slate.util.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;

public class ConfigurateItemParser {

    private final Slate slate;
    private final Plugin plugin;

    public ConfigurateItemParser(Slate slate) {
        this.slate = slate;
        this.plugin = slate.getPlugin();
    }

    public ItemStack parseBaseItem(ConfigurationNode config) {
        String key = config.node("key").getString();
        if (key != null) {
            ItemStack item = parseItemKey(key);
            if (item != null) {
                return item; // Returns the item if key parse was successful
            }
        }

        String materialString = config.node("material").getString();
        Validate.notNull(materialString, "Item must specify a material");

        ItemStack item = parseMaterialString(materialString);

        parseAmount(item, config);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        // Enchantments
        if (!config.node("enchantments").virtual()) {
            parseEnchantments(item, config);
        }
        // Custom potion effects
        if (!config.node("custom_effects").virtual()) {
            parseCustomEffects(config, item);
        }
        // Glowing w/o enchantments visible
        if (config.node("glow").getBoolean(false)) {
            parseGlow(item);
        }
        if (!config.node("flags").virtual()) {
            parseFlags(config, item);
        }
        if (!config.node("durability").virtual()) {
            parseDurability(config, item);
        }
        parseItemModel(config, item);
        // Parses custom_model_data and old format CustomModelData nbt map
        parseCustomModelData(config, item);

        ConfigurationNode skullMetaSection = config.node("skull_meta");
        if (!skullMetaSection.virtual()) {
            parseSkullMeta(item, item.getItemMeta(), skullMetaSection);
        }
        // Custom item meta parsers
        for (Map.Entry<String, ItemMetaParser> entry : slate.getOptions().itemMetaParsers().entrySet()) {
            ConfigurationNode section = config.node(entry.getKey());
            if (!section.virtual()) {
                item = entry.getValue().parse(item, section);
            }
        }
        return item;
    }

    @Nullable
    private ItemStack parseItemKey(String key) {
        KeyedItemProvider provider = slate.getOptions().keyedItemProvider();
        if (provider != null) {
            return provider.getItem(key);
        }
        return null;
    }

    private void parseDurability(ConfigurationNode section, ItemStack item) {
        ItemMeta meta = getMeta(item);
        int durability = section.node("durability").getInt();
        if (meta instanceof Damageable damageable) {
            short maxDurability = item.getType().getMaxDurability();
            damageable.setDamage(Math.max(maxDurability - durability, maxDurability));
            item.setItemMeta(meta);
        }
    }

    private void parseFlags(ConfigurationNode section, ItemStack item) {
        try {
            ItemMeta meta = getMeta(item);
            List<String> flags = section.node("flags").getList(String.class, new ArrayList<>());
            for (String flagName : flags) {
                ItemFlag itemFlag = ItemFlag.valueOf(flagName.toUpperCase(Locale.ROOT));
                if (itemFlag == ItemFlag.HIDE_ATTRIBUTES && VersionUtil.isAtLeastVersion(20, 5) && !slate.getOptions().isMock()) {
                    meta.setAttributeModifiers(Material.IRON_SWORD.getDefaultAttributeModifiers(EquipmentSlot.HAND));
                }
                meta.addItemFlags(itemFlag);
            }
            item.setItemMeta(meta);
        } catch (SerializationException ignored) {

        }
    }

    private void parseGlow(ItemStack item) {
        ItemMeta meta = getMeta(item);
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }

    @SuppressWarnings("deprecation")
    private void parseCustomEffects(ConfigurationNode section, ItemStack item) {
        PotionMeta potionMeta = (PotionMeta) getMeta(item);
        for (ConfigurationNode effectNode : section.node("custom_effects").childrenList()) {
            String effectName = effectNode.node("type").getString("SPEED");
            PotionEffectType type = PotionEffectType.getByName(effectName);
            if (type != null) {
                int duration = effectNode.node("duration").getInt();
                int amplifier = effectNode.node("amplifier").getInt();
                potionMeta.addCustomEffect(new PotionEffect(type, duration, amplifier), true);
                potionMeta.setColor(type.getColor());
            } else {
                throw new IllegalArgumentException("Invalid potion effect type " + effectName);
            }
        }
        item.setItemMeta(potionMeta);
    }

    private void parseEnchantments(ItemStack item, ConfigurationNode section) {
        try {
            ItemMeta meta = getMeta(item);
            List<String> enchantmentStrings = section.node("enchantments").getList(String.class, new ArrayList<>());
            for (String enchantmentEntry : enchantmentStrings) {
                String[] splitEntry = enchantmentEntry.split(" ");
                String enchantmentName = splitEntry[0];
                int level = 1;
                if (splitEntry.length > 1) {
                    level = NumberUtil.toInt(splitEntry[1], 1);
                }
                Enchantment enchantment = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchantmentName.toLowerCase(Locale.ROOT)));
                if (enchantment != null) {
                    if (item.getType() == Material.ENCHANTED_BOOK && meta instanceof EnchantmentStorageMeta esm) {
                        esm.addStoredEnchant(enchantment, level, true);
                        item.setItemMeta(esm);
                    } else {
                        meta.addEnchant(enchantment, level, true);
                        item.setItemMeta(meta);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid enchantment name " + enchantmentName);
                }
            }
        } catch (SerializationException ignored) {

        }
    }

    private ItemStack parseMaterialString(String materialString) {
        ItemStack item;
        String materialName = materialString.toUpperCase(Locale.ROOT);
        Material material = parseMaterial(materialName);
        if (material == null) {
            throw new IllegalArgumentException("Unknown material " + materialString);
        }
        if (!material.isItem()) { // Return fallback item if material isn't an item
            return new ItemStack(Material.GRAY_DYE);
        }
        item = new ItemStack(material);
        return item;
    }

    private @NotNull ItemMeta getMeta(ItemStack item) {
        return Objects.requireNonNull(item.getItemMeta());
    }

    @Nullable
    public String parseDisplayName(ConfigurationNode section) {
        if (!section.node("display_name").virtual()) {
            return section.node("display_name").getString();
        }
        return null;
    }

    @NotNull
    public List<LoreLine> parseLore(ConfigurationNode section) {
        ConfigurationNode loreNode = section.node("lore");
        return new LoreFactory(slate).getLore(loreNode);
    }



    protected Material parseMaterial(String name) {
        return Material.getMaterial(name);
    }

    private void parseSkullMeta(ItemStack item, ItemMeta meta, ConfigurationNode section) {
        if (!(meta instanceof SkullMeta skullMeta)) {
            return;
        }
        String uuid = section.node("uuid").getString();
        if (uuid != null) { // From UUID of player
            UUID id = UUID.fromString(uuid);
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(id));
            item.setItemMeta(meta);
        }
        String base64 = section.node("base64").getString();
        if (base64 != null) { // From base64 string
            SkullCreator.itemWithBase64(item, base64);
        }
        String url = section.node("url").getString();
        if (url != null) { // From Mojang URL
            SkullCreator.itemWithUrl(item, url);
        }
        String placeholder = section.node("placeholder_uuid").getString();
        if (placeholder != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(plugin, "skull_placeholder_uuid");
            container.set(key, PersistentDataType.STRING, placeholder);
            item.setItemMeta(meta);
        }
    }

    private void parseAmount(ItemStack item, ConfigurationNode section) {
        int amount = section.node("amount").getInt(1);
        item.setAmount(amount);
    }

    private void parseCustomModelData(ConfigurationNode config, ItemStack item) {
        if (!config.node("custom_model_data").virtual()) {
            int data = config.node("custom_model_data").getInt();
            ItemMeta meta = getMeta(item);
            meta.setCustomModelData(data);
            item.setItemMeta(meta);
        } else if (!config.node("nbt").node("CustomModelData").virtual()) {
            int data = config.node("nbt").node("CustomModelData").getInt();
            ItemMeta meta = getMeta(item);
            meta.setCustomModelData(data);
            item.setItemMeta(meta);
        }
    }

    private void parseItemModel(ConfigurationNode config, ItemStack item) {
        if (!config.node("item_model").empty() && VersionUtil.isAtLeastVersion(21, 2)) {
            String model = config.node("item_model").getString("");
            ItemMeta meta = getMeta(item);
            meta.setItemModel(NamespacedKey.fromString(model));
            item.setItemMeta(meta);
        }
    }

}
