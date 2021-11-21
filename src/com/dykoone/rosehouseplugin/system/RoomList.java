package com.dykoone.rosehouseplugin.system;

import org.bukkit.inventory.Inventory;

public class RoomList {
    private String gameName;
    private int roomNumber;
    private boolean isJoin;
    private GameRoom gameRoom;

    public RoomList(String gameName, int roomNumber) {
        this.gameName = gameName;
        this.roomNumber = roomNumber;
    }

    public RoomList addRoom(Inventory inventory) {
        gameRoom = new GameRoom().setGameRoomInventory(inventory);
        return this;
    }

    //플레이어 입장 가능 인원 9명 ( 9명 일경우 입장불가 return : false  )
    public boolean isJoin() {
        int playerSize = gameRoom.getPlayerSize();
        return playerSize < 9;
    }

    public String getGameName() {
        return gameName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public GameRoom getGameRoom() {
        return gameRoom;
    }
}
