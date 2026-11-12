package Kelvin;

public class KeyItem extends Item {
    private String progressionFlag;


    public KeyItem(String name, String description, int value, String specialEffect) {
        super(name, description, value);
        this.specialEffect = specialEffect;
    }

    public String getSpecialEffect() {
        return specialEffect;
    }

    public void setSpecialEffect(String specialEffect) {
        this.specialEffect = specialEffect;
    }
}
