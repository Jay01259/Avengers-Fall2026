package Kelvin;

public class Item {
    private String ID;
    private String name;
    private String description;
    private int valueSilver;
    boolean questItem;

    public Item(String name, String description, int value) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.isDroppable = true;
        this.questItem = false;
    }

    // Getters and setters
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isDroppable() {
        return isDroppable;
    }

    public void setDroppable(boolean droppable) {
        isDroppable = droppable;
    }

    public boolean isQuestItem() {
        return questItem;
    }

    public void setQuestItem(boolean questItem) {
        this.questItem = questItem;
    }

    public void inspect() {
        System.out.println("Name: " + name);
        System.out.println("Description: " + description);
        System.out.println("Value: " + value);
        System.out.println("Is Droppable: " + isDroppable);
        System.out.println("Is Quest Item: " + questItem);
    }
}
