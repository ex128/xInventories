package me.x128.xInventories.command;

import me.x128.xInventories.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by cole on 6/11/17.
 */
public class xTpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getLabel().equalsIgnoreCase("xtp")) {
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cConsole may not use this command");
            return true;
        }
        Player p = (Player)sender;
        if (!(args.length == 1)) {
            p.sendMessage("§c/xtp <world>");
            return true;
        }
        String world = args[0];
        if (!p.hasPermission("xinventories.xtp."+ world)) {
            p.sendMessage("§cYou do not have permission to teleport to this world!");
            return true;
        }
        if (Main.getPlugin().getServer().getWorld(world) == null) {
            p.sendMessage("§cThe world " + world + " does not exist!");
            return true;
        }

        //ok everything looks good, let's execute the command now
        p.sendMessage("test");
        return true;
    }
}
