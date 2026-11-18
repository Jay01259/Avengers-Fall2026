
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

                String[] data = line.split("\\|", -1);

                if (data.length != 7) {
                    System.out.println("Invalid room data: " + line);
                    continue;
                }

                String roomID = data[0].trim();
                String name = data[1].trim();
                String description = data[2].replace("\\n", "\n");

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

                Room room = new Room(
                        roomID, name, description,
                        null, null, null,
                        north, east, south, west
                );

                for (String visitID : visitRooms) {
                    room.addVisitRoom(visitID);
                }

                if (leaveTo != null) {
                    room.setLeaveTo(leaveTo);
                }

                if (data[4].trim().equalsIgnoreCase("true")) {
                    room.setChest(new StorageChest());
                }

                String unlockedBy = data[5].trim();

                if (unlockedBy.startsWith("W")) {
                    room.lockWithKey(unlockedBy);
                } else if (unlockedBy.startsWith("P")) {
                    room.lockWithPuzzle(unlockedBy);
                }

                room.setRandomEnemyRoom(
                        data[6].trim().equalsIgnoreCase("true"));

                rooms.add(room);
            }

        } catch (IOException e) {
            System.out.println("Error loading rooms: " + e.getMessage());
        }

        return rooms;
    }

    public void loadItems(GameWorld gameWorld) {
        File file = new File(dataDirectory, "Items.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] data = line.split("\\|", -1);

                if (data.length != 10) {
                    throw new IOException("Invalid item data: " + line);
                }

                String id = data[0].trim();
                String name = data[1].trim();
                String type = data[2].trim().toUpperCase();
                String description = data[3].trim();

                int value = Integer.parseInt(data[4].trim());

                int attackBonus = data[5].equals("-")
                        ? 0 : Integer.parseInt(data[5].trim());

                int durability = data[6].equals("-")
                        ? 0 : Integer.parseInt(data[6].trim());

                int defenseBonus = data[7].equals("-")
                        ? 0 : Integer.parseInt(data[7].trim());

                int healAmount = data[8].equals("-")
                        ? 0 : Integer.parseInt(data[8].trim());

                String[] roomIDs = data[9].split(",");

                Item item;

                switch (type) {
                    case "WEAPON":
                        item = new Weapon(
                                id, name, description, value,
                                attackBonus, durability);
                        break;

                    case "ARMOR":
                        item = new Armor(
                                id, name, description, value,
                                defenseBonus, durability);
                        break;

                    case "CONSUMABLE":
                        item = new Consumable(
                                id, name, description, value,
                                healAmount, 1);
                        break;

                    case "KEY":
                        String targetRoom =
                                id.equals("W07") ? "R15" : "R11";

                        String doorDirection =
                                id.equals("W07") ? "EAST" : "NORTH";

                        item = new KeyItem(
                                id, name, description, value,
                                targetRoom, doorDirection);
                        break;

                    default:
                        item = new Item(
                                id, name, description, value);
                        break;
                }

                if (id.equals("W09")
                        || id.equals("W16")
                        || id.equals("FP01")) {
                    item.setQuestItem(true);
                }

                gameWorld.addItem(item);

                for (String roomID : roomIDs) {
                    Room room = gameWorld.getRoomByID(roomID.trim());

                    if (room != null
                            && !id.equals("FP01")
                            && !id.equals("W01")
                            && !id.equals("W07")
                            && !id.equals("W08")
                            && !id.equals("W16")) {

                        Item roomItem = createItemCopy(item);

                        if (id.equals("W12")
                                && (roomID.trim().equals("R02")
                                || roomID.trim().equals("R06"))) {
                            room.addRandomItem(roomItem);
                        } else {
                            room.addItem(roomItem);
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Error loading items: " + e.getMessage(), e);
        }
    }

    public Item createItemCopy(Item item) {
        if (item instanceof Weapon) {
            Weapon weapon = (Weapon) item;

            return new Weapon(
                    item.getId(), item.getName(),
                    item.getDescription(), item.getValueSilver(),
                    weapon.getAttackBonus(), weapon.getDurabilityLimit());
        }

        if (item instanceof Armor) {
            Armor armor = (Armor) item;

            return new Armor(
                    item.getId(), item.getName(),
                    item.getDescription(), item.getValueSilver(),
                    armor.getDefenseBonus(), armor.getDurabilityLimit());
        }

        if (item instanceof Consumable) {
            Consumable consumable = (Consumable) item;

            return new Consumable(
                    item.getId(), item.getName(),
                    item.getDescription(), item.getValueSilver(),
                    consumable.getHealAmount(), 1);
        }

        if (item instanceof KeyItem) {
            KeyItem key = (KeyItem) item;

            return new KeyItem(
                    item.getId(), item.getName(),
                    item.getDescription(), item.getValueSilver(),
                    key.getRoomId(), key.getDoorId());
        }

        Item copy = new Item(
                item.getId(), item.getName(),
                item.getDescription(), item.getValueSilver());

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

        try (BufferedReader reader =
                     Files.newBufferedReader(file, StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {
                line = combatLine(line, ++lineNumber);

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] fields = line.split("\\|", -1);

                if (fields.length != 10) {
                    throw combatError(
                            file, lineNumber,
                            "Expected 10 pipe-separated enemy fields.");
                }

                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].trim();
                }

                if (fields[0].isEmpty()
                        || fields[1].isEmpty()
                        || fields[2].isEmpty()) {
                    throw combatError(
                            file, lineNumber,
                            "Enemy ID, name, and description are required.");
                }

                if (!ids.add(fields[0])) {
                    throw combatError(
                            file, lineNumber,
                            "Duplicate enemy ID: " + fields[0]);
                }

                int hp = combatInteger(
                        fields[3], 1, file, lineNumber, "maxHP");

                int attack = combatInteger(
                        fields[4], 0, file, lineNumber, "attack");

                int defense = combatInteger(
                        fields[5], Integer.MIN_VALUE,
                        file, lineNumber, "defense");

                if (!fields[6].equals("true")
                        && !fields[6].equals("false")) {
                    throw combatError(
                            file, lineNumber,
                            "boss must be true or false.");
                }

                int silver = combatInteger(
                        fields[7], 0, file, lineNumber, "silverDrop");

                int gold = combatInteger(
                        fields[8], 0, file, lineNumber, "goldDrop");

                Map<String, Integer> drops = new LinkedHashMap<>();

                if (!fields[9].equals("-")) {
                    for (String entry : fields[9].split(",", -1)) {
                        String[] drop = entry.split(":", -1);

                        if (drop.length != 2
                                || drop[0].trim().isEmpty()) {
                            throw combatError(
                                    file, lineNumber,
                                    "Drops must be itemID:quantity or -.");
                        }

                        String itemID = drop[0].trim();

                        int quantity = combatInteger(
                                drop[1].trim(), 1, file,
                                lineNumber, "drop quantity");

                        if (drops.putIfAbsent(itemID, quantity) != null) {
                            throw combatError(
                                    file, lineNumber,
                                    "Duplicate drop item ID: " + itemID);
                        }
                    }
                }

                try {
                    Enemy enemy = new Enemy(
                            fields[0],
                            fields[1],
                            fields[2].replace("\\n", "\n"),
                            hp, hp, attack, defense,
                            Boolean.parseBoolean(fields[6]),
                            silver, gold, drops
                    );

                    enemies.add(enemy);

                } catch (IllegalArgumentException e) {
                    throw new IOException(
                            file + ":" + lineNumber + ": "
                                    + e.getMessage(), e);
                }
            }
        }

        if (enemies.isEmpty()) {
            throw combatError(
                    file, Math.max(1, lineNumber),
                    "No enemy records. Add the official enemy data before loading.");
        }

        return enemies;
    }

    public void loadEncounterPlacements(GameWorld world) {
        Path file = Paths.get(dataDirectory, "encounters.txt");

        if (!Files.exists(file)) {
            throw new IllegalStateException(
                    "Missing data/encounters.txt");
        }

        try (BufferedReader reader =
                     Files.newBufferedReader(file, StandardCharsets.UTF_8)) {

            String line;
            int n = 0;

            while ((line = reader.readLine()) != null) {
                n++;
                line = line.trim();

                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }

                String[] fields = line.split("\\|", -1);

                if (fields.length != 2) {
                    throw new IOException(
                            "Expected roomID|enemyIDs at line " + n);
                }

                Room room = world.getRoomByID(fields[0].trim());

                if (room == null) {
                    throw new IOException(
                            "Unknown room " + fields[0] + " at line " + n);
                }

                for (String id : fields[1].split(",")) {
                    Enemy enemy = world.getEnemyByID(id.trim());

                    if (enemy == null) {
                        throw new IOException(
                                "Unknown enemy " + id + " at line " + n);
                    }

                    if (!room.getEnemies().contains(enemy)) {
                        room.getEnemies().add(enemy);
                    }
                }
            }

        } catch (IOException error) {
            throw new UncheckedIOException(
                    "Loading encounters failed: "
                            + error.getMessage(), error);
        }
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

        try (BufferedReader reader =
                     Files.newBufferedReader(file, StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {
                line = combatLine(line, ++lineNumber);

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] setting = line.split("=", -1);

                if (setting.length != 2
                        || setting[1].trim().isEmpty()) {
                    throw combatError(
                            file, lineNumber, "Expected key=value.");
                }

                String key = setting[0].trim();
                String value = setting[1].trim();

                if (rules.containsKey(key)) {
                    throw combatError(
                            file, lineNumber,
                            "Duplicate battle setting: " + key);
                }

                if (key.equals("movesPerTurn")) {
                    combatInteger(
                            value, 1, file, lineNumber, key);

                } else if (key.equals("baseFleeChance")
                        || key.equals("fleeIncrease")) {
                    combatProbability(
                            value, file, lineNumber, key);

                } else {
                    throw combatError(
                            file, lineNumber,
                            "Unknown battle setting: " + key);
                }

                rules.setProperty(key, value);
            }
        }

        String[] requiredSettings = {
                "movesPerTurn",
                "baseFleeChance",
                "fleeIncrease"
        };

        for (String key : requiredSettings) {
            if (!rules.containsKey(key)) {
                throw combatError(
                        file, Math.max(1, lineNumber),
                        "Missing battle setting: " + key);
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

    private static int combatInteger(
            String text,
            int minimum,
            Path file,
            int line,
            String field) throws IOException {

        try {
            int value = Integer.parseInt(text);

            if (value < minimum) {
                throw new NumberFormatException();
            }

            return value;

        } catch (NumberFormatException e) {
            throw combatError(
                    file, line,
                    field + " must be a whole number >= "
                            + minimum + ".");
        }
    }

    private static void combatProbability(
            String text,
            Path file,
            int line,
            String field) throws IOException {

        try {
            double value = Double.parseDouble(text);

            if (!Double.isFinite(value)
                    || value < 0 || value > 1) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {
            throw combatError(
                    file, line,
                    field + " must be a number between 0 and 1.");
        }
    }

    private static IOException combatError(
            Path file, int line, String message) {

        return new IOException(
                file + ":" + line + ": " + message);
    }

    public List<String> loadDialogue() {
        throw new UnsupportedOperationException(
                "Dialogue loading has not been implemented yet.");
    }

    public List<Puzzle> loadPuzzles() {
        List<Puzzle> puzzles = new ArrayList<>();
        File file = new File(dataDirectory, "puzzles.txt");

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {

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

                String description =
                        data[3].replace("\\n", "\n");

                String lockedDescription = data[4].equals("-")
                        ? null : data[4].replace("\\n", "\n");

                String solveText =
                        data[5].replace("\\n", "\n");

                List<String> leverIDs = new ArrayList<>();

                if (!data[6].equals("-")) {
                    leverIDs = Arrays.asList(data[6].split(","));
                }

                Set<String> solution = new LinkedHashSet<>();

                if (!data[7].equals("-")) {
                    solution.addAll(
                            Arrays.asList(data[7].split(",")));
                }

                String requiredItemID =
                        data[8].equals("-") ? null : data[8];

                Map<String, Integer> rewardItems =
                        new LinkedHashMap<>();

                if (!data[9].equals("-")) {
                    String[] rewards = data[9].split(",");

                    for (String reward : rewards) {
                        String[] itemData = reward.split(":");

                        String itemID = itemData[0].trim();

                        int quantity =
                                Integer.parseInt(itemData[1].trim());

                        rewardItems.put(itemID, quantity);
                    }
                }

                Puzzle puzzle = new Puzzle(
                        puzzleID, name, roomID, description,
                        lockedDescription, solveText, leverIDs,
                        solution, requiredItemID, rewardItems
                );

                puzzles.add(puzzle);
            }

        } catch (IOException e) {
            System.err.println("Error loading puzzles:");
            e.printStackTrace();
        }

        return puzzles;
    }

    public boolean saveGame(GameMemory memory) {
        if (memory == null || !memory.isValid()
                || saveDirectory == null
                || saveDirectory.trim().isEmpty()) {
            return false;
        }

        String saveName = memory.getSaveName().trim();

        if (saveName.isEmpty()
                || saveName.contains("/")
                || saveName.contains("\\")
                || saveName.contains("|")
                || saveName.contains("\n")
                || saveName.contains("\r")) {
            return false;
        }

        String saveData = memory.toText();

        if (saveData.isEmpty()) {
            return false;
        }

        try {
            Path saveFolder =
                    Paths.get(saveDirectory).toAbsolutePath().normalize();

            Files.createDirectories(saveFolder);

            Path saveFile =
                    saveFolder.resolve(saveName + ".txt").normalize();

            if (!saveFile.getParent().equals(saveFolder)) {
                return false;
            }

            Files.write(
                    saveFile,
                    saveData.getBytes(StandardCharsets.UTF_8)
            );

            return true;

        } catch (IOException
                 | InvalidPathException
                 | SecurityException e) {
            return false;
        }
    }

    public GameMemory restoreGame(String saveName) {
        if (saveName == null || saveName.trim().isEmpty()
                || saveDirectory == null
                || saveDirectory.trim().isEmpty()) {
            return null;
        }

        saveName = saveName.trim();

        if (saveName.contains("/")
                || saveName.contains("\\")
                || saveName.contains("|")
                || saveName.contains("\n")
                || saveName.contains("\r")) {
            return null;
        }

        try {
            Path saveFolder =
                    Paths.get(saveDirectory).toAbsolutePath().normalize();

            Path saveFile =
                    saveFolder.resolve(saveName + ".txt").normalize();

            if (!saveFile.getParent().equals(saveFolder)
                    || !Files.isRegularFile(saveFile)) {
                return null;
            }

            Map<String, String> savedFields = new LinkedHashMap<>();
            List<String> visitedRoomIDs = new ArrayList<>();
            List<String> completedPuzzleIDs = new ArrayList<>();
            List<String> defeatedEnemyIDs = new ArrayList<>();
            List<String[]> savedInventory = new ArrayList<>();

            try (BufferedReader reader =
                         Files.newBufferedReader(
                                 saveFile, StandardCharsets.UTF_8)) {

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
                            savedInventory.add(data);
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

                        case "DEFEATED_ENEMY":
                            if (data.length != 2) {
                                return null;
                            }
                            defeatedEnemyIDs.add(data[1].trim());
                            break;

                        default:
                            if (data.length != 2
                                    || savedFields.put(
                                    label, data[1].trim()) != null) {
                                return null;
                            }
                            break;
                    }
                }
            }

            String[] requiredFields = {
                    "SAVE_NAME",
                    "PLAYER_NAME",
                    "CURRENT_HP",
                    "MAX_HP",
                    "ATTACK",
                    "DEFENSE",
                    "CURRENT_ROOM",
                    "SILVER_COINS",
                    "GOLD_COINS",
                    "GAME_COMPLETED"
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

            String completed =
                    savedFields.get("GAME_COMPLETED");

            if (!completed.equals("true")
                    && !completed.equals("false")) {
                return null;
            }

            GameWorld restoredWorld = new GameWorld();

            for (Room room : loadRooms()) {
                restoredWorld.addRoom(room);
            }

            loadItems(restoredWorld);

            for (Enemy enemy : loadEnemies()) {
                restoredWorld.addEnemy(enemy);
            }

            loadEncounterPlacements(restoredWorld);

            for (String id : defeatedEnemyIDs) {
                Enemy defeated = restoredWorld.getEnemyByID(id);

                if (defeated != null) {
                    defeated.markDefeated();
                }
            }

            for (Puzzle puzzle : loadPuzzles()) {
                restoredWorld.addPuzzle(puzzle);

                Room puzzleRoom =
                        restoredWorld.getRoomByID(puzzle.getRoomID());

                if (puzzleRoom != null) {
                    puzzleRoom.getPuzzles().add(puzzle);
                }
            }

            Room currentRoom =
                    restoredWorld.getRoomByID(
                            savedFields.get("CURRENT_ROOM"));

            if (currentRoom == null) {
                return null;
            }

            List<String> restoredVisitedRooms = new ArrayList<>();

            for (String roomID : visitedRoomIDs) {
                if (restoredWorld.getRoomByID(roomID) != null
                        && !restoredVisitedRooms.contains(roomID)) {
                    restoredVisitedRooms.add(roomID);
                }
            }

            if (!restoredVisitedRooms.contains(
                    currentRoom.getRoomID())) {
                restoredVisitedRooms.add(currentRoom.getRoomID());
            }

            Inventory inventory =
                    new Inventory(Inventory.BASE_CAPACITY);

            for (String[] record : savedInventory) {
                String id = record[1].trim();
                Item definition = restoredWorld.getItemByID(id);

                if (definition == null) {
                    return null;
                }

                int count = Integer.parseInt(record[3]);
                int wear = Integer.parseInt(record[4]);

                if (count <= 0 || wear < 0) {
                    return null;
                }

                Item item = createItemCopy(definition);
                item.setQuantity(count);

                if (item instanceof Weapon) {
                    ((Weapon) item).setAttackCounter(wear);
                }

                if (item instanceof Armor) {
                    ((Armor) item).setHitsTakenCounter(wear);
                }

                if (!inventory.addItem(item)) {
                    return null;
                }
            }

            for (String puzzleID : completedPuzzleIDs) {
                Puzzle puzzle =
                        restoredWorld.getPuzzleByID(puzzleID);

                if (puzzle != null) {
                    puzzle.setCompleted(true);

                    for (Room room : restoredWorld.getRooms()) {
                        if (puzzleID.equalsIgnoreCase(
                                room.getRequiredPuzzleID())) {
                            room.unlock();
                        }
                    }
                }
            }

            Player restoredPlayer = new Player(
                    savedFields.get("PLAYER_NAME"),
                    Integer.parseInt(savedFields.get("CURRENT_HP")),
                    Integer.parseInt(savedFields.get("MAX_HP")),
                    Integer.parseInt(savedFields.get("ATTACK")),
                    Integer.parseInt(savedFields.get("DEFENSE")),
                    inventory,
                    currentRoom,
                    null,
                    null,
                    Integer.parseInt(savedFields.get("SILVER_COINS")),
                    Integer.parseInt(savedFields.get("GOLD_COINS")),
                    restoredVisitedRooms,
                    Boolean.parseBoolean(completed)
            );

            Item equippedWeapon = inventory.getItem(
                    savedFields.getOrDefault("EQUIPPED_WEAPON", ""));

            if (equippedWeapon instanceof Weapon) {
                restoredPlayer.setEquippedWeapon(
                        (Weapon) equippedWeapon);
            }

            Item equippedArmor = inventory.getItem(
                    savedFields.getOrDefault("EQUIPPED_ARMOR", ""));

            if (equippedArmor instanceof Armor) {
                restoredPlayer.setEquippedArmor(
                        (Armor) equippedArmor);
            }

            return new GameMemory(
                    saveName, restoredPlayer, restoredWorld);

        } catch (IOException | RuntimeException e) {
            return null;
        }
    }
}