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

    public DataLoader(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public GameWorld loadGameWorld(){

    }

    public List<Room> loadRooms(){

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


    // If a player is loading a saved game, this method will load the game memory from the save file.
    public GameMemory restoreGame(){

    }
}