package Kelvin;

public class Armor extends Item {
    private int defenseStats;
    private int durability;

    public Armor(String name, String description, int value, int defenseStats) {
        super(name, description, value);
        this.defenseStats = defenseStats;
        this.durability = 100; // Example initial durability
    }

    public int getDefenseStats() {
        return defenseStats;
    }

    public void setDefenseStats(int defenseStats) {
        this.defenseStats = defenseStats;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }
}