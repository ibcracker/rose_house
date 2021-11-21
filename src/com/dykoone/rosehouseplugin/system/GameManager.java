package com.dykoone.rosehouseplugin.system;

import com.dykoone.rosehouseplugin.gui.RoomGui;
import com.dykoone.rosehouseplugin.util.ServerUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class GameManager {
    private static int createNumber = 0;
    private static Map<String, RoomList> gameRoomMap = new HashMap<>(); //key : GameRoom Number, value : RoomList info

    //방생성
    public synchronized void create(Player player, String gameName) {
        createNumber++;
        RoomGui roomGui = new RoomGui(player, 0, createNumber);
        roomGui.viewGui();
        Inventory inventory = roomGui.getInventory();
        String roomNumber = String.valueOf(createNumber);
        RoomList roomList = new RoomList(gameName, createNumber).addRoom(inventory);
        gameRoomMap.put(roomNumber, roomList);
    }

    //목록에서 해당방 클릭할때 얻어질 정보
    public RoomList onClick(String roomNumber) {
        return gameRoomMap.get(roomNumber);
    }

    public void onJoin(Player player, String roomNumber) {
        if (gameRoomMap.containsKey(roomNumber)) {
            RoomList roomList = onClick(roomNumber);
            GameRoom gameRoom = roomList.getGameRoom();
            if (roomList.isJoin()) {
                gameRoom.onJoin(player);
            } else {
                player.sendMessage(ServerUtil.getSystemMessage("인원이 가득 찼습니다."));
            }
        } else {
            player.sendMessage(ServerUtil.getSystemMessage("해당 방이 삭제되었습니다."));
        }
    }

    //플레이어가 나갈때 이벤트
    public void onQuit(Player player, String roomNumber) {
        GameRoom gameRoom = onClick(roomNumber).getGameRoom();
        gameRoom.onQuit(player);
        int count = gameRoom.getPlayerSize();
        if (count <= 0) {
            gameRoomMap.remove(roomNumber);
        }
    }

}
