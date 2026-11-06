package Model;

public class GameWorld {
    private String startRoomID ="R01";
    private Map<String, Room> rooms;
    private Map<String, Puzzle> puzzles;

    public Room getRooms(String roomID) {
        return rooms;
    }
}