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
        view.displayMessage("THE STYGIAN TRAIL");
        view.displayMessage("---------------------------------------------------");
        view.displayMessage("What is your character's name?");
        String playerName = scanner.nextLine();
        view.displayMessage("For years, the city’s walls stood between its people and the monsters that " +
                "roamed the forests beyond them and " + playerName + "spent his life preparing for the day " +
                "he would cross those walls as a Hunter. At the prestigious Caine Drux University in the " +
                "heart of the country, he trained alongside his best friend, Segiel. One evening, after " +
                "training had ended, the two decided that the night was too quiet to waste and slipped " +
                "beyond the walls to put their skills to the test. What began as a harmless attempt to gain " +
                "extra experience quickly turned into something neither of them could have anticipated when, " +
                "after the fierce creature " + playerName + "was fighting off finally fell, " + playerName + "turned" +
                " toward Segiel, only to find him being dragged deep into the forest toward the Dead Woods. He " +
                "knew the stories surrounding those woods, and for the first time, the training that had always " +
                "seemed sufficient no longer felt like enough. With Segiel gone and the sun beginning to " +
                "rise, " + playerName + "returns to the city determined to find a way into the Dead Woods to rescue his friend.");
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