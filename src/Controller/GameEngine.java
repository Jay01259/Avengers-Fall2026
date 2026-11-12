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

        // Implement these
        // Load rooms, items, enemies, NPCs, and puzzles using DataLoader.

        // Get the starting room (R01).

        // Create the player's inventory.


        // Initialize the player with their starting stats and inventory.

        // Mark the starting room as visited.

        // Display introductory story
        view.displayStartNewGame(playerName);

        // TODO: Display the starting room.

        // TODO: Start the main game loop.
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



    public void processPlayerInput(String input) {

        // Normalize input
        String command = input.trim().replaceAll("\\s+", " ");
        String upperCommand = command.toUpperCase();

        // Commands without arguments
        switch (upperCommand) {

            case "HELP":
                view.displayHelp();
                return;

            case "EXITS":
                // Display available exits in current room
                return;

            case "INVENTORY":
                // Display inventory
                return;

            case "CHECK HEALTH":
                view.displayHealth(player);
                return;

            case "DISPLAY":
                // Display current puzzle
                return;

            case "RESET":
                // Reset current puzzle
                return;

            case "SOLVE":
                // Check current puzzle solution
                return;

            case "OPEN CHEST":
                // Open storage chest
                return;

            case "CLOSE CHEST":
                // Close storage chest
                return;

            case "QUIT":
                quitGame();
                return;

            case "SAVE":
                saveGame();
                return;

            case "LOAD":
                loadGame();
                return;

            case "LEAVE":
                // Leave current room
                return;
        }

        // Movement commands
        switch (upperCommand) {

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

        // Commands that require an argument
        String[] parts = command.split(" ", 2);

        String action = parts[0].toUpperCase();
        String argument = parts.length > 1 ? parts[1].trim() : "";

        switch (action) {

            case "VISIT":
                // Visit specified room
                break;

            case "INSPECT":
                // Inspect room, item, or enemy
                break;

            case "PICKUP":
                // TODO: Pick up specified item
                break;

            case "DROP":
                // Drop specified item
                break;

            case "USE":
                // Use specified item
                break;

            case "EQUIP":
                // Equip specified item
                break;

            case "PULL":
                // Pull specified lever
                break;

            case "TALK":
                // Talk to specified NPC
                break;

            case "BUY":
                // Buy specified item
                break;

            case "SELL":
                // Sell specified item
                break;

            case "STORE":
                // Store specified item in chest
                break;

            case "RETRIEVE":
                // Retrieve specified item from chest
                break;

            case "ATTACK":
                // Handle battle attack
                break;

            default:
                view.displayMessage("Invalid command. Type HELP to see available commands.");
                break;
        }
    }



    public void battleLoop(Enemy enemy) {

        // TODO: Initialize battle and prepare player stats.

        // TODO: Set up the player's turn.

        // TODO: Display the default battle menu.

        // TODO: Process player battle commands.

        // TODO: End the player's turn when 3 moves are used.

        // TODO: Process the enemy's turn if it is still alive.

        // TODO: Repeat until the battle ends.

        // TODO: Process battle rewards and dropped items.

        // TODO: Restore player stats and equipment after battle.
    }

    public void puzzleLoop(Puzzle puzzle) {
        // Start the puzzle loop

    }



    private void movePlayer(String direction) {

        Room currentRoom = player.getCurrentRoom();

        // Find the adjacent room
        Room nextRoom = gameWorld.getAdjacentRoom(currentRoom, direction);

        if (nextRoom == null) {
            view.displayMessage("You cannot go there.");
            return;
        }

        // Update player's location
        player.setCurrentRoom(nextRoom);

        // Record the room as visited
        player.markRoomVisited(nextRoom.getRoomID());

        // Display room information
        view.displayRoom(nextRoom);

        // Implement these
        // Display room-specific story dialogue
        // Process randomized item spawns
        // Process randomized enemy spawns
        // Display NPC dialogue when applicable
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

        // Create a snapshot of the current game state
        GameMemory memory = new GameMemory(saveName, player, gameWorld);

        // TODO: Check whether a save with this name already exists.
        // TODO: Ask for overwrite confirmation if necessary.

        // Save the game using DataLoader
        dataLoader.saveGame(memory);

        // TODO: Display success or failure based on DataLoader's result.
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

}