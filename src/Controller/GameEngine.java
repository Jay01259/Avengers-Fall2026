package Controller;

import java.util.Scanner;
import View.GameView;
import Model.*;

public class GameEngine {

    private final Scanner scanner = new Scanner(System.in);

    private boolean gameRunning;
    Player player;
    GameWorld gameWorld;
    GameView view = new GameView();
    Battle currentBattle;
    DataLoader dataLoader = new DataLoader("data", "saves");

    public static void main(String[] args) {
        GameEngine gameEngine = new GameEngine();
        gameEngine.startGame();
    }

    public void startGame() {

        while (true) {
            view.displayMessage("Would you like to start a new game or load a saved game?");
            view.displayMessage("Enter 'new' to start a new game or 'load' to load a saved game.");
            view.displayMessage("Your choice: ");

            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("new")) {
                startNewGame();
                break;
            }
            else if (choice.equalsIgnoreCase("load")) {
                loadGame();
                break;
            }
            else {
                view.displayMessage("Invalid input. Please enter 'new' or 'load'.");
            }
        }
    }


    // Update this method
    public void startNewGame() {
        view.displayTitle();
        view.displayMessage("What is your character's name?");
        String playerName = scanner.nextLine().trim();

        while (playerName.isEmpty()) {
            view.displayMessage("Your character's name cannot be empty.");
            playerName = scanner.nextLine().trim();
        }

        gameWorld = new GameWorld();

        // Load world data when available.
        try {
            for (Room room : dataLoader.loadRooms()) {
                gameWorld.addRoom(room);
            }
        } catch (UnsupportedOperationException ignored) {
        }

        try {
            for (Item item : dataLoader.loadItems()) {
                gameWorld.addItem(item);
            }
        } catch (UnsupportedOperationException ignored) {
        }

        try {
            for (Enemy enemy : dataLoader.loadEnemies()) {
                gameWorld.addEnemy(enemy);
            }
        } catch (RuntimeException ignored) {
        }

        try {
            for (Puzzle puzzle : dataLoader.loadPuzzles()) {
                gameWorld.addPuzzle(puzzle);
                Room room = gameWorld.getRoomByID(puzzle.getRoomID());
                if (room != null && !room.getPuzzles().contains(puzzle)) {
                    room.getPuzzles().add(puzzle);
                }
            }
        } catch (RuntimeException ignored) {
        }

        Room startingRoom = gameWorld.getRoomByID("R01");
        if (startingRoom == null) {
            startingRoom = gameWorld.getStartRoom();
        }
        if (startingRoom == null) {
            startingRoom = new Room("R01", "Player's Room", "Your room is a modest student chamber in the Caine Drux University dormitory.",
                    new java.util.ArrayList<>(), new java.util.ArrayList<>(), new java.util.ArrayList<>(),
                    new java.util.ArrayList<>(), null, null, null, null);
            gameWorld.addRoom(startingRoom);
        }

        Inventory inventory = new Inventory();

        player = new Player(playerName, 100, 100, 5, 0, inventory, startingRoom, null, null, 0, 0,
                new java.util.ArrayList<>(), false);

        player.markRoomVisited(startingRoom.getRoomID());

        view.displayStartNewGame(playerName);

        view.displayRoom(startingRoom);

        gameLoop();
    }

    public void gameLoop() {

        gameRunning = true;

        while (gameRunning && player != null && player.isAlive() && !player.isGameCompleted()) {

            view.displayMessage("What would you like to do?");

            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                view.displayMessage("Please enter a command.");
                continue;
            }
            processPlayerInput(input);
        }
    }



    // Replace processPlayerInput(...) with this
    public void processPlayerInput(String input) {
        if (player == null) {
            view.displayMessage("Game is not initialized.");
            return;
        }

        String command = input.trim().replaceAll("\\s+", " ");
        if (command.isEmpty()) {
            view.displayMessage("Please enter a command.");
            return;
        }

        String upper = command.toUpperCase();
        Room currentRoom = player.getCurrentRoom();

        // --- No-arg commands ---
        switch (upper) {
            case "HELP" -> {
                view.displayHelp();
                return;
            }
            case "EXITS" -> {
                java.util.List<String> exits = new java.util.ArrayList<>();

                if (currentRoom.getNorth() != null && !currentRoom.getNorth().isBlank()) exits.add("North - " + currentRoom.getNorth());
                if (currentRoom.getSouth() != null && !currentRoom.getSouth().isBlank()) exits.add("South - " + currentRoom.getSouth());
                if (currentRoom.getEast() != null && !currentRoom.getEast().isBlank()) exits.add("East - " + currentRoom.getEast());
                if (currentRoom.getWest() != null && !currentRoom.getWest().isBlank()) exits.add("West - " + currentRoom.getWest());

                for (String visit : currentRoom.getVisitRooms()) {
                    exits.add("Visit - " + visit);
                }

                if (currentRoom.getLeaveTo() != null && !currentRoom.getLeaveTo().isBlank()) {
                    exits.add("Leave - " + currentRoom.getLeaveTo());
                }

                view.displayExits(exits);
                return;
            }
            case "INVENTORY" -> {
                view.displayInventory(player.getInventory(), false);
                return;
            }
            case "CHECK HEALTH" -> {
                view.displayHealth(player);
                return;
            }
            case "DISPLAY" -> {
                Puzzle puzzle = gameWorld.getPuzzleInRoom(currentRoom);
                if (puzzle == null) {
                    view.displayMessage("You are not in a Puzzle Room.");
                    return;
                }

                String requiredItemId = puzzle.getRequiredItemID();
                boolean hasRequiredItem = requiredItemId == null || requiredItemId.isBlank()
                        || player.getInventory().getItem(requiredItemId) != null;

                view.displayPuzzle(puzzle, hasRequiredItem);
                return;
            }
            case "RESET" -> {
                Puzzle puzzle = gameWorld.getPuzzleInRoom(currentRoom);
                if (puzzle == null) {
                    view.displayMessage("You are not in a Puzzle Room.");
                    return;
                }

                view.displayMessage(puzzle.reset(currentRoom.getName()));
                return;
            }
            case "SOLVE" -> {
                Puzzle puzzle = gameWorld.getPuzzleInRoom(currentRoom);
                if (puzzle == null) {
                    view.displayMessage("You are not in a Puzzle Room.");
                    return;
                }

                String requiredItemId = puzzle.getRequiredItemID();
                boolean hasRequiredItem = requiredItemId == null || requiredItemId.isBlank()
                        || player.getInventory().getItem(requiredItemId) != null;

                if (puzzle.checkSolution(hasRequiredItem)) {
                    view.displayMessage("Your solution is correct.");
                    view.displayMessage(puzzle.getSolveText());
                } else {
                    view.displayMessage("Your solution is incorrect, try again.");
                }
                return;
            }
            case "OPEN CHEST" -> {
                StorageChest chest = currentRoom.getChest();
                if (chest == null) {
                    view.displayMessage("There is no chest in this room.");
                } else {
                    view.displayMessage(chest.open());
                }
                return;
            }
            case "CLOSE CHEST" -> {
                StorageChest chest = currentRoom.getChest();
                if (chest == null) {
                    view.displayMessage("There is no chest in this room.");
                } else {
                    view.displayMessage(chest.close());
                }
                return;
            }
            case "LEAVE" -> {
                Room leaveRoom = gameWorld.getLeaveRoom(currentRoom);
                if (leaveRoom == null) {
                    view.displayMessage("You cannot leave this room using that command.");
                    return;
                }

                if (!gameWorld.isRoomUnlocked(leaveRoom, player)) {
                    view.displayMessage("The room is locked.");
                    return;
                }

                player.setCurrentRoom(leaveRoom);
                player.markRoomVisited(leaveRoom.getRoomID());
                view.displayRoom(leaveRoom);
                return;
            }
            case "QUIT" -> {
                quitGame();
                return;
            }
            case "SAVE" -> {
                saveGame();
                return;
            }
            case "LOAD" -> {
                loadGame();
                return;
            }
            case "NORTH", "N" -> {
                movePlayer("NORTH");
                return;
            }
            case "SOUTH", "S" -> {
                movePlayer("SOUTH");
                return;
            }
            case "EAST", "E" -> {
                movePlayer("EAST");
                return;
            }
            case "WEST", "W" -> {
                movePlayer("WEST");
                return;
            }
        }

        // --- Commands with args ---
        String[] parts = command.split(" ", 2);
        String action = parts[0].toUpperCase();
        String argument = parts.length > 1 ? parts[1].trim() : "";

        switch (action) {
            case "VISIT" -> {
                if (argument.isEmpty()) {
                    view.displayMessage("Visit where?");
                    return;
                }

                Room target = gameWorld.getVisitRoom(currentRoom, argument);
                if (target == null) {
                    view.displayMessage("You cannot go there.");
                    return;
                }

                if (!gameWorld.isRoomUnlocked(target, player)) {
                    view.displayMessage("The room is locked.");
                    return;
                }

                player.setCurrentRoom(target);
                player.markRoomVisited(target.getRoomID());
                view.displayRoom(target);
            }
            case "INSPECT" -> {
                if (argument.isEmpty()) {
                    view.displayMessage("Inspect what?");
                    return;
                }

                if (argument.equalsIgnoreCase("room") || argument.equalsIgnoreCase(currentRoom.getName())) {
                    view.displayMessage(currentRoom.inspect());
                    return;
                }

                if (currentBattle != null && !currentBattle.isOver()) {
                    Enemy battleEnemy = currentBattle.getEnemy();
                    if (argument.equalsIgnoreCase("monster") || argument.equalsIgnoreCase("enemy") || argument.equalsIgnoreCase(battleEnemy.getName())) {
                        view.displayEnemyStats(battleEnemy, battleEnemy.getStatusHint());
                        return;
                    }
                }

                Item invItem = player.getInventory().getItem(argument);
                if (invItem != null) {
                    view.displayItem(invItem);
                    return;
                }

                Item roomItem = currentRoom.findItem(argument);
                if (roomItem != null) {
                    view.displayItem(roomItem);
                    return;
                }

                view.displayMessage("Item cannot be inspected.");
            }
            case "PICKUP" -> {
                if (argument.isEmpty()) {
                    view.displayMessage("There is nothing currently available to be picked up");
                    return;
                }

                Item roomItem = currentRoom.findItem(argument);
                if (roomItem == null) {
                    view.displayMessage("There is nothing currently available to be picked up");
                    return;
                }

                if (player.pickUpItem(roomItem)) {
                    view.displayMessage(roomItem.getName() + " was picked up.");
                } else {
                    view.displayMessage("cannot pick up, inventory is full. You must drop something first");
                }
            }
            case "DROP" -> {
                if (argument.isEmpty()) {
                    view.displayMessage("Drop what?");
                    return;
                }

                Item invItem = player.getInventory().getItem(argument);
                if (invItem == null) {
                    view.displayMessage("There is no such item in inventory.");
                    return;
                }

                if (player.dropItem(invItem)) {
                    view.displayMessage(invItem.getName() + " was dropped.");
                } else {
                    view.displayMessage("You cannot drop that item.");
                }
            }
            case "USE" -> {
                if (argument.isEmpty()) {
                    view.displayMessage("Use what?");
                    return;
                }

                int amount = 1;
                String itemName = argument;

                int idx = argument.toLowerCase().lastIndexOf(" x");
                if (idx > 0) {
                    String qty = argument.substring(idx + 2).trim();
                    try {
                        amount = Integer.parseInt(qty);
                        itemName = argument.substring(0, idx).trim();
                    } catch (NumberFormatException ignored) {
                        amount = 1;
                        itemName = argument;
                    }
                }

                Item invItem = player.getInventory().getItem(itemName);
                if (invItem == null) {
                    view.displayMessage("You do not have that item.");
                    return;
                }

                int beforeHP = player.getCurrentHP();
                boolean used = player.useItem(invItem, amount);
                if (!used) {
                    view.displayMessage("That item cannot be used right now.");
                    return;
                }

                int healed = player.getCurrentHP() - beforeHP;
                if (healed > 0) {
                    view.displayMessage("You recovered " + healed + " HP.");
                } else {
                    view.displayMessage("Used " + invItem.getName() + ".");
                }
            }
            case "EQUIP" -> {
                if (argument.isEmpty()) {
                    view.displayMessage("Equip what?");
                    return;
                }

                Item invItem = player.getInventory().getItem(argument);
                if (invItem == null) {
                    view.displayMessage("Item is not in inventory.");
                    return;
                }

                if (player.equipItem(invItem)) {
                    view.displayMessage(invItem.getName() + " equipped.");
                } else {
                    view.displayMessage("That item cannot be equipped.");
                }
            }
            case "PULL" -> {
                if (argument.isEmpty()) {
                    view.displayMessage("Pull which lever?");
                    return;
                }

                Puzzle puzzle = gameWorld.getPuzzleInRoom(currentRoom);
                if (puzzle == null) {
                    view.displayMessage("You are not in a Puzzle Room!");
                    return;
                }

                String requiredItemId = puzzle.getRequiredItemID();
                boolean hasRequiredItem = requiredItemId == null || requiredItemId.isBlank()
                        || player.getInventory().getItem(requiredItemId) != null;

                view.displayMessage(puzzle.pullLever(argument, hasRequiredItem));
            }
            case "TALK" -> view.displayMessage("Talk command is not available yet.");
            case "BUY" -> view.displayMessage("Buy command is not available yet.");
            case "SELL" -> view.displayMessage("Sell command is not available yet.");
            case "STORE" -> {
                StorageChest chest = currentRoom.getChest();
                if (chest == null || !chest.isOpen()) {
                    view.displayMessage("The chest is not open.");
                    return;
                }

                if (argument.isEmpty()) {
                    view.displayMessage("Store what?");
                    return;
                }

                Item invItem = player.getInventory().getItem(argument);
                if (invItem == null) {
                    view.displayMessage("You do not have that item.");
                    return;
                }

                if (chest.storeItem(invItem)) {
                    player.getInventory().removeItem(invItem);
                    view.displayMessage(invItem.getName() + " stored.");
                } else {
                    view.displayMessage("Unable to store item.");
                }
            }
            case "RETRIEVE" -> {
                StorageChest chest = currentRoom.getChest();
                if (chest == null || !chest.isOpen()) {
                    view.displayMessage("The chest is not open.");
                    return;
                }

                if (argument.isEmpty()) {
                    view.displayMessage("Retrieve what?");
                    return;
                }

                Item item = chest.retrieveItem(argument);
                if (item == null) {
                    view.displayMessage("That item is not in the chest.");
                    return;
                }

                if (player.getInventory().addItem(item)) {
                    view.displayMessage(item.getName() + " retrieved.");
                } else {
                    chest.storeItem(item);
                    view.displayMessage("cannot pick up, inventory is full. You must drop something first");
                }
            }
            case "ATTACK" -> {
                if (currentBattle == null || currentBattle.isOver()) {
                    view.displayMessage("There is no battle right now.");
                    return;
                }

                if (!currentBattle.isPlayerTurn()) {
                    view.displayMessage("It's not your turn.");
                    return;
                }

                int raw = player.getAttack();
                if (player.getEquippedWeapon() != null) {
                    raw += player.getEquippedWeapon().getAttackBonus();
                }

                int dealt = currentBattle.regularAttack(raw);
                view.displayMessage(dealt + " damage was dealt to " + currentBattle.getEnemy().getName() + "!");
            }
            case "SAVE" -> saveGame(argument);
            case "LOAD" -> loadGame();
            default -> view.displayMessage("Invalid command. Type HELP to see available commands.");
        }
    }



    public void battleLoop(Enemy enemy) {
        if (player == null || enemy == null || !player.isAlive() || !enemy.isAlive()) {
            return;
        }

        int baseDefense = player.getDefense();
        if (player.getEquippedArmor() != null) {
            baseDefense += player.getEquippedArmor().getDefenseBonus();
        }

        try {
            currentBattle = new Battle(player, enemy, baseDefense, dataLoader.loadBattleRules(), new java.util.Random());
        } catch (RuntimeException ex) {
            currentBattle = new Battle(player, enemy);
        }

        view.displayMessage("You've engaged in battle with " + enemy.getName() + "!");

        while (!currentBattle.isOver()) {
            if (currentBattle.isPlayerTurn()) {
                view.displayBattleMenu(currentBattle.getMovesRemaining());
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("attack")) {
                    int raw = player.getAttack();
                    if (player.getEquippedWeapon() != null) {
                        raw += player.getEquippedWeapon().getAttackBonus();
                    }

                    int dealt = currentBattle.regularAttack(raw);
                    view.displayMessage(dealt + " damage was dealt to " + enemy.getName() + "!");
                } else if (input.equalsIgnoreCase("flee")) {
                    if (currentBattle.attemptFlee()) {
                        view.displayMessage("You've successfully fled the battle!");
                    } else {
                        currentBattle.recordPlayerMove();
                        if (enemy.isBoss()) {
                            view.displayMessage("You're unable to flee from this battle. Fight it out!");
                        } else {
                            view.displayMessage("You couldn't flee from battle. Try again!");
                        }
                    }
                } else if (input.equalsIgnoreCase("inspect") || input.equalsIgnoreCase("inspect monster")) {
                    view.displayEnemyStats(enemy, enemy.getStatusHint());
                } else if (input.equalsIgnoreCase("inventory")) {
                    view.displayInventory(player.getInventory(), false);
                } else {
                    view.displayMessage("Invalid battle command.");
                }

                if (!currentBattle.isOver()) {
                    view.displayMessage("Moves remaining: " + currentBattle.getMovesRemaining());
                }
            } else {
                currentBattle.enemyTurn(b -> {
                    int raw = Battle.rawDamageFromAttack(enemy.getAttack(), 100, java.math.RoundingMode.HALF_UP);
                    int dealt = b.damagePlayer(raw);

                    view.displayMessage(enemy.getName() + " is using a Regular Attack!");
                    if (dealt > 0) {
                        view.displayMessage(dealt + " damage was dealt to " + player.getName() + "!");
                    }
                });
            }
        }

        if (currentBattle.getOutcome() == Battle.Outcome.VICTORY) {
            java.util.List<String> rewards = new java.util.ArrayList<>();

            if (currentBattle.claimRewards()) {
                if (enemy.getSilverDrop() > 0) {
                    player.setSilverCoins(player.getSilverCoins() + enemy.getSilverDrop());
                    rewards.add("Silver Coins x" + enemy.getSilverDrop());
                }

                if (enemy.getGoldDrop() > 0) {
                    player.setGoldCoins(player.getGoldCoins() + enemy.getGoldDrop());
                    rewards.add("Gold Coins x" + enemy.getGoldDrop());
                }

                for (java.util.Map.Entry<String, Integer> entry : enemy.getItemDrops().entrySet()) {
                    rewards.add(entry.getKey() + " x" + entry.getValue());
                }
            }

            view.displayBattleRewards(rewards);
        } else if (currentBattle.getOutcome() == Battle.Outcome.DEFEAT) {
            view.displayMessage("You've fallen in battle...");
            gameRunning = false;
        }

        currentBattle = null;
    }

    public void puzzleLoop(Puzzle puzzle) {
        // Start the puzzle loop

    }



    // Replace movePlayer(...) with locked-room decision logic
    private void movePlayer(String direction) {
        Room currentRoom = player.getCurrentRoom();
        Room nextRoom = gameWorld.getAdjacentRoom(currentRoom, direction);

        if (nextRoom == null) {
            view.displayMessage("You cannot go there.");
            return;
        }

        if (!gameWorld.isRoomUnlocked(nextRoom, player)) {
            view.displayMessage("The room is locked.");
            return;
        }

        player.setCurrentRoom(nextRoom);
        player.markRoomVisited(nextRoom.getRoomID());
        view.displayRoom(nextRoom);
    }



    // Player completes the game
    public void finishGame() {

        player.setGameCompleted(true);
        gameRunning = false;

        view.displayMessage("You found Segiel and defeated the Father of Beasts.");
        view.displayMessage("After everything you endured to reach this place, the two of you can finally return home.");
    }



    public void quitGame() {

        view.displayMessage("Would you like to save before exiting? (yes/no)");
        String choice = scanner.nextLine().trim();

        while (!choice.equalsIgnoreCase("yes") && !choice.equalsIgnoreCase("no")) {

            view.displayMessage("Please enter yes or no.");
            choice = scanner.nextLine().trim();
        }
        if (choice.equalsIgnoreCase("yes")) {
            view.displayMessage("Enter a name for your save file:");
            String saveName = scanner.nextLine().trim();
            saveGame(saveName);
        }

        gameRunning = false;
        view.displayMessage("Thank you for playing! Goodbye.");
    }



    // Saving and loading game state
    public void saveGame(String saveName) {
        if (player == null || gameWorld == null) {
            view.displayMessage("Game save could not be created.");
            return;
        }

        if (saveName == null || saveName.trim().isEmpty()) {
            view.displayMessage("Save name cannot be empty.");
            return;
        }

        saveName = saveName.trim();

        java.nio.file.Path savePath = java.nio.file.Paths.get("saves", saveName + ".txt");
        if (java.nio.file.Files.exists(savePath)) {
            view.displayMessage("A save with that name already exists. Overwrite? (yes/no)");
            String answer = scanner.nextLine().trim();

            while (!answer.equalsIgnoreCase("yes") && !answer.equalsIgnoreCase("no")) {
                view.displayMessage("Please enter yes or no.");
                answer = scanner.nextLine().trim();
            }

            if (answer.equalsIgnoreCase("no")) {
                view.displayMessage("Save cancelled.");
                return;
            }
        }

        GameMemory memory = new GameMemory(saveName, player, gameWorld);

        boolean saved = dataLoader.saveGame(memory);
        if (saved) {
            view.displayMessage("Game saved successfully!");
        } else {
            view.displayMessage("Game save could not be created.");
        }
    }

    public void loadGame() {

        view.displayMessage("What is the name of your saved game?");
        String saveFileName = scanner.nextLine().trim();

        if (saveFileName.isEmpty()) {
            view.displayMessage("Save name cannot be empty.");
            return;
        }

        // Restore the saved game
        GameMemory memory = dataLoader.restoreGame(saveFileName);

        if (memory == null || !memory.isValid()) {
            view.displayMessage("Unable to load game. Save file not found or invalid.");
            return;
        }

        // Retrieve the saved player and game world
        Player loadedPlayer = memory.getPlayer();
        GameWorld loadedWorld = memory.getGameWorld();

        // Verify that the player's current room exists in the loaded world
        if (loadedPlayer.getCurrentRoom() == null) {
            view.displayMessage("Unable to load game. Player location is missing.");
            return;
        }
        Room currentRoom = loadedWorld.getRoomByID(
                loadedPlayer.getCurrentRoom().getRoomID()
        );

        if (currentRoom == null) {
            view.displayMessage("Unable to load game. Current room could not be found.");
            return;
        }

        // Restore the player's current room reference and game state
        loadedPlayer.setCurrentRoom(currentRoom);
        player = loadedPlayer;
        gameWorld = loadedWorld;

        view.displayMessage("Game loaded successfully!");

        // Display the player's current room
        view.displayRoom(player.getCurrentRoom());

        // Resume the game
        gameLoop();
    }

    public void saveGame() {
        view.displayMessage("Enter a name for your save file:");
        String saveName = scanner.nextLine().trim();

        if (saveName.isEmpty()) {
            view.displayMessage("Save name cannot be empty.");
            return;
        }

        saveGame(saveName);
    }
}