package VCD;

import java.awt.Color;
import asciiPanel.AsciiPanel;

public enum Tile {

    // World tiles
    UNKNOWN(' ', AsciiPanel.white, "(unknown)"),
    FLOOR((char)250, AsciiPanel.yellow, "A dirt and rock cave floor."),    // Centered dot
    WALL((char)177, AsciiPanel.yellow, "A dirt and rock cave wall."),     // Vertical bar
    BOUNDS('x', AsciiPanel.brightBlack, "Beyond the edge of this world."),
    STAIRS_DOWN('<', AsciiPanel.white, "A stone staircase that goes down."),
    STAIRS_UP('>', AsciiPanel.white, "A stone staircase that goes up.");


    // Tile Variables
    private char glyph;
    public char glyph() { return glyph; }
    private Color color;
    public Color color() { return color; }
    private String details;
    public String details() { return details; }


    Tile(char glyph, Color color, String details) {
        this.glyph = glyph;
        this.color = color;
        this.details = details;
    }

    // If the tile is a wall the terrain is diggable
    public boolean isDiggable() {
        return this == Tile.WALL;
    }

    // If the tile is not a wall or boundary it is ground
    public boolean isGround() {
        return this != WALL && this != BOUNDS;
    }
}
