package phrase.hologramManager.hologram;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import phrase.hologramManager.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hologram {

    private static Map<Integer, Hologram> holograms = new HashMap<>();
    private Location location;
    private List<String> lines;
    private List<ArmorStand> armorStands = new ArrayList<>();

    public Hologram(Location location, List<String> lines) {
        this.location = location;
        this.lines = lines;

        spawnAll();
    }

    public static void removeAll() {

        for(int i = 0; i < holograms.size(); i++) {

            if(!getHolograms().containsKey(i)) {
                continue;
            }

            Hologram hologram = holograms.get(i);
            List<ArmorStand> armorStands = hologram.getArmorStands();

            for(ArmorStand armorStand : armorStands) {
                armorStand.remove();
            }

            holograms.remove(i);

        }

    }

    public static void remove(int id) {

        Hologram hologram = getHolograms().get(id);
        List<ArmorStand> armorStands = hologram.getArmorStands();

        for(ArmorStand armorStand : armorStands) {
            armorStand.remove();
        }

        holograms.remove(id);

    }

    private void spawnAll() {
        Location location = this.location.clone();

        double distance;
        try {
            distance = Double.parseDouble(Plugin.getInstance().getConfig().getString("settings.distance"));
        } catch (NumberFormatException e) {
            Plugin.getInstance().getLogger().severe("В конфигурационном файле в каталоге settings параметр distance должен состоять из цифр");
            distance = 0.3;
            return;
        }

        for(String line : lines) {

            spawn(line, location);

            location.setY(location.getY() - distance);
        }

    }

    private void spawn(String line, Location location) {

        ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setCustomName(ChatColor.translateAlternateColorCodes('&', line));
        armorStand.setCustomNameVisible(true);

        armorStands.add(armorStand);
    }

    public Location getLocation() {
        return location;
    }

    public List<ArmorStand> getArmorStands() {
        return armorStands;
    }

    public static Map<Integer, Hologram> getHolograms() {
        return holograms;
    }
}
