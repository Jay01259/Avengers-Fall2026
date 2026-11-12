package Kelvin;

public class Consumable extends Item {
    private int healthRestore;

    Consumable(String name, String description, int value, int healthRestore) {
        super(name, description, value);
        this.healthRestore = healthRestore;
    }


}
