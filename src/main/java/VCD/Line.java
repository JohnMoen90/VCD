package VCD;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Line implements Iterable<Point>{
    private List<Point> points;
    public List<Point> getPoints() { return points;}

    // We take the starting and final coordinates
    public Line(int x0, int y0, int x1, int y1) {
        points = new ArrayList<Point>();

        // Get absolute values of x and y subtracted from second point's x and y
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        // Determine where points are in relation to each other (to move line in right direction)
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx-dy;

        while (true) {

            // Add first point
            points.add(new Point(x0, y0, 0));

            // If the coordinates are the same spot, break
            if (x0 == x1 && y0 == y1)
                break;

            int e2 = err * 2;
            if (e2 > -dx) {
                err -= dy;
                x0 += sx;   // Change x0 according to sx value
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;   // Change y0 according to sx value
            }
        }
    }

    public Iterator<Point> iterator( ) {
        return points.iterator();
    }


}
