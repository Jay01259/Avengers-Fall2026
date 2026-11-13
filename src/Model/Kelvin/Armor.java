package Kelvin;

public class Armor extends Item {
    private int defenseStats;
    private int durability;
    private int hitcounter;

    public Armor(String name, String description, int value, int defenseStats) {
        super(name, description, value);
        this.defenseStats = defenseStats;
        this.durability = 100; // Example initial durability
        this.hitcounter = 0;
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

    public int getHitcounter() {
        return hitcounter;
    }

    public void setHitcounter(int hitcounter) {
        this.hitcounter = hitcounter;
    }
}