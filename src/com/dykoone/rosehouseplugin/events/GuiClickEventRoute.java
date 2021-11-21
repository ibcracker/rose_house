package com.dykoone.rosehouseplugin.events;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GuiClickEventRoute {
    public void clickRoute(InventoryClickEvent event, String code) {
        String prefix = code.charAt(0) + "" + code.charAt(1);
        String suffix = code.charAt(2) + "" + code.charAt(3);
        if (prefix.equals("20")) {
            suffixClick_20(event, suffix);
        }
    }

    private void suffixClick_20(InventoryClickEvent event, String suffix) {
        if (suffix.equals("00")) {
            GameRoomClickEvent clickEvent = new GameRoomClickEvent(event);
            Bukkit.getPluginManager().callEvent(clickEvent);
        }
    }
}
