package com.tracer0219.twarp.config;

import com.tracer0219.twarp.TWarp;
import com.tracer0219.twarp.entity.UnlockRegion;
import com.tracer0219.twarp.entity.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ConfigReader implements IConfigReader {
    private static FileConfiguration config;
    private static TWarp instance;

    public ConfigReader(TWarp plugin) {
        instance = plugin;
        File file = new File(plugin.getDataFolder(), "config.yml");
        file.mkdir();
        if (!file.exists())
            plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    @Override
    public int getReleaseTime() {
        return config.getInt("settings.release_time");
    }

    @Override
    public Warp getWarp(String warpName) {
        ConfigurationSection warpSec = config.getConfigurationSection("warps." + warpName);
        if (warpSec == null) return null;
        String world = warpSec.getString("world");
        double x = warpSec.getDouble("x");
        double y = warpSec.getDouble("y");
        double z = warpSec.getDouble("z");
        float pitch = (float) warpSec.getDouble("pitch");
        float yaw = (float) warpSec.getDouble("yaw");
        int x_length = warpSec.getInt("x_length");
        int y_length = warpSec.getInt("y_length");
        int z_length = warpSec.getInt("z_length");
        Location loc = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        UnlockRegion region = new UnlockRegion(x_length, y_length, z_length);
        double money = warpSec.getDouble("require.money", 0);
        List<ItemStack> stacks = new ArrayList<>();
        ConfigurationSection itemSec = warpSec.getConfigurationSection("require.items");
        for (String key : itemSec.getKeys(false)) {
            ItemStack stack = itemSec.getItemStack(key, new ItemStack(Material.AIR));
            stacks.add(stack);
        }

        return new Warp(warpName, loc, region, money, stacks);
    }

    @Override
    public List<Warp> getWarps() {
        List<Warp> warps = new ArrayList<>();
        for (String warpName : config.getConfigurationSection("warps").getKeys(false)) {
            warps.add(getWarp(warpName));
        }
        return warps;
    }

    @Override
    public boolean setWarpLoc(Warp warp) {
        boolean exists = false;
        String name = warp.getName();
        List<ItemStack> items = warp.getItems();
        Location loc = warp.getLoc();
        UnlockRegion region = warp.getRegion();
        double money = warp.getMoney();

        ConfigurationSection warpSec;
        exists = (warpSec = config.getConfigurationSection("warps." + name)) != null;

        warpSec.set("world", loc.getWorld().getName());
        warpSec.set("x", loc.getX());

        warpSec.set("y", loc.getY());

        warpSec.set("z", loc.getZ());

        warpSec.set("pitch", loc.getPitch());

        warpSec.set("yaw", loc.getYaw());

        warpSec.set("x_length", region.getX());

        warpSec.set("y_length", region.getY());

        warpSec.set("z_length", region.getZ());


        instance.saveConfig();


        return exists;

    }

    @Override
    public List<Warp> getAvailableWarps(List<String> regionNames) {
        List<Warp> result=new ArrayList<>();
        HashSet<String> warps=new HashSet<>();
        for (String regionName : regionNames) {
            warps.addAll(config.getStringList("worldguard."+regionName));
        }

        for (String warp : warps) {
            result.add(getWarp(warp));
        }

        return result;
    }

    @Override
    public List<Warp> getUnlockedWarps(OfflinePlayer p) {
        String s = p.getUniqueId().toString();
        List<String> stringList = config.getStringList("players." + s);
        List<Warp> result=new ArrayList<>();
        for (String s1 : stringList) {
            Warp warp = getWarp(s1);
            result.add(warp);
        }
        return result;
    }

    @Override
    public void unlockWarp(OfflinePlayer p, Warp warp) {
        String s = p.getUniqueId().toString();
        HashSet<String> set = new HashSet<>(config.getStringList("players." + s));
        set.add(warp.getName());
        config.set("players."+s,new ArrayList<>(set));
        instance.saveConfig();
    }

    @Override
    public void lockWarp(OfflinePlayer p, Warp warp) {
        String s = p.getUniqueId().toString();
        HashSet<String> set = new HashSet<>(config.getStringList("players." + s));
        set.remove(warp.getName());
        config.set("players."+s,new ArrayList<>(set));
        instance.saveConfig();
    }

    @Override
    public void reload() {
        instance.reloadConfig();
    }

    @Override
    public boolean setWarpLoc(String warpName, List<ItemStack> stacks, double money) {
        ConfigurationSection warpSec=config.getConfigurationSection("warps."+warpName);
        if(warpSec==null)
            return false;
        warpSec.set("require.money",money);

        for (ItemStack item : stacks) {
            warpSec.set("require.items."+item.hashCode(),item);
        }
        instance.saveConfig();
        return true;
    }
}
