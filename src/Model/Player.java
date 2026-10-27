package Model;

import java.util.ArrayList;
import java.util.List;

//Player feature - Jay

public class Player extends Character {

    List<Item> inventory;
    Room currentRoom;
    Weapon equippedWeapon;
    Armor equippedAmor;
    int currency;

    public Player(String name, int HP, int MaxHP, int attack, int defense, Room startingRoom) {
        super(name, HP, MaxHP, attack, defense);
        this.currentRoom = startingRoom;
        this.inventory = new ArrayList<>();
        this.equippedWeapon = null;
        this.equippedAmor = null;
        this.currency = 0;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public boolean moveRooms(String direction, List<Room> rooms) {
        Room current = rooms.get(currentRoom - 1);
        int nextRoom = 0;

        if (direction.equalsIgnoreCase("North")) {
            nextRoom = current.getNorth();
        } else if (direction.equalsIgnoreCase("East")) {
            nextRoom = current.getEast();
        } else if (direction.equalsIgnoreCase("South")) {
            nextRoom = current.getSouth();
        } else if (direction.equalsIgnoreCase("West")) {
            nextRoom = current.getWest();
        }

        if (nextRoom == 0) {
            return false;
        }

        currentRoom = nextRoom;
        return true;
    }
}