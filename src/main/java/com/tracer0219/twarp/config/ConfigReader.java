package com.tracer0219.twarp.config;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.tracer0219.twarp.TWarp;
import com.tracer0219.twarp.entity.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.tracer0219.twarp.command.WarpCommandExecutor.DEFAULT_DISTANCE;

public class ConfigReader implements IConfigReader {
    private static FileConfiguration config;
    private static TWarp instance;
    private RegionQuery query;


    public ConfigReader(TWarp plugin) {
        instance = plugin;
        File file = new File(plugin.getDataFolder(), "config.yml");
        file.mkdir();
        if (!file.exists())
            plugin.saveDefaultConfig();
        config = plugin.getConfig();
        update();

        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        query = regionContainer.createQuery();


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
        int distance = warpSec.getInt("distance",DEFAULT_DISTANCE);
        Location loc = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        double money = warpSec.getDouble("require.money", 0);
        List<ItemStack> stacks = new ArrayList<>();
        ConfigurationSection itemSec = warpSec.getConfigurationSection("require.items");
        if (itemSec != null)
            for (String key : itemSec.getKeys(false)) {
                ItemStack stack = itemSec.getItemStack(key, new ItemStack(Material.AIR));
                stacks.add(stack);
            }

        return new Warp(warpName, loc, distance, money, stacks);
    }
    private List<Warp> warps = new ArrayList<>();
    @Override
    public List<Warp> getWarps() {
        ArrayList<Warp> warpsClones = new ArrayList<>();
        for (Warp warp : warps) {
            warpsClones.add(warp.clone());
        }
        return warpsClones;
    }

    private void update(){
        warps.clear();
        for (String warpName : config.getConfigurationSection("warps").getKeys(false)) {
            warps.add(getWarp(warpName));
        }
    }
    @Override
    public boolean setWarp(Warp warp) {
        boolean exists = false;
        String name = warp.getName();
        List<ItemStack> items = warp.getItems();
        Location loc = warp.getLoc();
        int d = warp.getDistance();
        double money = warp.getMoney();
        ConfigurationSection warpSec;
        exists = (warpSec = config.getConfigurationSection("warps." + name)) != null;

        if (!exists) {
            config.getConfigurationSection("warps.").createSection(name);
            warpSec = config.getConfigurationSection("warps." + name);
        }
        warpSec.set("world", loc.getWorld().getName());
        warpSec.set("x", loc.getX());
        warpSec.set("y", loc.getY());
        warpSec.set("z", loc.getZ());
        warpSec.set("pitch", loc.getPitch());
        warpSec.set("yaw", loc.getYaw());
        warpSec.set("distance", warp.getDistance());
        instance.saveConfig();
        update();
        return exists;

    }

    @Override
    public List<Warp> getAvailableWarps(List<String> regionNames) {
        List<Warp> result = new ArrayList<>();
        HashSet<String> warps = new HashSet<>();
        for (String regionName : regionNames) {
            warps.addAll(config.getStringList("worldguard." + regionName));
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
        List<Warp> result = new ArrayList<>();
        for (String s1 : stringList) {
            Warp warp = getWarp(s1);
            result.add(warp);
        }
        return result;
    }

    @Override
    public boolean unlockWarp(OfflinePlayer p, Warp warp) {
        String s = p.getUniqueId().toString();
        HashSet<String> set = new HashSet<>(config.getStringList("players." + s));
        boolean add = set.add(warp.getName());
        config.set("players." + s, new ArrayList<>(set));
        instance.saveConfig();
        update();
        return add;
    }

    @Override
    public boolean lockWarp(OfflinePlayer p, Warp warp) {
        String s = p.getUniqueId().toString();
        HashSet<String> set = new HashSet<>(config.getStringList("players." + s));
        boolean remove = set.remove(warp.getName());
        config.set("players." + s, new ArrayList<>(set));
        instance.saveConfig();
        update();
        return remove;
    }

    @Override
    public void reload() {
        instance.reloadConfig();
        update();
    }

    @Override
    public boolean setWarpRequire(String warpName, List<ItemStack> stacks, double money) {
        ConfigurationSection warpSec = config.getConfigurationSection("warps." + warpName);
        if (warpSec == null)
            return false;
        warpSec.set("require.money", money);
        warpSec.set("require.items", null);
        for (ItemStack item : stacks) {
            warpSec.set("require.items." + UUID.randomUUID(), item);
        }
        instance.saveConfig();
        update();
        return true;
    }

    @Override
    public boolean hasUnlocked(OfflinePlayer p, String warpName) {
        String s = p.getUniqueId().toString();
        HashSet<String> set = new HashSet<>(config.getStringList("players." + s));
       return set.contains(warpName);
    }

    @Override
    public List<Warp> availableRegions(Location loc) {
        List<Warp> result=new ArrayList<>();
        List<String> resultNames=new ArrayList<>();
        ApplicableRegionSet applicableRegions = query.getApplicableRegions(BukkitAdapter.adapt(loc));
        for (ProtectedRegion region : applicableRegions) {
            resultNames.addAll(config.getStringList("worldguard." + region.getId()));
        }
        return getWarps().stream().filter(w->resultNames.contains(w.getName())).collect(Collectors.toList());
    }
}
