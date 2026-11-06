package View;

import Model.*;
import java.util.List;
import java.util.ArrayList;

public class GameView {

    // General
    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayTitle() {
        System.out.println("Welcome to The Stygian Trail!");
        System.out.println("---------------------------------------------------");
    }

    public void displayStartNewGame(String playerName) {
        System.out.println("For years, the city’s walls stood between its people and the monsters that roamed the forests " +
                "beyond them and " + playerName + " spent his life preparing for the day he would cross those walls as a " +
                "Hunter. At the prestigious Caine Drux University in the heart of the country, he trained alongside his best " +
                "friend, Segiel. One evening, after training had ended, the two decided that the night was too quiet to waste " +
                "and slipped beyond the walls to put their skills to the test. What began as a harmless attempt to gain extra " +
                "experience quickly turned into something neither of them could have anticipated when, after the fierce " +
                "creature " + playerName + " was fighting off finally fell, " + playerName + " turned toward Segiel, only to " +
                "find him being dragged deep into the forest toward the Dead Woods. He knew the stories surrounding those " +
                "woods, and for the first time, the training that had always seemed sufficient no longer felt like enough. " +
                "With Segiel gone and the sun beginning to rise, " + playerName + " returns to the city determined to find " +
                "a way into the Dead Woods to rescue his friend.");
        System.out.println();
    }

    public void displayHelp() {
        System.out.println("Available Commands:");
        System.out.println("---------------------------------------------------");
        System.out.println("LOOK       - Examine your current room.");
        System.out.println("EXITS      - View available exits.");
        System.out.println("INVENTORY  - View your inventory.");
        System.out.println("HEALTH     - View your current health.");
        System.out.println("TAKE       - Pick up an item.");
        System.out.println("DROP       - Drop an item.");
        System.out.println("USE        - Use an item.");
        System.out.println("EQUIP      - Equip a weapon or armor.");
        System.out.println("HELP       - Display available commands.");
        System.out.println("SAVE       - Save your game.");
        System.out.println("QUIT       - Exit the game.");
    }



    // Rooms
    public void displayRoom(Room room) {
        System.out.println("Current room: " + room.getName());
        System.out.println("Room description: " + room.getDescription());
    }

    public void displayExits(Room room) {
        System.out.println("Available exits: " + room.getExits());
    }

    public void exploreRoom(List<Item> items){
        System.out.println("Items in this room:");

        if (items == null || items.isEmpty()) {
            System.out.println("There are no items in this room.");
            return;
        }
        for (Item item : items) {
            System.out.println("- " + item.getName());
        }

    }



    // Player
    public void displayInventory(ArrayList<Item> inventory) {
        System.out.println("Inventory:");

        if (inventory == null || inventory.isEmpty()) {
            System.out.println("Your inventory is empty.");
            return;
        }

        for (Item item : inventory) {
            System.out.println("- " + item.getName());
        }
    }

    public void displayHealth(Player player) {
        System.out.println("Health: " + player.getCurrentHP() + "/" + player.getMaxHP() + " HP");
    }

    public void displayItem(Item item) {
        if (item == null) {
            System.out.println("Item not found.");
            return;
        }

        System.out.println("Item: " + item.getName());
        System.out.println("Description: " + item.getDescription());
        System.out.println("Value: " + item.getValue() + " coins");
    }



    // Combat
    public void displayBattleMenu() {
        System.out.println();
        System.out.println("Battle Menu:");
        System.out.println("---------------------------------------------------");
        System.out.println("ATTACK  - Perform a basic attack.");
        System.out.println("SKILLS  - View available alternate skills.");
        System.out.println("ITEM    - Use an item.");
        System.out.println("HEAL    - Use a healing item.");
        System.out.println("FLEE    - Attempt to escape the battle.");
    }

    public void displayEnemyStats(Enemy enemy) {
        System.out.println("Enemy: " + enemy.getName());
        System.out.println("Health: " + enemy.getCurrentHP() + "/" + enemy.getMaxHP() + " HP");
        System.out.println("Attack: " + enemy.getAttack());
        System.out.println("Defense: " + enemy.getDefense());
    }

    public void displayAltSkillsMenu() {
        System.out.println();
        System.out.println("Available Skills:");
        System.out.println("---------------------------------------------------");
        System.out.println("Select an available skill to use.");
        System.out.println("Enter BACK to return to the battle menu.");
    }



    // Puzzles
    public void displayPuzzleMenu(Puzzle puzzle, boolean hasRequiredItem) {
        System.out.println();
        System.out.println("Puzzle: " + puzzle.getName());
        System.out.println("---------------------------------------------------");
        System.out.println(puzzle.display(hasRequiredItem));

        System.out.println();
        System.out.println("Puzzle Commands:");
        System.out.println("PULL [lever name] - Pull a specific lever.");
        System.out.println("RESET             - Reset the puzzle.");
    }
}