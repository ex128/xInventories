package me.x128.xInventories.utils;

import me.x128.xInventories.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

/**
 * Created by Cole on 4/26/16.
 */
public class LogoutGroup {

    private FileConfiguration config;
    private File file;

    public LogoutGroup() {
        //get the group file
        file = new File(Main.getPlugin().getDataFolder() + File.separator + "logout_worlds.yml");
        config = FileManager.getYaml(file);

        //start autosave

       new BukkitRunnable(){
            @Override
            public void run(){
                save(true);
            }
        }.runTaskTimer(Main.getPlugin(), 0, 20 * 120);

    }

    public void save(boolean respectAsync) {
        FileManager.saveConfiguraton(file, config, respectAsync);
    }

    public void setGroup(String uuid, String group, String world) {
        config.set("v2-logout." + uuid + ".group", group);
        config.set("v2-logout." + uuid + ".world", world);
    }

    public String getGroup(String uuid) {
        if (config.contains("v2-logout." + uuid)) {
            return config.getString("v2-logout." + uuid + ".group");
        } else {
            return null;
        }
    }

    public String getWorld(String uuid) {
        if (config.contains("v2-logout." + uuid)) {
            return config.getString("v2-logout." + uuid + ".world");
        } else {
            return null;
        }
    }
}
