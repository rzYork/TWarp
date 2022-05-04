package com.tracer0219.twarp.entity;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Warp {

    private Location loc;
    private String name;

    public Warp(String name,Location loc, UnlockRegion region, double money, List<ItemStack> items) {
        this.name=name;
        this.loc = loc;
        this.region = region.clone();
        this.money = money;
        this.items = new ArrayList<ItemStack>();
        if(items!=null)
            this.items.addAll(items);
    }

    private UnlockRegion region;
    private double money;
    private List<ItemStack> items;


    @Override
    public String toString() {
        return "Warp{" +
                "loc=" + loc +
                ", region=" + region +
                ", money=" + money +
                ", items=" + StringUtils.join(items.stream().map(ItemStack::toString).collect(Collectors.toList()).toArray(new String[0]), ",") +
                '}';
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public String getName() {
        return name;
    }


    public UnlockRegion getRegion() {
        return new UnlockRegion(region.x, region.y, region.z);
    }

    public void setRegion(UnlockRegion region) {
        this.region = region;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public List<ItemStack> getItems() {
        return new ArrayList<>(items);
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
    }
}
