package Model;

import java.util.ArrayList;
import java.util.List;


public class Player extends Character {

    private Inventory inventory;
    private Room currentRoom;
    private Weapon equippedWeapon;
    private Armor equippedArmor;
    private int silverCoins;
    private int goldCoins;
    private List<String> visitedRoomsIDs;
    private boolean gameCompleted;

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

    // Drops an item from the player's inventory into the current room.
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

    // Inspects an item and returns its name and description.
    public String inspectItem(Item item) {
        if (item == null) {
            return "Item cannot be inspected.";
        }

        return "Item: " + item.getName() + "\nDescription: " + item.getDescription();
    }

    // Uses a consumable item or a room-specific key.
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

    // Equips a weapon or armor from the player's inventory.
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

    // Unequips a weapon or armor from the player.
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
    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    // Moves the player to another room and records the room as visited.
    public void moveTo(Room room) {
        if (room == null) {
            return;
        }

        currentRoom = room;
        markRoomVisited(room.getRoomID());
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