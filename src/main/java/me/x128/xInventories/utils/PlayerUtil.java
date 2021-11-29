package me.x128.xInventories.utils;

import me.x128.xInventories.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Created by Cole on 4/11/16.
 */
public class PlayerUtil {
    public static String getPlayerCurrentGroup(Player p) {
        String current = p.getWorld().getName();
        FileConfiguration config = Main.getPlugin().getConfig();
        if (config.contains("worlds." + current)) {
            return config.getString("worlds." + current);
        }
        return null;
    }
}
