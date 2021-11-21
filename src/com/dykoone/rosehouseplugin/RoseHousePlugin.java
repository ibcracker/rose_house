package com.dykoone.rosehouseplugin;

import com.dykoone.rosehouseplugin.commands.TestCommands;
import com.dykoone.rosehouseplugin.events.ClickEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class RoseHousePlugin extends JavaPlugin {

    private static RoseHousePlugin INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        new ClickEvents(this);
        registerCommand("rose", new TestCommands(this));
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "RoseHouse ON!");
    }

    @Override
    public void onDisable() {
        INSTANCE = null;
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "RoseHouse ON!");
    }

    public static RoseHousePlugin getInstance() {
        return INSTANCE;
    }

    private <T extends CommandExecutor & TabExecutor> void registerCommand(String commandName, T handler) {
        getCommand(commandName).setExecutor(handler);
        getCommand(commandName).setTabCompleter(handler);
    }
}
