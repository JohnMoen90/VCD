package VCD;

import java.awt.Color;
import asciiPanel.AsciiPanel;

public enum Tile {

    // World tiles
    FLOOR((char)250, AsciiPanel.yellow),    // Centered dot
    WALL((char)177, AsciiPanel.yellow),     // Vertical bar
    BOUNDS('x', AsciiPanel.brightBlack),
    STAIRS_DOWN('<', AsciiPanel.white),
    STAIRS_UP('>', AsciiPanel.white);


    // Tile Variables
    private char glyph;
    public char glyph() { return glyph; }
    private Color color;
    public Color color() { return color; }


    Tile(char glyph, Color color) {
        this.glyph = glyph;
        this.color = color;
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
