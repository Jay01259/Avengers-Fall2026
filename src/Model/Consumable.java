package Model;

public class Consumable extends Model.Item {
    private int healAmount;

    // Purpose: Creates a reusable or stackable item that restores health when used.
    public Consumable(String id, String name, String description, int valueSilver,
                      int healAmount, int quantity) {
        super(id, name, description, valueSilver);
        this.healAmount = healAmount;
        setQuantity(quantity);
        setStackable(true);
    }

    // Purpose: Returns the heal value restored by the consumable.
    public int getHealAmount() {
        return healAmount;
    }

    // Purpose: Sets the healing power of the potion or food item.
    public void setHealAmount(int healAmount) {
        this.healAmount = healAmount;
    }

    // Purpose: Decreases the item's quantity by one after a successful use.
    public void useItem() {
        if (getQuantity() > 0) {
            setQuantity(getQuantity() - 1);
        }
    }
}

