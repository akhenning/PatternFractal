import java.awt.geom.Point2D;
import java.util.ArrayList;

// Currently assumes that all child arms are "below" parent arms with no overlap
// Need a third one for the base, yeah...

public class Arm {
    Curve[] curves = new Curve[3];
    Arm[] children;
    double color;

    public Arm(Curve c1, Curve c2, boolean angle) {

        // int[] dens = { 4, 2 };
        // double[] full = { 1, 1 };
        // double[] len = { 100, 100 };
        // double[] rot = { 0, 0 };
        // double[] hook = { .1, .1 };
        // double[] l = { .2, .3 };
        // double[] r = { .01, .3 };
        // double[] gro = { 0, 0 };
        this(c1, c2, angle, new int[] { 0, 0 }, new double[] { 1, 1 }, new double[] { 100, 100 }, new double[] { 0, 0 },
                new double[] { 0, 0 }, new double[] { .2, .2 }, new double[] { .1, .1 }, new double[] { 0, 0 }, .9, 6,
                0);
    }

    /**
     * Construct a single arm thingy.
     * 
     * @param angle     true means that it opens downward and should rotate left,
     *                  false is vice versa
     * @param density   number of arms that should generate from this arm (0-4)
     * @param fullness  how much of the arm's available space should the arm take
     *                  (0<x<=1)
     * @param rotation  Angle of the endpoing of the child arms (radians)
     * @param hookiness how "hooked" the arm is. Can be negative. (-2 - 0 - 2)
     * @param l_offset  How much deadspace there should be on the left. (0-1)
     * @param r_offset  How much deadspace there should be on the right. (0-1)
     * @param growth    % increase or decrease from first to second child (-1 - 1,
     *                  % change = 1+(growth* childnumber))
     * @param color     shade (currently only b/w) (0-1)
     */
    public Arm(Curve c1, Curve c2, boolean angle, int[] density, double[] fullness, double[] length,
            double[] rotation, double[] hookiness,
            double[] l_offset, double[] r_offset, double[] growth, double color,
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
        double mid_x = (curves[0].p1.getX() + curves[1].p1.getX()) / 2;
        double mid_y = (curves[0].p1.getY() + curves[1].p1.getY()) / 2;
        double dx = -999;
        double dy = -999;
        // Calculate dist from midpoint to highest point
        if (curves[0].p1.getY() > curves[1].p1.getY()) {
            dx = curves[0].p1.getX() - mid_x;
            dy = curves[0].p1.getY() - mid_y;
        } else {
            dx = curves[1].p1.getX() - mid_x;
            dy = curves[1].p1.getY() - mid_y;
        }
        // Use that information to rotate it
        if (angle) {
            // left
            mid_x -= dy / 2;
            mid_y += dx / 2;
        } else {
            // right
            mid_x += dy / 2;
            mid_y -= dx / 2;
        }
        Point2D.Double bottom = new Point2D.Double(mid_x, mid_y);
        curves[2] = new Curve(curves[0].p1, bottom, curves[1].p1);

        if (Main.v > 0) {
            for (Curve c : curves) {
                System.out.println(c.toString());
            }
        }

        // angle check for curve 0. p1 connects to c[2].p1, p3 to c[1].p3
        curves[0].fillAdjacentCurveDirectionInformation(curves[2].getXForT(.0001), curves[1].getXForT(.9999), 0);
        // angle check for curve 1. p1 connects to c[2].p3, p3 to c[0].p3
        curves[1].fillAdjacentCurveDirectionInformation(curves[2].getXForT(.9999), curves[0].getXForT(.9999), 1);
        // angle check for curve 2. p1 connects to c[0].p1, p3 to c[1].p1
        curves[2].fillAdjacentCurveDirectionInformation(curves[0].getXForT(.0001), curves[1].getXForT(.0001), 2);

        if (step < Main.MAX_STEPS) {
            generateChildren(c1, c2, angle, density, fullness, length, rotation, hookiness, l_offset, r_offset, growth,
                    color,
                    step, seed);
        } else {
            children = new Arm[0];
        }

        for (Curve c : curves) {
            c.checkThatCurveAdjacencyWasSet();
        }
    }

    public void generateChildren(Curve c1, Curve c2, boolean angle, int[] density, double[] fullness, double[] length,
            double[] rotation, double[] hookiness,
            double[] l_offset, double[] r_offset, double[] growth, double color,
            int step, double seed) {
        System.out.println("CREATING CHILDREN");
        double[] check = { growth[0] * (density[0] - 1), growth[1] * (density[1] - 1) };
        if (check[0] >= 1 || check[0] <= -1 || check[1] >= 1 || check[1] <= -1) {
            System.out.println("BAD VALUE FOR GROWTH");
        }

        for (int tb = 0; tb < 1; tb += 1) {
            if (density[tb] <= 0) {
                continue;
            }
            children = new Arm[density[tb]];
            // available space respective to t
            double space = 1 - r_offset[tb] - l_offset[tb];
            // space for each child
            double one_space_offset = space / (density[tb]);
            // Calculate centerpoint for each child (on t)
            double[] centers = new double[density[tb]];
            for (double i = 0; i < density[tb]; i += 1) {
                // First is half the space for each child away from the edge plus l. off.
                centers[(int) i] = l_offset[tb] + (one_space_offset * (i + .5));
            }
            // Actual width, which is a percentage of the available space (halved since
            // radius)
            double each_width = (one_space_offset * fullness[tb]) / 2;

            for (int i = 0; i < density[tb]; i += 1) {
                // T value for each p1 is (centers +- each_width * growth)
                double t1 = centers[i] - (each_width * (1 + (((double) i) * growth[tb])));
                double t2 = centers[i] + (each_width * (1 + (((double) i) * growth[tb])));
                double[] xy1 = c2.getXYForT(t1);
                double[] xy2 = c2.getXYForT(t2);

                double mid_x = (xy1[0] + xy2[0]) / 2;
                double mid_y = (xy1[1] + xy2[1]) / 2;
                double end_x = -999;
                double end_y = -999;
                double dx = -999;
                double dy = -999;
                // Calculate dist from midpoint to highest point
                if (xy1[1] > xy2[1]) {
                    dx = xy1[0] - mid_x;
                    dy = xy1[1] - mid_y;
                } else {
                    dx = xy2[0] - mid_x;
                    dy = xy2[1] - mid_y;
                }
                // normalize
                double magnetude = dx + dy;
                dx /= magnetude;
                dy /= magnetude;

                double perp_angle = Math.atan2(dy, dx);
                System.out.println("Perpendecular angle from bottom of curve: " + (perp_angle * 180 / Math.PI));
                perp_angle += rotation[tb];
                dx = Math.cos(perp_angle);
                dy = Math.sin(perp_angle);

                dx *= length[tb];
                dy *= length[tb];
                // Use that information to rotate it
                if (angle) {
                    // left
                    end_x = mid_x - dy;
                    end_y = mid_y + dx;
                    mid_x -= dy / 2;
                    mid_y += dx / 2;
                } else {
                    // right
                    end_x = mid_x + dy;
                    end_y = mid_y - dx;
                    mid_x += dy / 2;
                    mid_y -= dx / 2;
                }
                Point2D.Double c1s = new Point2D.Double(xy1[0], xy1[1]);
                Point2D.Double c2s = new Point2D.Double(xy2[0], xy2[1]);

                Point2D.Double end = new Point2D.Double(end_x, end_y);
                Point2D.Double mid = new Point2D.Double(mid_x, mid_y);
                Curve c_c1 = new Curve(c1s, mid, end);
                Curve c_c2 = new Curve(c2s, mid, end);

                children[i] = new Arm(c_c1, c_c2, false, density, fullness, length, rotation, hookiness, l_offset,
                        r_offset, growth, color * .9, step + 1, seed);
            }
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
        // System.out.println("For x=" + x + ", y=" + y + " got values "
        // + contained + ", " + number_of_endpoints_above_this_point);

        // if (number_of_endpoints_above_this_point > 0) {
        // System.out.println("For x=" + x + ", y=" + y + " got values "
        // + contained + ", " + number_of_endpoints_above_this_point);
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
        for (Arm a : children) {
            a.calculate();
        }
    }
}
