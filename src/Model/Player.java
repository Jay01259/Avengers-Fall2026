package Model;

import java.util.ArrayList;
import java.util.List;

/** Represents the player character controlled by the user throughout the game.
 * This class extends Character and inherits its shared attributes, such as
 * health, attack, defense, and name. It manages the player's inventory,
 * equipped weapons and armor, current room, visited rooms, and currency.
 * Player also provides methods for picking up, dropping, inspecting, using,
 * equipping, and unequipping items. Additionally, it handles room movement,
 * tracking visited locations, managing silver and gold coins, and recording
 * whether the player has completed the game. */

public class Player extends Character {

    private Inventory inventory;
    private Room currentRoom;
    private Weapon equippedWeapon;
    private Armor equippedArmor;
    private int silverCoins;
    private int goldCoins;
    private List<String> visitedRoomsIDs;
    private boolean gameCompleted;

    /** Creates a player with the specified character statistics, inventory,
     * current room, equipped equipment, currency, visited rooms, and game
     * completion status. It initializes the player's attributes and passes
     * the character-related information to the Character superclass. */
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

    /** Returns the player's inventory, allowing other classes to access
     * the items currently stored by the player. */
    public Inventory getInventory() {
        return inventory;
    }

    /** Attempts to add an item to the player's inventory. If the item is
     * successfully added and exists in the current room, it is also removed
     * from that room. Returns true if the item is successfully picked up
     * and false if the item cannot be added. */
    public boolean pickUpItem(Item item) {
        if (item == null || inventory == null) {
            return false;
        }

        if (!inventory.addItem(item)) {
            return false;
        }

        if (currentRoom != null && currentRoom.getItems().contains(item)) {
            currentRoom.removeItem(item);
        }

        return true;
    }

    /** Attempts to remove an item from the player's inventory and place it
     * in the current room. The item must be droppable and exist in the
     * inventory. When dropping a consumable from a stack, a separate item
     * containing one unit is created so the remaining quantity stays in
     * the player's inventory. */
    public boolean dropItem(Item item) {
        if (item == null || inventory == null || currentRoom == null || !inventory.contains(item) || !item.isDroppable()) {
            return false;
        }

        Item droppedItem = item;

        // Create a separate item when dropping one from a stack.
        if (item instanceof Consumable && item.getQuantity() > 1) {
            Consumable consumable = (Consumable) item;

            droppedItem = new Consumable(item.getId(), item.getName(), item.getDescription(),
                    item.getValueSilver(), consumable.getHealAmount(), 1
            );

            droppedItem.setQuestItem(item.isQuestItem());
            droppedItem.setDroppable(item.isDroppable());
        }

        Item removedItem = inventory.removeItem(item);

        if (removedItem == null) {
            return false;
        }

        currentRoom.addItem(droppedItem);

        return true;
    }

    /** Returns the name and description of the specified item so the player
     * can examine its information. If the item is null, a message is
     * returned indicating that it cannot be inspected. */
    public String inspectItem(Item item) {
        if (item == null) {
            return "Item cannot be inspected.";
        }

        return "Item: " + item.getName() + "\nDescription: " + item.getDescription();
    }

    /** Attempts to use a specified amount of an item from the player's
     * inventory. Consumables restore the player's health and are removed
     * from the inventory when their quantity reaches zero. Key items can
     * be used when their target room is connected to the player's current
     * room. Returns true if the item is successfully used. */
    public boolean useItem(Item item, int amount) {
        if (item == null || inventory == null || amount <= 0 || !inventory.contains(item)) {
            return false;
        }

        // Use consumable items.
        if (item instanceof Consumable) {
            Consumable consumable = (Consumable) item;

            if (amount > consumable.getQuantity()) {
                return false;
            }

            int healingAmount = consumable.getHealAmount() * amount;

            // Do not consume items if no health can be restored.
            if (heal(healingAmount) == 0) {
                return false;
            }

            consumable.removeQuantity(amount);

            // Remove the item entirely when its quantity reaches zero.
            if (consumable.getQuantity() == 0) {
                inventory.removeItem(consumable);
            }

            return true;
        }

        // Use room-specific keys.
        if (item instanceof KeyItem && amount == 1 && currentRoom != null) {
            KeyItem key = (KeyItem) item;
            String targetRoomID = key.getRoomId();

            if (targetRoomID == null) {
                return false;
            }

            return targetRoomID.equalsIgnoreCase(currentRoom.getNorth()) || targetRoomID.equalsIgnoreCase(currentRoom.getEast())
                    || targetRoomID.equalsIgnoreCase(currentRoom.getSouth()) || targetRoomID.equalsIgnoreCase(currentRoom.getWest());
        }

        return false;
    }

    /** Attempts to equip a weapon or piece of armor from the player's
     * inventory. The item is assigned to the appropriate equipment slot
     * depending on its type. Returns true if the item is successfully
     * equipped and false if it cannot be equipped. */
    public boolean equipItem(Item item) {
        if (item == null || inventory == null || !inventory.contains(item)) {
            return false;
        }

        if (item instanceof Weapon) {
            equippedWeapon = (Weapon) item;
            return true;
        }
        else if (item instanceof Armor) {
            equippedArmor = (Armor) item;
            return true;
        }

        return false;
    }

    /** Attempts to remove an equipped weapon or piece of armor from the
     * player's equipment slot. The item must exist in the inventory and
     * match the currently equipped item. Returns true if the equipment
     * is successfully removed and false otherwise. */
    public boolean unequipItem(Item item) {
        if (item == null || inventory == null || !inventory.contains(item)) {
            return false;
        }

        if (item instanceof Weapon) {
            if (equippedWeapon == null || !equippedWeapon.getId().equalsIgnoreCase(item.getId())) {
                return false;
            }

            equippedWeapon = null;
            return true;
        }
        else if (item instanceof Armor) {
            if (equippedArmor == null || !equippedArmor.getId().equalsIgnoreCase(item.getId())) {
                return false;
            }

            equippedArmor = null;
            return true;
        }

        return false;
    }

    /** Returns the weapon currently equipped by the player. Returns null
     * if the player does not have a weapon equipped. */
    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    /** Sets the player's currently equipped weapon to the specified weapon.
     * This allows other classes to update which weapon the player is using. */
    public void setEquippedWeapon(Weapon equippedWeapon) {
        this.equippedWeapon = equippedWeapon;
    }

    /** Returns the armor currently equipped by the player. Returns null
     * if the player does not have any armor equipped. */
    public Armor getEquippedArmor() {
        return equippedArmor;
    }

    /** Sets the player's currently equipped armor to the specified armor.
     * This allows other classes to update which armor the player is wearing. */
    public void setEquippedArmor(Armor equippedArmor) {
        this.equippedArmor = equippedArmor;
    }



    // NPC Interaction Methods
    /*
    public boolean buyItem(String itemID, NPC npc) {
        // Implement
    }

    public boolean sellItem(String itemID, NPC npc) {
        // Implement
    }

    public boolean giveItem(String itemID, NPC npc) {
        // Implement
    }
     */


    // Room Methods
    /** Returns the room the player is currently occupying. This allows
     * other classes to determine the player's current location. */
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /** Changes the player's current room to the specified room. This updates
     * the player's location without performing any additional movement logic. */
    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    /** Moves the player to the specified room and updates their current
     * location. The room is also recorded in the player's visited rooms
     * list so the game can keep track of previously explored locations. */
    public void moveTo(Room room) {
        if (room == null) {
            return;
        }

        currentRoom = room;
        markRoomVisited(room.getRoomID());
    }

    /** Adds the specified room ID to the player's visited rooms list if
     * it has not already been recorded. This prevents duplicate room
     * entries when the player visits the same location multiple times. */
    public void markRoomVisited(String roomID) {
        if (roomID != null && !visitedRoomsIDs.contains(roomID)){
            visitedRoomsIDs.add(roomID);
        }
    }

    /** Checks whether the player has previously visited the room with the
     * specified ID. Returns true if the room is found in the visited rooms
     * list and false if it has not been visited. */
    public boolean hasVisitedRoom(String roomID) {
        return roomID != null && visitedRoomsIDs.contains(roomID);
    }

    /** Returns a copy of the list containing the IDs of all rooms the
     * player has visited. Returning a copy prevents other classes from
     * directly modifying the player's original list. */
    public List<String> getVisitedRoomsIDs() {
        return new ArrayList<>(visitedRoomsIDs);
    }

    /** Replaces the player's visited rooms list with the specified list
     * of room IDs. A new list is created to prevent outside modifications
     * from directly affecting the player's original list. */
    public void setVisitedRoomsIDs(List<String> visitedRoomsIDs) {
        this.visitedRoomsIDs = visitedRoomsIDs == null ? new ArrayList<>() : new ArrayList<>(visitedRoomsIDs);
    }

    /** Returns the number of coins currently owned by the player. */
    public int getSilverCoins() {
        return silverCoins;
    }

    public int getGoldCoins(){
        return goldCoins;
    }

    /** Updates the player's coin balance to the specified amount.
     * Negative amounts are not accepted. */
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

    /** Attempts to deduct the specified amount from the player's total
     * currency balance. Gold coins are converted into silver when calculating
     * the total, using 100 silver coins for each gold coin. Returns false
     * if the player does not have enough currency or the amount is invalid. */
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

    /** Returns whether the player has completed the game. This allows
     * other classes to check the player's game completion status. */
    public boolean isGameCompleted() {
        return gameCompleted;
    }

    /** Updates the player's game completion status. This can be used to
     * mark the game as completed when the player reaches the ending. */
    public void setGameCompleted(boolean gameCompleted) {
        this.gameCompleted = gameCompleted;
    }
}