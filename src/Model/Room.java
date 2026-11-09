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
    }
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
}