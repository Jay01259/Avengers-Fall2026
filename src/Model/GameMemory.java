
package Model;

public class GameMemory {

    private String saveName;
    private Player player;
    private GameWorld gameWorld;

    public GameMemory(String saveName, Player player, GameWorld gameWorld) {
        this.saveName = saveName;
        this.player = player;
        this.gameWorld = gameWorld;
    }

    public String getSaveName() {
        return saveName;
    }

    public Player getPlayer() {
        return player;
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public boolean isValid() {
        return saveName != null && !saveName.isEmpty()
                && player != null && gameWorld != null;
    }

    public String toText() {
        if (!isValid() || player.getInventory() == null
                || player.getCurrentRoom() == null) {
            return "";
        }

        StringBuilder text = new StringBuilder();

        text.append("SAVE_NAME|")
                .append(saveName).append('\n');

        text.append("PLAYER_NAME|")
                .append(player.getName()).append('\n');

        text.append("CURRENT_HP|")
                .append(player.getCurrentHP()).append('\n');

        text.append("MAX_HP|")
                .append(player.getMaxHP()).append('\n');

        text.append("ATTACK|")
                .append(player.getAttack()).append('\n');

        text.append("DEFENSE|")
                .append(player.getDefense()).append('\n');

        text.append("CURRENT_ROOM|")
                .append(player.getCurrentRoom().getRoomID()).append('\n');

        text.append("SILVER_COINS|")
                .append(player.getSilverCoins()).append('\n');

        text.append("GOLD_COINS|")
                .append(player.getGoldCoins()).append('\n');

        text.append("GAME_COMPLETED|")
                .append(player.isGameCompleted()).append('\n');

        String weaponID = player.getEquippedWeapon() == null
                ? "" : player.getEquippedWeapon().getId();

        String armorID = player.getEquippedArmor() == null
                ? "" : player.getEquippedArmor().getId();

        text.append("EQUIPPED_WEAPON|")
                .append(weaponID).append('\n');

        text.append("EQUIPPED_ARMOR|")
                .append(armorID).append('\n');

        for (Item item : player.getInventory()) {
            if (item == null || item.getId() == null) {
                continue;
            }

            String itemType = "ITEM";
            int itemState = 0;

            if (item instanceof Weapon) {
                itemType = "WEAPON";
                itemState = ((Weapon) item).getAttackCounter();

            } else if (item instanceof Armor) {
                itemType = "ARMOR";
                itemState = ((Armor) item).getHitsTakenCounter();

            } else if (item instanceof Consumable) {
                itemType = "CONSUMABLE";

            } else if (item instanceof KeyItem) {
                itemType = "KEY_ITEM";
            }

            text.append("INVENTORY_ITEM|")
                    .append(item.getId()).append('|')
                    .append(itemType).append('|')
                    .append(item.getQuantity()).append('|')
                    .append(itemState).append('\n');
        }

        for (String roomID : player.getVisitedRoomsIDs()) {
            text.append("VISITED_ROOM|")
                    .append(roomID).append('\n');
        }

        for (Puzzle puzzle : gameWorld.getPuzzles()) {
            if (puzzle != null && puzzle.isCompleted()) {
                text.append("COMPLETED_PUZZLE|")
                        .append(puzzle.getPuzzleID()).append('\n');
            }
        }

        for (Enemy enemy : gameWorld.getEnemies()) {
            if (enemy != null && enemy.isDefeated()) {
                text.append("DEFEATED_ENEMY|")
                        .append(enemy.getEnemyID()).append('\n');
            }
        }

        return text.toString();
    }
}
