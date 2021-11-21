package com.dykoone.rosehouseplugin.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameRoomEvent extends Event implements Cancellable {
    private boolean isCancel = false;

    @Override
    public boolean isCancelled() {
        return isCancel;
    }

    @Override
    public void setCancelled(boolean isCancel) {
        this.isCancel = isCancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private static final HandlerList handlers = new HandlerList();
}
