package Model;

public class Room {

    int north;
    int east;
    int south;
    int west;

    public Room(int north, int east, int south, int west) {
        this.north = north;
        this.east = east;
        this.south = south;
        this.west = west;
    }

    //Direction getters
    public int getNorth() {
        return north;
    }
    public int getEast() {
        return east;
    }
    public int getSouth() {
        return south;
    }
    public int getWest() {
        return west;
    }
}