package Controller;

import java.util.Scanner;
import View.GameView;
import Model.*;

public class GameEngine {

    Scanner scanner = new Scanner(System.in);

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
        String playerName = scanner.nextLine();
        view.displayStartNewGame(playerName);
    }

    public void startMainGameLoop() {

        gameRunning = true;

        while (gameRunning && player != null && player.isAlive() && !player.isGameCompleted()) {

            view.displayMessage("What would you like to do?");

            String input = scanner.nextLine().trim();

            processPlayerInput(input);
        }
    }


    public void processPlayerInput(String input) {

        String[] parts = input.trim().split("\\s+", 2);

        String command = parts[0].toUpperCase();
        String argument = parts.length > 1 ? parts[1].trim() : "";

        switch (command) {

            // Check doc and implement the commands
        }
    }


    public void startBattle(Enemy enemy) {
        // Start the battle loop

    }


    // Might not need this method. Check doc
    public void checkGameProgress() {
        //implementation for checking the player's progress in the game goes here

    }


    public void quitGame() {

        view.displayMessage("Would you like to save before exiting? (yes/no)");
        String choice = scanner.nextLine().trim();

        while (!choice.equalsIgnoreCase("yes") && !choice.equalsIgnoreCase("no")) {

            view.displayMessage("Please enter yes or no.");
            choice = scanner.nextLine().trim();
        }
        if (choice.equalsIgnoreCase("yes")) {
            saveGame();
        }

        gameRunning = false;
        view.displayMessage("Thank you for playing! Goodbye.");
    }

    // Player completes the game
    public void finishGame() {

        player.setGameCompleted(true);
        gameRunning = false;

        // Display the ending message here (Check the doc).
    }

    public void saveGame() {

        view.displayMessage("What would you like to name your save file?");
        String saveFileName = scanner.nextLine().trim();

        if (saveFileName.isEmpty()) {
            view.displayMessage("Save name cannot be empty.");
            return;
        }

        GameMemory memory = new GameMemory(saveFileName, player, gameWorld);

        dataLoader.saveGame(memory);

        view.displayMessage("Game saved successfully.");
    }

    public void loadGame() {

        view.displayMessage("What is the name of your saved game?");
        String saveFileName = scanner.nextLine().trim();

        if (saveFileName.isEmpty()) {
            view.displayMessage("Save name cannot be empty.");
            return;
        }

        // Restore the game using DataLoader.
        // Implement when when DataLoader is ready.
    }
}