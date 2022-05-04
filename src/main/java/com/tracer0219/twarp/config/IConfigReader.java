package com.tracer0219.twarp.config;

import com.tracer0219.twarp.entity.Warp;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IConfigReader {
    int getReleaseTime();
    Warp getWarp(String warpName);
    List<Warp> getWarps();

    /**
     *
     * @param warp
     * @return modify but not create if true
     */
    boolean setWarp(Warp warp);
    List<Warp> getAvailableWarps(List<String> regionNames);
    List<Warp> getUnlockedWarps(OfflinePlayer p);
    boolean unlockWarp(OfflinePlayer p,Warp warp);
    boolean lockWarp(OfflinePlayer p,Warp warp);

    void reload();


    boolean setWarpRequire(String warpName, List<ItemStack> stacks, double money);

    boolean hasUnlocked(OfflinePlayer p,String warpName);

    List<Warp> availableRegions(Location loc);

}
