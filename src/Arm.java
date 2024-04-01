import java.awt.geom.Point2D;
import java.util.ArrayList;

// Currently assumes that all child arms are "below" parent arms with no overlap
// Need a third one for the base, yeah...

public class Arm {
    Curve[] curves = new Curve[3];
    Arm[] children;
    double color;

    /**
     * Construct a single arm thingy.
     * 
     * @param angle     true means that it opens downward and should rotate left,
     *                  false is vice versa
     * @param density   number of arms that should generate from this arm (0-4)
     * @param fullness  how much of the arm's available space should the arm take
     *                  (0<x<=1)
     * @param hookiness how "hooked" the arm is. Can be negative. (-2 - 0 - 2)
     * @param l_offset  How much deadspace there should be on the left. (0-1)
     * @param r_offset  How much deadspace there should be on the right. (0-1)
     * @param color     shade (currently only b/w) (0-1)
     */
    public Arm(Curve c1, Curve c2, boolean angle, int density, double fullness, double length, double hookiness,
            double l_offset, double r_offset, double color,
            int step, double seed) {
        curves[0] = c1;
        curves[1] = c2;

        this.color = color;
        if (step > 6) {
            System.out.println("ERROR: INFINITE LOOP");
            System.exit(1);
        }

        // Double check that connects at p3
        if (!curves[0].p3.equals(curves[1].p3)) {
            System.out.println("ERROR: MISALIGNED CURVES" + curves[0].p3.toString() + ", " + curves[1].p3.toString());
        }
        // System.out.println("Finding midpoint between: " + curves[0].p1.getX() + ", "
        // + curves[1].p1.getX() + ". "
        // + curves[0].p1.getY() + ", "
        // + curves[1].p1.getY());
        // Calculate midpoint
        double mid_y = (curves[0].p1.getY() + curves[1].p1.getY()) / 2;
        double mid_x = (curves[0].p1.getX() + curves[1].p1.getX()) / 2;
        double dy = -999;
        double dx = -999;
        // Calculate dist from midpoint to highest point
        if (curves[0].p1.getY() > curves[1].p1.getY()) {
            dy = curves[0].p1.getY() - mid_y;
            dx = curves[0].p1.getX() - mid_x;
        } else {
            // System.out.println("Midpoint to highest: " + curves[0].p1.getX() + ", " +
            // mid_x + ". " + curves[0].p1.getY()
            // + ", " + mid_y);
            dy = curves[1].p1.getY() - mid_y;
            dx = curves[1].p1.getX() - mid_x;
        }
        // Use that information to rotate it
        if (angle) {
            // left
            // System.out.println("Rotating " + mid_x + ", " + dx + ". " + mid_y + ", " +
            // dy);
            mid_x -= dx;
            mid_y += dy;
        } else {
            // right
            mid_x += dx;
            mid_y -= dy;
        }
        Point2D.Double bottom = new Point2D.Double(mid_x, mid_y);
        curves[2] = new Curve(curves[0].p1, bottom, curves[1].p1);

        // angle check for curve 0. p1 connects to c[2].p1, p3 to c[1].p3
        curves[0].fillAdjacentCurveDirectionInformation(curves[2].getXForT(.001), curves[1].getXForT(.999), 0);
        // angle check for curve 1. p1 connects to c[2].p3, p3 to c[0].p3
        curves[1].fillAdjacentCurveDirectionInformation(curves[2].getXForT(.999), curves[0].getXForT(.999), 1);
        // angle check for curve 2. p1 connects to c[0].p1, p3 to c[1].p1
        curves[2].fillAdjacentCurveDirectionInformation(curves[0].getXForT(.001), curves[1].getXForT(.001), 2);

        for (Curve c : curves) {
            System.out.println(c.toString());
        }

        if (false && step < Main.MAX_STEPS) {
            children = new Arm[density];
            double space = 1 - r_offset - l_offset;
            double one_space_offset = space / (density + 1);
            double[] centers = new double[density];
            for (double i = 0; i < density; i += 1) {
                centers[(int) i] = (one_space_offset * (i + .5));
            }
            double each_width = (space / density) * fullness;

            // todo
            // Make function to calculate x and y for a given T on the curves. Not hard.
            // Calculate perpendicular line from those two points
            // Offset that line by hook's value

            for (int i = 0; i < density; i += 1) {
                // children[i] = new Arm(c1,c2,true,density,fullness,length *
                // .8,hookiness,l_offset,r_offset,color*.8,step,seed);
            }

        } else {
            children = new Arm[0];
        }
    }

    public double contains(int x, int y) {
        boolean contained = false;
        int number_of_endpoints_above_this_point = 0;
        for (Curve c : curves) {
            int[] isUnder = c.isAboveOddNumTimes(x, y);
            // System.out.println(y + ": " + isUnder + ", " + c.toString());
            number_of_endpoints_above_this_point += isUnder[1];
            if (isUnder[0] == 1) {
                contained = !contained;
            }
        }
        // if (number_of_endpoints_above_this_point > 0) {
        // System.out.println("Got " + number_of_endpoints_above_this_point + "
        // endpoints");
        // }
        if (number_of_endpoints_above_this_point % 2 != 0) {
            System.out.println("ERROR: ODD NUMBER OF ENDPOINTS AT " + x + ", " + y);
        }
        for (int i = 0; i < number_of_endpoints_above_this_point / 2; i += 1) {
            contained = !contained;
        }
        // System.out.println("");
        if (contained) {
            return color;
        } else {
            double childCol = 0;
            for (Arm a : children) {
                childCol = a.contains(x, y);
                if (childCol != 0) {
                    return childCol;
                }
            }
            return childCol;
        }
    }

    public void calculate() {
        for (Curve c : curves) {
            c.calculate();
        }
    }

    public void generateChildren() {

    }
}