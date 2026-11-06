package Model;

public class GameMemory {
    String saveName;
    String playerName;
    String currentRoomID;
    List<String> visitedRoomID;
    int currentHP;
    List<Item> inventoryData;
    String equippedWeaponID;
    String equippedArmorID;
    int silverCoins;
    int goldCoins;
    List<String> completedPuzzlesID;
    List<String> defeatedEnemiesID;
    List<Item> chestContents;
    boolean gameCompleted;
    Map<String, Integer> equipmentDurability;

    public GameMemory(String saveName, String playerName, String currentRoomID, List<String> visitedRoomID,
                      int currentHP, List<Item> inventoryData, String equippedWeaponID, String equippedArmorID,
                      int silverCoins, int goldCoins, List<String> completedPuzzlesID, List<String> defeatedEnemiesID,
                      List<Item> chestContents, boolean gameCompleted, Map<String, Integer> equipmentDurability) {
        this.saveName = saveName;
        this.playerName = playerName;
        this.currentRoomID = currentRoomID;
        this.visitedRoomID = visitedRoomID;
        this.currentHP = currentHP;
        this.inventoryData = inventoryData;
        this.equippedWeaponID = equippedWeaponID;
        this.equippedArmorID = equippedArmorID;
        this.silverCoins = silverCoins;
        this.goldCoins = goldCoins;
        this.completedPuzzlesID = completedPuzzlesID;
        this.defeatedEnemiesID = defeatedEnemiesID;
        this.chestContents = chestContents;
        this.gameCompleted = gameCompleted;
        this.equipmentDurability = equipmentDurability;
    }


    // Might be able to remove !equippedWeaponID.isEmpty()
// Might be able to remove !equippedArmorID.isEmpty()
    public boolean isValid() {
        return saveName != null && !saveName.isEmpty() &&
                playerName != null && !playerName.isEmpty() &&
                currentRoomID != null && !currentRoomID.isEmpty() &&
                visitedRoomID != null && !visitedRoomID.isEmpty() &&
                currentHP >= 0 &&
                inventoryData != null &&
                equippedWeaponID != null && !equippedWeaponID.isEmpty() &&
                equippedArmorID != null && !equippedArmorID.isEmpty() &&
                silverCoins >= 0 &&
                goldCoins >= 0 &&
                completedPuzzlesID != null &&
                defeatedEnemiesID != null &&
                chestContents != null &&
                equipmentDurability != null;
    }

    // Fill in
    public void restore() {

    }
}