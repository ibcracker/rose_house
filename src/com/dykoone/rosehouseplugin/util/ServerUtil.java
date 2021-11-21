package com.dykoone.rosehouseplugin.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Random;

public class ServerUtil {

    public static ItemStack getPlayerSkull(Player player) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        assert skullMeta != null;
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
        skullMeta.setDisplayName(ChatColor.AQUA + player.getName());
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

    public static String getSystemMessage(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("§f§l[ §6§l알림 §f§l]");
        stringBuffer.append(" ");
        stringBuffer.append(ChatColor.GREEN + string);
        return stringBuffer.toString();
    }

    public static int randomNumber(int maxInt) {
        return new Random().nextInt(maxInt) + 1;
    }
}
