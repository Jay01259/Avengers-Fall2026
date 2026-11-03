public class Lever {
    private String leverID;
    private String displayName;
    private boolean isOn;

    public Lever(String leverID) {
        this.leverID = leverID;
        this.displayName = toDisplayName(leverID);
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
        return isOn ? "ON" : "OFF";
    }

    public boolean matches(String typedName) {
        return displayName.equals(typedName);
    }
    private static String toDisplayName(String id) {
        return id.replaceAll("([a-z])([A-Z])", "$1 $2");
    }
}
