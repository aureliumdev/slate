package dev.aurelium.slate.util;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class YamlLoader {

    private final Plugin plugin;

    public YamlLoader(Plugin plugin) {
        this.plugin = plugin;
    }

    @Nullable
    public ConfigurationNode loadEmbeddedFile(String path) {
        URL url = this.getClass().getClassLoader().getResource(path);
        if (url == null) return null;

        try {
            URI uri = url.toURI();
            try (FileSystem ignored = FileSystems.newFileSystem(uri, Map.of("create", "true"))) {
                var loader = YamlConfigurationLoader.builder()
                        .defaultOptions(opts -> opts.shouldCopyDefaults(false))
                        .path(Path.of(uri))
                        .build();
                return loader.load();
            }
        } catch (URISyntaxException | IOException e) {
            plugin.getLogger().warning("Failed to load embedded file " + path);
            return null;
        }
    }

    public ConfigurationNode loadUserFile(File file) throws ConfigurateException {
        var loader = YamlConfigurationLoader.builder()
                .path(file.toPath())
                .defaultOptions(opts -> opts.shouldCopyDefaults(false))
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .build();
        return loader.load();
    }

    public void saveFile(File file, ConfigurationNode config) throws ConfigurateException {
        var loader = YamlConfigurationLoader.builder()
                .defaultOptions(opts -> opts.shouldCopyDefaults(false))
                .file(file)
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .build();
        loader.save(config);
    }

    public void saveIfUpdated(File file, ConfigurationNode embedded, ConfigurationNode user, ConfigurationNode merged) throws ConfigurateException {
        int embeddedCount = getLeafNodes(embedded).size();
        int userCount = getLeafNodes(user).size();
        if (embeddedCount > userCount) {
            saveFile(file, merged);
            String path = plugin.getDataFolder().toPath().relativize(file.toPath()).toString();
            int updated = embeddedCount - userCount;
            plugin.getLogger().info("Updated " + path + " with " + updated + " new key" + (updated != 1 ? "s" : ""));
        }
    }

    public void generateUserFile(String path) throws ConfigurateException {
        ConfigurationNode config = loadEmbeddedFile(path);
        if (config == null) return;

        var file = new File(plugin.getDataFolder(), path);
        if (!file.exists()) {
            saveFile(file, config);
        }
    }

    public List<ConfigurationNode> getLeafNodes(ConfigurationNode root) {
        var leafNodes = new ArrayList<ConfigurationNode>();
        var toProcess = new Stack<ConfigurationNode>();
        toProcess.addAll(root.childrenMap().values());
        while (!toProcess.isEmpty()) {
            ConfigurationNode node = toProcess.pop();
            if (node.isMap()) {
                toProcess.addAll(node.childrenMap().values());
            } else {
                leafNodes.add(node);
            }
        }
        return leafNodes;
    }

    public ConfigurationNode mergeNodes(@NotNull ConfigurationNode... nodes) throws SerializationException {
        if (nodes.length == 0) throw new IllegalArgumentException("Must provide at least one node");

        ConfigurationNode merged = nodes[0].copy();
        for (var node : nodes) {
            mergeRec(merged, node);
        }
        return merged;
    }

    public static String toDotString(NodePath nodePath) {
        var sb = new StringBuilder();
        for (Object segment : nodePath.array()) {
            sb.append(segment).append(".");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    private void mergeRec(ConfigurationNode base, ConfigurationNode source) throws SerializationException {
        for (ConfigurationNode child : source.childrenMap().values()) {
            if (child.isMap()) {
                mergeRec(base.node(child.key()), child);
            } else {
                base.node(child.key()).set(child.raw());
            }
        }
    }

}
