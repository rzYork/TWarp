package com.tracer0219.twarp.command;

import com.tracer0219.twarp.TWarp;
import com.tracer0219.twarp.config.ConfigReader;
import com.tracer0219.twarp.config.IConfigReader;
import com.tracer0219.twarp.entity.Warp;
import com.tracer0219.twarp.utils.PlayerUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class WarpExecutor implements CommandExecutor {
    private IConfigReader reader;
    private TWarp plugin;

    public WarpExecutor(IConfigReader reader, TWarp plugin) {
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
                if(args[0].equalsIgnoreCase("set")){
                    if(!sender.hasPermission("twarp.admin"))
                    {
                        p.sendMessage("Administrators Only");
                        return true;
                    }
                    Location loc = p.getLocation();


                }
                else if(args[0].equalsIgnoreCase("item")){

                    if(!sender.hasPermission("twarp.admin"))
                    {
                        p.sendMessage("Administrators Only");
                        return true;
                    }
                }
            }

        }
        return false;
    }
}
