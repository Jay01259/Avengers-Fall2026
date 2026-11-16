import Model.DataLoader;
import Kelvin.Item;

public class TestItemLoader {
    public static void main(String[] args) {
        DataLoader loader = new DataLoader("data", "saves");

        for (Item item : loader.loadItems()) {
            System.out.println(
                    item.getId()
                            + " | "
                            + item.getName()
                            + " | "
                            + item.getClass().getSimpleName()
            );
        }
    }
}