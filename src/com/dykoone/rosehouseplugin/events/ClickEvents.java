package com.dykoone.rosehouseplugin.events;

import com.dykoone.guikitplugin.util.UtilKit;
import com.dykoone.rosehouseplugin.system.GameManager;
import com.dykoone.rosehouseplugin.system.GameRoom;
import com.dykoone.rosehouseplugin.system.RoomList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

public class ClickEvents implements Listener {
    public ClickEvents(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (event.getClick().equals(ClickType.DOUBLE_CLICK)) {
            event.setCancelled(true);
            if (!UtilKit.isInventoryCode(title))
                event.setCancelled(false);
        }

        /*키보드로 핫바키 사용불가*/
        if (event.getClick().isKeyboardClick()) {
            if (event.getView().getBottomInventory().getItem(event.getHotbarButton()) != null) {
                event.setCancelled(true);
            }
        }
        if (event.getCurrentItem() == null) return;

        if (UtilKit.isInventoryCode(title)) {
            if (title.charAt(1) == '1') event.setCancelled(true);
            new GuiClickEventRoute().clickRoute(event, UtilKit.getInventoryCode(title));
        }
    }

    @EventHandler
    public void gameRoomClick(GameRoomClickEvent event) {
        Player player = event.getPlayer();
        int slot = event.getGlassNumber();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "잔디 번호 : " + slot);

        int startSlot = event.getEvent().getSlot();
        String roomNumber = event.getRoomNumber();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "roomNumber : " + roomNumber);
        GameManager gameManager = new GameManager();
        RoomList roomList = gameManager.onClick(roomNumber);
        GameRoom gameRoom = roomList.getGameRoom();

        if (event.isEventCancel()) {
            if (startSlot == 0) {
                gameRoom.start();
            }
        } else {
            int playerIdx = gameRoom.getPlayerIdx(player);
            event.setItemClickNumber(playerIdx, slot);
        }
    }
}
