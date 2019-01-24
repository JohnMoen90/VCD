package VCD;

public class FieldOfView {
    private World world;
    private int depth;

    private boolean[][] visible;
    public boolean isVisible(int x, int y, int z) {     // Checks if a tile is visible
        return z == depth   // Check if z level is same
                && x >= 0 && y >= 0 // Check if x and y are out of bounds
                && x < visible.length && y < visible[0].length // Check if x,y are within length of vision range
                && visible[x][y]; // Check if tile is visible (nothing in the way)
    }

    private Tile[][][] tiles;
    public Tile tile(int x, int y, int z){
        return tiles[x][y][z];
    }

    // The constructor initializes every tile as unknown
    public FieldOfView(World world) {
        this.world = world;
        this.visible = new boolean[world.width()][world.height()];
        this.tiles = new Tile[world.width()][world.height()][world.depth()];

        // For every tile
        for (int x = 0; x < world.width(); x++) {
            for (int y = 0; y < world.height(); y++) {
                for (int z = 0; z < world.depth(); z++) {

                    // Make unknown
                    tiles[x][y][z] = Tile.UNKNOWN;
                }
            }
        }

    }

    public void update(int wx, int wy, int wz, int r) {
        depth = wz;
        visible = new boolean[world.width()][world.height()];

        // Cuts off the corners of the square
        for (int x = -r; x < r; x++) {
            for (int y = -r; y < r; y++) {
                if (x*x + y*y > r*r)
                    continue;

                // Make sure the point is in bounds
                if (wx + x < 0 || wx + x >= world.width()
                    || wy + y < 0 || wy + y >= world.height())
                    continue;

                // For every point in the line, starting from first point
                for (Point p : new Line(wx, wy, wx + x, wy + y)) {
                    Tile tile = world.tile(p.x, p.y, wz);
                    visible[p.x][p.y] = true;
                    tiles[p.x][p.y][wz] = tile;

                    if (!tile.isGround())   //You can only see over ground
                        break;
                }
            }
        }
    }

}
