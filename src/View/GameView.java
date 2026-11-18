package View;

import Model.*;
import java.util.List;

public class GameView {

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayTitle() {
        System.out.println("THE STYGIAN TRAIL");
        System.out.println("---------------------------------------------------");
    }

    public void displayStartNewGame(String playerName) {

        System.out.println(
                "For years, the city’s walls stood between its people and the monsters that roamed the forests " +
                        "beyond them and " + playerName + " spent his life preparing for the day he would \ncross those walls as a " +
                        "Hunter. At the prestigious Caine Drux University in the heart of the country, he trained alongside his best " +
                        "friend, Segiel. One evening, \nafter training had ended, the two decided that the night was too quiet to waste " +
                        "and slipped beyond the walls to put their skills to the test. What began as a \nharmless attempt to gain extra " +
                        "experience quickly turned into something neither of them could have anticipated when, after the fierce " +
                        "creature " + playerName + " was fighting off \nfinally fell, " + playerName + " turned toward Segiel, only to " +
                        "find him being dragged deep into the forest toward the Dead Woods. He knew the stories surrounding those " +
                        "woods, and \nfor the first time, the training that had always seemed sufficient no longer felt like enough. " +
                        "With Segiel gone and the sun beginning to rise, " + playerName + " returns to the city \ndetermined to find " +
                        "a way into the Dead Woods to rescue his friend.");

        System.out.println();
    }

    public void displayHelp() {

        System.out.println();
        System.out.println("AVAILABLE COMMANDS");
        System.out.println("---------------------------------------------------");

        System.out.println(
                "HELP            - Display the list of available commands.");

        System.out.println("\nMOVEMENT / EXPLORATION");
        System.out.println("NORTH (N)       - Move north.");
        System.out.println("SOUTH (S)       - Move south.");
        System.out.println("EAST (E)        - Move east.");
        System.out.println("WEST (W)        - Move west.");
        System.out.println("VISIT [ROOM]    - Enter an accessible room.");
        System.out.println("LEAVE           - Leave the current room.");
        System.out.println("EXITS           - Display available exits.");
        System.out.println("INSPECT [TARGET]- Inspect a room, item, or monster.");
        System.out.println("DISPLAY         - Display the current puzzle.");

        System.out.println("\nPLAYER AND INVENTORY");
        System.out.println("INVENTORY       - View your inventory.");
        System.out.println("CHECK HEALTH    - View your current health.");
        System.out.println("PICKUP [ITEM]   - Pick up an item.");
        System.out.println("DROP [ITEM]     - Drop an item.");
        System.out.println("USE [ITEM]      - Use an item.");
        System.out.println("EQUIP [ITEM]    - Equip a weapon or armor.");
        System.out.println("UNEQUIP [ITEM]  - Unequip your weapon or armor.");

        System.out.println("\nPUZZLES");
        System.out.println("PULL [LEVER]    - Pull a lever.");
        System.out.println("SOLVE           - Check your puzzle solution.");
        System.out.println("RESET           - Reset the current puzzle.");

        System.out.println("\nNPCs AND SHOPS");
        System.out.println("TALK [NPC]      - Not yet available.");
        System.out.println("BUY [ITEM]      - Not yet available.");
        System.out.println("SELL [ITEM]     - Not yet available.");

        System.out.println("\nSTORAGE CHEST");
        System.out.println("OPEN CHEST      - Open the storage chest.");
        System.out.println("CLOSE CHEST     - Close the storage chest.");
        System.out.println("STORE [ITEM]    - Store an item in the chest.");
        System.out.println("RETRIEVE [ITEM] - Retrieve an item from the chest.");

        System.out.println("\nCOMBAT (BATTLE ONLY)");
        System.out.println("ATTACK          - Perform a regular attack.");
        System.out.println("FLEE            - Attempt to escape battle.");
        System.out.println("ALT SKILLS      - Open the alternate skills menu.");
        System.out.println("BACK ALT SKILLS - Return to the battle menu.");
        System.out.println("ALT 1           - Use your first weapon skill.");
        System.out.println("ALT 2           - Use your second weapon skill.");
        System.out.println("INSPECT MONSTER - Inspect the enemy.");
        System.out.println("INVENTORY       - View your items.");
        System.out.println("USE [ITEM]      - Use an item during battle.");

        System.out.println("\nGAME");
        System.out.println("SAVE            - Save your game.");
        System.out.println("LOAD            - Load a saved game.");
        System.out.println("QUIT            - Exit the game.");

        System.out.println("---------------------------------------------------");
    }

    public void displayRoom(Room room) {
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXX");
        System.out.println(room.getFullDescription());
    }

    public void displayExits(List<String> exits) {

        if (exits == null || exits.isEmpty()) {
            System.out.println("There are no available exits.");
            return;
        }

        System.out.println("Available exits:");

        for (String exit : exits) {
            System.out.println("- " + exit);
        }
    }

    public void displayRoomItems(List<Item> items) {

        System.out.println("Items in this room:");

        if (items == null || items.isEmpty()) {
            System.out.println("There are no items in this room.");
            return;
        }

        for (Item item : items) {
            System.out.println("- " + item.getName());
        }
    }

    public void displayInventory(
            Inventory inventory, boolean isStorageChest) {

        if (inventory == null || inventory.isEmpty()) {
            if (isStorageChest) {
                System.out.println("The chest is empty.");
            } else {
                System.out.println("You have no items in your inventory");
            }
            return;
        }

        if (isStorageChest) {
            System.out.print("Storage Chest: ");
        } else {
            System.out.print("You currently have ");
        }

        boolean firstItem = true;

        for (Item item : inventory) {

            if (!firstItem) {
                System.out.print(", ");
            }

            System.out.print("'" + item.getName() + "'");

            if (item.isStackable()) {
                System.out.print(" x" + item.getQuantity());
            }

            firstItem = false;
        }

        System.out.println();
    }

    public void displayHealth(Player player) {
        System.out.println(
                "Current Health: "
                        + player.getCurrentHP()
                        + "/"
                        + player.getMaxHP()
                        + " HP");
    }

    public void displayItem(Item item) {

        if (item == null) {
            System.out.println("Item cannot be inspected.");
            return;
        }

        System.out.println("Item: " + item.getName());
        System.out.println("Description: " + item.getDescription());
    }

    // UPDATED BATTLE MENU
    public void displayBattleMenu(int movesRemaining) {

        System.out.println();
        System.out.println("========== BATTLE MENU ==========");
        System.out.println("Moves remaining: " + movesRemaining);
        System.out.println("---------------------------------");

        System.out.println("ATTACK          - Perform a regular attack.");
        System.out.println("ALT SKILLS      - Open available weapon skills.");
        System.out.println("ALT 1           - Use the first alternate skill.");
        System.out.println("ALT 2           - Use the second alternate skill.");
        System.out.println("BACK ALT SKILLS - Return to the battle menu.");
        System.out.println("FLEE            - Attempt to escape battle.");
        System.out.println("INSPECT MONSTER - Inspect the enemy.");
        System.out.println("INVENTORY       - View your inventory.");
        System.out.println("USE [ITEM]      - Use a consumable.");

        System.out.println("---------------------------------");
    }

    // NEW: DISPLAY BOTH FIGHTERS' CURRENT HP
    // Includes text values and visual health bars.
    public void displayCombatStatus(
            Player player,
            Enemy enemy,
            int movesRemaining) {

        if (player == null || enemy == null) {
            return;
        }

        System.out.println();
        System.out.println("========== CURRENT HP ==========");

        System.out.println(
                "YOU   " + player.getName() + ": "
                        + player.getCurrentHP() + "/"
                        + player.getMaxHP() + " HP "
                        + hpBar(player.getCurrentHP(), player.getMaxHP()));

        System.out.println(
                "ENEMY " + enemy.getName() + ": "
                        + enemy.getCurrentHP() + "/"
                        + enemy.getMaxHP() + " HP "
                        + hpBar(enemy.getCurrentHP(), enemy.getMaxHP()));

        System.out.println(
                "Moves remaining: " + Math.max(0, movesRemaining));
    }

    // Creates a 20-character HP bar.
    private String hpBar(int current, int maximum) {

        int width = 20;

        int filled = maximum <= 0
                ? 0
                : (int) Math.round(
                width * Math.max(
                        0.0,
                        Math.min(
                                1.0,
                                (double) current / maximum)));

        return "["
                + "#".repeat(filled)
                + "-".repeat(width - filled)
                + "]";
    }

    public void displayEnemyStats(
            Enemy enemy, String enemyStatus) {

        if (enemy == null) {
            System.out.println("There is no monster to inspect.");
            return;
        }

        System.out.println("Enemy: " + enemy.getName());

        System.out.println(
                "Current HP: "
                        + enemy.getCurrentHP()
                        + "/"
                        + enemy.getMaxHP());

        System.out.println("Attack: " + enemy.getAttack());
        System.out.println("Defense: " + enemy.getDefense());
        System.out.println("Status: " + enemyStatus);
    }

    public void displayAltSkillsMenu(
            String skill1Name,
            int skill1Cooldown,
            String skill2Name,
            int skill2Cooldown) {

        System.out.println();
        System.out.println("========== ALTERNATE SKILLS ==========");

        if (skill1Name != null) {
            System.out.println(
                    "ALT 1 - " + skill1Name
                            + " (Cooldown: " + skill1Cooldown + ")");
        }

        if (skill2Name != null) {
            System.out.println(
                    "ALT 2 - " + skill2Name
                            + " (Cooldown: " + skill2Cooldown + ")");
        }

        if (skill1Name == null && skill2Name == null) {
            System.out.println("No alternate skills available.");
        }

        System.out.println();
        System.out.println(
                "BACK ALT SKILLS - Return to the Default Battle Menu.");
    }

    public void displayBattleRewards(List<String> rewards) {

        System.out.println("You've won the battle! Here are your spoils:");

        for (String reward : rewards) {
            System.out.println("- " + reward);
        }
    }

    public void displayPuzzle(
            Puzzle puzzle, boolean hasRequiredItem) {

        if (puzzle == null) {
            System.out.println("There is not a puzzle in this room.");
            return;
        }

        if (!hasRequiredItem
                && puzzle.getLockedDescription() != null) {
            System.out.println(puzzle.getLockedDescription());
            return;
        }

        System.out.println(puzzle.getDescription());
        System.out.println();

        for (Lever lever : puzzle.getLevers()) {
            String state = lever.isOn() ? "On" : "Off";

            System.out.println(
                    lever.getDisplayName()
                            + " is in the " + state + " state.");
        }
    }
}
