package Kelvin;

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

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = Math.max(1, maxCapacity);
    }

    public boolean isChestOpen() {
        return chestOpen;
    }

    public void setChestOpen(boolean chestOpen) {
        this.chestOpen = chestOpen;
    }

    public String getChestRoomId() {
        return chestRoomId;
    }

    public void setChestRoomId(String chestRoomId) {
        this.chestRoomId = chestRoomId;
    }

    public int getEffectiveCapacity() {
        return hasLeatherSatchel() ? SATCHEL_CAPACITY : maxCapacity;
    }

    public int getFreeSpace() {
        return Math.max(0, getEffectiveCapacity() - getUsedSlots());
    }

    public int getUsedSlots() {
        int used = 0;
        for (Item item : items) {
            if (item != null && !item.isKey()) {
                used++;
            }
        }
        return used;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean hasLeatherSatchel() {
        for (Item item : items) {
            if (item != null && Item.LEATHER_SATCHEL.equalsIgnoreCase(item.getId())) {
                return true;
            }
        }
        return false;
    }

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

    public boolean contains(Item item) {
        if (item == null) {
            return false;
        }
        return getItem(item.getId() != null && !item.getId().trim().isEmpty()
                ? item.getId()
                : item.getName()) != null;
    }

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

    public Item removeItem(Item item) {
        if (item == null) {
            return null;
        }
        return removeItem(item.getId() != null && !item.getId().trim().isEmpty()
                ? item.getId()
                : item.getName());
    }

    public boolean openChest(Object player) {
        if (isPlayerInRoom(player, "R01")) {
            chestOpen = true;
            return true;
        }
        return false;
    }

    public boolean closeChest() {
        chestOpen = false;
        return true;
    }

    public boolean storeItem(Object player, Item item) {
        if (!chestOpen || !isPlayerInRoom(player, "R01")) {
            return false;
        }
        return addItem(item);
    }

    public Item retrieveItem(Object player, String identifier) {
        if (!chestOpen || !isPlayerInRoom(player, "R01")) {
            return null;
        }
        return removeItem(identifier);
    }

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

    @Override
    public Iterator<Item> iterator() {
        return items.iterator();
    }
}

