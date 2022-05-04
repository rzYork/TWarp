package com.tracer0219.twarp.config;

import com.tracer0219.twarp.entity.Warp;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IConfigReader {
    int getReleaseTime();
    Warp getWarp(String warpName);
    List<Warp> getWarps();
    boolean setWarpLoc(Warp warp);
    List<Warp> getAvailableWarps(List<String> regionNames);
    List<Warp> getUnlockedWarps(OfflinePlayer p);
    void unlockWarp(OfflinePlayer p,Warp warp);
    void lockWarp(OfflinePlayer p,Warp warp);

    void reload();

    boolean setWarpLoc(String warpName, List<ItemStack> stacks, double money);
}
