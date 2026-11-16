package Model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Properties;
import java.util.Random;
import java.util.function.Consumer;

public class Battle {

    public enum Outcome {
        IN_PROGRESS, VICTORY, DEFEAT, FLED
    }

    public enum Menu {
        DEFAULT, ALT_SKILLS
    }

    private final Character player;
    private final Enemy enemy;
    private final Random random;

    private final int movesPerTurn;
    private final double baseFleeChance;
    private final double fleeIncrease;
    private final int baseDefense;

    private final String[] skillNames = new String[2];
    private final int[] cooldowns = new int[2];

    private int playerDefense;
    private int movesRemaining;
    private int failedFleeAttempts;
    private boolean playerTurn;

    private Menu menu = Menu.DEFAULT;
    private Outcome outcome = Outcome.IN_PROGRESS;

    public Battle(Character player, Enemy enemy) {
        this(player, enemy, defaultRules(), new Random());
    }

    public Battle(Character player, Enemy enemy,
                  Properties rules, Random random) {

        this(player, enemy,
                player == null ? 0 : player.getDefense(),
                rules, random);
    }

    // startingDefense includes equipped armor when supplied.
    public Battle(Character player, Enemy enemy,
                  int startingDefense,
                  Properties rules, Random random) {

        if (player == null || enemy == null
                || player == enemy
                || !player.isAlive() || !enemy.isAlive()
                || random == null || rules == null) {
            throw new IllegalArgumentException(
                    "Battle needs two living opponents and rules.");
        }

        this.player = player;
        this.enemy = enemy;
        this.random = random;

        for (String key : rules.stringPropertyNames()) {
            if (!key.equals("movesPerTurn")
                    && !key.equals("baseFleeChance")
                    && !key.equals("fleeIncrease")) {
                throw new IllegalArgumentException(
                        "Unknown battle rule: " + key);
            }
        }

        movesPerTurn = Integer.parseInt(
                required(rules, "movesPerTurn"));

        baseFleeChance = Double.parseDouble(
                required(rules, "baseFleeChance"));

        fleeIncrease = Double.parseDouble(
                required(rules, "fleeIncrease"));

        if (movesPerTurn < 1
                || !probability(baseFleeChance)
                || !probability(fleeIncrease)) {
            throw new IllegalArgumentException(
                    "Invalid battle rules.");
        }

        baseDefense = startingDefense;
        playerDefense = startingDefense;

        // Opening coin flip.
        playerTurn = random.nextBoolean();
        movesRemaining = playerTurn ? movesPerTurn : 0;
    }

    public Character getPlayer() {
        return player;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public Menu getMenu() {
        return menu;
    }

    public int getMovesRemaining() {
        return movesRemaining;
    }

    public int getBaseDefense() {
        return baseDefense;
    }

    public int getPlayerDefense() {
        return playerDefense;
    }

    public boolean isOver() {
        return outcome != Outcome.IN_PROGRESS;
    }

    public boolean isPlayerTurn() {
        return !isOver() && playerTurn;
    }

    // Changing menus does not consume a move.
    public void enterAltSkillsMenu() {
        if (isPlayerTurn()) {
            menu = Menu.ALT_SKILLS;
        }
    }

    public void leaveAltSkillsMenu() {
        menu = Menu.DEFAULT;
    }

    /*
     * Names come from the equipped weapon.
     * Pass null, null when no weapon skills are available.
     */
    public void setPlayerSkills(String first, String second) {
        if ((first == null) != (second == null)
                || (first != null
                && (first.trim().isEmpty()
                || second.trim().isEmpty()))) {
            throw new IllegalArgumentException(
                    "Supply two weapon skill names or neither.");
        }

        skillNames[0] = first;
        skillNames[1] = second;

        // Equipment code supplies cooldowns when weapons change.
    }

    public String getPlayerSkillName(int slot) {
        return validSlot(slot) ? skillNames[slot - 1] : null;
    }

    public int getSkillCooldown(int slot) {
        return validSlot(slot) ? cooldowns[slot - 1] : 0;
    }

    public void setSkillCooldown(int slot, int turns) {
        if (!validSlot(slot) || turns < 0) {
            throw new IllegalArgumentException(
                    "Invalid skill slot or cooldown.");
        }

        cooldowns[slot - 1] = turns;
    }

    // Supply attack damage BEFORE defense.
    public int regularAttack(int rawDamage) {
        if (!canAct()) {
            return 0;
        }

        if (rawDamage < 0) {
            throw new IllegalArgumentException(
                    "Damage cannot be negative.");
        }

        int hpLost = damageEnemy(rawDamage);
        spendMove();
        return hpLost;
    }

    /*
     * exactEffects contains the actual equipped weapon's skill.
     * Inside it, use damageEnemy(), damagePlayer(), Character.heal(),
     * or defense methods. The skill already spends one move.
     */
    public boolean useAlternateSkill(
            int slot,
            int cooldownTurns,
            Consumer<Battle> exactEffects) {

        if (!canAct()
                || !validSlot(slot)
                || skillNames[slot - 1] == null
                || cooldowns[slot - 1] > 0) {
            return false;
        }

        if (cooldownTurns < 0 || exactEffects == null) {
            throw new IllegalArgumentException(
                    "Supply the skill's effect and cooldown.");
        }

        exactEffects.accept(this);
        updateOutcome();

        cooldowns[slot - 1] = cooldownTurns;
        spendMove();
        return true;
    }

    // Executes the enemy's configured attack or skill.
    public boolean enemyTurn(Consumer<Battle> exactAction) {
        if (isOver() || playerTurn) {
            return false;
        }

        if (exactAction == null) {
            throw new IllegalArgumentException(
                    "Supply the enemy's actual action.");
        }

        enemy.incrementCharge();

        if (enemy.isStunned()) {
            enemy.recoverFromStun();
        } else {
            exactAction.accept(this);
        }

        updateOutcome();

        if (!isOver()) {
            playerTurn = true;
            movesRemaining = movesPerTurn;
            menu = Menu.DEFAULT;
        }

        return true;
    }

    public int damageEnemy(int rawDamage) {
        if (!canAct()) {
            return 0;
        }

        int hpLost = enemy.takeDamage(rawDamage);
        updateOutcome();
        return hpLost;
    }

    /*
     * Applies an enemy attack.
     * Also usable when the actual CDU Guard counterattack rule triggers.
     */
    public int damagePlayer(int rawDamage) {
        if (rawDamage < 0) {
            throw new IllegalArgumentException(
                    "Damage cannot be negative.");
        }

        if (isOver()) {
            return 0;
        }

        long adjustedDamage =
                Math.max(1L, (long) rawDamage - playerDefense);

        int hpLost =
                (int) Math.min(player.getCurrentHP(), adjustedDamage);

        player.setCurrentHP(player.getCurrentHP() - hpLost);
        updateOutcome();

        return hpLost;
    }

    public void reducePlayerDefense(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Reduction cannot be negative.");
        }

        if (!isOver()) {
            playerDefense = (int) Math.max(
                    Integer.MIN_VALUE,
                    (long) playerDefense - amount);
        }
    }

    public int restorePlayerDefense(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Restoration cannot be negative.");
        }

        if (isOver()) {
            return 0;
        }

        int before = playerDefense;

        playerDefense = (int) Math.min(
                baseDefense,
                (long) playerDefense + amount);

        return (int) Math.min(
                Integer.MAX_VALUE,
                (long) playerDefense - before);
    }

    public double getFleeChance() {
        if (enemy.isBoss()) {
            return 0.0;
        }

        return Math.min(
                1.0,
                baseFleeChance
                        + failedFleeAttempts * fleeIncrease);
    }

    /*
     * Implements escape probability.
     * The documents do not specify a flee move cost.
     */
    public boolean attemptFlee() {
        if (!canAct() || enemy.isBoss()) {
            return false;
        }

        if (random.nextDouble() < getFleeChance()) {
            outcome = Outcome.FLED;
            movesRemaining = 0;
            return true;
        }

        if (failedFleeAttempts < Integer.MAX_VALUE) {
            failedFleeAttempts++;
        }

        return false;
    }

    /*
     * Call after another successful action that consumes a move,
     * such as an item action under the team's action rules.
     */
    public boolean recordPlayerMove() {
        if (!canAct()) {
            return false;
        }

        spendMove();
        return true;
    }

    /*
     * On true, apply this enemy's coins and item drops
     * through the team's Player/inventory code exactly once.
     */
    public boolean claimRewards() {
        if (outcome != Outcome.VICTORY
                || enemy.areRewardsClaimed()) {
            return false;
        }

        enemy.markRewardsClaimed();
        return true;
    }

    /*
     * Calculates a percentage of Attack.
     * Percentage and rounding are supplied explicitly.
     */
    public static int rawDamageFromAttack(
            int attack,
            double percentage,
            RoundingMode rounding) {

        if (attack < 0
                || !Double.isFinite(percentage)
                || percentage < 0
                || rounding == null) {
            throw new IllegalArgumentException(
                    "Supply valid attack, percentage, and rounding.");
        }

        return BigDecimal.valueOf(attack)
                .multiply(BigDecimal.valueOf(percentage))
                .divide(BigDecimal.valueOf(100), 0, rounding)
                .intValueExact();
    }

    private boolean canAct() {
        return isPlayerTurn() && movesRemaining > 0;
    }

    private static boolean validSlot(int slot) {
        return slot == 1 || slot == 2;
    }

    private void spendMove() {
        if (isOver()) {
            movesRemaining = 0;
            return;
        }

        movesRemaining--;

        if (movesRemaining == 0) {
            for (int i = 0; i < cooldowns.length; i++) {
                cooldowns[i] =
                        Math.max(0, cooldowns[i] - 1);
            }

            playerTurn = false;
        }
    }

    private void updateOutcome() {
        if (!player.isAlive()) {
            outcome = Outcome.DEFEAT;
        } else if (!enemy.isAlive()) {
            outcome = Outcome.VICTORY;
        }

        if (isOver()) {
            movesRemaining = 0;
        }
    }

    private static boolean probability(double value) {
        return Double.isFinite(value)
                && value >= 0
                && value <= 1;
    }

    private static String required(
            Properties rules, String key) {

        String value = rules.getProperty(key);

        if (value == null) {
            throw new IllegalArgumentException(
                    "Missing battle rule: " + key);
        }

        return value.trim();
    }

    private static Properties defaultRules() {
        Properties rules = new Properties();

        rules.setProperty("movesPerTurn", "3");
        rules.setProperty("baseFleeChance", "0.50");
        rules.setProperty("fleeIncrease", "0.10");

        return rules;
    }
}
