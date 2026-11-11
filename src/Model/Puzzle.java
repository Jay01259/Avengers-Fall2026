package Model;

import java.util.*;

public class Puzzle {
    private String puzzleID;
    private String name;
    private String roomID;
    private String description;        // shown by DISPLAY
    private String lockedDescription;  // shown when the required item is missing (P8); may be null
    private String solveText;          // shown when the puzzle is solved
    private List<Lever> levers;
    private Set<String> solution;      // IDs of levers that must be On; all others must be Off
    private String requiredItemID;     // e.g. "W16" (Beast Medallion) for P8; null if none
    private Map<String, Integer> rewardItems; // itemID -> quantity, placed in the room when solved
    private boolean isCompleted;

    public Puzzle(String puzzleID, String name, String roomID, String description, String lockedDescription,
                  String solveText, List<String> leverIDs, Set<String> solution, String requiredItemID,
                  Map<String, Integer> rewardItems) {
        this.puzzleID = puzzleID;
        this.name = name;
        this.roomID = roomID;
        this.description = description;
        this.lockedDescription = lockedDescription;
        this.solveText = solveText;
        this.levers = new ArrayList<>();
        for (String id : leverIDs) {
            levers.add(new Lever(id));
        }
        this.solution = new LinkedHashSet<>(solution);
        this.requiredItemID = requiredItemID;
        this.rewardItems = new LinkedHashMap<>(rewardItems);
        this.isCompleted = false;
    }

    public String getPuzzleID() {
        return puzzleID;
    }

    public String getName() {
        return name;
    }

    public String getRoomID() {
        return roomID;
    }

    public String getSolveText() {
        return solveText;
    }

    public String getRequiredItemID() {
        return requiredItemID;
    }

    public Map<String, Integer> getRewardItems() {
        return rewardItems;
    }

    public List<Lever> getLevers() {
        return levers;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }
    public String getDescription() {
        return description;
    }

    public String getLockedDescription() {
        return lockedDescription;
    }

    public String display(boolean hasRequiredItem) {
        if (!hasRequiredItem && lockedDescription != null) {
            return lockedDescription;
        }
        StringBuilder sb = new StringBuilder(description);
        for (Lever lever : levers) {
            sb.append("\n").append(lever.getDisplayName()).append(" is in the ")
                    .append(lever.getStateName()).append(" state.");
        }
        return sb.toString();
    }


    public String pullLever(String typedName, boolean hasRequiredItem) {
        if (!hasRequiredItem) {
            return "These levers do not look like they can be pulled.";
        }
        Lever lever = findLever(typedName);
        if (lever == null) {
            return "There isn't a Lever with that name. Try again!";
        }
        lever.toggle();
        return "You pull the " + lever.getDisplayName() + ". It is now in the " + lever.getStateName() + " state.";
    }

    public String reset(String roomName) {
        if (isCompleted) {
            return "There is no reason to reset this puzzle!";
        }
        for (Lever lever : levers) {
            lever.reset();
        }
        return "The Puzzle in " + roomName + " has been reset.";
    }


    public boolean checkSolution(boolean hasRequiredItem) {
        if (isCompleted || !hasRequiredItem) {
            return false;
        }
        for (Lever lever : levers) {
            boolean shouldBeOn = solution.contains(lever.getLeverID());
            if (lever.isOn() != shouldBeOn) {
                return false;
            }
        }
        isCompleted = true;
        return true;
    }

    private Lever findLever(String typedName) {
        for (Lever lever : levers) {
            if (lever.matches(typedName)) {
                return lever;
            }
        }
        return null;
    }
}
