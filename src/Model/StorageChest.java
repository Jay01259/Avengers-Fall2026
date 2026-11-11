package Model;

import java.util.ArrayList;


public class StorageChest {
    private boolean isOpen;
    private ArrayList<Item> items;

    public StorageChest(){
        this.isOpen = false;
        this.items = new ArrayList<>();
    }

    public String open() {
        if (isOpen) {
            return "The chest is already open.";
        }
        isOpen = true;
        if (items.isEmpty()) {
            return "You open the chest. It is empty.";
        }
        String list = "";
        for (Item item : items) {
            if (!list.isEmpty()) {
                list += ", ";
            }
            list += item.getName();
        }
        return "You open the chest. Inside you find: " + list;
    }

    public String close() {
        if (!isOpen) {
            return "The chest is already closed.";
        }
        isOpen = false;
        return "You close the chest.";
    }


    public boolean storeItem(Item item) {
        if (!isOpen) {
            return false;
        }
        items.add(item);
        return true;
    }

    public Item retrieveItem(String itemName) {
        if (!isOpen) {
            return null;
        }
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                items.remove(item);
                return item;
            }
        }
        return null;
    }

    public boolean isOpen() {
        return isOpen;
    }


    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> savedItems) {
        items = savedItems;
    }

}
