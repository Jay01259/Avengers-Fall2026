package Model;

/** Represents the base class for all characters in the game, including the
 * player and enemies. This class stores the shared attributes that every
 * character has, such as their name, current health, maximum health, attack,
 * and defense.
 *
 * Character also provides methods for checking whether a character is alive,
 * restoring health, retrieving character statistics, and updating health
 * and defense values. Since this class is abstract, it cannot be instantiated
 * directly. Instead, other classes extend Character and inherit its fields
 * and methods.*/

public abstract class Character {

    private String name;
    private int currentHP;
    private int maxHP;
    private int attack;
    private int defense;

    /** Creates a character with the specified name, health, attack, and defense
     * values. These values initialize the character's attributes when an object
     * is created by a class that extends Character.*/
    public Character(String name, int currentHP, int maxHP, int attack, int defense) {
        this.name = name;
        this.currentHP = currentHP;
        this.maxHP = maxHP;
        this.attack = attack;
        this.defense = defense;
    }

    /** Returns the character's name. This allows other classes to retrieve
     * the character's name without directly accessing the private field.*/
    public String getName() {
        return name;
    }

    /** Checks whether the character is still alive by determining if their
     * current health is greater than zero. Returns true if the character
     * is alive and false if their health has reached zero.*/
    public boolean isAlive(){
        return currentHP > 0;
    }

    /** Restores the character's health by the specified amount. Healing cannot
     * exceed the character's maximum health, and characters cannot be healed
     * if they are already at maximum health, are dead, or the healing amount
     * is zero or negative. Returns the actual amount of health restored.*/
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

    /** Returns the character's current health points. This allows other classes
     * to check how much health the character has remaining.*/
    public int getCurrentHP() {
        return currentHP;
    }

    /** Updates the character's current health to the specified value. Health
     * cannot exceed the character's maximum health or fall below zero. Any
     * value outside these limits is automatically adjusted to the nearest
     * valid health value.*/
    public void setCurrentHP(int newHP) {
        if (newHP > maxHP) {
            currentHP = maxHP;
        } else if (newHP < 0) {
            currentHP = 0;
        } else {
            currentHP = newHP;
        }
    }

    /** Returns the character's maximum health points. This value represents
     * the highest amount of health the character can have.*/
    public int getMaxHP() {
        return maxHP;
    }

    /** Returns the character's attack stat. This value determines the character's
     * base offensive strength when dealing damage during combat.*/
    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int newDefense) {
        defense = newDefense;
    }
}