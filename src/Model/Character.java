package Model;

public abstract class Character {

    private String name;
    private int currentHP;
    private int maxHP;
    private int attack;
    private int defense;

    // Purpose: Creates the base game fighter with health, attack, and defense stats used by the player and enemies.
    public Character(String name, int currentHP, int maxHP, int attack, int defense) {
        this.name = name;
        this.currentHP = currentHP;
        this.maxHP = maxHP;
        this.attack = attack;
        this.defense = defense;
    }

    // Purpose: Returns the character's display name.
    public String getName() {
        return name;
    }

    // Purpose: Tests whether the character still has health remaining in combat.
    public boolean isAlive(){
        return currentHP > 0;
    }

    // Purpose: Restores HP without exceeding the character's maximum health limit.
    public int heal(int amount){
        if (amount <= 0 || currentHP >= maxHP || !isAlive()){
            return 0;
        }
        int previousHP = currentHP;
        currentHP += amount;
        if (currentHP > maxHP) {
            currentHP = maxHP;
        }
        return currentHP - previousHP;
    }

    // Purpose: Returns the current HP value for status checks and UI output.
    public int getCurrentHP() {
        return currentHP;
    }

    // Purpose: Updates the current HP while clamping it to valid battle bounds.
    public void setCurrentHP(int newHP) {
        if (newHP > maxHP) {
            currentHP = maxHP;
        } else if (newHP < 0) {
            currentHP = 0;
        } else {
            currentHP = newHP;
        }
    }

    // Purpose: Returns the maximum health value of the character.
    public int getMaxHP() {
        return maxHP;
    }

    // Purpose: Returns the character's attack stat used in combat calculations.
    public int getAttack() {
        return attack;
    }

    // Purpose: Returns the character's defense stat used to reduce incoming damage.
    public int getDefense() {
        return defense;
    }

    // Purpose: Changes defense for buffs, debuffs, or armor effects.
    public void setDefense(int newDefense) {
        defense = newDefense;
    }
}

