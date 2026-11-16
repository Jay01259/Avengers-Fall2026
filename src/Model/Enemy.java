package Model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Enemy extends Character {

    private final String enemyID;
    private final String description;
    private final boolean boss;
    private final int silverDrop;
    private final int goldDrop;
    private final Map<String, Integer> itemDrops;

    private int chargeCounter;
    private boolean stunned;
    private boolean chargingAllOutAttack;
    private boolean rewardsClaimed;
    private String statusHint = "";

    public Enemy(String enemyID, String name, String description,
                 int currentHP, int maxHP, int attack, int defense,
                 boolean boss, int silverDrop, int goldDrop,
                 Map<String, Integer> itemDrops) {

        super(name, currentHP, maxHP, attack, defense);

        if (enemyID == null || enemyID.trim().isEmpty()
                || name == null || name.trim().isEmpty()
                || maxHP <= 0 || currentHP < 0 || currentHP > maxHP
                || attack < 0 || silverDrop < 0 || goldDrop < 0) {
            throw new IllegalArgumentException(
                    "Invalid enemy identity or stats.");
        }

        this.enemyID = enemyID.trim();
        this.description = description == null ? "" : description;
        this.boss = boss;
        this.silverDrop = silverDrop;
        this.goldDrop = goldDrop;
        this.itemDrops = new LinkedHashMap<>();

        if (itemDrops != null) {
            for (Map.Entry<String, Integer> entry
                    : itemDrops.entrySet()) {

                if (entry.getKey() == null
                        || entry.getKey().trim().isEmpty()
                        || entry.getValue() == null
                        || entry.getValue() <= 0) {
                    throw new IllegalArgumentException(
                            "Invalid enemy drop.");
                }

                this.itemDrops.put(
                        entry.getKey(), entry.getValue());
            }
        }
    }

    public String getEnemyID() {
        return enemyID;
    }

    public String getDescription() {
        return description;
    }

    public boolean isBoss() {
        return boss;
    }

    public int getSilverDrop() {
        return silverDrop;
    }

    public int getGoldDrop() {
        return goldDrop;
    }

    public Map<String, Integer> getItemDrops() {
        return Collections.unmodifiableMap(itemDrops);
    }

    public int getChargeCounter() {
        return chargeCounter;
    }

    public boolean isStunned() {
        return stunned;
    }

    public boolean isChargingAllOutAttack() {
        return chargingAllOutAttack;
    }

    public boolean isDefeated() {
        return !isAlive();
    }

    public boolean areRewardsClaimed() {
        return rewardsClaimed;
    }

    public String getStatusHint() {
        if (!isAlive()) {
            return "Defeated.";
        }

        return stunned
                ? "Stunned: misses the next turn."
                : statusHint;
    }

    public void setStatusHint(String statusHint) {
        this.statusHint = statusHint == null ? "" : statusHint;
    }

    // rawDamage is the attack amount BEFORE defense.
    public int takeDamage(int rawDamage) {
        if (rawDamage < 0) {
            throw new IllegalArgumentException(
                    "Damage cannot be negative.");
        }

        if (!isAlive()) {
            return 0;
        }

        long adjustedDamage =
                Math.max(1L, (long) rawDamage - getDefense());

        int hpLost =
                (int) Math.min(getCurrentHP(), adjustedDamage);

        setCurrentHP(getCurrentHP() - hpLost);
        return hpLost;
    }

    public void reduceDefense(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Defense reduction cannot be negative.");
        }

        setDefense((int) Math.max(
                Integer.MIN_VALUE,
                (long) getDefense() - amount));
    }

    public void incrementCharge() {
        if (isAlive() && chargeCounter < Integer.MAX_VALUE) {
            chargeCounter++;
        }
    }

    public void resetCharge() {
        chargeCounter = 0;
    }

    public boolean hasEnoughCharge(int requiredCharge) {
        if (requiredCharge < 1) {
            throw new IllegalArgumentException(
                    "Required charge must be positive.");
        }

        return isAlive()
                && !stunned
                && chargeCounter >= requiredCharge;
    }

    public void setChargingAllOutAttack(boolean charging) {
        if (charging
                && !"Father of Beasts".equalsIgnoreCase(getName())) {
            throw new IllegalArgumentException(
                    "All-Out Attack belongs to Father of Beasts.");
        }

        chargingAllOutAttack = charging;
    }

    /*
     * Supply the damage amount being checked under the team's rule.
     * This method does not assume whether that amount is accumulated
     * across attacks or measured from a single attack.
     */
    public boolean checkAllOutStun(int damageDuringCharge) {
        if (damageDuringCharge < 0) {
            throw new IllegalArgumentException(
                    "Damage cannot be negative.");
        }

        if (isAlive()
                && chargingAllOutAttack
                && damageDuringCharge >= 200) {
            stun();
            return true;
        }

        return false;
    }

    public void stun() {
        if (isAlive()) {
            stunned = true;
            chargingAllOutAttack = false;
        }
    }

    public void recoverFromStun() {
        stunned = false;
    }

    public void markDefeated() {
        setCurrentHP(0);
    }

    void markRewardsClaimed() {
        rewardsClaimed = true;
    }
}
