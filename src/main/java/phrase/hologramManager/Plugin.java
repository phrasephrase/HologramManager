package phrase.hologramManager;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import phrase.hologramManager.commands.HologramManagerCMD;
import phrase.hologramManager.hologram.Hologram;

import java.io.File;
import java.util.List;

public final class Plugin extends JavaPlugin {

    private static Plugin instance;

    @Override
    public void onEnable() {
        instance = this;

        getCommand("hologrammanager").setExecutor(new HologramManagerCMD());
        saveDefaultConfig();
        loadConfig();

    }

    @Override
    public void onDisable() {

        saveConfig();
        Hologram.removeAll();

    }

    public static Plugin getInstance() {
        return instance;
    }

    private void loadConfig() {

        File file = new File(Plugin.getInstance().getDataFolder(), "holograms.yml");
        if(!file.exists()) {
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for(String key : config.getKeys(false)) {

            List<String> lines = config.getStringList(key + ".lines");
            Location location = config.getLocation(key + ".location");

            Hologram hologram = new Hologram(location, lines);
            Hologram.getHolograms().put(
                    Integer.parseInt(key),
                    hologram
            );

        }



    }
}
