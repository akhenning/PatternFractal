
import java.awt.geom.Point2D;
import java.util.Arrays;

public class Curve {

    int[][] allYValsForX;

    Point2D p1 = new Point2D.Double(100, 100);
    Point2D p2 = new Point2D.Double(450, 150);
    Point2D p3 = new Point2D.Double(200, 200);

    double[][] endpoints;

    public Curve(Point2D p1, Point2D p2, Point2D p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;

        allYValsForX = new int[Main.width][3];
        // this is an array where each X value has its two y values, plus an additional
        // value for flags, and another to track number of endpoints

        // Last integer represents whether it is a corner or a flat connection
        double[][] endpointss = {
                { p1.getX(), p1.getY(), 0 },
                { p3.getX(), p3.getY(), 0 }
        };
        // System.out.println(endpointss.toString());
        // bruh. java moment
        endpoints = endpointss;
    }

    public double[] getXYForT(double t) {
        if (t > 1 || t < 0) {
            System.out.println("ERROR: INVALID T " + t);
        }
        double x = (Math.pow(1 - t, 2) * p1.getX()) + ((2 * t * (1 - t)) * p2.getX()) + (Math.pow(t, 2) * p3.getX());
        double y = (Math.pow(1 - t, 2) * p1.getY()) + ((2 * t * (1 - t)) * p2.getY()) + (Math.pow(t, 2) * p3.getY());

        double[] xy = { x, y };
        return xy;
    }

    public double getXForT(double t) {
        if (t > 1 || t < 0) {
            System.out.println("ERROR: INVALID T " + t);
        }
        double x = (Math.pow(1 - t, 2) * p1.getX()) + ((2 * t * (1 - t)) * p2.getX()) + (Math.pow(t, 2) * p3.getX());
        return x;
    }

    // gives x for curve connected to corresponding point. Compare if same
    // direction.
    public void fillAdjacentCurveDirectionInformation(double p1_connected_x, double p3_connected_x, int num) {
        double p1x = p1.getX();
        // get if curve is to the left ( positive ) or right (negative)
        double p1Dir = p1x - getXForT(.0001);
        double connectedP1Dir = p1x - p1_connected_x;
        if (p1Dir < 0) {
            p1Dir = -1;
        } else if (p1Dir > 0) {
            p1Dir = 1;
        }
        if (connectedP1Dir < 0) {
            connectedP1Dir = -1;
        } else if (connectedP1Dir > 0) {
            connectedP1Dir = 1;
        }
        // if same polarity then -1, if same then 1
        if (p1Dir == connectedP1Dir) {
            endpoints[0][2] = -1;
        } else if (p1Dir == connectedP1Dir * -1) {
            endpoints[0][2] = 1;
        } else {
            System.out.println(
                    "SANITY CHECK FAILED: CURVE " + num + "'s P1 DIRECTIONALITY OUTPUT MAKES NO SENSE " + p1Dir + ", "
                            + connectedP1Dir
                            + " FROM INPUTS " + p1x + ", " + getXForT(.0001) + ", " + p1x + ", " + p1_connected_x);
        }

        if (Main.v > 2) {
            System.out.println(
                    "CURVE " + num + "'s P1 DIRECTIONALITY OUTPUT " + p1Dir + ", "
                            + connectedP1Dir
                            + " FROM INPUTS " + p1x + ", " + getXForT(.0001) + ", " + p1x + ", " + p1_connected_x);
        }

        double p3x = p3.getX();
        // get if curve is to the left ( positive ) or right (negative)
        double p3Dir = p3x - getXForT(.9999);
        double connectedP3Dir = p3x - p3_connected_x;
        if (p3Dir < 0) {
            p3Dir = -1;
        } else if (p3Dir > 0) {
            p3Dir = 1;
        }
        if (connectedP3Dir < 0) {
            connectedP3Dir = -1;
        } else if (connectedP3Dir > 0) {
            connectedP3Dir = 1;
        }
        // if same polarity then -1, if same then 1
        if (p3Dir == connectedP3Dir) {
            endpoints[1][2] = -1;
        } else if (p3Dir == connectedP3Dir * -1) {
            endpoints[1][2] = 1;
        } else {
            System.out.println(
                    "SANITY CHECK FAILED: CURVE " + num + "'s P3 DIRECTIONALITY OUTPUT MAKES NO SENSE " + p3Dir + ", "
                            + connectedP3Dir
                            + " FROM INPUTS " + p3x + ", " + getXForT(.9999) + ", " + p3x + ", " + p3_connected_x);
        }
        if (Main.v > 2) {
            System.out.println(
                    "CURVE " + num + "'s P3 DIRECTIONALITY OUTPUT " + p3Dir + ", "
                            + connectedP3Dir
                            + " FROM INPUTS " + p3x + ", " + getXForT(.9999) + ", " + p3x + ", " + p3_connected_x);
        }

        System.out.println("Final: " + Arrays.deepToString(endpoints));
    }

    public void checkThatCurveAdjacencyWasSet() {
        if (endpoints[1][2] == 0) {
            System.out.println("ERROR: CURVE ADJACENCY NOT SET");
        }
    }

    // todo ok to eliminate any values not in box created by x and y values of
    // points
    public void calculate() {
        // T= -2p1 - 2p2 +- sqrt(4p2p2 - 4p1p3 + 4Txp1 -8Txp2 + 4Txp3) ) / 2(p1-2p2+p3)
        // Calculate values of T where the curve overlaps with X
        if (Main.v > 0) {
            System.out.println("Calculating for " + p1.toString() + ", " + p2.toString() + ", " + p3.toString());
        }

        int left_bounds = Main.LEFT_BOUNDS;
        int right_bounds = Main.RIGHT_BOUNDS + 1;
        if (Main.DEBUG_SPECIFIC_X) {
            left_bounds = Main.x_to_debug;
            right_bounds = Main.x_to_debug + 1;
        }
        for (int x = left_bounds; x < right_bounds; x += 1) {
            // for (int x = 200; x < 201; x += 1) {
            if (Main.v > 2) {
                System.out.println("Calculating for x=" + x);
            }

            double y1 = -9999;
            double y2 = -9999;

            if ((((p1.getX()) - (2 * p2.getX()) + (p3.getX())) == 0)
                    || (4 * Math.pow(p2.getX(), 2)) - (4 * p1.getX() * p3.getX()) + (4 * x * p1.getX())
                            - (8 * x * p2.getX()) + (4 * x * p3.getX()) < 0) {
                // Check for if the following equation will throw an error
                // from sqrt(-1) or x/0
                allYValsForX[x][0] = -9999;
                allYValsForX[x][1] = -9999;
                continue;
            }

            double t1 = ((2 * p1.getX()) - (2 * p2.getX()) +
                    Math.sqrt((4 * Math.pow(p2.getX(), 2)) - (4 * p1.getX() * p3.getX()) + (4 * x * p1.getX())
                            - (8 * x * p2.getX()) + (4 * x * p3.getX())))
                    / ((2 * p1.getX()) - (4 * p2.getX()) + (2 * p3.getX()));

            if (t1 >= -.000001 && t1 <= 1.000001) {
                y1 = (Math.pow(1 - t1, 2) * p1.getY()) + ((2 * t1 * (1 - t1)) * p2.getY())
                        + (Math.pow(t1, 2) * p3.getY());
            }
            if (Double.isNaN(t1)) {
                // I think this means the sqrt section is negative
                // both have the same section so we know it is nothing
                // allYValsForX[x][0] = -9999;
                // allYValsForX[x][1] = -9999;
                System.out.println("ERROR: THIS SHOULD HAVE BEEN PATCHED " + x);

                // continue;
            }

            double t2 = ((2 * p1.getX()) - (2 * p2.getX()) -
                    Math.sqrt((4 * Math.pow(p2.getX(), 2)) - (4 * p1.getX() * p3.getX()) + (4 * x * p1.getX())
                            - (8 * x * p2.getX()) + (4 * x * p3.getX())))
                    / ((2 * p1.getX()) - (4 * p2.getX()) + (2 * p3.getX()));

            if (Main.v >= 3) {
                System.out.println("Got time values of " + t1 + ", " + t2);
            }
            if ((t1 == t2) && (t1 >= .999999 || t1 <= .000001)) {
                // What the fuck? Oh, it's an apex on a corner
                System.out.println("Curve apex happening at " + x + ", " + this.toString());
                t2 = -9999;
                // Other issue is just what I do when two meet in the first place. Maybe we can
                // just track that by returning it when applicable.
            }

            if (t2 >= -.000001 && t2 <= 1.000001) {
                y2 = (Math.pow(1 - t2, 2) * p1.getY()) + ((2 * t2 * (1 - t2)) * p2.getY())
                        + (Math.pow(t2, 2) * p3.getY());
            }

            if (Main.v >= 3) {
                System.out.println("At point " + x + ": Got y values of " + y1 + ", " + y2);
            }

            allYValsForX[x][0] = (int) y1;
            allYValsForX[x][1] = (int) y2;

            // if one point is on the end, and the other is valid value
            if ((t1 == 0 || t1 == 1) && (t2 > 0 && t2 < 1)) {
                // System.out.println("1 "+x+", "+y+", "+y2);
                allYValsForX[x][2] = 1;
            }
            if ((t2 == 0 || t2 == 1) && (t1 > 0 && t1 < 1)) {
                // System.out.println("2 "+x+", "+y+", "+y1);
                allYValsForX[x][2] = 2;
            }
        }
    }

    // First represents if there is an odd number of intersections for this X above
    // this Y
    // Second represents how many CROSSING endpoints there are above this point
    public int[] isAboveOddNumTimes(int x, int y) {
        int ys_above = 0;

        double y1 = allYValsForX[x][0];
        double y2 = allYValsForX[x][1];

        if (y1 != -9999 && y >= y1) {
            ys_above += 1;
        }

        if (y2 != -9999 && y >= y2) {
            ys_above += 1;
        }

        // if exactly at endpoint and loops over itself, needs to do a special check to
        // prevent some odd results
        // may be depreciated
        // if (allYValsForX[x][2] == 1 && (y > y1)) {
        // System.out.println("1 "+x+", "+y+", "+y2);
        // ys_above -= 1;
        // }
        // if (allYValsForX[x][2] == 2 && (y > y2)) {
        // System.out.println("2 "+x+", "+y+", "+y1);
        // ys_above -= 1;
        // }

        if (ys_above % 2 == 1) {
            int[] rtrn = { 1, numberValidCornersForThisXY(x, y) };
            return rtrn;
        } else {
            int[] rtrn = { 0, numberValidCornersForThisXY(x, y) };
            return rtrn;
        }
    }

    public int numberValidCornersForThisXY(int x, int y) {
        int how_many = 0;
        // System.out.println((x == endpoints[0][0]) + ", " + (y >= endpoints[0][1]) +
        // ", " + (endpoints[0][2] == 1));

        // todo this 1 will need to be changed to scale with zoom
        // if ((x >= endpoints[0][0] && x < endpoints[0][3]) && y >= endpoints[0][1] &&
        // endpoints[0][2] == 1) {
        if ((Math.abs(x - endpoints[0][0]) < .001) && y >= endpoints[0][1] && endpoints[0][2] == 1) {
            how_many += 1;
        }
        // System.out.println((x == endpoints[1][0]) + ", " + (y >= endpoints[1][1]) +
        // ", " + (endpoints[1][2] == 1));
        if ((Math.abs(x - endpoints[1][0]) < .001) && y >= endpoints[1][1] && endpoints[1][2] == 1) {
            how_many += 1;
        }
        return how_many;
    }

    public String toString() {
        return "Curve with points: [" + p1.getX() + ", " + p1.getY() + "], [" + p2.getX() + ", " + p2.getY() + "], ["
                + p3.getX() + ", " + p3.getY() + "]";
    }
}
