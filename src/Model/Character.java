package Model;

public abstract class Character {

    private String name;
    private int currentHP;
    private int maxHP;
    private int attack;
    private int defense;

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
        return currentHP > 0;
    }

    public int heal(int amount){
        // Returns the amount of HP restored, or 0 if no HP was restored
        // In main game loop, use the return value to tell user how much HP was restored
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

    public int getCurrentHP() {
        return currentHP;
    }

    public void setCurrentHP(int newHP) {
        if (newHP > maxHP) {
            currentHP = maxHP;
        } else if (newHP < 0) {
            currentHP = 0;
        } else {
            currentHP = newHP;
        }
    }

    public int getMaxHP() {
        return maxHP;
    }

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