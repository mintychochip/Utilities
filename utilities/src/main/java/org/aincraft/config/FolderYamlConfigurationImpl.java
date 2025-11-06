package org.aincraft.config;

import java.io.File;
import java.nio.file.Path;
import org.bukkit.plugin.Plugin;

public class FolderYamlConfigurationImpl {

  private final Plugin plugin;

  public FolderYamlConfigurationImpl(Plugin plugin, String path) {
    this.plugin = plugin;
    Path resolved = plugin.getDataFolder().toPath().resolve(path);
    if (!resolved.toFile().exists()) {
      plugin.saveResource(path, false);
    }
    File file = resolved.toFile();
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      for (File f : files) {
        f.toPath();
      }
    }
  }

}
