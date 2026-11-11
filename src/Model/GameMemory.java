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
        return saveName != null && !saveName.isEmpty() && player != null && gameWorld != null;
    }
}