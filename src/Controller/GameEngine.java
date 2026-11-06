package Controller;

import java.util.Scanner;
import View.GameView;

public class GameEngine {

    Scanner scanner = new Scanner(System.in);

    Player player;
    GameWorld gameWorld;
    GameView view = new GameView();
    Battle currentBattle;

    public static void main(String[] args) {
        GameEngine gameEngine = new GameEngine();
        gameEngine.startGame();
    }

    // Uses GameView to ask the player if they want to start a new game or load a saved game.
    public void startGame() {
        view.displayMessage("Would you like to start a new game or load a saved game?");
    }

    public void startNewGame() {
        view.displayTitle();
        view.displayMessage("What is your character's name?");
        String playerName = scanner.nextLine();
        view.displayStartNewGame(playerName);
    }

    public void loadGame(String saveName){
        view.displayMessage("What is the name of your saved game?");
        String saveFileName = scanner.nextLine();
        // Load the game from the save file using the provided saveFileName
        // Implementation for loading the game from the save file goes here

    }

    public void saveGame(String saveName){
        view.displayMessage("What would you like to name your save file?");
        String saveFileName = scanner.nextLine();
        // Save the current game state to a file using the provided saveFileName
        // Implementation for saving the game state goes here

    }

    public void processPlayerInput(String input) {
        // Process the player's input and update the game state accordingly
        // Implementation for processing player input goes here

    }

    public void startBattle(Enemy enemy) {
        //implementation for starting a battle with the specified enemy goes here

    }

    public void checkGameProgress() {
        //implementation for checking the player's progress in the game goes here

    }

    public void endGame(){
        // Show ending message here

    }
}