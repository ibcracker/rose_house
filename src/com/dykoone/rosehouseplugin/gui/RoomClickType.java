package com.dykoone.rosehouseplugin.gui;

public enum RoomClickType {
    LIST(0), GLASS_BOX(1), PLAYER_GOLD_BUTTON(2);

    public final int type;

    RoomClickType(int type) {
        this.type = type;
    }

    public int getRoomClickType() {
        return type;
    }
}
