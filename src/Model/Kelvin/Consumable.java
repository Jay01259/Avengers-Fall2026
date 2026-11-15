package Kelvin;

public class Consumable extends Item {
    private int healAmount;

    public Consumable(String id, String name, String description, int valueSilver,
                      int healAmount, int quantity) {
        super(id, name, description, valueSilver);
        this.healAmount = healAmount;
        setQuantity(quantity);
        setStackable(true);
    }

    public int getHealAmount() {
        return healAmount;
    }

    public void setHealAmount(int healAmount) {
        this.healAmount = healAmount;
    }

    public void useItem() {
        if (getQuantity() > 0) {
            setQuantity(getQuantity() - 1);
        }
    }
}