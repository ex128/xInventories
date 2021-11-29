package me.x128.xInventories.command;

import me.x128.xInventories.Main;
import me.x128.xInventories.listener.GamemodeChangeEvent;
import me.x128.xInventories.utils.InventoryInstance;
import me.x128.xInventories.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cole on 4/11/16.
 */
public class xInventoriesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getLabel().equalsIgnoreCase("xInventories")) {
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            if (sender.hasPermission("xinventories.reload")) {
                Main.getPlugin().reloadConfig();
                if (Main.getPlugin().getConfig().getBoolean("respect-gamemode")) {
                    Bukkit.getServer().getPluginManager().registerEvents(new GamemodeChangeEvent(), Main.getPlugin());
                }
                Main.getPlugin().getLogger().info("Configuration reloaded");
                sender.sendMessage("§3[xInventories] §6Plugin configuration successfully reloaded!");
            } else {
                sender.sendMessage("§cYou do not have permission to execute this command!");
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("report")) {
            //generate a bunch of data to send to the user
            if (!sender.hasPermission("xInventories.report")) {
                sender.sendMessage("§cYou do not have permission to execute this command!");
                return true;
            }
            ArrayList<String> arr = new ArrayList<String>();
            arr.add("server version: " + Bukkit.getServer().getVersion());
            arr.add("bukkit version: " + Bukkit.getServer().getBukkitVersion());
            arr.add("system: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " (" + System.getProperty("os.arch") + ")");
            arr.add("java: " + System.getProperty("user.name") + "/" + System.getProperty("java.version"));
            arr.add("xInventories version: " + Main.getPlugin().getDescription().getVersion());

            //generate plugins
            String plugins = "plugins: [";
            for (Plugin p : Bukkit.getServer().getPluginManager().getPlugins()) {
                plugins += p.getName() + ", ";
            }
            plugins = plugins.substring(0, plugins.length() - 2) + "]";
            arr.add(plugins);

            //generate worlds
            String worlds = "worlds: [";
            for (World w : Bukkit.getServer().getWorlds()) {
                worlds += w.getName() + ", ";
            }
            worlds = worlds.substring(0, worlds.length() - 2) + "]";
            arr.add(worlds);

            //send data to user
            sender.sendMessage("§6----------------§3[xInventories Report]§6----------------");
            for (String s : arr) {
                sender.sendMessage(" §7- " + s);
            }
            sender.sendMessage("§6----------------------------------------------------");

        } else if (args.length > 0 && args[0].equalsIgnoreCase("forceload")) {
            //permission check
            if (!sender.hasPermission("xinventories.forceload")) {
                sender.sendMessage("§cYou do not have permission to execute this command!");
                return true;
            }

            if (args.length == 1) {
                sender.sendMessage("§3[xInventories] §6Reset the user's current inventory to the state it was when they last switched groups");
                sender.sendMessage("§cUsage: /xinventories forceload <username>");
            }

            if (args.length == 2) {
                String username = args[1];
                Player target = Bukkit.getServer().getPlayer(username);
                if (target == null) {
                    sender.sendMessage("§3[xInventories] §b" + username + " §6is not online");
                    return true;
                }
                //load the users group and set
                FileConfiguration config = Main.getPlugin().getConfig();
                String groupName = PlayerUtil.getPlayerCurrentGroup(target);
                if (groupName == null) {
                    //world is not in config file
                    sender.sendMessage("§3[xInventories] §6World §b" + target.getWorld() + "§6 is not assigned to a group");
                    return true;
                }

                InventoryInstance newInv = new InventoryInstance(target, groupName, target.getGameMode());
                newInv.append();
                sender.sendMessage("§3[xInventories] §6Successfully reloaded §b" + target.getName() + "'s §6inventory from group §b" + groupName);

            }
        } else {
            String extensions = "";
            for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
                if (pl.getName().startsWith("xInventories-")) {
                    extensions += pl.getName() + ", ";
                }
            }
            if (extensions.length() > 0) {
                extensions = extensions.substring(0, extensions.length() - 2);
            } else {
                extensions = "none";
            }

            List<String> strs = new ArrayList<String>();
            strs.add("§6--------------------§3[xInventories]§6--------------------");
            strs.add(" §9Plugin Author: §bx128");
            strs.add(" §9Plugin Version: §b" + Main.getPlugin().getDescription().getVersion());
            strs.add(" §9Use §b/xinventories reload §9to reload the plugin configuration");
            strs.add(" §9Extensions: §b" + extensions);
            strs.add("§6----------------------------------------------------");
            sender.sendMessage(strs.toArray(new String[0]));
        }
        return true;
    }

}
