package VCD;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * This world class uses tiles to create a 3D world full of creatures,
 * items, the character, and environment
 */
public class World {

    private Tile[][][] tiles;
    private int width;
    public int width() { return width; }

    private int height;
    public int height() {return height; }

    private int depth;
    public int depth() {return depth;}

    private List<Creature> creatures;


    public World(Tile[][][] tiles) {
        this.tiles = tiles;
        this.width = tiles.length;
        this.height = tiles[0].length;
        this.depth = tiles[0][0].length;
        this.creatures = new ArrayList<Creature>(); // ArrayList of creatures currently in world
    }


    // The building blocks of the world
    public Tile tile(int x, int y, int z) {

        //
        if (x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth)
            return Tile.BOUNDS;
        else
            return tiles[x][y][z];
    }


    // Get creature from coordinates
    public Creature creature(int x, int y, int z){
        for (Creature c : creatures){
            if (c.x == x && c.y == y && c.z == z)
                return c;
        }
        return null;
    }

    // Get glyph from coordinates
    public char glyph(int x, int y, int z) {
        return tile(x, y, z).glyph();
    }

    // Get color from coordinates
    public Color color(int x, int y, int z) {
        return tile(x, y, z).color();
    }

    // Removes wall, replaces it will floor
    public void dig(int x, int y, int z) {
        if (tile(x,y,z).isDiggable())
            tiles[x][y][z] = Tile.FLOOR;
    }

    // Give a creature random coordinates
    public void addAtEmptyLocation(Creature creature, int z){
        int x, y;

        do {    //Check if a random coordinate is a legal spot and assign
            x = (int)(Math.random() * width);
            y = (int)(Math.random() * height);
        } while (!tile(x, y, z).isGround() || creature(x, y, z) != null);
        creature.x = x;
        creature.y = y;
        creature.z = z;

        creatures.add(creature);    // Add creature to creature list

    }

    // Remove creature from game
    public void remove(Creature other) {
        creatures.remove(other);
    }

    // Update each creature in the list
    public void update(){
        List<Creature> toUpdate = new ArrayList<Creature>(creatures);
        for (Creature creature : toUpdate) {
            creature.update();
        }
    }
}


