package com.dykoone.rosehouseplugin.gui;

import com.dykoone.guikitplugin.util.GuiKit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.Arrays;
import java.util.Collections;

public class RoomGui extends GuiKit {

    //gameNumber : 0 = 랜덤 플라워 캐치, 1 = 랜덤 플라워 어마운트
    private String title;
    private int gameNumber;
    private int roomNumber;

    public RoomGui(Player player) {
        super(player);
    }

    public RoomGui(Player player, int gameNumber, int roomNumber) {
        super(player);
        this.gameNumber = gameNumber;
        this.roomNumber = roomNumber;
        if (gameNumber == 0) {
            title = "테이킹 플라워";
        } else {
            title = "카운트 플라워";
        }
    }

    @Override
    public String setPrefixCode() {
        return setUniqueCode("12000r");
    }

    @Override
    public String setNameCode() {
        return setUniqueCode("rose");
    }

    @Override
    public String setGuiName() {
        return ChatColor.BLACK + title;
    }

    @Override
    public InventoryType setInventoryType() {
        return null;
    }

    @Override
    public int setInventoryLine() {
        return 6;
    }

    @Override
    public void setInventory() {
        removeFlagStack(ChatColor.WHITE + "" + roomNumber, Material.PLAYER_HEAD, 1,
                Collections.singletonList(ChatColor.WHITE + title), 0, getInventory());
        removeFlagStack(ChatColor.WHITE + "배수", Material.GOLD_INGOT, 1,
                Arrays.asList(ChatColor.WHITE + "배팅금 X 배수", ChatColor.WHITE + "현재배수 : " + ChatColor.GOLD + "1"), 9, getInventory());
        removeFlagStack(" ", Material.RED_STAINED_GLASS_PANE, 1, null, 18, getInventory());
        removeFlagStack(ChatColor.WHITE + "나가기", Material.OAK_DOOR, 1, null, 27, getInventory());

        for (int i = 0; i < 9; i++) {
            if (i < 8) {
                removeFlagStack(" ", Material.GRASS_BLOCK, 1, null, i + 1, getInventory());
                removeFlagStack(" ", Material.GRASS_BLOCK, 1, null, i + 10, getInventory());
                removeFlagStack(" ", Material.GRASS_BLOCK, 1, null, i + 19, getInventory());
                removeFlagStack(" ", Material.GRASS_BLOCK, 1, null, i + 28, getInventory());
            }
            removeFlagStack(ChatColor.GOLD + "0", Material.GOLD_INGOT, 1, null, i + 45, getInventory());
        }
    }
}
