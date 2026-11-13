package Kelvin;

public class Weapon extends Item{
    private int damage;
    private int durability;
    int CurrentUses;


    public Weapon(String name, String description, int value, int damage) {
        super(name, description, value);
        this.damage = damage;
        this.durability = 100; // Example initial durability
        this.CurrentUses = 0;
        // this.altskill = "None"; // Example initial alt skill
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }
    public void IncrementUse() {
        this.CurrentUses++;
    }
    public boolean isBroken() {
        return this.durability<=this.CurrentUses;
    }

}