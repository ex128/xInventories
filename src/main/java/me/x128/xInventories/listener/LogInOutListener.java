package me.x128.xInventories.listener;

import me.x128.xInventories.Main;
import me.x128.xInventories.utils.InventoryInstance;
import me.x128.xInventories.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

/**
 * Created by Cole on 4/26/16.
 *
 * Designed to save a player's logout group to file, and check it when he logs back in
 * if the world differs, save his current inventory to the old group and load the new inventory from file
 */
public class LogInOutListener implements Listener {

    @EventHandler
    public void onLogout(PlayerQuitEvent ev) {
        String uuid = ev.getPlayer().getUniqueId().toString();
        String group = PlayerUtil.getPlayerCurrentGroup(ev.getPlayer());
        Main.getLogoutGroup().setGroup(uuid, group, ev.getPlayer().getWorld().getName());
        //Main.getLogoutGroup().save();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent ev) {
        Player p = ev.getPlayer();
        String uuid = p.getUniqueId().toString();

        String prevGroup = Main.getLogoutGroup().getGroup(uuid);
        String curGroup = PlayerUtil.getPlayerCurrentGroup(p);

        String prevWorld = Main.getLogoutGroup().getWorld(uuid);
        String curWorld = p.getWorld().getName();

        if ((prevGroup != null && !curGroup.equalsIgnoreCase(prevGroup)) && (prevWorld != null && !curWorld.equalsIgnoreCase(prevWorld))) {
            if (Bukkit.getWorld(prevWorld) != null) {
                return;
            }
            Main.getPlugin().getConfig().getMapList("worlds");
            Main.getPlugin().getLogger().info(p.getName() + " has logged into an unloaded world");

            //switch player inventory groups
            InventoryInstance fromInv = new InventoryInstance(p);
            fromInv.setGroup(prevGroup);
            fromInv.serialize();
            InventoryInstance toInv = new InventoryInstance(p, curGroup, p.getGameMode());
            toInv.append();
        }
    }
}
