package Kelvin;

public class Armor extends Item {
    private int defenseBonus;
    private int durabilityLimit;
    private int hitsTakenCounter;

    public Armor(String id, String name, String description, int valueSilver,
                 int defenseBonus, int durabilityLimit) {
        super(id, name, description, valueSilver);
        this.defenseBonus = defenseBonus;
        this.durabilityLimit = durabilityLimit;
        this.hitsTakenCounter = 0;
        setStackable(false);
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public void setDefenseBonus(int defenseBonus) {
        this.defenseBonus = defenseBonus;
    }

    public int getDurabilityLimit() {
        return durabilityLimit;
    }

    public void setDurabilityLimit(int durabilityLimit) {
        this.durabilityLimit = durabilityLimit;
    }

    public int getHitsTakenCounter() {
        return hitsTakenCounter;
    }

    public void setHitsTakenCounter(int hitsTakenCounter) {
        this.hitsTakenCounter = hitsTakenCounter;
    }

    public void takeHit() {
        if (durabilityLimit > 0) {
            hitsTakenCounter++;
        }
    }

    public boolean isInfiniteDurability() {
        return durabilityLimit < 0;
    }

    public boolean isBroken() {
        if (isInfiniteDurability()) {
            return false;
        }
        return hitsTakenCounter >= durabilityLimit;
    }
}