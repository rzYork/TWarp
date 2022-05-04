package com.tracer0219.twarp.command;

import com.tracer0219.twarp.TWarp;
import com.tracer0219.twarp.config.IConfigReader;
import com.tracer0219.twarp.entity.UnlockRegion;
import com.tracer0219.twarp.entity.Warp;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

public class WarpCommandExecutor implements CommandExecutor {
    private IConfigReader reader;
    private TWarp plugin;

    private static final UnlockRegion DEFAULT_REGION=new UnlockRegion(16,16,16);
    public WarpCommandExecutor(IConfigReader reader, TWarp plugin) {
        this.reader = reader;
        this.plugin = plugin;
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
                    //TODO open GUI
                    return true;
                } else if (args[0].equalsIgnoreCase("list")) {
                    if(!sender.hasPermission("twarp.admin"))
                    {
                        p.sendMessage("Administrators Only");
                        return true;
                    }
                    for (Warp warp : reader.getWarps()) {
                        p.sendMessage(warp.toString());
                    }
                    return true;
                }
            }else if(args.length==2){
                String warpName=args[1];
                if(args[0].equalsIgnoreCase("target")){
                    if(!sender.hasPermission("twarp.admin"))
                    {
                        p.sendMessage("Administrators Only");
                        return true;
                    }
                    Location loc = p.getLocation();
                    boolean b = reader.setWarpLoc(new Warp(warpName, loc, DEFAULT_REGION, 0, null));
                    sender.sendMessage("successfully "+(b?"modify":"create")+" the warp "+warpName+" target is "+loc.toString()+" and active region is "+DEFAULT_REGION.toString());
                    return true;

                }
               else if(args[0].equalsIgnoreCase("go")){
                    if(!sender.hasPermission("twarp.use")){
                        sender.sendMessage("You have no permission to use this!");
                        return true;
                    }

                    //TODO teleport

                    return true;
                }
            }else if(args.length==3){
                if(args[0].equalsIgnoreCase("require")){
                    String warpName=args[1];
                    String moneyStr=args[2];
                    double money=0;
                    try {
                        money=Double.parseDouble(moneyStr);
                        if(money<0)
                            throw new NumberFormatException();
                    } catch (NumberFormatException e) {
                        p.sendMessage("Invalid money format! please enter the positive number");
                        return true;
                    }
                    reader.setWarpLoc(warpName,
                            Arrays.asList(p.getInventory()  .getContents())
                                    .stream()
                                    .filter(i->i!=null&&i.getType()!= Material.AIR)
                                    .collect(Collectors.toList()), money);
                    return true;

                }
            }
        }
        return false;
    }
}
