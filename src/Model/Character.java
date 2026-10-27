package Model;

import java.util.ArrayList;

public abstract class Character {

    String ID;
    String name;
    int HP;
    int MaxHP;
    int attack;
    int defense;

    public Character(String name, int hp, int maxHP, int attack, int defense) {
        this.name = name;
        this.HP = hp;
        this.MaxHP = maxHP;
        this.attack = attack;
        this.defense = defense;
    }

    public String getName() {
        return name;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }
}