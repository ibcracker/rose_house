package com.dykoone.rosehouseplugin.commands;

import com.dykoone.rosehouseplugin.RoseHousePlugin;
import com.dykoone.rosehouseplugin.gui.RoomGui;
import com.dykoone.rosehouseplugin.system.GameManager;
import com.dykoone.rosehouseplugin.system.GameRoom;
import com.dykoone.rosehouseplugin.system.RoomList;
import com.ibcracker.commandKit.command.YDCommandKits;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class TestCommands extends YDCommandKits<RoseHousePlugin> {
    public TestCommands(RoseHousePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        Player player = (Player) commandSender;
        switch (args[0]) {
            case "room":
                new RoomGui(player, 0, 0).viewGui();
                return true;
            case "start":
                GameManager gameManager = new GameManager();
                gameManager.create(player, "0");
                RoomList roomList = gameManager.onClick("1");
                GameRoom gameRoom = roomList.getGameRoom();
                gameRoom.onJoin(player);
                gameRoom.start();
                return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], getCustomName(), new ArrayList<>());
        }
        return null;
    }

    @Override
    public String getPermissionNode() {
        return null;
    }

    @Override
    public boolean isConsoleFriendly() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    public Collection<String> getCustomName() {
        Set<String> commandName = new HashSet<>();
        commandName.add("room");
        commandName.add("start");
        return commandName;
    }
}
