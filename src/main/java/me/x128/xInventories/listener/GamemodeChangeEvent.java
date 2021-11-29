package me.x128.xInventories.listener;

import me.x128.xInventories.Main;
import me.x128.xInventories.utils.InventoryInstance;
import me.x128.xInventories.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

/**
 * Created by Cole on 4/11/16.
 */

public class GamemodeChangeEvent implements Listener {
    @EventHandler (priority = EventPriority.MONITOR)
    public void onGamemode(PlayerGameModeChangeEvent ev) {
        Player p = ev.getPlayer();
        if (!Main.getPlugin().getConfig().getBoolean("respect-gamemode")) {
            return;
        }

        if (!ev.isCancelled()) {
            if ((ev.getNewGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.CREATIVE) && ev.getNewGameMode() != p.getGameMode()) {
                InventoryInstance current = new InventoryInstance(p);
                InventoryInstance newInv = new InventoryInstance(p, PlayerUtil.getPlayerCurrentGroup(p), ev.getNewGameMode());
                current.serialize();
                newInv.append();
            }
        }
    }
}
