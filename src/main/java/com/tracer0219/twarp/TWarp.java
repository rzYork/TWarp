package com.tracer0219.twarp;

import com.tracer0219.twarp.command.WarpCommandExecutor;
import com.tracer0219.twarp.config.ConfigReader;
import com.tracer0219.twarp.config.IConfigReader;
import com.tracer0219.twarp.eco.VaultSupporter;
import com.tracer0219.twarp.events.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class TWarp extends JavaPlugin {
    private IConfigReader reader;
    private VaultSupporter vault;
    @Override
    public void onEnable() {
        vault=new VaultSupporter(this);
        reader=new ConfigReader(this);
        getCommand("twarp").setExecutor(new WarpCommandExecutor(reader,this,vault));
        getServer().getPluginManager().registerEvents(new PlayerListener(this,reader,vault),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
