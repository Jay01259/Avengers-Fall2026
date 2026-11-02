package View;

public class GameView {

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayRoom(Room room){
        System.out.println("Current room: " + room.getName());
        System.out.println("Room description: " + room.getDescription());
        System.out.println("Available exits: " + room.getExits());
    }
    public void exploreRoom(Room room){
        System.out.println("Exploring room: " + room.getName());
        System.out.println("Available exits: " + room.getExits());
        System.out.println("Items in the room: " + room.getItems());
    }

    public void displayInventory(Inventory inventory) {
        System.out.println("Inventory:");
        for (Item item : inventory) {
            System.out.println("- " + item.getName());
        }
    }

    public void displayBattleMenu(){
        System.out.println("Battle Menu:");
        // Display battle options here
    }

    public void displayPuzzleMenu(){
        System.out.println("Puzzle Menu:");
        // Display puzzle options here
    }
}