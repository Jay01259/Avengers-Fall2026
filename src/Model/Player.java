package Model;

import java.util.ArrayList;
import java.util.List;

//Player feature - Jay

public class Player extends Character {

    Inventory inventory;
    Room currentRoom;
    Weapon equippedWeapon;
    Armor equippedAmor;
    int silverCoins;
    int goldCoins;
    List<String> visitedRoomsID;
    boolean gameCompleted;
    // Check doc to see if player can check which puzzles they have completed
    // List <String> completedPuzzlesID;

    public Player(String name, int currentHP, int maxHP, int attack, int defense, Inventory inventory,
                  Room currentRoom, Weapon equippedWeapon, Armor equippedAmor, int silverCoins, int goldCoins,
                  List<String> visitedRoomsID, boolean gameCompleted) {
        super(name, currentHP, maxHP, attack, defense);
        this.inventory = inventory;
        this.currentRoom = currentRoom;
        this.equippedWeapon = equippedWeapon;
        this.equippedAmor = equippedAmor;
        this.silverCoins = silverCoins;
        this.goldCoins = goldCoins;
        this.visitedRoomsID = visitedRoomsID;
        this.gameCompleted = gameCompleted;
    }

    public Inventory getInventory() {
        // Implement
    }

    public Room getCurrentRoom() {
        // Implement
    }

    public boolean pickUpItem(Item item) {
        // Implement
    }

    public boolean dropItem(Item item) {
        // Implement
    }

    public boolean useItem(Item item) {
        // Implement
    }

    public boolean equipItem(Item item) {
        // Implement
    }

    public boolean buyItem(String itemID, NPC npc) {
        // Implement
    }

    public boolean sellItem(String itemID, NPC npc) {
        // Implement
    }

    public void moveTo(Room room) {
        // Implement
    }

    public void markRoomVisited(String roomID) {
        // Implement
    }

    public boolean hasVisitedRoom(String roomID) {
        // Implement
    }

    public int getSilverCoins() {
        return silverCoins
    }

    public int getGoldCoins(){
        return goldCoins;
    }

    public void setSilverCoins(int amount){
        // Implement
    }

    public void setGoldCoins(int amount){
        // Implement
    }

    // Convert gold coins to silver and check whether the player has enough coins to spend
    public boolean spendCoins(int amountInSilver){
        // Implement
    }

    public boolean isGameCompleted() {
        return gameCompleted;
    }

    public void setGameCompleted(boolean gameCompleted) {
        // Implement
    }
}