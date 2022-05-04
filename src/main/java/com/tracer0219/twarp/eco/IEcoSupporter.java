package com.tracer0219.twarp.eco;

import org.bukkit.OfflinePlayer;

public interface IEcoSupporter {
    void deposit(OfflinePlayer p, double amount);

    void withdraw(OfflinePlayer p, double amount);

    double balance(OfflinePlayer p);


}
