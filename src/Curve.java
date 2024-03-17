
import java.awt.geom.Point2D;

public class Curve {
    boolean v = false;

    double[][] allYValsForX;

    // todo
    // precalculate y values for a given x value so we dont have to do it a million times

    Point2D p1 = new Point2D.Double(100,100);
    Point2D p2 = new Point2D.Double(450,150);
    Point2D p3 = new Point2D.Double(200,200);
    double colorVal = 1;

    public Curve(Point2D.Double p1,Point2D.Double p2,Point2D.Double p3, double colorVal) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;

        this.colorVal = colorVal;

        allYValsForX = new double[Main.width][3];
        // this is an array where each X value has its two y values, plus an additional value for flags
    }

    public void calculate() {
        // T= -2p1 - 2p2 +- sqrt(4p2p2 - 4p1p3 + 4Txp1 -8Txp2 + 4Txp3) ) / 2(p1-2p2+p3)
        // Calculate values of T where the curve overlaps with X

        for (int x = Main.LEFT_BOUNDS; x < Main.RIGHT_BOUNDS + 1; x += 1) {
            double y1 = -9999;
            double y2 = -9999;

            if ((((p1.getX()) - (2* p2.getX()) + (p3.getX())) == 0) || (4*Math.pow(p2.getX(),2))-(4*p1.getX()*p3.getX()) + (4*x*p1.getX()) - (8*x*p2.getX()) + (4*x* p3.getX()) < 0) {
                // Check for if the following equation will throw an error
                // from sqrt(-1) or x/0
                allYValsForX[x][0] = -9999;
                allYValsForX[x][1] = -9999;
                continue;
            }
    
            double t1 = ((2*p1.getX()) - (2*p2.getX()) +
                    Math.sqrt((4*Math.pow(p2.getX(),2))-(4*p1.getX()*p3.getX()) + (4*x*p1.getX()) - (8*x*p2.getX()) + (4*x* p3.getX()))
                    ) / ((2*p1.getX()) - (4* p2.getX()) + (2*p3.getX()));
    
            if (t1 >= 0 && t1 <= 1) {
                y1 = (Math.pow(1-t1,2)*p1.getY()) + ((2*t1*(1-t1))*p2.getY()) + (Math.pow(t1,2)*p3.getY());
            }
            if (Double.isNaN(t1)) {
                // I think this means the sqrt section is negative
                // both have the same section so we know it is nothing
                //allYValsForX[x][0] = -9999;
                //allYValsForX[x][1] = -9999;
                System.out.println("ERROR: THIS SHOULD AHVE BEEN PATCHED " + x);
                
                //continue;
            }
    
            double t2 = ((2*p1.getX()) - (2*p2.getX()) -
                    Math.sqrt((4*Math.pow(p2.getX(),2))-(4*p1.getX()*p3.getX()) + (4*x*p1.getX()) - (8*x*p2.getX()) + (4*x* p3.getX()))
                    ) / ((2*p1.getX()) - (4* p2.getX()) + (2*p3.getX()));
    
            if (v) {
                System.out.println("Got time values of " + t1 + ", " + t2);
            }
            if (t2 >= 0 && t2 <= 1) {
                y2 = (Math.pow(1-t2,2)*p1.getY()) + ((2*t2*(1-t2))*p2.getY()) + (Math.pow(t2,2)*p3.getY());
            }
            if (v) {
                System.out.println("At point "+x+": Got y values of " + y1 + ", " + y2);
            }

            allYValsForX[x][0] = y1;
            allYValsForX[x][1] = y2;

            if ((t1 == 0 || t1 == 1) && (t2 > 0 && t2 < 1)) {
                //System.out.println("1 "+x+", "+y+", "+y2);
                allYValsForX[x][2] = 1;
            }
            if ((t2 == 0 || t2 == 1) && (t1 > 0 && t1 < 1)) {
                //System.out.println("2 "+x+", "+y+", "+y1);
                allYValsForX[x][2] = 2;
            }
        }
    }

    public double contains(int x, int y) {
        int ys_above = 0;

        double y1 = allYValsForX[x][0];
        double y2 = allYValsForX[x][1];
        
        if (y1 != -9999 && y >= y1) {
            ys_above += 1;
        }

        if (y2 != -9999 && y >= y2) {
            ys_above += 1;
        }


        // if exactly at endpoint and loops over itself, needs to do a special check to prevent some odd results
        if (allYValsForX[x][2] == 1 && (y > y1)) {
            //System.out.println("1 "+x+", "+y+", "+y2);
            ys_above -= 1;
        }
        if (allYValsForX[x][2] == 2 && (y > y2)) {
            //System.out.println("2 "+x+", "+y+", "+y1);
            ys_above -= 1;
        }

        if (ys_above%2 == 1) {
            return colorVal;
        } else {
            return 0;
        }
    }
}
