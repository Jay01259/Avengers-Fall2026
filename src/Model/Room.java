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

    // Purpose: Creates a room with its world location, exits, contents, and puzzle metadata.
    public Room(String roomID, String name, String description, List<Item> items, List<Puzzle> puzzles,
                List<Enemy> enemies, String north, String east, String south, String west) {
        this(roomID, name, description, items, puzzles, null, enemies, north, east, south, west);
    }

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
    // Purpose: Returns the list of items currently placed in the room.
    public List<Item> getItems() {
        return items;
    }

    // Purpose: Returns the active puzzles tied to this room.
    public List<Puzzle> getPuzzles() {
        return puzzles;
    }

    public List<NPC> getNPCs() {
        return npcs;
    }

    public void addNPC(NPC npc) {
        if (npc != null && !npcs.contains(npc)) {
            npcs.add(npc);
        }
    }

    public NPC getNPCByID(String npcID) {
        if (npcID == null) {
            return null;
        }
        for (NPC npc : npcs) {
            if (npc.getNPCID().equalsIgnoreCase(npcID)) {
                return npc;
            }
        }
        return null;
    }

    public NPC getNPCByName(String name) {
        if (name == null) {
            return null;
        }
        for (NPC npc : npcs) {
            if (npc.getName().equalsIgnoreCase(name.trim())) {
                return npc;
            }
        }
        return null;
    }

    // Purpose: Returns enemies that are currently present in this room.
    public List<Enemy> getEnemies() {
        return enemies;
    }

    // Purpose: Returns the room's unique identifier used for room lookups and transitions.
    public String getRoomID() {
        return roomID;
    }

    // Purpose: Returns the human-readable room name shown in the game output.
    public String getName() {
        return name;
    }

    // Purpose: Returns the room description read by the UI when the player inspects or enters a room.
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

    // Purpose: Adds a room that can be visited from the current room via a special transition.
    public void addVisitRoom(String roomID) {
        visitRooms.add(roomID);
    }

    // Purpose: Sets the destination used by the LEAVE command for a room that exits to another area.
    public void setLeaveTo(String roomID) {
        leaveTo = roomID;
    }

    // Purpose: Locks the room behind a key requirement to match puzzle and progression logic.
    public void lockWithKey(String keyID) {
        locked = true;
        requiredKeyID = keyID;
    }

    // Purpose: Locks the room behind a puzzle requirement so the player must solve a challenge first.
    public void lockWithPuzzle(String puzzleID) {
        locked = true;
        requiredPuzzleID = puzzleID;
    }

    // Purpose: Adds a possible random drop or loot item that could appear in the room later.
    public void addRandomItem(Item item) {
        randomItems.add(item);
    }

    // Purpose: Marks the room as one where random enemies can appear during world progression.
    public void setRandomEnemyRoom(boolean randomEnemyRoom) {
        this.randomEnemyRoom = randomEnemyRoom;
    }

    // Purpose: Attaches a storage chest to the room so the player can store and retrieve items there.
    public void setChest(StorageChest chest) {
        this.chest = chest;
    }

    // Purpose: Builds a detailed room description that includes any active puzzle information.
    public String getFullDescription() {
        String text = name + "\n" + description;
        if (!npcs.isEmpty()) {
            StringBuilder npcList = new StringBuilder();
            for (NPC npc : npcs) {
                if (npcList.length() > 0) {
                    npcList.append(", ");
                }
                npcList.append(npc.getName());
            }
            text += "\nNPCs here: " + npcList;
        }
        if (hasUnsolvedPuzzle()) {
            text += "\nThere is a puzzle in this room.";
        }
        return text;
    }


    // Purpose: Checks whether the given room ID is an allowed visit target from this room.
    public boolean canVisit(String roomID) {
        return visitRooms.contains(roomID);
    }

    public List<String> getVisitRooms() {
        return visitRooms;
    }

    public String getLeaveTo() {
        return leaveTo;
    }


    // Purpose: Determines whether the player can pass through the room after checking key and puzzle requirements.
    public boolean isUnlocked(Player player) {
        if (!locked) {
            return true;
        }
        if (requiredKeyID != null && player.getInventory().getItem(requiredKeyID) != null) {
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

    // Purpose: Adds an item to the room so it can be picked up or examined by the player.
    public void addItem(Item item) {
        items.add(item);
    }

    // Purpose: Removes an item from the room when it is picked up, used, or otherwise no longer present.
    public void removeItem(Item item) {
        items.remove(item);
    }


    // Purpose: Finds a room item by name so pickup and inspection commands can locate the correct object.
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
        if (requiredKeyID != null && player.getInventory().getItem(requiredKeyID) != null) {
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

