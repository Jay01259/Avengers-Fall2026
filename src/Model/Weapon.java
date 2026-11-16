package Model;

public class Weapon extends Item {
    private int attackBonus;
    private int durabilityLimit;
    private int attackCounter;

    // Purpose: Creates a weapon with its damage bonus and durability rules.
    public Weapon(String id, String name, String description, int valueSilver,
                  int attackBonus, int durabilityLimit) {
        super(id, name, description, valueSilver);
        this.attackBonus = attackBonus;
        this.durabilityLimit = durabilityLimit;
        this.attackCounter = 0;
        setStackable(false);
    }

    // Purpose: Returns the weapon's attack damage bonus used in battle calculations.
    public int getAttackBonus() {
        return attackBonus;
    }

    // Purpose: Updates the weapon's combat bonus after balancing or upgrades.
    public void setAttackBonus(int attackBonus) {
        this.attackBonus = attackBonus;
    }

    // Purpose: Returns the maximum number of attacks before the weapon breaks, or -1 for infinite durability.
    public int getDurabilityLimit() {
        return durabilityLimit;
    }

    // Purpose: Changes how long the weapon remains usable before it is destroyed.
    public void setDurabilityLimit(int durabilityLimit) {
        this.durabilityLimit = durabilityLimit;
    }

    // Purpose: Returns how many times the weapon has been used in combat.
    public int getAttackCounter() {
        return attackCounter;
    }

    // Purpose: Sets the weapon use count for durability tracking.
    public void setAttackCounter(int attackCounter) {
        this.attackCounter = attackCounter;
    }

    // Purpose: Advances the weapon's usage count when a turn is spent attacking.
    public void useTurn() {
        if (durabilityLimit > 0) {
            attackCounter++;
        }
    }

    // Purpose: Checks whether the weapon has unlimited durability instead of a finite lifespan.
    public boolean isInfiniteDurability() {
        return durabilityLimit < 0;
    }

    // Purpose: Determines if the weapon has worn out and is no longer combat-ready.
    public boolean isBroken() {
        if (isInfiniteDurability()) {
            return false;
        }
        return attackCounter >= durabilityLimit;
    }
}

