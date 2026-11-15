package Kelvin;

public class KeyItem extends Item {
    private String roomId;
    private String doorId;

    public KeyItem(String id, String name, String description, int valueSilver,
                   String roomId, String doorId) {
        super(id, name, description, valueSilver);
        setQuestItem(true);
        this.roomId = roomId;
        this.doorId = doorId;
        setStackable(false);
    }

    @Override
    public boolean isKey() {
        return true;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getDoorId() {
        return doorId;
    }

    public void setDoorId(String doorId) {
        this.doorId = doorId;
    }

    public String unlockTarget() {
        return roomId + ":" + doorId;
    }
}