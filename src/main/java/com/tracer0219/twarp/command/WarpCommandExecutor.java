package com.tracer0219.twarp.command;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.tracer0219.twarp.TWarp;
import com.tracer0219.twarp.config.IConfigReader;
import com.tracer0219.twarp.eco.VaultSupporter;
import com.tracer0219.twarp.entity.Warp;
import com.tracer0219.twarp.events.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class WarpCommandExecutor implements CommandExecutor {
    private IConfigReader reader;
    private TWarp plugin;

    private VaultSupporter vault;
    public static final int DEFAULT_DISTANCE = 16;


    public WarpCommandExecutor(IConfigReader reader, TWarp plugin,VaultSupporter vault) {
        this.reader = reader;
        this.plugin = plugin;
        this. vault = vault;

    }

    private ChestGui createGUI(Player p) {
        ChestGui gui = new ChestGui(6, "当前位置可用传送点");
        PaginatedPane pages = new PaginatedPane(0, 0, 9, 6);
        List<GuiItem> warpItems = new ArrayList<>();
        List<Warp> warps = reader.availableRegions(p.getLocation());
        for (Warp warp : warps) {
            boolean unlocked = reader.hasUnlocked(p, warp.getName());
            ItemStack stack = new ItemStack(unlocked ? Material.GREEN_WOOL : Material.RED_WOOL);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName("§" + (unlocked ? "b" : "c") + warp.getName());
            List<String> lore = new ArrayList<>();
            lore.add("§7传送花费金币: §e" + warp.getMoney());
            if (vault.balance(p) < warp.getMoney()) {
                lore.add("§7 -- (您的金币不足以传送)");
            }

            lore.add("§7传送消耗物品:");
            for (ItemStack item : warp.getItems()) {
                String name = item.getType().name();
                if (item.hasItemMeta()) {
                    if (item.getItemMeta().hasDisplayName()) {
                        name = item.getItemMeta().getDisplayName();
                    }
                }
                lore.add("§7 -- " + name+" §7§lx§e"+item.getAmount());
            }
            lore.add("§4§l<左键开始传送>");
            lore.add("§7传送有" + reader.getReleaseTime() + "秒前摇");
            meta.setLore(lore);
            stack.setItemMeta(meta);
            GuiItem item = new GuiItem(stack, e -> {
                p.closeInventory();
                e.setCancelled(true);
                switch (e.getClick()) {
                    case LEFT:
                    case SHIFT_LEFT:
                        Bukkit.dispatchCommand(p, "twarp go " + warp.getName());
                        break;
                }
            });
            warpItems.add(item);
        }
        pages.populateWithGuiItems(warpItems);
        gui.addPane(pages);
        return gui;
    }

    private void openGUI(Player p) {
        createGUI(p).show(p);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("twarp")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("player only");
                return true;
            }
            Player p = (Player) sender;
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("gui")) {
                    if (!p.hasPermission("twarp.use")) {
                        sender.sendMessage("no permission!");
                        return true;
                    }
                    openGUI(p);
                    return true;
                } else if (args[0].equalsIgnoreCase("list")) {
                    if (!sender.hasPermission("twarp.admin")) {
                        p.sendMessage("Administrators Only");
                        return true;
                    }
                    for (Warp warp : reader.getWarps()) {
                        p.sendMessage(warp.toString());
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (!sender.hasPermission("twarp.admin")) {
                        p.sendMessage("Administrators Only");
                        return true;
                    }
                    reader.reload();
                    sender.sendMessage("reload successfully!");
                    return true;
                }
            } else if (args.length == 2) {
                String warpName = args[1];
                if (args[0].equalsIgnoreCase("target")) {
                    if (!sender.hasPermission("twarp.admin")) {
                        p.sendMessage("Administrators Only");
                        return true;
                    }
                    Location loc = p.getLocation();
                    boolean b = reader.setWarp(new Warp(warpName, loc, DEFAULT_DISTANCE, 0, null));
                    sender.sendMessage("successfully " + (b ? "modify" : "create") + " the warp " + warpName + " target is " + loc.toString() + " and active distance is " + DEFAULT_DISTANCE);
                    return true;
                } else if (args[0].equalsIgnoreCase("go")) {
                    if (!sender.hasPermission("twarp.use")) {
                        sender.sendMessage("You have no permission to use this!");
                        return true;
                    }
                    if (!reader.hasUnlocked(p, warpName)) {
                        p.sendMessage("§c未解锁!");
                        return true;
                    }
                    List<Warp> warps = reader.availableRegions(p.getLocation());
                    boolean allow=false;
                    Warp target = null;
                    for (Warp warp : warps) {
                        if(warp.getName().equals(warpName)){
                            allow=true;
                            target=warp;
                            break;
                        }
                    }
                    if(!allow){
                        p.sendMessage("§c当前不在传送点可用区!!");
                        return true;
                    }
                    if(target==null){
                        new RuntimeException("NULL WARP!").printStackTrace();
                        return true;
                    }
                    PlayerListener.addPlayer(p,target,reader.getReleaseTime());

                    return true;
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("require")) {
                    String warpName = args[1];
                    String moneyStr = args[2];
                    double money;
                    try {
                        money = Double.parseDouble(moneyStr);
                        if (money < 0)
                            throw new NumberFormatException();
                    } catch (NumberFormatException e) {
                        p.sendMessage("Invalid money format! please enter the positive number");
                        return true;
                    }
                    if (!reader.setWarpRequire(warpName,
                            Arrays.asList(p.getInventory().getContents())
                                    .stream()
                                    .filter(i -> i != null && i.getType() != Material.AIR)
                                    .collect(Collectors.toList()), money)) {
                        p.sendMessage("The warp does not exist, please use '/twarp target' create it first!");
                    }
                    return true;
                }
                if (args[0].contains("lock")) {
                    String warpName = args[1];
                    String pName = args[2];
                    if (!p.hasPermission("twarp.admin")) {
                        p.sendMessage("Administrators Only!");
                        return true;
                    }
                    Warp warp = reader.getWarp(warpName);
                    if (warp == null) {
                        p.sendMessage("warp does not exist");
                        return true;
                    }
                    Player target = Bukkit.getPlayer(pName);
                    if (target == null || !target.isOnline()) {
                        p.sendMessage("invalid player or offline!");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("lock")) {

                        reader.lockWarp(target, warp);
                        sender.sendMessage("Has locked the using permission of " + warpName + " for " + p.getName());

                    } else if (args[0].equalsIgnoreCase("unlock")) {
                        reader.unlockWarp(target, warp);
                        sender.sendMessage("Has unlocked the using permission of " + warpName + " for " + p.getName());

                    }
                    return true;
                }
            }
        }
        return false;
    }
}
