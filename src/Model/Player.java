package Model;

import java.util.ArrayList;
import java.util.List;

//Player feature - Jay

public class Player extends Character {

    private Inventory inventory;
    private Room currentRoom;
    private Weapon equippedWeapon;
    private Armor equippedArmor;
    private int silverCoins;
    private int goldCoins;
    private List<String> visitedRoomsIDs;
    private boolean gameCompleted;

    /*
    Check doc to see if player can check which puzzles they have completed without being
    in the room with the puzzle. If so, we can add a list of completed puzzles to the player class.

    private List<String> completedPuzzlesID;
    */

    public Player(String name, int currentHP, int maxHP, int attack, int defense, Inventory inventory,
                  Room currentRoom, Weapon equippedWeapon, Armor equippedArmor, int silverCoins,
                  int goldCoins, List<String> visitedRoomsIDs, boolean gameCompleted) {
        super(name, currentHP, maxHP, attack, defense);
        this.inventory = inventory;
        this.currentRoom = currentRoom;
        this.equippedWeapon = equippedWeapon;
        this.equippedArmor = equippedArmor;
        this.silverCoins = silverCoins;
        this.goldCoins = goldCoins;
        this.visitedRoomsIDs = visitedRoomsIDs == null ? new ArrayList<>() : new ArrayList<>(visitedRoomsIDs);
        this.gameCompleted = gameCompleted;
    }



    //Inventory Management Methods
    public Inventory getInventory() {
        return inventory;
    }

    public boolean pickUpItem(Item item) {
        // Implement
    }

    public boolean dropItem(Item item) {
        // Implement
    }

    public String inspectItem(Item item) {
        // Implement
    }

    public boolean useItem(Item item) {
        // Implement
    }

    // Use if/else statements to check if the item is a weapon or armor, and then equip it accordingly.
    public boolean equipItem(Item item) {
        // Implement
    }

    // Use if/else statements to check if the item is a weapon or armor, and then unequip it accordingly.
    public boolean unequipItem(Item item) {
        // Implement
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public void setEquippedWeapon(Weapon equippedWeapon) {
        this.equippedWeapon = equippedWeapon;
    }

    public Armor getEquippedArmor() {
        return equippedArmor;
    }

    public void setEquippedArmor(Armor equippedArmor) {
        this.equippedArmor = equippedArmor;
    }

    public boolean buyItem(String itemID, NPC npc) {
        // Implement
    }

    public boolean sellItem(String itemID, NPC npc) {
        // Implement
    }



    // Room Methods
    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public void moveTo(Room room) {
        // Implement
    }

    public void markRoomVisited(String roomID) {
        if (roomID != null && !visitedRoomsIDs.contains(roomID)){
            visitedRoomsIDs.add(roomID);
        }
    }

    public boolean hasVisitedRoom(String roomID) {
        return roomID != null && visitedRoomsIDs.contains(roomID);
    }

    public List<String> getVisitedRoomsIDs() {
        return new ArrayList<>(visitedRoomsIDs);
    }

    public void setVisitedRoomsIDs(List<String> visitedRoomsIDs) {
        this.visitedRoomsIDs = visitedRoomsIDs == null ? new ArrayList<>() : new ArrayList<>(visitedRoomsIDs);
    }

    // Currency Methods
    public int getSilverCoins() {
        return silverCoins;
    }

    public int getGoldCoins(){
        return goldCoins;
    }

    public void setSilverCoins(int amount){
        if (amount >= 0){
            this.silverCoins = amount;
        }
    }

    public void setGoldCoins(int amount){
        if (amount >= 0){
            this.goldCoins = amount;
        }
    }

    public boolean spendCoins(int amountInSilver){
        if (amountInSilver < 0){
            return false;
        }

        int totalSilver = silverCoins + (goldCoins * 100);

        if (totalSilver < amountInSilver){
            return false;
        }

        totalSilver -= amountInSilver;

        goldCoins = totalSilver / 100;
        silverCoins = totalSilver % 100;
        return true;
    }



    // Game Completion Methods
    public boolean isGameCompleted() {
        return gameCompleted;
    }

    public void setGameCompleted(boolean gameCompleted) {
        this.gameCompleted = gameCompleted;
    }
}