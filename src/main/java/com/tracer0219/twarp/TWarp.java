package com.tracer0219.twarp;

import com.tracer0219.twarp.command.WarpCommandExecutor;
import com.tracer0219.twarp.config.ConfigReader;
import com.tracer0219.twarp.config.IConfigReader;
import org.bukkit.plugin.java.JavaPlugin;

public final class TWarp extends JavaPlugin {
    private IConfigReader reader;
    @Override
    public void onEnable() {
        reader=new ConfigReader(this);
        getCommand("twarp").setExecutor(new WarpCommandExecutor(reader,this));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
