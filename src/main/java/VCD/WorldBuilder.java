package VCD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldBuilder {
    private int width;
    private int height;
    private int depth;
    private Tile[][][] tiles;
    private int[][][] regions;
    private int nextRegion;

    public WorldBuilder(int width, int height, int depth){
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.tiles = new Tile[width][height][depth];
        this.regions = new int[width][height][depth];
        this.nextRegion = 1;
    }

    public World build() {
        return new World(tiles);
    }

    // Randomize tiles to seed world generation
    public WorldBuilder randomizeTiles() {

        // Make every tile a floor or wall randomly
        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    tiles[x][y][z] = Math.random() < 0.5 ? Tile.FLOOR : Tile.WALL;  // <-- 50/50 chance floor/wall
                }
            }
        }
        return this;
    }

    /**
     * Smooth out the randomly generated cave
     * @param times number of times you want the map smoothed
     */
    private WorldBuilder smooth(int times) {

        // Create new map to overwrite old map
        Tile[][][] tiles2 =new Tile[width][height][depth];

        // For as many times as specified
        for (int time = 0; time < times; time++) {

            // For every tile in world
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < depth; z++){

                        // Start counting number of floors and 'rocks'(floor bits) surrounding each tile
                        int floors = 0;
                        int rocks =0;

                        // For each tile around x,y,z tile
                        for (int ox = -1; ox < 2; ox++) {
                            for (int oy = -1; oy < 2; oy++) {

                                // If the tile is out of bounds, continue
                                if (x + ox < 0 || x + ox >= width || y + oy < 0 || y + oy >= height)
                                    continue;

                                // If the nearby tile is a floor
                                if (tiles[x + ox][y + oy][z] == Tile.FLOOR)
                                    floors++;   // Add to floors
                                else
                                    rocks++;    // Otherwise add to rocks

                            }
                        }

                        // Add tile to new map a floor if there are more floors around it, if not, add a wall
                        tiles2[x][y][z] = floors >= rocks ? Tile.FLOOR : Tile.WALL;
                    }
                }
            }

            // Finally, replace all tiles with the new tiles
            tiles = tiles2;
        }
        return this;
    }


    // Regions are used to connect and distinguish areas of the map after smoothing
    private WorldBuilder createRegions(){
        regions = new int[width][height][depth];

        // For every tile
        for (int z = 0; z < depth; z++){
            for (int x = 0; x < width; x++){
                for (int y = 0; y < height; y++){

                    // If the tile isn't a wall and there is no region assigned
                    if (tiles[x][y][z] != Tile.WALL && regions[x][y][z] == 0){

                        // Fill region and check size, if too small turn to it into wall
                        int size = fillRegion(nextRegion++, x, y, z);
                        if (size < 25)
                            removeRegion(nextRegion - 1, z);
                    }
                }
            }
        }
        return this;

    }

    private void removeRegion(int region, int z){

        // For every tile on one floor
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++) {

                // If the region has the region number, return it to zero
                if (regions[x][y][z] == region){
                    regions[x][y][z] = 0;
                    tiles[x][y][z] = Tile.WALL; // Change floors in region to walls
                }
            }
        }
    }

    private int fillRegion(int region, int x, int y, int z) {

        // Initialize variables
        int size = 1;
        ArrayList<Point> open = new ArrayList<Point>(); // Make ArrayList of points
        open.add(new Point(x,y,z));
        regions[x][y][z] = region;  // Add region number to starter location

        // While open is not empty
        while (!open.isEmpty()){

            // Remove the first point of the list, save to p
            Point p = open.remove(0);

            // For each of the point's 8 neighbors
            for (Point neighbor : p.neightbors8()){

                // If neighbor is out of bounds, continue
                if (neighbor.x < 0 || neighbor.y < 0 || neighbor.x >= width || neighbor.y >= height)
                    continue;

                // If neighbor has region number or is a wall, continue
                if (regions[neighbor.x][neighbor.y][neighbor.z] > 0
                        || tiles[neighbor.x][neighbor.y][neighbor.z] == Tile.WALL)
                    continue;

                // Add to region size, add region number to coordinates in regions
                size++;
                regions[neighbor.x][neighbor.y][neighbor.z] = region;
                open.add(neighbor); // Add neighbor to open list so it can be evaluated as well
            }
        }
        // Return the final size of the region
        return size;
    }


    public WorldBuilder connectRegions(){

        // Call connect Regions down for every floor
        for (int z = 0; z < depth - 1; z++) {
            connectRegionsDown(z);
        }
        return this;
    }

    public void connectRegionsDown(int z) { // We must pass in the floor to reference the exact tile & neighboring floor

        // Initialize list of connected regions
        List<String> connected = new ArrayList<String>();

        // For every tile on one floor
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){

                // Create a fake tuple of the regions of the point and tile exactly a floor below to make evaluations with
                // TODO: Make this more efficient, abstract data type?
                String region = regions[x][y][z] + "," + regions[x][y][z+1];

                // If both tiles are floor
                if (tiles[x][y][z] == Tile.FLOOR
                        && tiles[x][y][z+1] == Tile.FLOOR
                        && !connected.contains(region)){    // And connected doesn't contain the region combination
                    connected.add(region);  // Add the fake tuple to the list

                    // Note that this is overloaded, this call to connectRegionsDown is below
                    connectRegionsDown(z, regions[x][y][z], regions[x][y][z+1]);
                }
            }
        }
    }

    /**
     * Connects regions vertically with stairs
     * @param z Starter floor level
     * @param r1 region number of upper level
     * @param r2 region number of lower level
     */
    private void connectRegionsDown(int z, int r1, int r2){
        List<Point> candidates = findRegionOverlaps(z, r1, r2);

        // Count number of stairs So we don't fill a map with them
        int stairs = 0;
        do{     // remove a candidate and place stairs at and below the location
            Point p = candidates.remove(0);
            tiles[p.x][p.y][z] = Tile.STAIRS_DOWN;
            tiles[p.x][p.y][z+1] = Tile.STAIRS_UP;
            stairs++;   // Add to stairs list
        }
        while (candidates.size() / stairs > 300); // Higher the number the less connecting stairs
    }

    /**
     *
     * @param z Starter floor level
     * @param r1 Coordinate on upper level
     * @param r2 Coordinate on lower level
     * @return List of
     */
    public List<Point> findRegionOverlaps(int z, int r1, int r2) {
        ArrayList<Point> candidates = new ArrayList<Point>();

        // For every tile on floor
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){

                // If the tile is a floor
                if (tiles[x][y][z] == Tile.FLOOR
                        && tiles[x][y][z+1] == Tile.FLOOR  // And below is a floor
                        && regions[x][y][z] == r1          // and the region above matches
                        && regions[x][y][z+1] == r2){      // and the region below matches
                    candidates.add(new Point(x,y,z));
                }
            }
        }

        // Shuffle the candidates so they are not chosen systematically
        Collections.shuffle(candidates);
        return candidates;
    }


    //Where the magic happens! randomize, smooth, and create and connect regions
    public WorldBuilder makeCaves() {
        return randomizeTiles().
                smooth(8)
                .createRegions()
                .connectRegions();
    }

}
