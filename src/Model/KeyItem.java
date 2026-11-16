package Model;

public class KeyItem extends Item {
    private String roomId;
    private String doorId;

    // Purpose: Creates a special key that unlocks a specific room or door target.
    public KeyItem(String id, String name, String description, int valueSilver,
                   String roomId, String doorId) {
        super(id, name, description, valueSilver);
        setQuestItem(true);
        this.roomId = roomId;
        this.doorId = doorId;
        setStackable(false);
    }

    // Purpose: Confirms that this item is treated as a key by the inventory and room logic.
    @Override
    public boolean isKey() {
        return true;
    }

    // Purpose: Returns the room ID this key is meant to open.
    public String getRoomId() {
        return roomId;
    }

    // Purpose: Changes the target room for the key when data is updated.
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    // Purpose: Returns the exact door identifier associated with the lock target.
    public String getDoorId() {
        return doorId;
    }

    // Purpose: Updates the locked door ID for the key's unlock target.
    public void setDoorId(String doorId) {
        this.doorId = doorId;
    }

    // Purpose: Formats the key's target as a room:door pair for lock checks.
    public String unlockTarget() {
        return roomId + ":" + doorId;
    }
}

