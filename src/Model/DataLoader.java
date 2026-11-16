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
        Map<String, Item> catalog = getItemCatalog();
        File file = resolveRoomFile();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();

                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                String[] data = trimmed.split("\\|", -1);

                if (data.length != 8) {
                    throw new IOException("Invalid room data: " + trimmed);
                }

                String roomID = data[0].trim();
                String name = data[1].trim();
                String description = data[2].replace("\\n", "\n");

                String exits = data[3].trim();
                boolean hasChest = data[4].trim().equalsIgnoreCase("true");
                String unlockKey = data[5].trim();
                boolean randomEnemyRoom = data[6].trim().equalsIgnoreCase("true");
                String itemListText = data[7].trim();

                Room room = buildRoom(roomID, name, description, exits);

                if (hasChest) {
                    room.setChest(new StorageChest());
                }

                if (!unlockKey.isEmpty() && !"-".equals(unlockKey)) {
                    if (unlockKey.startsWith("W")) {
                        room.lockWithKey(unlockKey);
                    } else if (unlockKey.startsWith("P")) {
                        room.lockWithPuzzle(unlockKey);
                    }
                }

                room.setRandomEnemyRoom(randomEnemyRoom);

                if (!itemListText.isEmpty() && !"-".equals(itemListText)) {
                    for (String itemID : itemListText.split(",")) {
                        String cleanID = itemID.trim();
                        if (cleanID.isEmpty()) {
                            continue;
                        }

                        Item item = catalog.get(cleanID);
                        if (item != null) {
                            room.addItem(createItemCopy(item));
                        }
                    }
                }

                rooms.add(room);
            }
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException("Error loading rooms: " + e.getMessage(), e);
        }

        return rooms;
    }

    private File resolveRoomFile() {
        File file = new File(dataDirectory, "rooms.txt");
        if (file.isFile()) {
            return file;
        }

        file = new File(dataDirectory, "room.txt");
        if (file.isFile()) {
            return file;
        }

        file = new File("src", "room.txt");
        if (file.isFile()) {
            return file;
        }

        file = new File("src", "rooms.txt");
        if (file.isFile()) {
            return file;
        }

        throw new RuntimeException("Missing room data file in directory: " + dataDirectory);
    }

    private Room buildRoom(String roomID, String name, String description, String exits) {
        String north = null;
        String east = null;
        String south = null;
        String west = null;
        List<String> visitRooms = new ArrayList<>();
        String leaveTo = null;

        if (exits != null && !exits.isBlank() && !"-".equals(exits)) {
            for (String exit : exits.split(",")) {
                String token = exit.trim();
                if (token.isEmpty()) {
                    continue;
                }

                String[] exitData = token.split(":", 2);
                if (exitData.length < 2) {
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
                    default:
                        break;
                }
            }
        }

        Room room = new Room(roomID, name, description, null, null, null, north, east, south, west);

        for (String visitRoom : visitRooms) {
            room.addVisitRoom(visitRoom);
        }

        if (leaveTo != null) {
            room.setLeaveTo(leaveTo);
        }

        return room;
    }

    private Map<String, Item> getItemCatalog() {
        Map<String, Item> catalog = new LinkedHashMap<>();
        for (Item item : loadItems()) {
            catalog.put(item.getId(), item);
        }
        return catalog;
    }


    public List<Item> loadItems() {
        List<Item> loadedItems = new ArrayList<>();
        File file = resolveItemFile();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();

                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                String[] data = trimmed.split("\\|", -1);

                if (data.length != 9) {
                    throw new IOException("Invalid item data: " + trimmed);
                }

                Item item = parseItem(data);
                loadedItems.add(item);
            }
        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException("Error loading items: " + e.getMessage(), e);
        }

        return loadedItems;
    }

    public void loadItems(GameWorld gameWorld) {
        if (gameWorld == null) {
            return;
        }

        for (Item item : loadItems()) {
            gameWorld.addItem(item);

            for (String roomID : getRoomIDs(item)) {
                Room room = gameWorld.getRoomByID(roomID.trim());
                if (room != null && !"FP01".equals(item.getId())) {
                    room.addItem(createItemCopy(item));
                }
            }
        }
    }

    public List<NPC> loadNPCs() {
        List<NPC> npcs = new ArrayList<>();
        npcs.add(NPC.createLombart());
        npcs.add(NPC.createUndertaker());
        npcs.add(NPC.createBarkeep());
        return npcs;
    }

    private File resolveItemFile() {
        File file = new File(dataDirectory, "items.txt");
        if (file.isFile()) {
            return file;
        }

        file = new File(dataDirectory, "Items.txt");
        if (file.isFile()) {
            return file;
        }

        throw new RuntimeException("Missing item data file in directory: " + dataDirectory);
    }

    private Item parseItem(String[] data) throws IOException {
        String id = data[0].trim();
        String type = data[1].trim().toUpperCase();
        String name = data[2].trim();
        String description = data[3].trim();
        int valueSilver = Integer.parseInt(data[4].trim());
        int statValue = data[5].trim().equals("-") ? 0 : Integer.parseInt(data[5].trim());
        int durability = data[6].trim().equals("-") ? 0 : Integer.parseInt(data[6].trim());
        String roomText = data[7].trim();
        String effectText = data[8].trim();

        Item item;

        switch (type) {
            case "WEAPON":
                item = new Weapon(id, name, description, valueSilver, statValue, durability);
                break;
            case "ARMOR":
                item = new Armor(id, name, description, valueSilver, statValue, durability);
                break;
            case "CONSUMABLE":
                item = new Consumable(id, name, description, valueSilver, statValue, 1);
                break;
            case "KEY":
                String target = effectText.startsWith("UNLOCKS:")
                        ? effectText.substring("UNLOCKS:".length()).trim()
                        : effectText;
                String roomId = target;
                String doorId = "";

                if (target.contains(":")) {
                    String[] keyTarget = target.split(":", 2);
                    roomId = keyTarget[0].trim();
                    doorId = keyTarget[1].trim();
                }

                item = new KeyItem(id, name, description, valueSilver, roomId, doorId);
                break;
            default:
                item = new Item(id, name, description, valueSilver);
                break;
        }

        if (effectText.contains("QUEST_ITEM") || "W09".equals(id) || "W12".equals(id)
                || "W16".equals(id) || "FP01".equals(id)) {
            item.setQuestItem(true);
        }

        if (roomText != null && !roomText.isBlank() && !"-".equals(roomText)) {
            item.setDescription(item.getDescription() + "\nSpawn locations: " + roomText);
        }

        if ("CURRENCY".equals(type)) {
            item.setStackable(true);
            item.setQuantity(Math.max(1, statValue));
        }

        return item;
    }

    private List<String> getRoomIDs(Item item) {
        List<String> roomIDs = new ArrayList<>();

        if (item == null || item.getDescription() == null) {
            return roomIDs;
        }

        String description = item.getDescription();
        int index = description.indexOf("Spawn locations:");
        if (index < 0) {
            return roomIDs;
        }

        String roomText = description.substring(index + "Spawn locations:".length()).trim();
        if (roomText.isEmpty() || "-".equals(roomText)) {
            return roomIDs;
        }

        for (String roomID : roomText.split(",")) {
            String trimmed = roomID.trim();
            if (!trimmed.isEmpty()) {
                roomIDs.add(trimmed);
            }
        }

        return roomIDs;
    }


    private Item createItemCopy(Item item) {

        if (item instanceof Weapon) {
            Weapon weapon = (Weapon) item;

            return new Weapon(item.getId(), item.getName(), item.getDescription(),
                    item.getValueSilver(), weapon.getAttackBonus(),
                    weapon.getDurabilityLimit());
        }

        if (item instanceof Armor) {
            Armor armor = (Armor) item;

            return new Armor(item.getId(), item.getName(), item.getDescription(),
                    item.getValueSilver(), armor.getDefenseBonus(),
                    armor.getDurabilityLimit());
        }

        if (item instanceof Consumable) {
            Consumable consumable = (Consumable) item;

            return new Consumable(item.getId(), item.getName(), item.getDescription(),
                    item.getValueSilver(), consumable.getHealAmount(), 1);
        }

        if (item instanceof KeyItem) {
            KeyItem key = (KeyItem) item;

            return new KeyItem(item.getId(), item.getName(), item.getDescription(),
                    item.getValueSilver(), key.getRoomId(), key.getDoorId());
        }

        Item copy = new Item(item.getId(), item.getName(), item.getDescription(),
                item.getValueSilver());

        copy.setQuestItem(item.isQuestItem());
        copy.setDroppable(item.isDroppable());

        return copy;
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
            System.err.println("Error loading puzzles:");
            e.printStackTrace();
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

    // Waiting on Battle and Items to get finished so the game can restore everything
    // Restores a saved game from a text file
    public GameMemory restoreGame(String saveName) {

        if (saveName == null || saveName.trim().isEmpty() || saveDirectory == null || saveDirectory.trim().isEmpty()) {
            return null;
        }

        saveName = saveName.trim();

        if (saveName.contains("/") || saveName.contains("\\") || saveName.contains("|") || saveName.contains("\n")
                || saveName.contains("\r")) {
            return null;
        }

        try {
            // Find the save file.
            Path saveFolder = Paths.get(saveDirectory).toAbsolutePath().normalize();
            Path saveFile = saveFolder.resolve(saveName + ".txt").normalize();

            if (!saveFile.getParent().equals(saveFolder) || !Files.isRegularFile(saveFile)) {
                return null;
            }

            // Read the saved information.
            Map<String, String> savedFields = new LinkedHashMap<>();
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
                            // Inventory restoration is currently optional.
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
                            if (data.length != 2
                                    || savedFields.put(label, data[1].trim()) != null) {
                                return null;
                            }
                            break;
                    }
                }
            }

            // Check that the required information exists.
            String[] requiredFields = {"SAVE_NAME", "PLAYER_NAME", "CURRENT_HP", "MAX_HP", "ATTACK", "DEFENSE",
                    "CURRENT_ROOM", "SILVER_COINS", "GOLD_COINS", "GAME_COMPLETED"
            };

            for (String field : requiredFields) {
                if (!savedFields.containsKey(field)) {
                    return null;
                }
            }

            if (!saveName.equals(savedFields.get("SAVE_NAME"))) {
                return null;
            }

            if (savedFields.get("PLAYER_NAME").isEmpty()) {
                return null;
            }

            String completed = savedFields.get("GAME_COMPLETED");

            if (!completed.equals("true") && !completed.equals("false")) {
                return null;
            }

            // Restore the game world using available data.
            GameWorld restoredWorld = new GameWorld();

            for (Room room : loadRooms()) {
                restoredWorld.addRoom(room);
            }

            loadItems(restoredWorld);

            // Restore puzzles if their data is available.
            try {
                for (Puzzle puzzle : loadPuzzles()) {
                    restoredWorld.addPuzzle(puzzle);
                }
            } catch (RuntimeException e) {
                System.out.println("Puzzle data could not be restored.");
            }

            // Items, enemies, and NPCs are not required to load the saved player.
            // They can be added to the world later when their loaders are ready.

            // Restore the player's current room.
            Room currentRoom = restoredWorld.getRoomByID(savedFields.get("CURRENT_ROOM"));

            if (currentRoom == null) {
                return null;
            }

            // Restore visited rooms.
            List<String> restoredVisitedRooms = new ArrayList<>();

            for (String roomID : visitedRoomIDs) {
                if (restoredWorld.getRoomByID(roomID) != null && !restoredVisitedRooms.contains(roomID)) {
                    restoredVisitedRooms.add(roomID);
                }
            }

            // Make sure the current room is included in visited rooms.
            if (!restoredVisitedRooms.contains(currentRoom.getRoomID())) {
                restoredVisitedRooms.add(currentRoom.getRoomID());
            }

            // Restore an empty inventory until item loading is implemented.
            Inventory inventory = new Inventory(Inventory.BASE_CAPACITY);

            // Restore completed puzzles when their data is available.
            for (String puzzleID : completedPuzzleIDs) {

                Puzzle puzzle = restoredWorld.getPuzzleByID(puzzleID);

                if (puzzle != null) {
                    puzzle.setCompleted(true);
                }
            }

            // Recreate the player using the saved information.
            Player restoredPlayer = new Player(savedFields.get("PLAYER_NAME"), Integer.parseInt(savedFields.get("CURRENT_HP")),
                    Integer.parseInt(savedFields.get("MAX_HP")), Integer.parseInt(savedFields.get("ATTACK")), Integer.parseInt(savedFields.get("DEFENSE")),
                    inventory, currentRoom, null, null, Integer.parseInt(savedFields.get("SILVER_COINS")),
                    Integer.parseInt(savedFields.get("GOLD_COINS")), restoredVisitedRooms, Boolean.parseBoolean(completed)
            );

            return new GameMemory(saveName, restoredPlayer, restoredWorld);

        } catch (IOException | RuntimeException e) {
            return null;
        }
    }
}
