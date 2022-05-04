package com.tracer0219.twarp.entity;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Warp {

    @Override
    public Warp clone() {
        List<ItemStack> clones=new ArrayList<>();
        for (ItemStack item : items) {
            clones.add(item.clone());
        }
        return new Warp(name,loc.clone(),distance,money,clones);
    }

    private Location loc;
    private String name;

    public Warp(String name,Location loc, int distance, double money, List<ItemStack> items) {
        this.name=name;
        this.loc = loc;
        this.distance=distance;
        this.money = money;
        this.items = new ArrayList<ItemStack>();
        if(items!=null)
            this.items.addAll(items);
    }

    private int distance;

    public int getDistance() {
        return distance;
    }

    private double money;
    private List<ItemStack> items;


    @Override
    public String toString() {
        return "Warp{" +
                "loc=" + loc +
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
