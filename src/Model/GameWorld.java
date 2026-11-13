package Model;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class GameWorld {

    private String startRoomID ="R01";

    private List<Room> rooms;
    private List<Enemy> enemies;
    private List<Item> items;
    private List<NPC> npcs;
    private List<Puzzle> puzzles;

    private Random random;

    public GameWorld() {
        rooms = new ArrayList<>();
        enemies = new ArrayList<>();
        items = new ArrayList<>();
        npcs = new ArrayList<>();
        puzzles = new ArrayList<>();

        random = new Random();
    }

    // Room Methods
    public Room getRoomByID(String roomID) {

        if (roomID == null) {
            return null;
        }
        for (Room room : rooms) {
            if (room.getRoomID().equals(roomID)) {
                return room;
            }
        }
        return null;
    }
    public Room getRoomByName(String roomName) {
        if (roomName == null) {
            return null;
        }
        for (Room room : rooms) {
            if (room.getName().equalsIgnoreCase(roomName.trim())) {
                return room;
            }
        }
        return null;
    }

    public Room getStartRoom() {
        return getRoomByID(startRoomID);
    }

    public Room getAdjacentRoom(Room currentRoom, String direction) {

        if (currentRoom == null || direction == null) {
            return null;
        }
        String nextRoomID = null;

        switch (direction.toUpperCase()) {
            case "NORTH", "N":
                nextRoomID = currentRoom.getNorth();
                break;

            case "SOUTH", "S":
                nextRoomID = currentRoom.getSouth();
                break;

            case "EAST", "E":
                nextRoomID = currentRoom.getEast();
                break;

            case "WEST", "W":
                nextRoomID = currentRoom.getWest();
                break;

            default:
                return null;
        }
        return getRoomByID(nextRoomID);
    }

    public Room getVisitRoom(Room currentRoom, String roomName) {
        Room target = getRoomByName(roomName);
        if (target == null || !currentRoom.canVisit(target.getRoomID())) {
            return null;
        }
        return target;
    }
    public Room getLeaveRoom(Room currentRoom) {
        return getRoomByID(currentRoom.getLeaveTo());
    }


    public boolean isRoomUnlocked(Room room, Player player) {

        if (room == null || player == null) {
            return false;
        }
        return room.isUnlocked(player);
    }

    public List<Room> getRooms() {
        return rooms;
    }




    // ENEMY METHODS
    public Enemy getEnemyByID(String enemyID) {

        if (enemyID == null) {
            return null;
        }
        for (Enemy enemy : enemies) {
            if (enemy.getEnemyID().equals(enemyID)) {
                return enemy;
            }
        }
        return null;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Enemy> getEnemiesInRoom(Room room) {

        if (room == null) {
            return new ArrayList<>();
        }
        return room.getEnemies();
    }

    public Enemy getRandomEnemy(List<Enemy> possibleEnemies) {

        if (possibleEnemies == null || possibleEnemies.isEmpty()) {
            return null;
        }
        int randomIndex = random.nextInt(possibleEnemies.size());
        return possibleEnemies.get(randomIndex);
    }



    // ITEM METHODS
    public Item getItemByID(String itemID) {

        if (itemID == null) {
            return null;
        }
        for (Item item : items) {
            if (item.getItemID().equals(itemID)) {
                return item;
            }
        }
        return null;
    }

    public List<Item> getItems() {
        return items;
    }

    public Item getRandomItem(List<Item> possibleItems) {
        if (possibleItems == null || possibleItems.isEmpty()) {
            return null;
        }
        int randomIndex = random.nextInt(possibleItems.size());
        return possibleItems.get(randomIndex);
    }

    public List<Item> getItemsInRoom(Room room) {

        if (room == null) {
            return new ArrayList<>();
        }
        return room.getItems();
    }



    // NPC METHODS
    public NPC getNPCByID(String npcID) {

        if (npcID == null) {
            return null;
        }
        for (NPC npc : npcs) {
            if (npc.getNPCID().equals(npcID)) {
                return npc;
            }
        }
        return null;
    }

    public List<NPC> getNPCs() {
        return npcs;
    }

    public List<NPC> getNPCsInRoom(Room room) {

        if (room == null) {
            return new ArrayList<>();
        }
        return room.getNPCs();
    }



    // PUZZLE METHODS
    public Puzzle getPuzzleByID(String puzzleID) {

        if (puzzleID == null) {
            return null;
        }
        for (Puzzle puzzle : puzzles) {
            if (puzzle.getPuzzleID().equals(puzzleID)) {
                return puzzle;
            }
        }
        return null;
    }

    public Puzzle getPuzzleInRoom(Room room) {

        if (room == null) {
            return null;
        }
        for (Puzzle puzzle : puzzles) {
            if (puzzle.getRoomID().equals(room.getRoomID()) && !puzzle.isCompleted()) {

                return puzzle;
            }
        }
        return null;
    }

    public List<Puzzle> getPuzzles() {
        return puzzles;
    }



    // Add Methods
    // Adds everything to GameWorld
    public void addRoom(Room room) {
        if (room != null && room.getRoomID() != null && getRoomByID(room.getRoomID()) == null) {
            rooms.add(room);
        }
    }

    public void addEnemy(Enemy enemy) {
        if (enemy != null && enemy.getEnemyID() != null && getEnemyByID(enemy.getEnemyID()) == null) {
            enemies.add(enemy);
        }
    }

    public void addItem(Item item) {
        if (item != null && item.getItemID() != null && getItemByID(item.getItemID()) == null) {
            items.add(item);
        }
    }

    public void addNPC(NPC npc) {
        if (npc != null && npc.getNPCID() != null && getNPCByID(npc.getNPCID()) == null) {
            npcs.add(npc);
        }
    }

    public void addPuzzle(Puzzle puzzle) {
        if (puzzle != null && puzzle.getPuzzleID() != null && getPuzzleByID(puzzle.getPuzzleID()) == null) {
            puzzles.add(puzzle);
        }
    }



    // Random Methods
    public boolean shouldSpawn() {
        return random.nextBoolean();
    }
}