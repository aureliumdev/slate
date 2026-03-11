package dev.aurelium.slate.fabric.menu;


import dev.aurelium.slate.fabric.Slate;

import java.io.File;
import java.nio.file.Path;

public class MenuFileGenerator {

    private final Slate slate;

    public MenuFileGenerator(Slate slate) {
        this.slate = slate;
    }

    public void generate() {
        File menuDir = slate.getOptions().mainDirectory();
        Path pluginPath = slate.getDataFolder().toPath();

        for (String menuName : slate.getBuiltMenus().keySet()) {
            File file = new File(menuDir, menuName + ".yml");

            // Skip existing files
            if (file.exists()) continue;

            String relative = pluginPath.relativize(file.toPath()).toString();

            slate.saveResource(relative, false);
        }
    }
}
