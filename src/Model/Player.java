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
    // Purpose: Returns the player's active inventory container so item commands can read and modify it.
    public Inventory getInventory() {
        return inventory;
    }

    // Purpose: Adds an item from the current room into the inventory and removes it from the room when successful.
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
    // Purpose: Drops an item from inventory into the current room so the room can receive it again.
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
    // Purpose: Returns a readable item inspection string for the current item being examined.
    public String inspectItem(Item item) {
        if (item == null) {
            return "Item cannot be inspected.";
        }

        return "Item: " + item.getName() + "\nDescription: " + item.getDescription();
    }

    // Uses a consumable item or a room-specific key.
    // Purpose: Uses a consumable or key item from inventory when the situation allows it.
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
    // Purpose: Equips a weapon or armor from inventory so the player gains its combat bonuses.
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
    // Purpose: Unequips a currently equipped weapon or armor and clears the slot.
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

    // Purpose: Returns the currently equipped weapon so combat code can read its bonus.
    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    // Purpose: Sets the active weapon slot for equipment control and combat logic.
    public void setEquippedWeapon(Weapon equippedWeapon) {
        this.equippedWeapon = equippedWeapon;
    }

    // Purpose: Returns the armor currently equipped to reduce damage in combat.
    public Armor getEquippedArmor() {
        return equippedArmor;
    }

    // Purpose: Sets the active armor slot to match the equipment the player is wearing.
    public void setEquippedArmor(Armor equippedArmor) {
        this.equippedArmor = equippedArmor;
    }



    // NPC Interaction Methods
    public boolean buyItem(String itemID, NPC npc) {
        if (itemID == null || npc == null || inventory == null) {
            return false;
        }

        Item item = inventory.getItem(itemID);
        if (item == null) {
            item = new Item(itemID, itemID, "", 0);
        }

        int price = npc.getBuyPrice(itemID);
        if (price <= 0) {
            price = item.getValueSilver();
        }
        if (!npc.sellsItem(itemID) && price <= 0) {
            return false;
        }

        int totalSilver = silverCoins + (goldCoins * 100);
        if (totalSilver < price) {
            return false;
        }

        if (!inventory.addItem(item)) {
            return false;
        }
        spendCoins(price);
        return true;
    }

    public boolean sellItem(String itemID, NPC npc) {
        if (itemID == null || npc == null || inventory == null) {
            return false;
        }

        Item item = inventory.getItem(itemID);
        if (item == null) {
            return false;
        }

        int payout = npc.getSellPrice(itemID);
        if (payout <= 0) {
            payout = item.getValueSilver();
        }
        if (!npc.buysItem(itemID) && payout <= 0) {
            return false;
        }

        inventory.removeItem(item);
        int newSilver = silverCoins + payout;
        if (newSilver >= 100) {
            goldCoins += newSilver / 100;
            silverCoins = newSilver % 100;
        } else {
            silverCoins = newSilver;
        }
        return true;
    }

    public boolean giveItem(String itemID, NPC npc) {
        if (itemID == null || npc == null || inventory == null) {
            return false;
        }

        Item item = inventory.getItem(itemID);
        if (item == null) {
            return false;
        }

        if (npc.getNPCID().equals("NPC03") && "W12".equalsIgnoreCase(item.getId())) {
            int armorCount = 0;
            for (Item invItem : inventory) {
                if (invItem != null && "W12".equalsIgnoreCase(invItem.getId())) {
                    armorCount++;
                }
            }
            if (armorCount >= 3) {
                Item reward = new Item("W11", "Silver pin", "A beautiful silver pin adorned with gemstones.", 400);
                inventory.removeItem(item);
                inventory.addItem(reward);
                npc.setQuestComplete(true);
                return true;
            }
        }

        if (npc.getNPCID().equals("NPC03") && item.getId() != null && item.getId().equalsIgnoreCase("W12")) {
            inventory.removeItem(item);
            return true;
        }

        inventory.removeItem(item);
        return true;
    }


    // Room Methods
    // Purpose: Returns the room the player is currently standing in.
    public Room getCurrentRoom() {
        return currentRoom;
    }

    // Purpose: Moves the player to a different room and updates the current location state.
    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    // Moves the player to another room and records the room as visited.
    // Purpose: Moves the player to a target room and records it as visited for navigation and saving.
    public void moveTo(Room room) {
        if (room == null) {
            return;
        }

        currentRoom = room;
        markRoomVisited(room.getRoomID());
    }

    // Purpose: Adds a room ID to the visited list so the game can track explored locations.
    public void markRoomVisited(String roomID) {
        if (roomID != null && !visitedRoomsIDs.contains(roomID)){
            visitedRoomsIDs.add(roomID);
        }
    }

    // Purpose: Checks whether the given room has already been visited during the session.
    public boolean hasVisitedRoom(String roomID) {
        return roomID != null && visitedRoomsIDs.contains(roomID);
    }

    // Purpose: Returns a copy of the visited-room list so callers can inspect or save the progress safely.
    public List<String> getVisitedRoomsIDs() {
        return new ArrayList<>(visitedRoomsIDs);
    }

    // Purpose: Replaces the visited map with a saved list used when restoring a player state.
    public void setVisitedRoomsIDs(List<String> visitedRoomsIDs) {
        this.visitedRoomsIDs = visitedRoomsIDs == null ? new ArrayList<>() : new ArrayList<>(visitedRoomsIDs);
    }

    // Currency Methods
    // Purpose: Returns the player's silver currency total for purchases and sales.
    public int getSilverCoins() {
        return silverCoins;
    }

    // Purpose: Returns the player's gold currency amount used for more valuable transactions.
    public int getGoldCoins(){
        return goldCoins;
    }

    // Purpose: Sets the silver balance after a save load or a shop transaction.
    public void setSilverCoins(int amount){
        if (amount >= 0){
            this.silverCoins = amount;
        }
    }

    // Purpose: Sets the gold balance after a save load or a transaction involving higher-value currency.
    public void setGoldCoins(int amount){
        if (amount >= 0){
            this.goldCoins = amount;
        }
    }

    // Purpose: Spends currency using a combined silver total so purchases can work across gold and silver denominations.
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
    // Purpose: Returns whether the game win condition has been reached.
    public boolean isGameCompleted() {
        return gameCompleted;
    }

    // Purpose: Marks the player as having finished the main objective of the game.
    public void setGameCompleted(boolean gameCompleted) {
        this.gameCompleted = gameCompleted;
    }
}

