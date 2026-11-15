package Model;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.nio.file.InvalidPathException;

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

                // Items, puzzles and enemies are added to the room by their own loaders
                Room room = new Room(roomID, name, description, null, null, null,
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

    public List<Item> loadItems() {
        throw new UnsupportedOperationException("Item loading has not been implemented yet.");
    }

    public List<Enemy> loadEnemies() {
        try {
            return readEnemyDefinitions();
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    private List<Enemy> readEnemyDefinitions() throws IOException {
        Path file = Paths.get(dataDirectory, "enemies.txt");
        List<Enemy> enemies = new ArrayList<>();
        Set<String> ids = new HashSet<>();
        int lineNumber = 0;

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {
                line = combatLine(line, ++lineNumber);

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] fields = line.split("\\|", -1);

                if (fields.length != 10) {
                    throw combatError(file, lineNumber, "Expected 10 pipe-separated enemy fields.");
                }

                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].trim();
                }

                if (fields[0].isEmpty() || fields[1].isEmpty() || fields[2].isEmpty()) {

                    throw combatError(file, lineNumber, "Enemy ID, name, and description are required.");
                }

                if (!ids.add(fields[0])) {
                    throw combatError(file, lineNumber, "Duplicate enemy ID: " + fields[0]);
                }

                int hp = combatInteger(fields[3], 1, file, lineNumber, "maxHP");

                int attack = combatInteger(fields[4], 0, file, lineNumber, "attack");

                int defense = combatInteger(fields[5], Integer.MIN_VALUE, file, lineNumber, "defense");

                if (!fields[6].equals("true") && !fields[6].equals("false")) {

                    throw combatError(file, lineNumber, "boss must be true or false.");
                }

                int silver = combatInteger(fields[7], 0, file, lineNumber, "silverDrop");

                int gold = combatInteger(fields[8], 0, file, lineNumber, "goldDrop");

                Map<String, Integer> drops = new LinkedHashMap<>();

                if (!fields[9].equals("-")) {
                    for (String entry : fields[9].split(",", -1)) {
                        String[] drop = entry.split(":", -1);

                        if (drop.length != 2 || drop[0].trim().isEmpty()) {

                            throw combatError(file, lineNumber, "Drops must be itemID:quantity or -.");
                        }

                        String itemID = drop[0].trim();

                        int quantity = combatInteger(drop[1].trim(), 1, file, lineNumber, "drop quantity");

                        if (drops.putIfAbsent(itemID, quantity) != null) {
                            throw combatError(file, lineNumber, "Duplicate drop item ID: " + itemID);
                        }
                    }
                }

                try {
                    Enemy enemy = new Enemy(fields[0], fields[1], fields[2].replace("\\n", "\n"), hp, hp, attack,
                            defense, Boolean.parseBoolean(fields[6]), silver, gold, drops);

                    enemies.add(enemy);

                } catch (IllegalArgumentException e) {
                    throw new IOException(file + ":" + lineNumber + ": " + e.getMessage(), e);
                }
            }
        }

        if (enemies.isEmpty()) {
            throw combatError(file, Math.max(1, lineNumber), "No enemy records. Add the official enemy data before loading.");
        }

        return enemies;
    }

    public Properties loadBattleRules() {
        try {
            return readBattleRules();
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    private Properties readBattleRules() throws IOException {
        Path file = Paths.get(dataDirectory, "battle.txt");
        Properties rules = new Properties();
        int lineNumber = 0;

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {
                line = combatLine(line, ++lineNumber);

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] setting = line.split("=", -1);

                if (setting.length != 2 || setting[1].trim().isEmpty()) {

                    throw combatError(file, lineNumber, "Expected key=value.");
                }

                String key = setting[0].trim();
                String value = setting[1].trim();

                if (rules.containsKey(key)) {
                    throw combatError(file, lineNumber, "Duplicate battle setting: " + key);
                }

                if (key.equals("movesPerTurn")) {
                    combatInteger(value, 1, file, lineNumber, key);

                } else if (key.equals("baseFleeChance") || key.equals("fleeIncrease")) {

                    combatProbability(value, file, lineNumber, key);

                } else {
                    throw combatError(file, lineNumber, "Unknown battle setting: " + key);
                }

                rules.setProperty(key, value);
            }
        }

        String[] requiredSettings = {"movesPerTurn", "baseFleeChance", "fleeIncrease"
        };

        for (String key : requiredSettings) {
            if (!rules.containsKey(key)) {
                throw combatError(file, Math.max(1, lineNumber), "Missing battle setting: " + key);
            }
        }

        return rules;
    }

    private static String combatLine(String line, int number) {
        if (number == 1 && line.startsWith("\uFEFF")) {
            line = line.substring(1);
        }

        return line.trim();
    }

    private static int combatInteger(String text, int minimum, Path file, int line, String field) throws IOException {

        try {
            int value = Integer.parseInt(text);

            if (value < minimum) {
                throw new NumberFormatException();
            }

            return value;

        } catch (NumberFormatException e) {
            throw combatError(file, line, field + " must be a whole number >= " + minimum + ".");
        }
    }

    private static void combatProbability(String text, Path file, int line, String field) throws IOException {

        try {
            double value = Double.parseDouble(text);

            if (!Double.isFinite(value) || value < 0 || value > 1) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {
            throw combatError(file, line, field + " must be a number between 0 and 1.");
        }
    }

    private static IOException combatError(Path file, int line, String message) {

        return new IOException(file + ":" + line + ": " + message);
    }

    public List<String> loadDialogue() {
        throw new UnsupportedOperationException("Dialogue loading has not been implemented yet.");
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

                List<String> leverIDs = new ArrayList<>();

                if (!data[6].equals("-")) {
                    leverIDs = Arrays.asList(data[6].split(","));
                }

                Set<String> solution = new LinkedHashSet<>();

                if (!data[7].equals("-")) {
                    solution.addAll(Arrays.asList(data[7].split(",")));
                }

                String requiredItemID = data[8].equals("-") ? null : data[8];

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

                Puzzle puzzle = new Puzzle(puzzleID, name, roomID, description, lockedDescription, solveText, leverIDs,
                        solution, requiredItemID, rewardItems);

                puzzles.add(puzzle);
            }

        } catch (IOException e) {
            System.err.println("Error loading puzzles: " + e.getMessage());
        }

        return puzzles;
    }

    // Saves the current game state to a text file
    public boolean saveGame(GameMemory memory) {

        if (memory == null || !memory.isValid() || saveDirectory == null || saveDirectory.trim().isEmpty()) {
            return false;
        }

        String saveName = memory.getSaveName().trim();

        if (saveName.isEmpty() || saveName.contains("/") || saveName.contains("\\") || saveName.contains("|")
                || saveName.contains("\n") || saveName.contains("\r")) {
            return false;
        }

        String saveData = memory.toText();

        if (saveData.isEmpty()) {
            return false;
        }

        try {
            // Create the save directory if it does not exist.
            Path saveFolder = Paths.get(saveDirectory).toAbsolutePath().normalize();
            Files.createDirectories(saveFolder);

            // Create the save file inside the save directory.
            Path saveFile = saveFolder.resolve(saveName + ".txt").normalize();

            // Make sure the save file is inside the designated directory.
            if (!saveFile.getParent().equals(saveFolder)) {
                return false;
            }

            // Write the save data to the text file.
            Files.write(saveFile, saveData.getBytes(StandardCharsets.UTF_8));

            return true;

        } catch (IOException | InvalidPathException | SecurityException e) {
            return false;
        }
    }

    // Restores a saved game from a text file
    public GameMemory restoreGame(String saveName) {

        if (saveName == null || saveName.trim().isEmpty() || saveDirectory == null || saveDirectory.trim().isEmpty()) {
            return null;
        }

        saveName = saveName.trim();

        if (saveName.contains("/") || saveName.contains("\\") || saveName.contains("|") || saveName.contains("\n") || saveName.contains("\r")) {
            return null;
        }

        try {
            // Find the save file inside the designated save directory.
            Path saveFolder = Paths.get(saveDirectory).toAbsolutePath().normalize();
            Path saveFile = saveFolder.resolve(saveName + ".txt").normalize();

            if (!saveFile.getParent().equals(saveFolder) || !Files.isRegularFile(saveFile)) {
                return null;
            }

            // Read the saved information.
            Map<String, String> savedFields = new LinkedHashMap<>();
            List<String[]> savedItems = new ArrayList<>();
            List<String> visitedRoomIDs = new ArrayList<>();
            List<String> completedPuzzleIDs = new ArrayList<>();

            try (BufferedReader reader = Files.newBufferedReader(saveFile, StandardCharsets.UTF_8)) {

                String line;

                while ((line = reader.readLine()) != null) {

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] data = line.split("\\|", -1);
                    String label = data[0].trim();

                    switch (label) {

                        case "INVENTORY_ITEM":
                            if (data.length != 5) {
                                return null;
                            }
                            savedItems.add(data);
                            break;

                        case "VISITED_ROOM":
                            if (data.length != 2) {
                                return null;
                            }
                            visitedRoomIDs.add(data[1].trim());
                            break;

                        case "COMPLETED_PUZZLE":
                            if (data.length != 2) {
                                return null;
                            }
                            completedPuzzleIDs.add(data[1].trim());
                            break;

                        default:
                            if (data.length != 2 || savedFields.put(label, data[1].trim()) != null) {
                                return null;
                            }
                            break;
                    }
                }
            }

            // Check that the required saved information exists.
            String[] requiredFields = {"SAVE_NAME", "PLAYER_NAME", "CURRENT_HP", "MAX_HP", "ATTACK", "DEFENSE", "CURRENT_ROOM",
                    "SILVER_COINS", "GOLD_COINS", "GAME_COMPLETED", "EQUIPPED_WEAPON", "EQUIPPED_ARMOR"
            };

            for (String field : requiredFields) {
                if (!savedFields.containsKey(field)) {
                    return null;
                }
            }

            if (!saveName.equals(savedFields.get("SAVE_NAME"))) {
                return null;
            }

            String completed = savedFields.get("GAME_COMPLETED");

            if (!completed.equals("true") && !completed.equals("false")) {
                return null;
            }

            // Recreate the game world using the existing game data.
            GameWorld restoredWorld = new GameWorld();

            for (Room room : loadRooms()) {
                restoredWorld.addRoom(room);
            }

            restoredWorld.getItems().addAll(loadItems());

            for (Enemy enemy : loadEnemies()) {
                restoredWorld.addEnemy(enemy);
            }

            for (Puzzle puzzle : loadPuzzles()) {
                restoredWorld.addPuzzle(puzzle);
            }

            // TODO: Load NPCs when NPC loading is implemented.

            // Restore the player's current room.
            Room currentRoom = restoredWorld.getRoomByID(savedFields.get("CURRENT_ROOM"));

            if (currentRoom == null) {
                return null;
            }

            // Restore visited rooms.
            for (String roomID : visitedRoomIDs) {
                if (restoredWorld.getRoomByID(roomID) == null) {
                    return null;
                }
            }

            // Restore inventory.
            Inventory inventory = new Inventory(Inventory.SATCHEL_CAPACITY);

            for (String[] data : savedItems) {

                String itemID = data[1].trim();
                String itemType = data[2].trim();

                int quantity = Integer.parseInt(data[3].trim());
                int itemState = Integer.parseInt(data[4].trim());

                // Find the item using its ID.
                Item item = null;

                for (Item availableItem : restoredWorld.getItems()) {
                    if (availableItem.getId().equalsIgnoreCase(itemID)) {
                        item = availableItem;
                        break;
                    }
                }

                if (item == null) {
                    return null;
                }

                item.setQuantity(quantity);

                // Restore the item's specific state.
                switch (itemType) {

                    case "WEAPON":
                        if (!(item instanceof Weapon)) {
                            return null;
                        }
                        ((Weapon) item).setAttackCounter(itemState);
                        break;

                    case "ARMOR":
                        if (!(item instanceof Armor)) {
                            return null;
                        }
                        ((Armor) item).setHitsTakenCounter(itemState);
                        break;

                    case "CONSUMABLE":
                        if (!(item instanceof Consumable)) {
                            return null;
                        }
                        break;

                    case "KEY_ITEM":
                        if (!(item instanceof KeyItem)) {
                            return null;
                        }
                        break;

                    case "ITEM":
                        if (item instanceof Weapon || item instanceof Armor || item instanceof Consumable || item instanceof KeyItem) {
                            return null;
                        }
                        break;

                    default:
                        return null;
                }

                if (!inventory.addItem(item)) {
                    return null;
                }
            }

            // Restore the normal inventory capacity.
            inventory.setMaxCapacity(Inventory.BASE_CAPACITY);

            if (inventory.getUsedSlots() > inventory.getEffectiveCapacity()) {
                return null;
            }

            // Restore equipped weapon.
            Weapon equippedWeapon = null;
            String weaponID = savedFields.get("EQUIPPED_WEAPON");

            if (!weaponID.isEmpty()) {
                for (Item item : inventory) {
                    if (item instanceof Weapon && item.getId().equalsIgnoreCase(weaponID)) {
                        equippedWeapon = (Weapon) item;
                        break;
                    }
                }

                if (equippedWeapon == null) {
                    return null;
                }
            }

            // Restore equipped armor.
            Armor equippedArmor = null;
            String armorID = savedFields.get("EQUIPPED_ARMOR");

            if (!armorID.isEmpty()) {
                for (Item item : inventory) {
                    if (item instanceof Armor && item.getId().equalsIgnoreCase(armorID)) {
                        equippedArmor = (Armor) item;
                        break;
                    }
                }

                if (equippedArmor == null) {
                    return null;
                }
            }

            // Restore completed puzzles.
            for (String puzzleID : completedPuzzleIDs) {

                Puzzle puzzle = restoredWorld.getPuzzleByID(puzzleID);

                if (puzzle == null) {
                    return null;
                }

                puzzle.setCompleted(true);

                // Also update the puzzle instance attached to its room, if separate.
                for (Room room : restoredWorld.getRooms()) {
                    for (Puzzle roomPuzzle : room.getPuzzles()) {
                        if (roomPuzzle.getPuzzleID().equalsIgnoreCase(puzzleID)) {
                            roomPuzzle.setCompleted(true);
                        }
                    }
                }
            }

            // Recreate the player with the saved information.
            Player restoredPlayer = new Player(savedFields.get("PLAYER_NAME"), Integer.parseInt(savedFields.get("CURRENT_HP")),
                    Integer.parseInt(savedFields.get("MAX_HP")), Integer.parseInt(savedFields.get("ATTACK")),
                    Integer.parseInt(savedFields.get("DEFENSE")), inventory, currentRoom, equippedWeapon, equippedArmor,
                    Integer.parseInt(savedFields.get("SILVER_COINS")), Integer.parseInt(savedFields.get("GOLD_COINS")),
                    visitedRoomIDs, Boolean.parseBoolean(completed)
            );

            return new GameMemory(saveName, restoredPlayer, restoredWorld);

        } catch (IOException | RuntimeException e) {
            return null;
        }
    }
}