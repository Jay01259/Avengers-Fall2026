package Kelvin;

public class Item {
    private String id;
    private String name;
    private String description;
    private int valueSilver;
    private boolean isQuestItem;
    private boolean isDroppable;
    private int quantity;
    private boolean stackable;

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

    public boolean isKey() {
        return id != null && (
                id.equalsIgnoreCase("W07") ||
                        id.equalsIgnoreCase("W08") ||
                        id.equalsIgnoreCase("W16")
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getValueSilver() {
        return valueSilver;
    }

    public void setValueSilver(int valueSilver) {
        this.valueSilver = valueSilver;
    }

    public boolean isQuestItem() {
        return isQuestItem;
    }

    public void setQuestItem(boolean questItem) {
        isQuestItem = questItem;
    }

    public boolean isDroppable() {
        return isDroppable;
    }

    public void setDroppable(boolean droppable) {
        isDroppable = droppable;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
    }

    public void addQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    public void removeQuantity(int amount) {
        if (amount > 0) {
            this.quantity = Math.max(0, this.quantity - amount);
        }
    }

    public boolean isStackable() {
        return stackable;
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    public boolean matches(Item other) {
        if (other == null) {
            return false;
        }
        if (id != null && other.id != null) {
            return id.equalsIgnoreCase(other.id);
        }
        return name.equalsIgnoreCase(other.name);
    }

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