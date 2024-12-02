package phrase.hologramManager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import phrase.hologramManager.Plugin;
import phrase.hologramManager.hologram.Hologram;
import phrase.hologramManager.utils.ChatUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HologramManagerCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command,
                             String s, String[] strings) {

        if(!(commandSender instanceof Player)) {
            ChatUtil.sendMessage(commandSender, Plugin.getInstance().getConfig().getString("isAPlayer"));
            return true;
        }

        Player player = (Player) commandSender;

        if(strings.length < 1) {
            ChatUtil.sendMessage(player, Plugin.getInstance().getConfig().getString("usage"));
            return true;
        }

        if(strings[0].equalsIgnoreCase("reload")) {
            Plugin.getInstance().onDisable();
            Plugin.getInstance().onEnable();
            Plugin.getInstance().reloadConfig();

            ChatUtil.sendMessage(player, Plugin.getInstance().getConfig().getString("reload"));
            return true;
        }

        if(strings[0].equalsIgnoreCase("spawn")) {

            if(strings.length < 2) {

                ChatUtil.sendMessage(player, Plugin.getInstance().getConfig().getString("message.spawn.usage"));
                return true;

            }

            int id;
            try {
                id = Integer.parseInt(strings[1]);
            } catch (NumberFormatException e) {
                ChatUtil.sendMessage(player, Plugin.getInstance().getConfig().getString("id"));
                return true;
            }

            if(Hologram.getHolograms().containsKey(id)) {
                ChatUtil.sendMessage(player, Plugin.getInstance().getConfig().getString("message.spawn.alreadyExists"));
                return true;
            }

            List<String> lines = new ArrayList<>();
            for(int i = 2; i<strings.length; i++) {

                if(Plugin.getInstance().getConfig().getString("settings.separator") == null) {
                    Plugin.getInstance().getLogger().severe("Значение в каталоге settings параметр separator имеет null");
                    return true;
                }

                lines.add(strings[i].replace(Plugin.getInstance().getConfig().getString("settings.separator"), " "));

            }

            Hologram hologram = new Hologram(player.getLocation(), lines);
            Hologram.getHolograms().put(id, hologram);

            final int finalId = id;
            new BukkitRunnable() {
                @Override
                public void run() {
                    File file = new File(Plugin.getInstance().getDataFolder(), "holograms.yml");

                    if(!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            Plugin.getInstance().getLogger().severe("Не удалось создать конфигурационный файл: " + e.getMessage());
                            cancel();
                        }
                    }

                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    config.set(finalId + ".lines", lines);
                    config.set(finalId + ".location", hologram.getLocation());

                    try {
                        config.save(file);
                        cancel();
                    } catch (IOException e) {
                        Plugin.getInstance().getLogger().severe("Не удалось сохранить конфигурационный файл: " + e.getMessage());
                        cancel();
                    }
                }
            }.runTaskAsynchronously(Plugin.getInstance());

            return true;
        }
        if(strings[0].equalsIgnoreCase("remove")) {

            if(strings.length < 2) {
                ChatUtil.sendMessage(player, Plugin.getInstance().getConfig().getString("message.remove.usage"));
                return true;
            }

            int id;
            try {
                id = Integer.parseInt(strings[1]);
            } catch (NumberFormatException e) {
                ChatUtil.sendMessage(player, Plugin.getInstance().getConfig().getString("id"));
                return true;
            }

            if(!Hologram.getHolograms().containsKey(id)) {
                ChatUtil.sendMessage(player, Plugin.getInstance().getConfig().getString("message.remove.doesNoExists"));
                return true;
            }

            Hologram.remove(id);

            final int finalId = id;
            new BukkitRunnable() {
                @Override
                public void run() {
                    File file = new File(Plugin.getInstance().getDataFolder(), "holograms.yml");
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

                    for(String key : config.getKeys(false)) {

                        if(key.equals(String.valueOf(finalId))) {
                            config.set(key, null);
                        }

                    }

                    try {
                        config.save(file);
                        cancel();
                    } catch (IOException e) {
                        Plugin.getInstance().getLogger().severe("Не удалось сохранить конфигурационный файл: " + e.getMessage());
                    }
                }
            }.runTaskAsynchronously(Plugin.getInstance());

        }

        return true;
    }
}
