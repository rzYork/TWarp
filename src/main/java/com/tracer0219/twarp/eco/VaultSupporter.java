package com.tracer0219.twarp.eco;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class VaultSupporter implements IEcoSupporter {

    private static Economy econ = null;

    public VaultSupporter(JavaPlugin plugin) {
        if (!setupEconomy()) {
            getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().info("null vault");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().info("null rsp");
            return false;
        }
        econ = rsp.getProvider();
        if(econ==null)
            getLogger().info("null econ");
        return (econ != null);
    }

    @Override
    public void deposit(OfflinePlayer p, double amount) {
        econ.depositPlayer(p, amount);
    }

    @Override
    public void withdraw(OfflinePlayer p, double amount) {
        econ.withdrawPlayer(p, amount);
    }

    @Override
    public double balance(OfflinePlayer p){
        return econ.getBalance(p);
    }




}
