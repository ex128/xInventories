package me.x128.xInventories.utils;

import me.x128.xInventories.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by Cole on 4/11/16.
 */
public class FileManager {
    public static FileConfiguration getYaml(File f) {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
        return fc;
    }

    public static void saveConfiguraton(final File f, final FileConfiguration fc, boolean respectAsync) {
        /**
         We now have two methods of saving, user configurable.
         The regular way (and the safest way) is to save syncronously.
         This new method of async saving was added in 2.4 to appease
         someone unsatisfied with syncronous performance.

         Option for async override is used only for saving the player logout file when plugin is disabled.
         **/

        if (!Main.getPlugin().getConfig().getBoolean("save-async") || !respectAsync) {
            //if config option is not set or false, we save normally. also allows async override
            try {
                fc.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //async-save is TRUE. Save config files asyncronously
            Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    try {
                        fc.save(f);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Main.getPlugin().getLogger().info(
                                "An xInventories error occured while trying to save file (" + f.getPath() + "["+ f.getName() + "]" + ")");
                    }
                }
            });
        }
    }
}
