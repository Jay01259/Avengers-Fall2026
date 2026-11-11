package Model;

public class Lever {
    private String leverID;
    private String displayName;
    private boolean isOn;

    public Lever(String leverID, String displayName) {
        this.leverID = leverID;
        this.displayName = displayName;
        this.isOn = false;
    }

    public String getLeverID() {
        return leverID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isOn() {
        return isOn;
    }

    public void toggle() {
        isOn = !isOn;
    }

    public void reset() {
        isOn = false;
    }

    public String getStateName() {
        return isOn ? "on" : "off";
    }

    public boolean matches(String typedName) {
        String typed = typedName.toLowerCase().replace(" ", "");
        String id = leverID.toLowerCase();
        if (typed.isEmpty()) {
            return false;
        }
        return id.startsWith(typed);
    }
    private static String toDisplayName(String id) {
        return id.replaceAll("([a-z])([A-Z])", "$1 $2");
    }

    public void setOn(boolean isOn) {
        this.isOn = isOn;
    }
}
