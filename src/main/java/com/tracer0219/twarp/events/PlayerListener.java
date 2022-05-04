package com.tracer0219.twarp.events;

import com.tracer0219.twarp.TWarp;
import com.tracer0219.twarp.config.IConfigReader;
import com.tracer0219.twarp.eco.VaultSupporter;
import com.tracer0219.twarp.entity.MyEntry;
import com.tracer0219.twarp.entity.Warp;
import com.tracer0219.twarp.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerListener implements Listener {
    private static HashMap<MyEntry<Player, Warp>, Integer> TeleportQueue = new HashMap<>();
    private VaultSupporter vault;

    public static boolean addPlayer(Player p, Warp w, int t) {
        Integer put = TeleportQueue.put(new MyEntry<>(p, w), t);
        return !(put == null || put <= 0);
    }

    public static void playerMove(Player p) {
        Iterator<Map.Entry<MyEntry<Player, Warp>, Integer>> I =
                TeleportQueue.entrySet().iterator();
        while (I.hasNext()) {
            Map.Entry<MyEntry<Player, Warp>, Integer> e = I.next();
            if (e.getKey().getKey() == p) {
                TeleportQueue.remove(e.getKey());
                p.sendMessage("§e§l§n由于大幅度移动! 传送已终止");
            }
        }
    }


    private TWarp plugin;
    private IConfigReader reader;

    public PlayerListener(TWarp plugin, IConfigReader reader, VaultSupporter vault) {
        this.plugin = plugin;
        this.reader = reader;
        this.vault = vault;

        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<Map.Entry<MyEntry<Player, Warp>, Integer>> I = TeleportQueue.entrySet().iterator();
                while (I.hasNext()) {
                    Map.Entry<MyEntry<Player, Warp>, Integer> E = I.next();
                    if (E.getValue() == null || E.getValue() <= 0) {
                        new RuntimeException("NULL ENTRY ").printStackTrace();
                    }
                    E.setValue(E.getValue() - 1);
                    if (E.getValue() == 0) {
                        MyEntry<Player, Warp> e = E.getKey();
                        preTeleport(e.getKey(), e.getValue());
                        TeleportQueue.remove(E.getKey());
                        continue;
                    }
                    E.getKey().getKey().sendMessage("§c传送§e" + E.getValue() + "§c秒后开始");
                }

            }
        }.runTaskTimer(plugin, 0L, 20);
    }

    public void preTeleport(Player p, Warp warp) {

        Warp clone = warp.clone();
        double money_need = clone.getMoney();
        double balance = vault.balance(p);
        if (balance < money_need) {
            p.sendMessage("§c金币余额不足!");
            return;
        }
        Inventory inventory = Bukkit.createInventory(null, InventoryType.PLAYER, String.valueOf(UUID.randomUUID()));
        inventory.setContents(p.getInventory().getContents().clone());
        List<ItemStack> lackOf = PlayerUtils.takeItemStacks(inventory, clone.getItems());

        lackOf = lackOf.stream().filter(i -> i.getAmount() > 0).collect(Collectors.toList());
        if (!lackOf.isEmpty()) {
            p.sendMessage("§c所需物品不足!");
            StringBuilder builder = new StringBuilder();
            for (ItemStack stack : lackOf) {
                String name = stack.getType().name();
                if (stack.hasItemMeta()) {
                    if (stack.getItemMeta().hasDisplayName()) {
                        name = stack.getItemMeta().getDisplayName();
                    }
                }
                builder.append("§7" + name + " x" + stack.getAmount() + "  ");

            }
            p.sendMessage(builder.toString());
            return;
        }

        clone=warp.clone();
        List<ItemStack> items = clone.getItems();
        for (int i = 0; i < items.size(); i++) {
            items.set(i,items.get(i).clone());
        }
        PlayerUtils.takeItemStacks(p.getInventory(), items);
        p.teleport(clone.getLoc());
        p.sendMessage("§c已经传送至§e" + clone.getName());
        p.sendMessage("§7消耗金币: §b" + clone.getMoney());
        StringBuilder builder = new StringBuilder();
        for (ItemStack stack : clone.getItems()) {
            String name = stack.getType().name();
            if (stack.hasItemMeta()) {
                if (stack.getItemMeta().hasDisplayName()) {
                    name = stack.getItemMeta().getDisplayName();
                }
            }
            builder.append("§7" + name + " x" + stack.getAmount() + "  ");

        }
        p.sendMessage("§7消耗物品: " + builder.toString());
        p.getWorld().playEffect(p.getLocation(), Effect.ENDER_SIGNAL,1,2);
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT,1,1);

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        doPlayerMove(e.getPlayer(), e.getTo());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        doPlayerMove(e.getPlayer(), e.getTo());
    }

    private void doPlayerMove(Player p, Location loc) {
        List<Warp> warps = reader.getWarps();
        for (Warp warp : warps) {
            Location center = warp.getLoc();
            int distance = warp.getDistance();
            if (loc.distance(center) < distance) {
                if (reader.unlockWarp(p, warp)) {
                    p.sendMessage("§e已解锁传送点: §7" + warp.getName());
                }
            }
        }
        if (p.getPlayer().getLocation().getBlockZ() == loc.getBlockZ()
                && p.getPlayer().getLocation().getBlockX() == loc.getBlockX()
                && p.getPlayer().getLocation().getBlockY() == loc.getBlockY())
            return;

        PlayerListener.playerMove(p);
    }


}
