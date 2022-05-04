package com.tracer0219.twarp.utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PlayerUtils {
    public static List<ItemStack> takeItemStacks(Player p, List<ItemStack> stacks) {
        List<ItemStack> lack = new ArrayList<>();
        if (p == null || !p.isOnline())
            return stacks;

        for (int i = 0; i < stacks.size(); i++) {
            ItemStack need = stacks.get(i);
            if(need==null)
                continue;;
            ItemStack[] contents = p.getInventory().getContents();
            for (int j = 0; j < contents.length && need.getAmount() > 0; j++) {
                if(contents[j]==null)
                    continue;
                if (need.isSimilar(contents[j])) {
                    if (need.getAmount() >= contents[j].getAmount()) {
                        need.setAmount(need.getAmount() - contents[j].getAmount());
                        contents[j].setAmount(0);
                    } else {
                        contents[j].setAmount(contents[j].getAmount() - need.getAmount());
                        need.setAmount(0);
                    }
                }
            }
            if(need.getAmount()>0){
                lack.add(need);
            }
        }
        return lack;


        /**
         * 遍历所有需要的stacks
         * 拿到第一个需要的stacks
         * 从玩家的inventory的contents里面删除stacks
         * 如果contents里面不够stacks，把不够的stacks加入到lack
         */


    }
}
