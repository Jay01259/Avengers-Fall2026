
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
            } else if (choice.equalsIgnoreCase("load")) {
                loadGame();
                break;
            } else {
                view.displayMessage("Invalid input. Please enter 'new' or 'load'.");
            }
        }
    }

    public void startNewGame() {
        view.displayTitle();
        view.displayMessage("What is your character's name?");
        String playerName = scanner.nextLine().trim();

        while (playerName.isEmpty()) {
            view.displayMessage("Your character's name cannot be empty.");
            playerName = scanner.nextLine().trim();
        }

        gameWorld = new GameWorld();

        try {
            for (Room room : dataLoader.loadRooms()) {
                gameWorld.addRoom(room);
            }
        } catch (UnsupportedOperationException ignored) {
        }

        dataLoader.loadItems(gameWorld);

        try {
            for (Enemy enemy : dataLoader.loadEnemies()) {
                gameWorld.addEnemy(enemy);
            }

            dataLoader.loadEncounterPlacements(gameWorld);
        } catch (RuntimeException error) {
            view.displayMessage(
                    "Enemy data could not be loaded: "
                            + error.getMessage());
        }

        try {
            for (Puzzle puzzle : dataLoader.loadPuzzles()) {
                gameWorld.addPuzzle(puzzle);

                Room room = gameWorld.getRoomByID(puzzle.getRoomID());

                if (room != null && !room.getPuzzles().contains(puzzle)) {
                    room.getPuzzles().add(puzzle);
                } else if (room == null) {
                    view.displayMessage(
                            "Puzzle " + puzzle.getPuzzleID()
                                    + " references missing room: "
                                    + puzzle.getRoomID());
                }
            }
        } catch (RuntimeException e) {
            view.displayMessage("Error loading puzzles:");
            e.printStackTrace();
        }

        Room startingRoom = gameWorld.getRoomByID("R01");

        if (startingRoom == null) {
            startingRoom = gameWorld.getStartRoom();
        }

        if (startingRoom == null) {
            startingRoom = new Room(
                    "R01",
                    "Player's Room",
                    "Your room is a modest student chamber in the Caine Drux University dormitory.",
                    new java.util.ArrayList<>(),
                    new java.util.ArrayList<>(),
                    new java.util.ArrayList<>(),
                    null, null, null, null
            );

            gameWorld.addRoom(startingRoom);
        }

        Inventory inventory = new Inventory();

        player = new Player(
                playerName,
                100,
                100,
                5,
                0,
                inventory,
                startingRoom,
                null,
                null,
                0,
                0,
                new java.util.ArrayList<>(),
                false
        );

        player.markRoomVisited(startingRoom.getRoomID());

        view.displayStartNewGame(playerName);
        view.displayRoom(startingRoom);

        gameLoop();
    }

    public void gameLoop() {
        gameRunning = true;

        while (gameRunning && player != null
                && player.isAlive() && !player.isGameCompleted()) {

            view.displayMessage("What would you like to do?");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                view.displayMessage("Please enter a command.");
                continue;
            }

            processPlayerInput(input);
        }
    }

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

        switch (upper) {

            case "HELP": {
                view.displayHelp();
                return;
            }

            case "EXITS": {
                displayDetailedExits(currentRoom);
                return;
            }

            case "INVENTORY": {
                view.displayInventory(player.getInventory(), false);
                return;
            }

            case "CHECK HEALTH": {
                view.displayHealth(player);
                return;
            }

            case "DISPLAY": {
                Puzzle puzzle = gameWorld.getPuzzleInRoom(currentRoom);

                if (puzzle == null) {
                    view.displayMessage("You are not in a Puzzle Room.");
                    return;
                }

                String requiredItemId = puzzle.getRequiredItemID();

                boolean hasRequiredItem =
                        requiredItemId == null
                                || requiredItemId.isBlank()
                                || player.getInventory().getItem(requiredItemId) != null;

                view.displayPuzzle(puzzle, hasRequiredItem);
                return;
            }

            case "RESET": {
                Puzzle puzzle = gameWorld.getPuzzleInRoom(currentRoom);

                if (puzzle == null) {
                    view.displayMessage("You are not in a Puzzle Room.");
                    return;
                }

                view.displayMessage(puzzle.reset(currentRoom.getName()));
                return;
            }

            case "SOLVE": {
                solveCurrentPuzzle();
                return;
            }

            case "OPEN CHEST": {
                StorageChest chest = currentRoom.getChest();

                if (chest == null) {
                    view.displayMessage("There is no chest in this room.");
                } else {
                    view.displayMessage(chest.open());
                }
                return;
            }

            case "CLOSE CHEST": {
                StorageChest chest = currentRoom.getChest();

                if (chest == null) {
                    view.displayMessage("There is no chest in this room.");
                } else {
                    view.displayMessage(chest.close());
                }
                return;
            }

            case "LEAVE": {
                Room leaveRoom = gameWorld.getLeaveRoom(currentRoom);

                if (leaveRoom == null) {
                    view.displayMessage(
                            "You cannot leave this room using that command.");
                    return;
                }

                if (!gameWorld.isRoomUnlocked(leaveRoom, player)) {
                    view.displayMessage("The room is locked.");
                    return;
                }

                enterRoom(leaveRoom);
                return;
            }

            case "QUIT": {
                quitGame();
                return;
            }

            case "SAVE": {
                saveGame();
                return;
            }

            case "LOAD": {
                loadGame();
                return;
            }

            case "NORTH":
            case "N":
                movePlayer("NORTH");
                return;

            case "SOUTH":
            case "S":
                movePlayer("SOUTH");
                return;

            case "EAST":
            case "E":
                movePlayer("EAST");
                return;

            case "WEST":
            case "W":
                movePlayer("WEST");
                return;
        }

        String[] parts = command.split(" ", 2);
        String action = parts[0].toUpperCase();
        String argument = parts.length > 1 ? parts[1].trim() : "";

        switch (action) {

            case "VISIT": {
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

                enterRoom(target);
                return;
            }

            case "INSPECT": {
                if (argument.isEmpty()) {
                    view.displayMessage("Inspect what?");
                    return;
                }

                if (argument.equalsIgnoreCase("exits")) {
                    displayDetailedExits(currentRoom);
                    return;
                }

                if (argument.equalsIgnoreCase("room")
                        || argument.equalsIgnoreCase(currentRoom.getName())) {

                    view.displayMessage(currentRoom.getFullDescription());
                    view.displayMessage(currentRoom.inspect());
                    displayDetailedExits(currentRoom);
                    return;
                }

                if (currentBattle != null && !currentBattle.isOver()) {
                    Enemy battleEnemy = currentBattle.getEnemy();

                    if (argument.equalsIgnoreCase("monster")
                            || argument.equalsIgnoreCase("enemy")
                            || argument.equalsIgnoreCase(battleEnemy.getName())) {

                        view.displayEnemyStats(
                                battleEnemy, battleEnemy.getStatusHint());
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
                return;
            }

            case "PICKUP": {
                if (argument.isEmpty()) {
                    view.displayMessage(
                            "There is nothing currently available to be picked up");
                    return;
                }

                Item roomItem = currentRoom.findItem(argument);

                if (roomItem == null) {
                    view.displayMessage(
                            "There is nothing currently available to be picked up");
                    return;
                }

                if ("W14".equalsIgnoreCase(roomItem.getId())) {
                    currentRoom.removeItem(roomItem);
                    player.setSilverCoins(player.getSilverCoins() + 1);
                    view.displayMessage("Picked up 1 Silver Coin.");

                } else if ("W15".equalsIgnoreCase(roomItem.getId())) {
                    currentRoom.removeItem(roomItem);
                    player.setGoldCoins(player.getGoldCoins() + 1);
                    view.displayMessage("Picked up 1 Gold Coin.");

                } else if (player.pickUpItem(roomItem)) {
                    view.displayMessage(roomItem.getName() + " was picked up.");

                } else {
                    view.displayMessage(
                            "Cannot pick up, inventory is full. Drop something first.");
                }
                return;
            }

            case "DROP": {
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
                return;
            }

            case "USE": {
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
                return;
            }

            case "EQUIP": {
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
                return;
            }

            case "UNEQUIP": {
                if (argument.isEmpty()) {
                    view.displayMessage("Unequip what? Enter WEAPON or ARMOR.");
                    return;
                }

                Item equippedItem = null;

                if (argument.equalsIgnoreCase("WEAPON")) {
                    equippedItem = player.getEquippedWeapon();
                } else if (argument.equalsIgnoreCase("ARMOR")) {
                    equippedItem = player.getEquippedArmor();
                } else {
                    equippedItem = player.getInventory().getItem(argument);
                }

                if (equippedItem == null) {
                    view.displayMessage("That item is not currently equipped.");
                    return;
                }

                if (player.unequipItem(equippedItem)) {
                    view.displayMessage(equippedItem.getName() + " unequipped.");
                } else {
                    view.displayMessage("That item is not currently equipped.");
                }
                return;
            }

            case "PULL": {
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

                boolean hasRequiredItem =
                        requiredItemId == null
                                || requiredItemId.isBlank()
                                || player.getInventory().getItem(requiredItemId) != null;

                view.displayMessage(
                        puzzle.pullLever(argument, hasRequiredItem));
                return;
            }

            case "TALK":
                view.displayMessage("Talk command is not available yet.");
                return;

            case "BUY":
                view.displayMessage("Buy command is not available yet.");
                return;

            case "SELL":
                view.displayMessage("Sell command is not available yet.");
                return;

            case "STORE": {
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
                return;
            }

            case "RETRIEVE": {
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
                    view.displayMessage(
                            "Cannot retrieve item, inventory is full.");
                }
                return;
            }

            case "ATTACK": {
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

                view.displayMessage(
                        dealt + " damage was dealt to "
                                + currentBattle.getEnemy().getName() + "!");

                view.displayCombatStatus(
                        player,
                        currentBattle.getEnemy(),
                        currentBattle.getMovesRemaining());
                return;
            }

            case "SAVE":
                saveGame(argument);
                return;

            case "LOAD":
                loadGame();
                return;

            default:
                view.displayMessage(
                        "Invalid command. Type HELP to see available commands.");
                return;
        }
    }

    // COMPLETE COMBAT SYSTEM:
    // Displays both HP totals after attacks, skills, and enemy turns.
    // Switches turns when moves reach zero.
    // Ends immediately on victory, defeat, or escape.
    public void battleLoop(Enemy enemy) {

        if (player == null || enemy == null
                || !player.isAlive() || !enemy.isAlive()) {
            return;
        }

        int startingDefense = player.getDefense();

        if (player.getEquippedArmor() != null) {
            startingDefense += player.getEquippedArmor().getDefenseBonus();
        }

        try {
            currentBattle = new Battle(
                    player,
                    enemy,
                    startingDefense,
                    dataLoader.loadBattleRules(),
                    new java.util.Random()
            );

        } catch (RuntimeException ex) {
            view.displayMessage(
                    "Battle settings could not be loaded. Using defaults.");

            java.util.Properties defaults = new java.util.Properties();
            defaults.setProperty("movesPerTurn", "3");
            defaults.setProperty("baseFleeChance", "0.50");
            defaults.setProperty("fleeIncrease", "0.10");

            currentBattle = new Battle(
                    player, enemy, startingDefense,
                    defaults, new java.util.Random());
        }

        Weapon equippedAtStart = player.getEquippedWeapon();

        if (equippedAtStart != null) {
            if ("W01".equalsIgnoreCase(equippedAtStart.getId())) {
                currentBattle.setPlayerSkills(
                        "Lifesteal Strike", "Crimson Lunge");
            } else {
                // Demo names until official skill data is supplied.
                currentBattle.setPlayerSkills(
                        "Heavy Strike (demo)", "Power Strike (demo)");
            }
        } else {
            currentBattle.setPlayerSkills(null, null);
        }

        view.displayMessage("\n========== BATTLE START ==========");
        view.displayMessage(
                "Opponent: " + enemy.getName()
                        + " [" + enemy.getCurrentHP()
                        + "/" + enemy.getMaxHP() + " HP]");

        view.displayCombatStatus(
                player, enemy, currentBattle.getMovesRemaining());

        int round = 1;
        boolean showStartOfTurn = true;

        while (!currentBattle.isOver()) {

            if (currentBattle.isPlayerTurn()) {

                if (showStartOfTurn) {
                    view.displayMessage(
                            "\n========== YOUR TURN (ROUND "
                                    + round + ") ==========");

                    view.displayMessage(
                            "You have "
                                    + currentBattle.getMovesRemaining()
                                    + " moves.");

                    showStartOfTurn = false;
                }

                if (currentBattle.getMenu() == Battle.Menu.ALT_SKILLS) {
                    view.displayAltSkillsMenu(
                            currentBattle.getPlayerSkillName(1),
                            currentBattle.getSkillCooldown(1),
                            currentBattle.getPlayerSkillName(2),
                            currentBattle.getSkillCooldown(2));
                } else {
                    view.displayBattleMenu(
                            currentBattle.getMovesRemaining());
                }

                String input = scanner.nextLine().trim();
                String upper =
                        input.toUpperCase(java.util.Locale.ROOT);

                if (upper.equals("ALT SKILLS")) {

                    currentBattle.enterAltSkillsMenu();

                    if (player.getEquippedWeapon() == null) {
                        view.displayMessage(
                                "Equip a weapon before using alternate skills.");
                    }

                    continue;
                }

                if (upper.equals("BACK ALT SKILLS")
                        || upper.equals("BACK")) {

                    currentBattle.leaveAltSkillsMenu();
                    continue;
                }

                if (upper.equals("ALT 1") || upper.equals("ALT 2")) {

                    int slot = upper.endsWith("1") ? 1 : 2;
                    Weapon weapon = player.getEquippedWeapon();

                    if (weapon == null
                            || currentBattle.getPlayerSkillName(slot) == null) {

                        view.displayMessage(
                                "You have no equipped weapon skills.");
                        continue;
                    }

                    if (currentBattle.getSkillCooldown(slot) > 0) {
                        view.displayMessage(
                                "That skill is cooling down for "
                                        + currentBattle.getSkillCooldown(slot)
                                        + " more turn(s).");
                        continue;
                    }

                    // TEMPORARY DEMO VALUES.
                    // Replace with official weapon skill requirements.
                    final int percentage = slot == 1 ? 150 : 200;
                    final int cooldown = slot == 1 ? 2 : 3;

                    final int rawAttack =
                            Battle.rawDamageFromAttack(
                                    player.getAttack()
                                            + weapon.getAttackBonus(),
                                    percentage,
                                    java.math.RoundingMode.HALF_UP);

                    final String skillName =
                            currentBattle.getPlayerSkillName(slot);

                    final boolean lifesteal =
                            slot == 1
                                    && "W01".equalsIgnoreCase(weapon.getId());

                    boolean used = currentBattle.useAlternateSkill(
                            slot,
                            cooldown,
                            battle -> {

                                int dealt =
                                        battle.damageEnemy(rawAttack);

                                view.displayMessage(
                                        skillName + " dealt "
                                                + dealt + " damage!");

                                if (lifesteal) {
                                    int healed = player.heal(dealt / 2);

                                    view.displayMessage(
                                            "Lifesteal restored "
                                                    + healed + " HP.");
                                }
                            });

                    if (used) {

                        // DISPLAY UPDATED HEALTH FOR BOTH FIGHTERS
                        view.displayCombatStatus(
                                player, enemy,
                                currentBattle.getMovesRemaining());

                        weapon.useTurn();

                        if (weapon.isBroken()) {
                            player.unequipItem(weapon);
                            player.getInventory().removeItem(weapon);
                            currentBattle.setPlayerSkills(null, null);

                            view.displayMessage(
                                    weapon.getName() + " broke!");
                        }

                        currentBattle.leaveAltSkillsMenu();

                    } else {
                        view.displayMessage(
                                "Cannot use that skill right now.");
                    }

                } else if (upper.equals("ATTACK")
                        || upper.startsWith("ATTACK ")) {

                    int raw = player.getAttack();
                    Weapon weapon = player.getEquippedWeapon();

                    if (weapon != null) {
                        raw += weapon.getAttackBonus();
                    }

                    int dealt = currentBattle.regularAttack(raw);

                    view.displayMessage(
                            "You dealt " + dealt + " damage to "
                                    + enemy.getName() + ".");

                    // DISPLAY HEALTH AFTER EACH REGULAR ATTACK
                    view.displayCombatStatus(
                            player, enemy,
                            currentBattle.getMovesRemaining());

                    if (weapon != null) {
                        weapon.useTurn();

                        if (weapon.isBroken()) {
                            player.unequipItem(weapon);
                            player.getInventory().removeItem(weapon);
                            currentBattle.setPlayerSkills(null, null);

                            view.displayMessage(
                                    weapon.getName() + " broke!");
                        }
                    }

                } else if (upper.equals("FLEE")) {

                    if (currentBattle.attemptFlee()) {
                        view.displayMessage(
                                "You successfully escaped!");
                    } else {
                        currentBattle.recordPlayerMove();

                        view.displayMessage(
                                enemy.isBoss()
                                        ? "You cannot flee from a boss battle!"
                                        : "Escape failed. One move used.");
                    }

                } else if (upper.equals("INSPECT")
                        || upper.equals("INSPECT MONSTER")
                        || upper.equals("INSPECT ENEMY")
                        || input.equalsIgnoreCase(
                        "INSPECT " + enemy.getName())) {

                    view.displayEnemyStats(
                            enemy, enemy.getStatusHint());

                } else if (upper.equals("INVENTORY")) {

                    view.displayInventory(
                            player.getInventory(), false);

                } else if (upper.startsWith("USE ")) {

                    String itemName = input.substring(4).trim();
                    Item item = player.getInventory().getItem(itemName);

                    if (item != null && player.useItem(item, 1)) {

                        view.displayMessage(
                                "Used " + item.getName()
                                        + ". Your HP is now "
                                        + player.getCurrentHP()
                                        + "/" + player.getMaxHP());

                        currentBattle.recordPlayerMove();

                        // DISPLAY HEALTH AFTER HEALING
                        view.displayCombatStatus(
                                player, enemy,
                                currentBattle.getMovesRemaining());

                    } else {
                        view.displayMessage(
                                "You cannot use that item now.");
                    }

                } else {
                    view.displayMessage(
                            "Try ATTACK, ALT SKILLS, FLEE, "
                                    + "INSPECT MONSTER, INVENTORY, or USE [ITEM].");
                }

                if (!currentBattle.isOver()
                        && !currentBattle.isPlayerTurn()) {

                    view.displayMessage(
                            "\n*** Your 3 moves are finished. "
                                    + "ENEMY TURN! ***");
                }

            } else {

                view.displayMessage(
                        "\n========== "
                                + enemy.getName().toUpperCase()
                                + "'S TURN ==========");

                boolean stunned = enemy.isStunned();

                currentBattle.enemyTurn(battle -> {

                    int dealt =
                            battle.damagePlayer(enemy.getAttack());

                    view.displayMessage(
                            enemy.getName()
                                    + " attacked and dealt "
                                    + dealt + " damage.");

                    if (dealt > 0) {
                        Armor armor = player.getEquippedArmor();

                        if (armor != null) {
                            armor.takeHit();

                            if (armor.isBroken()) {
                                player.unequipItem(armor);
                                player.getInventory().removeItem(armor);

                                battle.reducePlayerDefense(
                                        armor.getDefenseBonus());

                                view.displayMessage(
                                        armor.getName() + " broke!");
                            }
                        }
                    }
                });

                if (stunned) {
                    view.displayMessage(
                            enemy.getName()
                                    + " was stunned and missed its attack.");
                }

                // DISPLAY HEALTH AFTER THE ENEMY ATTACKS
                view.displayCombatStatus(
                        player, enemy,
                        currentBattle.getMovesRemaining());

                if (!currentBattle.isOver()) {
                    round++;
                    showStartOfTurn = true;

                    view.displayMessage(
                            "Enemy turn over. Your 3 moves "
                                    + "have been restored.");
                }
            }
        }

        Battle.Outcome result = currentBattle.getOutcome();

        // DISPLAY FINAL HEALTH BEFORE ENDING BATTLE
        view.displayCombatStatus(
                player, enemy, currentBattle.getMovesRemaining());

        view.displayMessage(
                "\n========== BATTLE FINISHED ==========");

        if (result == Battle.Outcome.VICTORY) {

            view.displayMessage(
                    "VICTORY! You defeated " + enemy.getName() + "!");

            java.util.List<String> rewards =
                    new java.util.ArrayList<>();

            if (currentBattle.claimRewards()) {

                if (enemy.getSilverDrop() > 0) {
                    player.setSilverCoins(
                            player.getSilverCoins()
                                    + enemy.getSilverDrop());

                    rewards.add(
                            "Silver Coins x" + enemy.getSilverDrop());
                }

                if (enemy.getGoldDrop() > 0) {
                    player.setGoldCoins(
                            player.getGoldCoins()
                                    + enemy.getGoldDrop());

                    rewards.add(
                            "Gold Coins x" + enemy.getGoldDrop());
                }

                for (java.util.Map.Entry<String, Integer> drop
                        : enemy.getItemDrops().entrySet()) {

                    Item definition =
                            gameWorld.getItemByID(drop.getKey());

                    if (definition == null) {
                        view.displayMessage(
                                "Unknown reward item: " + drop.getKey());
                        continue;
                    }

                    for (int i = 0; i < drop.getValue(); i++) {
                        player.getCurrentRoom().addItem(
                                dataLoader.createItemCopy(definition));
                    }

                    rewards.add(
                            definition.getName() + " x"
                                    + drop.getValue() + " (on the floor)");
                }
            }

            Enemy widow = gameWorld.getEnemyByID("M04");
            Enemy walker = gameWorld.getEnemyByID("M05");

            if (("M04".equalsIgnoreCase(enemy.getEnemyID())
                    || "M05".equalsIgnoreCase(enemy.getEnemyID()))
                    && widow != null && walker != null
                    && widow.isDefeated() && walker.isDefeated()) {

                Item valence = gameWorld.getItemByID("W01");

                if (valence != null) {
                    player.getCurrentRoom().addItem(
                            dataLoader.createItemCopy(valence));

                    rewards.add(
                            valence.getName()
                                    + " (on the floor; use PICKUP)");
                }
            }

            if (rewards.isEmpty()) {
                view.displayMessage("No coins or items dropped.");
            } else {
                view.displayBattleRewards(rewards);
            }

            if ("M08".equalsIgnoreCase(enemy.getEnemyID())) {

                Item endingItem = gameWorld.getItemByID("FP01");

                if (endingItem != null) {
                    player.getInventory().addItem(
                            dataLoader.createItemCopy(endingItem));
                }

                finishGame();
            }

        } else if (result == Battle.Outcome.DEFEAT) {

            view.displayMessage(
                    "DEFEAT! Your character was defeated.");
            gameRunning = false;

        } else if (result == Battle.Outcome.FLED) {

            view.displayMessage(
                    "You fled combat. You can continue exploring.");
        }

        currentBattle = null;
    }

    public void puzzleLoop(Puzzle puzzle) {
        if (puzzle == null) {
            return;
        }

        view.displayPuzzle(puzzle, hasPuzzleRequirement(puzzle));

        if (!hasPuzzleRequirement(puzzle)) {
            return;
        }

        while (!puzzle.isCompleted()) {

            view.displayMessage(
                    "Puzzle: PULL [LEVER], DISPLAY, RESET, SOLVE, BACK");

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("BACK")) {
                return;
            }

            if (input.equalsIgnoreCase("SOLVE")) {
                solveCurrentPuzzle();
            } else {
                processPlayerInput(input);
            }
        }
    }

    private boolean hasPuzzleRequirement(Puzzle puzzle) {
        String required = puzzle.getRequiredItemID();

        return required == null
                || required.isBlank()
                || player.getInventory().getItem(required) != null;
    }

    private void solveCurrentPuzzle() {

        Room room = player.getCurrentRoom();
        Puzzle puzzle = gameWorld.getPuzzleInRoom(room);

        if (puzzle == null) {
            view.displayMessage(
                    "You are not in an unsolved Puzzle Room.");
            return;
        }

        if (!puzzle.checkSolution(hasPuzzleRequirement(puzzle))) {
            view.displayMessage(
                    "Your solution is incorrect, or a required item is missing.");
            return;
        }

        view.displayMessage("Your solution is correct.");
        view.displayMessage(puzzle.getSolveText());

        for (java.util.Map.Entry<String, Integer> reward
                : puzzle.getRewardItems().entrySet()) {

            String itemID = reward.getKey();
            int quantity = reward.getValue();

            if ("W14".equalsIgnoreCase(itemID)) {
                player.setSilverCoins(
                        player.getSilverCoins() + quantity);

                view.displayMessage(
                        "Received " + quantity + " Silver Coins.");

            } else if ("W15".equalsIgnoreCase(itemID)) {
                player.setGoldCoins(
                        player.getGoldCoins() + quantity);

                view.displayMessage(
                        "Received " + quantity + " Gold Coins.");

            } else {

                Item item = gameWorld.getItemByID(itemID);

                if (item != null) {
                    for (int i = 0; i < quantity; i++) {
                        room.addItem(
                                dataLoader.createItemCopy(item));
                    }

                    view.displayMessage(
                            "Reward: " + item.getName() + " x"
                                    + quantity + " (use PICKUP).");
                } else {
                    view.displayMessage(
                            "Missing item definition for puzzle reward "
                                    + itemID);
                }
            }
        }

        for (Room candidate : gameWorld.getRooms()) {
            if (puzzle.getPuzzleID().equalsIgnoreCase(
                    candidate.getRequiredPuzzleID())) {

                candidate.unlock();

                view.displayMessage(
                        "Unlocked: " + candidate.getName());
            }
        }
    }

    private void displayDetailedExits(Room room) {
        java.util.List<String> exits = new java.util.ArrayList<>();

        addExit(exits, "North", room.getNorth());
        addExit(exits, "South", room.getSouth());
        addExit(exits, "East", room.getEast());
        addExit(exits, "West", room.getWest());

        for (String id : room.getVisitRooms()) {
            addExit(exits, "Visit", id);
        }

        addExit(exits, "Leave", room.getLeaveTo());

        view.displayExits(exits);
    }

    private void addExit(
            java.util.List<String> exits,
            String direction,
            String roomId) {

        if (roomId == null || roomId.isBlank()) {
            return;
        }

        Room room = gameWorld.getRoomByID(roomId);
        String destination =
                room == null ? roomId : room.getName();

        exits.add(
                direction + " - " + destination
                        + (room != null
                        && !gameWorld.isRoomUnlocked(room, player)
                        ? " (locked)" : ""));
    }

    private void enterRoom(Room room) {

        if (room == null) {
            return;
        }

        player.moveTo(room);
        room.spawnRandomItem();
        view.displayRoom(room);

        Enemy enemy = gameWorld.getRandomEncounter(room);

        if (!room.isRandomEnemyRoom()) {
            for (Enemy present : room.getEnemies()) {
                if (present.isAlive()) {
                    enemy = present;
                    break;
                }
            }
        }

        if (enemy != null && gameRunning && player.isAlive()) {
            battleLoop(enemy);
        }
    }

    private void movePlayer(String direction) {

        Room currentRoom = player.getCurrentRoom();
        Room nextRoom =
                gameWorld.getAdjacentRoom(currentRoom, direction);

        if (nextRoom == null) {
            view.displayMessage("You cannot go there.");
            return;
        }

        if (!gameWorld.isRoomUnlocked(nextRoom, player)) {
            view.displayMessage("The room is locked.");
            return;
        }

        enterRoom(nextRoom);
    }

    public void finishGame() {
        player.setGameCompleted(true);
        gameRunning = false;

        view.displayMessage(
                "You found Segiel and defeated the Father of Beasts.");

        view.displayMessage(
                "After everything you endured to reach this place, "
                        + "the two of you can finally return home.");
    }

    public void quitGame() {

        view.displayMessage(
                "Would you like to save before exiting? (yes/no)");

        String choice = scanner.nextLine().trim();

        while (!choice.equalsIgnoreCase("yes")
                && !choice.equalsIgnoreCase("no")) {

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

        java.nio.file.Path savePath =
                java.nio.file.Paths.get("saves", saveName + ".txt");

        if (java.nio.file.Files.exists(savePath)) {

            view.displayMessage(
                    "A save with that name already exists. Overwrite? (yes/no)");

            String answer = scanner.nextLine().trim();

            while (!answer.equalsIgnoreCase("yes")
                    && !answer.equalsIgnoreCase("no")) {

                view.displayMessage("Please enter yes or no.");
                answer = scanner.nextLine().trim();
            }

            if (answer.equalsIgnoreCase("no")) {
                view.displayMessage("Save cancelled.");
                return;
            }
        }

        GameMemory memory =
                new GameMemory(saveName, player, gameWorld);

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

        GameMemory memory =
                dataLoader.restoreGame(saveFileName);

        if (memory == null || !memory.isValid()) {
            view.displayMessage(
                    "Unable to load game. Save file not found or invalid.");
            return;
        }

        Player loadedPlayer = memory.getPlayer();
        GameWorld loadedWorld = memory.getGameWorld();

        if (loadedPlayer.getCurrentRoom() == null) {
            view.displayMessage(
                    "Unable to load game. Player location is missing.");
            return;
        }

        Room currentRoom = loadedWorld.getRoomByID(
                loadedPlayer.getCurrentRoom().getRoomID());

        if (currentRoom == null) {
            view.displayMessage(
                    "Unable to load game. Current room could not be found.");
            return;
        }

        loadedPlayer.setCurrentRoom(currentRoom);

        player = loadedPlayer;
        gameWorld = loadedWorld;

        view.displayMessage("Game loaded successfully!");
        view.displayRoom(player.getCurrentRoom());

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
