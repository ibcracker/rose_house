package com.dykoone.rosehouseplugin.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class GameRoomClickEvent extends GameRoomEvent {

    private InventoryClickEvent event;

    public GameRoomClickEvent(InventoryClickEvent event) {
        this.event = event;
    }

    public Player getPlayer() {
        return (Player) event.getWhoClicked();
    }

    public InventoryClickEvent getEvent() {
        return event;
    }

    //TOP 인벤토리 잔디번호 추출
    public int getGlassNumber() {
        int slot = event.getSlot();
        if (slot >= 1 && slot <= 8) {
            return slot;
        } else if (slot >= 10 && slot <= 17) {
            return slot - 1;
        } else if (slot >= 19 && slot <= 26) {
            return slot - 2;
        } else if (slot >= 28 && slot <= 35) {
            return slot - 3;
        } else {
            return -1;
        }
    }

    //게임룸 이벤트 캔슬
    public boolean isEventCancel() {
        Inventory inventory = event.getView().getTopInventory();
        ItemStack itemStack = inventory.getItem(18);
        assert itemStack != null;
        return itemStack.getType().equals(Material.RED_STAINED_GLASS_PANE);
    }

    //클릭타입 (


    //플레이어 잔디 클릭시 머리 아이템개수 변동
    public void setItemClickNumber(int idx, int number) {
        //36 ~ 44
        int slot = 36 + idx;
        Inventory inventory = event.getView().getTopInventory();
        ItemStack itemStack = inventory.getItem(slot);
        if (itemStack != null) {
            itemStack.setAmount(number);
            inventory.setItem(slot, itemStack);
        }
    }

    //방번호
    public String getRoomNumber() {
        Inventory inventory = event.getView().getTopInventory();
        return ChatColor.stripColor(Objects.requireNonNull(Objects.requireNonNull(inventory.getItem(0)).getItemMeta()).getDisplayName());
    }
}
