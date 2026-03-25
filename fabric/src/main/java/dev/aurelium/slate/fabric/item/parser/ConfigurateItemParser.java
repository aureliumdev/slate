package dev.aurelium.slate.fabric.item.parser;

import com.google.common.collect.ImmutableMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.fabric.function.ItemMetaParser;
import dev.aurelium.slate.fabric.item.provider.KeyedItemProvider;
import dev.aurelium.slate.item.parser.ItemParser;
import dev.aurelium.slate.lore.LoreFactory;
import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.ref.ItemRef;
import dev.aurelium.slate.util.NumberUtil;
import dev.aurelium.slate.util.Validate;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;

import static dev.aurelium.slate.fabric.ref.FabricItemRef.wrap;

public class ConfigurateItemParser implements ItemParser {

    private final Slate slate;
    public static final String SKULL_PLACEHOLDER_UUID = "skull_placeholder_uuid";

    public ConfigurateItemParser(Slate slate) {
        this.slate = slate;
    }

    @Override
    public ItemRef parseBaseItem(ConfigurationNode config) {
        return parseBaseItem(config, Set.of());
    }

    public ItemRef parseBaseItem(ConfigurationNode config, Set<String> excludedKeys) {
        String key = config.node("key").getString();
        if (key != null && !excludedKeys.contains("key")) {
            ItemStack item = parseItemKey(key);
            if (item != null) {
                return wrap(item); // Returns the item if key parse was successful
            }
        }

        String materialString = config.node("material").getString();
        Validate.notNull(materialString, "Item must specify a material");

        Item itemType = BuiltInRegistries.ITEM.getValue(Identifier.parse(materialString.toLowerCase(Locale.ROOT)));
        ItemStack itemStack = new ItemStack(itemType);

        if (!excludedKeys.contains("amount")) {
            int amount = config.node("amount").getInt(1);
            itemStack.setCount(amount);
        }

        if (!config.node("enchantments").empty() && !excludedKeys.contains("enchantments")) {
            parseEnchantments(itemStack, config);
        }
        if (!config.node("flags").empty() && !excludedKeys.contains("flags")) {
            parseFlags(config, itemStack);
        }
        if (!config.node("hide_tooltip").empty() && !excludedKeys.contains("hide_tooltip")) {
            parseHideTooltip(config, itemStack);
        }
        if (!config.node("skull_meta").empty() && !excludedKeys.contains("skull_meta")) {
            parseSkullMeta(config, itemStack);
        }
        if (!config.node("potion_data").empty() && !excludedKeys.contains("potion_data")) {
            parsePotionData(config, itemStack);
        }

        // Custom item meta parsers
        for (Map.Entry<String, ItemMetaParser> entry : slate.getOptions().itemMetaParsers().entrySet()) {
            ConfigurationNode section = config.node(entry.getKey());
            if (!section.virtual()) {
                itemStack = entry.getValue().parse(itemStack, section);
            }
        }

        return wrap(itemStack);
    }

    private void parsePotionData(ConfigurationNode node, ItemStack item) {
        String typeName = node.node("potion_data", "type").getString("water").toLowerCase(Locale.ROOT);
        typeName = substitutePotionType(typeName);

        Reference<Potion> potion = BuiltInRegistries.POTION.get(Identifier.parse(typeName)).orElse(null);
        if (potion == null) {
            slate.getLogger().warning("Potion type " + typeName + " not found in Minecraft registry");
            return;
        }

        PotionContents potionContents = new PotionContents(potion);

        item.set(DataComponents.POTION_CONTENTS, potionContents);
    }

    private String substitutePotionType(String name) {
        return switch (name) {
            case "instant_damage" -> "harming";
            case "instant_heal" -> "healing";
            case "regen" -> "regeneration";
            case "jump" -> "leaping";
            case "speed" -> "swiftness";
            default -> name;
        };
    }

    private void parseEnchantments(ItemStack item, ConfigurationNode config) {
        try {
            List<String> enchantmentStrings = config.node("enchantments").getList(String.class, new ArrayList<>());
            for (String enchantmentEntry : enchantmentStrings) {
                String[] splitEntry = enchantmentEntry.split(" ");
                String enchantmentName = splitEntry[0];
                int level = 1;
                if (splitEntry.length > 1) {
                    level = NumberUtil.toInt(splitEntry[1], 1);
                }
                Registry<Enchantment> registry = slate.getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
                Reference<Enchantment> enchantRef = registry.get(Identifier.parse(enchantmentName.toLowerCase(Locale.ROOT))).orElse(null);
                if (enchantRef != null) {
                    item.enchant(enchantRef, level);
                } else {
                    throw new IllegalArgumentException("Invalid enchantment name " + enchantmentName);
                }
            }
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseFlags(ConfigurationNode config, ItemStack item) {
        try {
            List<String> flags = config.node("flags").getList(String.class, new ArrayList<>())
                    .stream()
                    .map(String::toLowerCase)
                    .toList();
            for (String flagName : flags) {
                TooltipDisplay tooltipDisplay = item.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
                SequencedSet<DataComponentType<?>> hidden = new ReferenceLinkedOpenHashSet<>(tooltipDisplay.hiddenComponents());
                switch (flagName) {
                    case "hide_attributes" -> {
                        ItemAttributeModifiers defModifiers = Items.IRON_SWORD.components().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
                        item.set(DataComponents.ATTRIBUTE_MODIFIERS, defModifiers);
                        hidden.add(DataComponents.ATTRIBUTE_MODIFIERS);
                    }
                    case "hide_enchants" -> hidden.add(DataComponents.ENCHANTMENTS);
                    case "hide_stored_enchants" -> hidden.add(DataComponents.STORED_ENCHANTMENTS);
                    case "hide_unbreakable" -> hidden.add(DataComponents.UNBREAKABLE);
                    case "hide_potion_effects" -> hidden.add(DataComponents.POTION_CONTENTS);
                }
                item.set(DataComponents.TOOLTIP_DISPLAY, new TooltipDisplay(false, hidden));
            }
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseHideTooltip(ConfigurationNode config, ItemStack item) {
        boolean hideTooltip = config.node("hide_tooltip").getBoolean();
        TooltipDisplay tooltipDisplay = item.get(DataComponents.TOOLTIP_DISPLAY);
        SequencedSet<DataComponentType<?>> hiddenComponents = new ReferenceLinkedOpenHashSet<>();
        if (tooltipDisplay != null) {
            hiddenComponents = new ReferenceLinkedOpenHashSet<>(tooltipDisplay.hiddenComponents());
        }
        item.set(DataComponents.TOOLTIP_DISPLAY, new TooltipDisplay(hideTooltip, hiddenComponents));
    }

    private void parseSkullMeta(ConfigurationNode config, ItemStack item) {
        ConfigurationNode section = config.node("skull_meta");

        String uuid = section.node("uuid").getString();
        if (uuid != null) {
            ResolvableProfile unresolved = ResolvableProfile.createUnresolved(uuid);
            item.set(DataComponents.PROFILE, unresolved);
        }
        String base64 = section.node("base64").getString();
        if (base64 != null) {
            skullWithBase64(item, base64);
        }
        String url = section.node("url").getString();
        if (url != null) {
            skullWithBase64(item, urlToBase64(url));
        }
        String placeholder = section.node("placeholder_uuid").getString();
        if (placeholder != null) {
            updateCustomDataTag(item, tag -> {
                tag.putString(SKULL_PLACEHOLDER_UUID, placeholder);
            });
        }
        TooltipDisplay tooltipDisplay = item.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT).withHidden(DataComponents.PROFILE, true);
        item.set(DataComponents.TOOLTIP_DISPLAY, tooltipDisplay);
    }

    public static void updateCustomDataTag(ItemStack item, Consumer<CompoundTag> slateTagConsumer) {
        CustomData.update(DataComponents.CUSTOM_DATA, item, compoundTag -> {
            CompoundTag slateTag = compoundTag.getCompoundOrEmpty("slate");
            slateTagConsumer.accept(slateTag);
            compoundTag.put("slate", slateTag);
        });
    }

    private void skullWithBase64(ItemStack item, String base64) {
        ImmutableMultimap<String, Property> multimap = ImmutableMultimap.of("textures",
                new Property("textures", base64, null));
        GameProfile gameProfile = new GameProfile(Util.NIL_UUID, "", new PropertyMap(multimap));
        ResolvableProfile resolved = ResolvableProfile.createResolved(gameProfile);
        item.set(DataComponents.PROFILE, resolved);
    }

    private String urlToBase64(String url) {
        URI actualUrl;
        try {
            actualUrl = new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String toEncode = "{\"textures\":{\"SKIN\":{\"url\":\"" + actualUrl + "\"}}}";
        return Base64.getEncoder().encodeToString(toEncode.getBytes());
    }

    @Nullable
    private ItemStack parseItemKey(String key) {
        KeyedItemProvider provider = slate.getOptions().keyedItemProvider();
        if (provider != null) {
            return provider.getItem(key);
        }
        return null;
    }

    @Override
    public @Nullable String parseDisplayName(ConfigurationNode section) {
        if (!section.node("display_name").virtual()) {
            return section.node("display_name").getString();
        }
        return null;
    }

    @Override
    public @NotNull List<LoreLine> parseLore(ConfigurationNode section) {
        ConfigurationNode loreNode = section.node("lore");
        return new LoreFactory(slate).getLore(loreNode);
    }
}
