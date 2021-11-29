package me.x128.xInventories.listener;

import me.x128.xInventories.Main;
import me.x128.xInventories.utils.InventoryInstance;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Cole on 4/11/16.
 */
public class WorldChangeEvent implements Listener {
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent ev) {
        Player p = ev.getPlayer();

        //get the new and old worlds and check if they have different groups
        String to = p.getWorld().getName();
        String from = ev.getFrom().getName();
        FileConfiguration config = Main.getPlugin().getConfig();
        if (!(config.contains("worlds." + from) && config.contains("worlds." + to)))
            return;
        String fromGroup = config.getString("worlds." + from);
        String toGroup = config.getString("worlds." + to);
        if (fromGroup.equals(toGroup))
            return;

        //switch player inventory groups
        InventoryInstance fromInv = new InventoryInstance(p);
        fromInv.setGroup(fromGroup);
        fromInv.serialize();
        InventoryInstance toInv = new InventoryInstance(p, toGroup, p.getGameMode());
        toInv.append();
    }
}
