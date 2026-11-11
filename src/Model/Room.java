package Model;

import java.util.List;
import java.util.ArrayList;

public class Room {

    private String roomID;
    private String name;
    private String description;
    private List<Item> items;
    private List<Puzzle> puzzles;
    private List<NPC> npcs;
    private List<Enemy> enemies;

    private String north;
    private String east;
    private String south;
    private String west;

    private List<String> visitRooms;
    private String leaveTo;
    private boolean locked;
    private String requiredKeyID;
    private String requiredPuzzleID;
    private List<Item> randomItems;
    private boolean randomItemChecked;
    private boolean randomEnemyRoom;
    private StorageChest chest;

    public Room(String roomID, String name, String description, List<Item> items, List<Puzzle> puzzles,
                List<NPC> npcs, List<Enemy> enemies, String north, String east, String south, String west) {
        this.roomID = roomID;
        this.name = name;
        this.description = description;
        this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
        this.puzzles = puzzles == null ? new ArrayList<>() : new ArrayList<>(puzzles);
        this.npcs = npcs == null ? new ArrayList<>() : new ArrayList<>(npcs);
        this.enemies = enemies == null ? new ArrayList<>() : new ArrayList<>(enemies);
        this.north = north;
        this.east = east;
        this.south = south;
        this.west = west;
        this.visitRooms = new ArrayList<>();
        this.leaveTo = null;
        this.locked = false;
        this.requiredKeyID = null;
        this.requiredPuzzleID = null;
        this.randomItems = new ArrayList<>();
        this.randomItemChecked = false;
        this.randomEnemyRoom = false;
        this.chest = null;
    }

    //getters for Room attributes
    public List<Item> getItems() {
        return items;
    }

    public List<Puzzle> getPuzzles() {
        return puzzles;
    }

    public List<NPC> getNPCs() {
        return npcs;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public String getRoomID() {
        return roomID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getNorth() {
        return north;
    }

    public String getEast() {
        return east;
    }

    public String getSouth() {
        return south;
    }

    public String getWest() {
        return west;
    }

    public void addVisitRoom(String roomID) {
        visitRooms.add(roomID);
    }

    public void setLeaveTo(String roomID) {
        leaveTo = roomID;
    }

    public void lockWithKey(String keyID) {
        locked = true;
        requiredKeyID = keyID;
    }

    public void lockWithPuzzle(String puzzleID) {
        locked = true;
        requiredPuzzleID = puzzleID;
    }

    public void addRandomItem(Item item) {
        randomItems.add(item);
    }

    public void setRandomEnemyRoom(boolean randomEnemyRoom) {
        this.randomEnemyRoom = randomEnemyRoom;
    }

    public void setChest(StorageChest chest) {
        this.chest = chest;
    }

    public String getFullDescription() {
        String text = name + "\n" + description;
        if (hasUnsolvedPuzzle()) {
            text += "\nThere is a puzzle in this room.";
        }
        return text;
    }


    public boolean canVisit(String roomID) {
        return visitRooms.contains(roomID);
    }

    public List<String> getVisitRooms() {
        return visitRooms;
    }

    public String getLeaveTo() {
        return leaveTo;
    }


    public boolean isUnlocked(Player player) {
        if (!locked) {
            return true;
        }
        if (requiredKeyID != null && player.getInventory().hasItem(requiredKeyID)) {
            locked = false;
        }
        return !locked;
    }

    public void unlock() {
        locked = false;
    }

    public boolean isLocked() {
        return locked;
    }

    public String getRequiredKeyID() {
        return requiredKeyID;
    }

    public String getRequiredPuzzleID() {
        return requiredPuzzleID;
    }


    public String inspect() {
        if (items.isEmpty()) {
            return "There are no items in this room.";
        }
        String list = "";
        for (Item item : items) {
            if (!list.isEmpty()) {
                list += ", ";
            }
            list += item.getName();
        }
        return "You find: " + list;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }


    public Item findItem(String itemName) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }


    public void spawnRandomItem() {
        if (randomItemChecked || randomItems.isEmpty()) {
            return;
        }
        randomItemChecked = true;
        if (Math.random() < 0.5) {
            int pick = (int) (Math.random() * randomItems.size());
            items.add(randomItems.get(pick));
        }
    }


    public Puzzle getActivePuzzle() {
        for (Puzzle puzzle : puzzles) {
            if (!puzzle.isCompleted()) {
                return puzzle;
            }
        }
        return null;
    }

    public boolean hasUnsolvedPuzzle() {
        return getActivePuzzle() != null;
    }

    public boolean isPuzzleRoom() {
        return !puzzles.isEmpty();
    }

    public boolean unlocked(Player player) {
        if (!locked) {
            return true;
        }
        if (requiredKeyID != null && player.getInventory().hasItem(requiredKeyID)) {
            locked = false;
        }
        return !locked;
    }



    public boolean isRandomEnemyRoom() {
        return randomEnemyRoom;
    }

    public StorageChest getChest() {
        return chest;
    }


}