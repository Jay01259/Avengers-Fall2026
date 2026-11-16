package Model;

public class Item {
    private String id;
    private String name;
    private String description;
    private int valueSilver;
    private boolean isQuestItem;
    private boolean isDroppable;
    private int quantity;
    private boolean stackable;

    // Purpose: Creates the base item record with the core stats used by every item in the game.
    public Item(String id, String name, String description, int valueSilver) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.valueSilver = valueSilver;
        this.isQuestItem = false;
        this.isDroppable = true;
        this.quantity = 1;
        this.stackable = false;
    }

    // Purpose: Checks whether the item is one of the special key items used for unlocking doors.
    public boolean isKey() {
        return id != null && (
                id.equalsIgnoreCase("W07") ||
                        id.equalsIgnoreCase("W08") ||
                        id.equalsIgnoreCase("W16")
        );
    }

    // Purpose: Returns the unique item ID used by the world and loader.
    public String getId() {
        return id;
    }

    // Purpose: Stores the item ID so it matches the game's item data file.
    public void setId(String id) {
        this.id = id;
    }

    // Purpose: Returns the display name shown to the player in the UI and inventory.
    public String getName() {
        return name;
    }

    // Purpose: Updates the item name in case the item data changes at runtime.
    public void setName(String name) {
        this.name = name;
    }

    // Purpose: Returns the detailed item description used when inspecting an object.
    public String getDescription() {
        return description;
    }

    // Purpose: Replaces the item description when a specialized item needs custom text.
    public void setDescription(String description) {
        this.description = description;
    }

    // Purpose: Returns the silver value of the item for trading or selling.
    public int getValueSilver() {
        return valueSilver;
    }

    // Purpose: Updates the silver value when an item is adjusted in balance or economy.
    public void setValueSilver(int valueSilver) {
        this.valueSilver = valueSilver;
    }

    // Purpose: Indicates whether the item is special quest content and should be preserved.
    public boolean isQuestItem() {
        return isQuestItem;
    }

    // Purpose: Marks the item as a quest item so it cannot be treated as ordinary loot.
    public void setQuestItem(boolean questItem) {
        isQuestItem = questItem;
    }

    // Purpose: Returns whether the item can be dropped from inventory into the room.
    public boolean isDroppable() {
        return isDroppable;
    }

    // Purpose: Enables or disables dropping for items that should stay in the inventory.
    public void setDroppable(boolean droppable) {
        isDroppable = droppable;
    }

    // Purpose: Gets the current quantity for stackable items.
    public int getQuantity() {
        return quantity;
    }

    // Purpose: Sets the quantity and clamps it to a safe non-negative value.
    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
    }

    // Purpose: Adds to an item's quantity when stacking or consuming a stackable item.
    public void addQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    // Purpose: Removes quantity while preventing the count from going below zero.
    public void removeQuantity(int amount) {
        if (amount > 0) {
            this.quantity = Math.max(0, this.quantity - amount);
        }
    }

    // Purpose: Returns whether items of this type can stack together in the inventory.
    public boolean isStackable() {
        return stackable;
    }

    // Purpose: Toggles stack behavior for items such as currency and potions.
    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    // Purpose: Compares another item by ID so identical items can be merged or matched.
    public boolean matches(Item other) {
        if (other == null) {
            return false;
        }
        if (id != null && other.id != null) {
            return id.equalsIgnoreCase(other.id);
        }
        return name.equalsIgnoreCase(other.name);
    }

    // Purpose: Prints a readable summary of the item for debugging or command-line checks.
    public void inspect() {
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Description: " + description);
        System.out.println("Value: " + valueSilver + " silver");
        System.out.println("Quest Item: " + isQuestItem);
        System.out.println("Droppable: " + isDroppable);
        System.out.println("Quantity: " + quantity);
    }
}

