package Model;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

public class DataLoader {

    private String dataDirectory;
    private String saveDirectory;

    public DataLoader(String dataDirectory, String saveDirectory) {
        this.dataDirectory = dataDirectory;
        this.saveDirectory = saveDirectory;
    }

    public List<Room> loadRooms() {

        List<Room> rooms = new ArrayList<>();

        File file = new File(dataDirectory, "rooms.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                // ID | Name | Description | Exits | Chest | Unlocked by | Random enemy room
                String[] data = line.split("\\|", -1);

                if (data.length != 7) {
                    System.out.println("Invalid room data: " + line);
                    continue;
                }

                String roomID = data[0].trim();
                String name = data[1].trim();
                String description = data[2].replace("\\n", "\n");

                // Exits, e.g. NORTH:R02,SOUTH:R04,EAST:R07 or VISIT:R08,VISIT:R09 or LEAVE:R23
                String north = null;
                String east = null;
                String south = null;
                String west = null;
                List<String> visitRooms = new ArrayList<>();
                String leaveTo = null;

                for (String exit : data[3].split(",")) {

                    String[] exitData = exit.split(":");

                    if (exitData.length != 2) {
                        continue;
                    }

                    String direction = exitData[0].trim().toUpperCase();
                    String targetRoomID = exitData[1].trim();

                    switch (direction) {
                        case "NORTH":
                            north = targetRoomID;
                            break;
                        case "EAST":
                            east = targetRoomID;
                            break;
                        case "SOUTH":
                            south = targetRoomID;
                            break;
                        case "WEST":
                            west = targetRoomID;
                            break;
                        case "VISIT":
                            visitRooms.add(targetRoomID);
                            break;
                        case "LEAVE":
                            leaveTo = targetRoomID;
                            break;
                    }
                }

                // Items, puzzles, NPCs and enemies are added to the room by their own loaders
                Room room = new Room(roomID, name, description, null, null, null, null,
                        north, east, south, west);

                for (String visitID : visitRooms) {
                    room.addVisitRoom(visitID);
                }

                if (leaveTo != null) {
                    room.setLeaveTo(leaveTo);
                }

                // Storage chest (only the Player's Room)
                if (data[4].trim().equalsIgnoreCase("true")) {
                    room.setChest(new StorageChest());
                }

                // What unlocks the room: a key (W07, W08) or a puzzle (P5, P8). "-" means not locked.
                String unlockedBy = data[5].trim();

                if (unlockedBy.startsWith("W")) {
                    room.lockWithKey(unlockedBy);
                }
                else if (unlockedBy.startsWith("P")) {
                    room.lockWithPuzzle(unlockedBy);
                }

                // Random enemy room (Storage Room #2, The Hollows, Crucible Bounty)
                room.setRandomEnemyRoom(data[6].trim().equalsIgnoreCase("true"));

                rooms.add(room);
            }
        } catch (IOException e) {
            System.out.println("Error loading rooms: " + e.getMessage());
        }
        return rooms;
    }

    public List<Item> loadItems(){

    }

    public List<Enemy> loadEnemies(){

    }

    public List<String> loadDialogue(){

    }

    public List<Puzzle> loadPuzzles() {

        List<Puzzle> puzzles = new ArrayList<>();

        File file = new File(dataDirectory, "puzzles.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split("\\|", -1);

                if (data.length != 10) {
                    System.out.println("Invalid puzzle data: " + line);
                    continue;
                }

                String puzzleID = data[0].trim();
                String name = data[1].trim();
                String roomID = data[2].trim();
                String description = data[3].replace("\\n", "\n");
                String lockedDescription = data[4].equals("-") ? null : data[4].replace("\\n", "\n");
                String solveText = data[5].replace("\\n", "\n");

                // Lever IDs
                List<String> leverIDs = new ArrayList<>();

                if (!data[6].equals("-")) {
                    leverIDs = Arrays.asList(data[6].split(","));
                }

                // Correct lever combo
                Set<String> solution = new LinkedHashSet<>();

                if (!data[7].equals("-")) {
                    solution.addAll(Arrays.asList(data[7].split(",")));
                }

                // Required item
                String requiredItemID = data[8].equals("-") ? null : data[8];

                // Reward items
                Map<String, Integer> rewardItems = new LinkedHashMap<>();

                if (!data[9].equals("-")) {

                    String[] rewards = data[9].split(",");

                    for (String reward : rewards) {

                        String[] itemData = reward.split(":");

                        String itemID = itemData[0].trim();
                        int quantity = Integer.parseInt(itemData[1].trim());

                        rewardItems.put(itemID, quantity);
                    }
                }
                Puzzle puzzle = new Puzzle(puzzleID, name, roomID, description, lockedDescription, solveText,
                        leverIDs, solution, requiredItemID, rewardItems);
                puzzles.add(puzzle);
            }
        } catch (IOException e) {
            view.displayMessage("Error loading puzzles: " + e.getMessage());
        }
        return puzzles;
    }



    // Saves the current game state to a text file.
    public void saveGame(GameMemory memory) {

    }

    // Restores a saved game from a text file.
    public GameMemory restoreGame(String saveName) {

    }
}