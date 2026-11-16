package Model;

public class Armor extends Item {
    private int defenseBonus;
    private int durabilityLimit;
    private int hitsTakenCounter;

    // Purpose: Creates protective armor with defense and wear tracking for combat encounters.
    public Armor(String id, String name, String description, int valueSilver,
                 int defenseBonus, int durabilityLimit) {
        super(id, name, description, valueSilver);
        this.defenseBonus = defenseBonus;
        this.durabilityLimit = durabilityLimit;
        this.hitsTakenCounter = 0;
        setStackable(false);
    }

    // Purpose: Returns the armor's defense bonus applied during combat.
    public int getDefenseBonus() {
        return defenseBonus;
    }

    // Purpose: Updates the armor's defense value when balancing equipment or loot.
    public void setDefenseBonus(int defenseBonus) {
        this.defenseBonus = defenseBonus;
    }

    // Purpose: Returns the maximum number of hits before the armor breaks.
    public int getDurabilityLimit() {
        return durabilityLimit;
    }

    // Purpose: Updates armor durability rules as design changes require.
    public void setDurabilityLimit(int durabilityLimit) {
        this.durabilityLimit = durabilityLimit;
    }

    // Purpose: Returns how many hits the armor has taken so far.
    public int getHitsTakenCounter() {
        return hitsTakenCounter;
    }

    // Purpose: Forces the armor's wear counter to a value used by the save/load system.
    public void setHitsTakenCounter(int hitsTakenCounter) {
        this.hitsTakenCounter = hitsTakenCounter;
    }

    // Purpose: Marks a struck hit so the armor can degrade over time.
    public void takeHit() {
        if (durabilityLimit > 0) {
            hitsTakenCounter++;
        }
    }

    // Purpose: Checks whether this armor has no finite lifespan.
    public boolean isInfiniteDurability() {
        return durabilityLimit < 0;
    }

    // Purpose: Determines when the armor has used up its durability and is no longer effective.
    public boolean isBroken() {
        if (isInfiniteDurability()) {
            return false;
        }
        return hitsTakenCounter >= durabilityLimit;
    }
}

