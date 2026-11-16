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
import java.util.Locale;

import Kelvin.*;

public class DataLoader {

    private String dataDirectory;
    private String saveDirectory;

    public DataLoader(String dataDirectory, String saveDirectory) {
        this.dataDirectory = dataDirectory;
        this.saveDirectory = saveDirectory;
    }

    public List<Room> loadRooms() {
        throw new UnsupportedOperationException(
                "Room loading has not been implemented yet.");
    }

    public List<Item> loadItems() {
        try {
            return readItemDefinitions();
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    private List<Item> readItemDefinitions() throws IOException {
        Path file = Paths.get(dataDirectory, "items.txt");
        List<Item> items = new ArrayList<>();
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

                if (fields.length != 9) {
                    throw combatError(
                            file,
                            lineNumber,
                            "Expected 9 pipe-separated item fields.");
                }

                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].trim();
                }

                if (fields[0].isEmpty()
                        || fields[1].isEmpty()
                        || fields[2].isEmpty()) {

                    throw combatError(
                            file,
                            lineNumber,
                            "Item ID, type, and name are required.");
                }

                if (!ids.add(fields[0])) {
                    throw combatError(
                            file,
                            lineNumber,
                            "Duplicate item ID: " + fields[0]);
                }

                items.add(createItem(file, lineNumber, fields));
            }
        }

        if (items.isEmpty()) {
            throw combatError(
                    file,
                    Math.max(1, lineNumber),
                    "No item records. Add the official item data before loading.");
        }

        return items;
    }

    private static Item createItem(Path file, int lineNumber, String[] fields)
            throws IOException {

        String id = fields[0];
        String type = fields[1].toUpperCase(Locale.ROOT);
        String name = fields[2];
        String description = fields[3].replace("\\n", "\n");
        int valueSilver = parseItemInteger(
                fields[4], 0, file, lineNumber, "valueSilver");
        int statValue = parseItemInteger(
                fields[5], Integer.MIN_VALUE, file, lineNumber, "statValue");
        int durability = parseItemInteger(
                fields[6], Integer.MIN_VALUE, file, lineNumber, "durability");
        String rooms = fields[7];
        String effect = fields[8];

        switch (type) {
            case "WEAPON":
                return new Weapon(
                        id,
                        name,
                        description,
                        valueSilver,
                        statValue,
                        durability);

            case "CONSUMABLE":
                return new Consumable(
                        id,
                        name,
                        description,
                        valueSilver,
                        statValue,
                        Math.max(1, durability));

            case "KEY":
                String target = effect.startsWith("UNLOCKS:")
                        ? effect.substring("UNLOCKS:".length()).trim()
                        : effect;

                if (target.contains(":")) {
                    String[] pieces = target.split(":", 2);
                    return new KeyItem(
                            id,
                            name,
                            description,
                            valueSilver,
                            pieces[0].trim(),
                            pieces[1].trim());
                }

                return new KeyItem(
                        id,
                        name,
                        description,
                        valueSilver,
                        target,
                        "");

            case "ARMOR":
                return new Armor(
                        id,
                        name,
                        description,
                        valueSilver,
                        statValue,
                        durability);

            case "CHARM":
            case "UTILITY":
            case "VALUABLE":
            case "CURRENCY":
            case "TROPHY":
            default:
                Item item = new Item(id, name, description, valueSilver);

                if (type.equals("CURRENCY")) {
                    item.setStackable(true);
                    item.setQuantity(Math.max(1, statValue));
                }

                if (!rooms.equals("-") && !rooms.isEmpty()) {
                    item.setDescription(item.getDescription()
                            + "\nLocations: " + rooms);
                }

                return item;
        }
    }

    private static int parseItemInteger(
            String text,
            int minimum,
            Path file,
            int lineNumber,
            String field) throws IOException {

        try {
            int value = Integer.parseInt(text);

            if (value < minimum) {
                throw new NumberFormatException();
            }

            return value;

        } catch (NumberFormatException e) {
            throw combatError(
                    file,
                    lineNumber,
                    field + " must be a whole number >= "
                            + minimum + ".");
        }
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
                            file,
                            lineNumber,
                            "Expected 10 pipe-separated enemy fields.");
                }

                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].trim();
                }

                if (fields[0].isEmpty()
                        || fields[1].isEmpty()
                        || fields[2].isEmpty()) {

                    throw combatError(
                            file,
                            lineNumber,
                            "Enemy ID, name, and description are required.");
                }

                if (!ids.add(fields[0])) {
                    throw combatError(
                            file,
                            lineNumber,
                            "Duplicate enemy ID: " + fields[0]);
                }

                int hp = combatInteger(
                        fields[3], 1, file, lineNumber, "maxHP");

                int attack = combatInteger(
                        fields[4], 0, file, lineNumber, "attack");

                int defense = combatInteger(
                        fields[5],
                        Integer.MIN_VALUE,
                        file,
                        lineNumber,
                        "defense");

                if (!fields[6].equals("true")
                        && !fields[6].equals("false")) {

                    throw combatError(
                            file,
                            lineNumber,
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
                                    file,
                                    lineNumber,
                                    "Drops must be itemID:quantity or -.");
                        }

                        String itemID = drop[0].trim();

                        int quantity = combatInteger(
                                drop[1].trim(),
                                1,
                                file,
                                lineNumber,
                                "drop quantity");

                        if (drops.putIfAbsent(itemID, quantity) != null) {
                            throw combatError(
                                    file,
                                    lineNumber,
                                    "Duplicate drop item ID: " + itemID);
                        }
                    }
                }

                try {
                    Enemy enemy = new Enemy(
                            fields[0],
                            fields[1],
                            fields[2].replace("\\n", "\n"),
                            hp,
                            hp,
                            attack,
                            defense,
                            Boolean.parseBoolean(fields[6]),
                            silver,
                            gold,
                            drops);

                    enemies.add(enemy);

                } catch (IllegalArgumentException e) {
                    throw new IOException(
                            file + ":" + lineNumber + ": "
                                    + e.getMessage(),
                            e);
                }
            }
        }

        if (enemies.isEmpty()) {
            throw combatError(
                    file,
                    Math.max(1, lineNumber),
                    "No enemy records. Add the official enemy data before loading.");
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
                            file,
                            lineNumber,
                            "Expected key=value.");
                }

                String key = setting[0].trim();
                String value = setting[1].trim();

                if (rules.containsKey(key)) {
                    throw combatError(
                            file,
                            lineNumber,
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
                            file,
                            lineNumber,
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
                        file,
                        Math.max(1, lineNumber),
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
                    file,
                    line,
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
                    || value < 0
                    || value > 1) {

                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {
            throw combatError(
                    file,
                    line,
                    field + " must be a number between 0 and 1.");
        }
    }

    private static IOException combatError(
            Path file,
            int line,
            String message) {

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
                    System.out.println(
                            "Invalid puzzle data: " + line);
                    continue;
                }

                String puzzleID = data[0].trim();
                String name = data[1].trim();
                String roomID = data[2].trim();

                String description =
                        data[3].replace("\\n", "\n");

                String lockedDescription =
                        data[4].equals("-")
                                ? null
                                : data[4].replace("\\n", "\n");

                String solveText =
                        data[5].replace("\\n", "\n");

                List<String> leverIDs = new ArrayList<>();

                if (!data[6].equals("-")) {
                    leverIDs =
                            Arrays.asList(data[6].split(","));
                }

                Set<String> solution =
                        new LinkedHashSet<>();

                if (!data[7].equals("-")) {
                    solution.addAll(
                            Arrays.asList(data[7].split(",")));
                }

                String requiredItemID =
                        data[8].equals("-")
                                ? null
                                : data[8];

                Map<String, Integer> rewardItems =
                        new LinkedHashMap<>();

                if (!data[9].equals("-")) {
                    String[] rewards =
                            data[9].split(",");

                    for (String reward : rewards) {
                        String[] itemData =
                                reward.split(":");

                        String itemID =
                                itemData[0].trim();

                        int quantity =
                                Integer.parseInt(
                                        itemData[1].trim());

                        rewardItems.put(
                                itemID,
                                quantity);
                    }
                }

                Puzzle puzzle = new Puzzle(
                        puzzleID,
                        name,
                        roomID,
                        description,
                        lockedDescription,
                        solveText,
                        leverIDs,
                        solution,
                        requiredItemID,
                        rewardItems);

                puzzles.add(puzzle);
            }

        } catch (IOException e) {
            System.err.println(
                    "Error loading puzzles: "
                            + e.getMessage());
        }

        return puzzles;
    }

    public void saveGame(GameMemory memory) {
        throw new UnsupportedOperationException(
                "Game saving has not been implemented yet.");
    }

    public GameMemory restoreGame(String saveName) {
        throw new UnsupportedOperationException(
                "Game restoration has not been implemented yet.");
    }
}