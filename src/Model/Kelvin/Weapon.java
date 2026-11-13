package Kelvin;

public class Weapon extends Item {
    private int attackBonus;
    private int durabilityLimit;
    private int attackCounter;

    public Weapon(String id, String name, String description, int valueSilver,
                  int attackBonus, int durabilityLimit) {
        super(id, name, description, valueSilver);
        this.attackBonus = attackBonus;
        this.durabilityLimit = durabilityLimit;
        this.attackCounter = 0;
        setStackable(false);
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public void setAttackBonus(int attackBonus) {
        this.attackBonus = attackBonus;
    }

    public int getDurabilityLimit() {
        return durabilityLimit;
    }

    public void setDurabilityLimit(int durabilityLimit) {
        this.durabilityLimit = durabilityLimit;
    }

    public int getAttackCounter() {
        return attackCounter;
    }

    public void setAttackCounter(int attackCounter) {
        this.attackCounter = attackCounter;
    }

    public void useTurn() {
        if (durabilityLimit > 0) {
            attackCounter++;
        }
    }

    public boolean isInfiniteDurability() {
        return durabilityLimit < 0;
    }

    public boolean isBroken() {
        if (isInfiniteDurability()) {
            return false;
        }
        return attackCounter >= durabilityLimit;
    }
}