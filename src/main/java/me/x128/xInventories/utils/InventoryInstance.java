package me.x128.xInventories.utils;

import com.elmakers.mine.bukkit.api.magic.Mage;
import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.elmakers.mine.bukkit.api.wand.Wand;
import me.x128.xInventories.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Cole on 4/11/16.
 */
public class InventoryInstance {
    private Player p;
    private String group;
    private String gamemode;

    private Inventory inv;
    private Inventory ender;

    //armor contents
    private ItemStack helm;
    private ItemStack chest;
    private ItemStack pants;
    private ItemStack boots;

    private double health;
    private int hunger;
    private float exp;
    private int expLevel;
    private int remainingAir;
    private int fireTicks;
    private float saturation;
    private float exhaustion;

    private ArrayList<PotionEffect> potions;


    public InventoryInstance(Player p) {
        this.p = p;

        // If Magic is installed, make sure the wand inventory gets closed when changing groups
        MagicAPI magic = Main.getMagicAPI();
        if (magic != null) {
            Mage mage = magic.getMage(p);
            Wand activeWand = mage.getActiveWand();
            if (activeWand != null) {
                activeWand.closeInventory();
            }
        }

        this.group = PlayerUtil.getPlayerCurrentGroup(p);
        inv = Bukkit.getServer().createInventory(null, InventoryType.PLAYER);
        ender = Bukkit.getServer().createInventory(null, InventoryType.ENDER_CHEST);
        potions = new ArrayList<PotionEffect>();

        //save mode
        String mode = "survival";
        if (Main.getPlugin().getConfig().getBoolean("respect-gamemode") && p.getGameMode() == GameMode.CREATIVE) {
            mode = "creative";
        }
        gamemode = mode;

        //load inv
        int i = 0;
        for (ItemStack is : p.getInventory().getContents()) {
            inv.setItem(i, deNull(is));
            i++;
        }

        //load enderchest
        i = 0;
        for (ItemStack is : p.getEnderChest().getContents()) {
            ender.setItem(i, deNull(is));
            i++;
        }

        //load armor contents
        helm = p.getInventory().getHelmet();
        chest = p.getInventory().getChestplate();
        pants = p.getInventory().getLeggings();
        boots = p.getInventory().getBoots();

        //load potions
        for (PotionEffect pe : p.getActivePotionEffects()) {
            potions.add(pe);
        }

        health = p.getHealth();
        hunger = p.getFoodLevel();
        exp = p.getExp();
        expLevel = p.getLevel();
        remainingAir = p.getRemainingAir();
        fireTicks = p.getFireTicks();
        saturation = p.getSaturation();
        exhaustion = p.getExhaustion();

    }

    public InventoryInstance(Player p, String group, GameMode gm) {
        this.p = p;
        this.group = group;
        inv = Bukkit.getServer().createInventory(null, InventoryType.PLAYER);
        ender = Bukkit.getServer().createInventory(null, InventoryType.ENDER_CHEST);
        potions = new ArrayList<PotionEffect>();

        //detect gamemode
        String mode = "survival";
        if (Main.getPlugin().getConfig().getBoolean("respect-gamemode") && gm == GameMode.CREATIVE) {
            mode = "creative";
        }
        gamemode = mode;

        //load the inventory file into memory
        File file = new File(Main.getPlugin().getDataFolder() + File.separator + "groups" + File.separator + group
                + File.separator + mode + File.separator + p.getUniqueId() + ".yml");
        FileConfiguration config = FileManager.getYaml(file);

        //load inventory
        if (config.contains("inventory")) {
            for (String s: config.getConfigurationSection("inventory").getKeys(false)) {
                try {
                    ItemStack is = parseItem(config, "inventory." + s);
                    inv.setItem(Integer.parseInt(s), is);
                } catch (Exception e) {
                    e.printStackTrace();
                    Main.getPlugin().getLogger().info("xInventories encountered an error loading an item " +
                            "(MAIN_" + p.getName() + "_" + group + "_" + gamemode);
                }
            }
        }

        //load ender chest
        if (config.contains("ender_chest")) {
            for (String s: config.getConfigurationSection("ender_chest").getKeys(false)) {
                try {
                    ender.setItem(Integer.parseInt(s), parseItem(config, "ender_chest." + s));
                } catch (Exception e) {
                    e.printStackTrace();
                    Main.getPlugin().getLogger().info("xInventories encountered an error loading an item " +
                            "(ENDER_" + p.getName() + "_" + group + "_" + gamemode);
                }
            }
        }

        //load potion effects
        if (config.contains("potion_effect")) {
            potions = new ArrayList<PotionEffect>();
            for (String s : config.getConfigurationSection("potion_effect").getKeys(false)) {
                try {
                    PotionEffect effect = new PotionEffect(PotionEffectType.getByName(config.getString("potion_effect."
                            + s + ".type")), config.getInt("potion_effect." + s + ".duration"), config.getInt("potion_effect." + s + ".level"));
                    potions.add(effect);
                } catch (Exception e) {
                    e.printStackTrace();
                    Main.getPlugin().getLogger().info("xInventories encountered an error loading an item " +
                            "(POTION_" + p.getName() + "_" + group + "_" + gamemode);
                }
            }
        }

        //load armor contents
        if (config.contains("armor_contents")) {
            helm = parseItem(config, "armor_contents.helmet");
            chest = parseItem(config, "armor_contents.chestplate");
            pants = parseItem(config, "armor_contents.leggings");
            boots = parseItem(config, "armor_contents.boots");
        }

        //load health and hunger
        if (config.contains("health")) {
            health = config.getDouble("health");
            hunger = config.getInt("hunger");
        } else {
            health = p.getMaxHealth();
            hunger = 20;
        }

        //load experience
        if (config.contains("exp") && !config.contains("exp-level")) {
            int total = config.getInt("exp");
            p.setTotalExperience(total);
            p.setLevel(0);
            p.setExp(0);
            for(;total > p.getExpToLevel();)
            {
                total -= p.getExpToLevel();
                p.setLevel(p.getLevel()+1);
            }
            float xp = (float)total / (float)p.getExpToLevel();
            exp = xp;
        } else if (config.contains("exp-level") && config.contains("exp")) {
            exp = (float) config.getDouble("exp");
            expLevel = config.getInt("exp-level");
        } else if (config.contains("exp-level") && !config.contains("exp")) {
           expLevel = config.getInt("exp-level");
        }
        if (config.contains("remainingAir")) {
            remainingAir = config.getInt("remainingAir");
        }
        if (config.contains("fireTicks")) {
            fireTicks = config.getInt("fireTicks");
        }
        if (config.contains("saturation")) {
            saturation = (float) config.getDouble("saturation");
        }
        if (config.contains("exhaustion")) {
            exhaustion = (float) config.getDouble("exhaustion");
        }
    }

    public void serialize() {
        //load file
        File file = new File(Main.getPlugin().getDataFolder() + File.separator + "groups" + File.separator + group
                + File.separator + gamemode + File.separator + p.getUniqueId() + ".yml");
        FileConfiguration config = FileManager.getYaml(file);

        //serialize inv
        int pos = 0;
        for (ItemStack is : inv.getContents()) {

            config.set("inventory." + pos, is);
            pos ++;
        }

        //serialize ender
        pos = 0;
        for (ItemStack is : ender.getContents()) {
            config.set("ender_chest." + pos, is);
            pos ++;
        }

        //save armor
        config.set("armor_contents.helmet", helm);
        config.set("armor_contents.chestplate", chest);
        config.set("armor_contents.leggings", pants);
        config.set("armor_contents.boots", boots);

        //save location
        config.set("location", p.getLocation());

        //save potions
        pos = 0;
        config.set("potion_effect", null);
        for (PotionEffect pe : potions) {
            config.set("potion_effect." + pos + ".type", pe.getType().getName());
            config.set("potion_effect." + pos + ".level", pe.getAmplifier());
            config.set("potion_effect." + pos + ".duration", pe.getDuration());
            pos ++;
        }

        //save other data
        config.set("health", health);
        config.set("hunger", hunger);
        config.set("exp", exp);
        config.set("exp-level", expLevel);
        config.set("remainingAir", remainingAir);
        config.set("fireTicks", fireTicks);
        config.set("saturation", saturation);
        config.set("exhaustion", exhaustion);

        FileManager.saveConfiguraton(file, config, true);
    }

    public void append() {
        //delete users stuff
        p.getInventory().setHelmet(new ItemStack(Material.AIR));
        p.getInventory().setChestplate(new ItemStack(Material.AIR));
        p.getInventory().setLeggings(new ItemStack(Material.AIR));
        p.getInventory().setBoots(new ItemStack(Material.AIR));
        p.getInventory().clear();
        p.getEnderChest().clear();
        for (PotionEffect pe : p.getActivePotionEffects()) {
            p.removePotionEffect(pe.getType());
        }

        //load inventory
        for (int i = 0; i < inv.getSize(); i++) {
            p.getInventory().setItem(i, inv.getItem(i));
        }

        //load ender
        for (int i = 0; i < ender.getSize(); i++) {
            p.getEnderChest().setItem(i, ender.getItem(i));
        }

        //load armor contents
        p.getInventory().setHelmet(helm);
        p.getInventory().setChestplate(chest);
        p.getInventory().setLeggings(pants);
        p.getInventory().setBoots(boots);

        //load potions
        for (PotionEffect pe : potions) {
            p.addPotionEffect(pe);
        }

        //load other stuff
        p.setExp(exp);
        p.setLevel(expLevel);
        try {
            if (health > 0) {
                p.setHealth(health);
            } else {
                p.setHealth(p.getMaxHealth());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        p.setFoodLevel(hunger);
        p.setRemainingAir(remainingAir);
        p.setFireTicks(fireTicks);
        p.setSaturation(saturation);
        p.setExhaustion(exhaustion);
    }

    public void setGroup(String group) {
        this.group = group;
    }

    private ItemStack parseItem(FileConfiguration conf, String path) {
        ItemStack res = new ItemStack(Material.AIR);
        try {
            res = conf.getItemStack(path);
        } catch (Exception e) {
            e.printStackTrace();
            Main.getPlugin().getLogger().warning("Encountered an error while parsing item: " + conf.get(path));
        }
        return res;
    }

    private ItemStack deNull(ItemStack is) {
        if (is == null) {
            return new ItemStack(Material.AIR);
        }
        return is;
    }

    //API METHODS FOR THE FAMS
    public Inventory getInventory() {
        return inv;
    }

}
