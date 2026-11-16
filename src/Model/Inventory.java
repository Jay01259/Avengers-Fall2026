package Model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Inventory implements Iterable<Item> {
    public static final int BASE_CAPACITY = 6;
    public static final int SATCHEL_CAPACITY = 10;

    private int maxCapacity;
    private final List<Item> items;
    private boolean chestOpen;
    private String chestRoomId;

    public Inventory() {
        this(BASE_CAPACITY, null);
    }

    public Inventory(String chestRoomId) {
        this(BASE_CAPACITY, chestRoomId);
    }

    public Inventory(int maxCapacity) {
        this(maxCapacity, null);
    }

    public Inventory(int maxCapacity, String chestRoomId) {
        this.maxCapacity = Math.max(1, maxCapacity);
        this.items = new ArrayList<>();
        this.chestOpen = false;
        this.chestRoomId = chestRoomId;
    }

    // Purpose: Returns the inventory's normal slot capacity before any satchel bonus is applied.
    public int getMaxCapacity() {
        return maxCapacity;
    }

    // Purpose: Changes the normal inventory capacity when a reward or upgrade adds more slots.
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = Math.max(1, maxCapacity);
    }

    // Purpose: Returns whether the storage chest is currently unlocked and ready for chest commands.
    public boolean isChestOpen() {
        return chestOpen;
    }

    // Purpose: Opens or closes the chest state to control whether items can be stored or retrieved.
    public void setChestOpen(boolean chestOpen) {
        this.chestOpen = chestOpen;
    }

    // Purpose: Returns the room ID associated with the chest being used for storage.
    public String getChestRoomId() {
        return chestRoomId;
    }

    // Purpose: Stores the chest's room ID so the system can verify the correct place is being used.
    public void setChestRoomId(String chestRoomId) {
        this.chestRoomId = chestRoomId;
    }

    // Purpose: Returns the real inventory capacity after applying the satchel upgrade if it is present.
    public int getEffectiveCapacity() {
        return hasLeatherSatchel() ? SATCHEL_CAPACITY : maxCapacity;
    }

    // Purpose: Calculates the open slots left in the inventory before trying to add another item.
    public int getFreeSpace() {
        return Math.max(0, getEffectiveCapacity() - getUsedSlots());
    }

    // Purpose: Counts occupied non-key slots so the game can enforce inventory limits correctly.
    public int getUsedSlots() {
        int used = 0;
        for (Item item : items) {
            if (item != null && !item.isKey()) {
                used++;
            }
        }
        return used;
    }

    // Purpose: Returns true when the inventory has no items at all.
    public boolean isEmpty() {
        return items.isEmpty();
    }


    // Purpose: Checks whether the leather satchel upgrade is presently stored in the inventory.
    public boolean hasLeatherSatchel() {
        for (Item item : items) {
            if (item != null && "LEATHER_SATCHEL".equalsIgnoreCase(item.getId())) {
                return true;
            }
        }
        return false;
    }


    // Purpose: Adds an item to the inventory, merging stacks and preventing overflow beyond the current capacity.
    public boolean addItem(Item item) {
        if (item == null) {
            return false;
        }

        if (item.isKey()) {
            items.add(item);
            return true;
        }

        if (item.isStackable()) {
            Item existing = findMatchingStack(item);
            if (existing != null) {
                existing.addQuantity(item.getQuantity());
                return true;
            }
        }

        if (getFreeSpace() <= 0) {
            return false;
        }

        items.add(item);
        return true;
    }

    // Purpose: Finds an item in the inventory by ID or name so commands can act on the correct object.
    public Item getItem(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return null;
        }

        for (Item item : items) {
            if (item == null) {
                continue;
            }
            if (identifier.equalsIgnoreCase(item.getId())
                    || identifier.equalsIgnoreCase(item.getName())) {
                return item;
            }
        }
        return null;
    }

    // Purpose: Checks whether a specific item instance or matching item is currently in the inventory.
    public boolean contains(Item item) {
        if (item == null) {
            return false;
        }
        return getItem(item.getId() != null && !item.getId().trim().isEmpty()
                ? item.getId()
                : item.getName()) != null;
    }

    // Purpose: Removes an item by ID or name and returns it so the caller can place it elsewhere.
    public Item removeItem(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return null;
        }

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item == null) {
                continue;
            }

            if (identifier.equalsIgnoreCase(item.getId())
                    || identifier.equalsIgnoreCase(item.getName())) {
                if (item.isStackable() && item.getQuantity() > 1) {
                    item.removeQuantity(1);
                    return item;
                }
                items.remove(i);
                return item;
            }
        }
        return null;
    }

    // Purpose: Removes a specific item object from the inventory using its identifier when available.
    public Item removeItem(Item item) {
        if (item == null) {
            return null;
        }
        return removeItem(item.getId() != null && !item.getId().trim().isEmpty()
                ? item.getId()
                : item.getName());
    }

    // Purpose: Opens the storage chest when the player is in the right room, enabling chest actions.
    public boolean openChest(Object player) {
        if (isPlayerInRoom(player, "R01")) {
            chestOpen = true;
            return true;
        }
        return false;
    }

    // Purpose: Closes the storage chest to block further store or retrieve operations.
    public boolean closeChest() {
        chestOpen = false;
        return true;
    }

    // Purpose: Stores an item in the open chest after checking the player location and chest state.
    public boolean storeItem(Object player, Item item) {
        if (!chestOpen || !isPlayerInRoom(player, "R01")) {
            return false;
        }
        return addItem(item);
    }

    // Purpose: Retrieves an item from the chest by ID or name when the chest is open in the correct room.
    public Item retrieveItem(Object player, String identifier) {
        if (!chestOpen || !isPlayerInRoom(player, "R01")) {
            return null;
        }
        return removeItem(identifier);
    }

    // Purpose: Finds an existing stack that matches the new item so stackable items can be combined.
    private Item findMatchingStack(Item item) {
        for (Item existing : items) {
            if (existing == null) {
                continue;
            }
            if (existing.isStackable() && item.isStackable() && existing.matches(item)) {
                return existing;
            }
        }
        return null;
    }

    // Purpose: Validates that the player is in the expected room before chest actions are allowed.
    private boolean isPlayerInRoom(Object player, String expectedRoomId) {
        if (player == null) {
            return false;
        }

        try {
            Method getCurrentRoomMethod = player.getClass().getMethod("getCurrentRoom");
            Object room = getCurrentRoomMethod.invoke(player);

            if (room == null) {
                return false;
            }

            Method getRoomIdMethod = room.getClass().getMethod("getRoomID");
            Object roomIdValue = getRoomIdMethod.invoke(room);

            String roomId = roomIdValue == null ? "" : roomIdValue.toString();
            return expectedRoomId.equalsIgnoreCase(roomId);
        } catch (Exception e) {
            return false;
        }
    }

    // Purpose: Returns an iterator so the inventory can be displayed and looped through by the UI.
    @Override
    public Iterator<Item> iterator() {
        return items.iterator();
    }
}



