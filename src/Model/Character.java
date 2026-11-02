package Model;

import java.util.ArrayList;

public abstract class Character {

    String name;
    int currentHP;
    int maxHP;
    int attack;
    int defense;

    public Character(String name, int currentHP, int maxHP, int attack, int defense) {
        this.name = name;
        this.currentHP = currentHP;
        this.maxHP = maxHP;
        this.attack = attack;
        this.defense = defense;
    }

    public String getName() {
        return name;
    }

    public boolean isAlive(){
        //Implement
    }
    public int heal(int amount){;
        // Implement
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public void setCurrentHP(int HP) {
        this.currentHP = HP;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getAttack() {
        // Use if statements to check whether the character has a weapon equipped and return the appropriate attack value

    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }
}