
import java.awt.geom.Point2D;

public class Curve {
    boolean v = false;

    int[][] allYValsForX;

    // todo
    // precalculate y values for a given x value so we dont have to do it a million times

    Point2D p1 = new Point2D.Double(100,100);
    Point2D p2 = new Point2D.Double(450,150);
    Point2D p3 = new Point2D.Double(200,200);
    public Curve(Point2D.Double p1,Point2D.Double p2,Point2D.Double p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;

        allYValsForX = new int[Main.width][2];
    }

    public void calculate() {
    }

    public boolean contains(int x, int y) {
        int ys_above = 0;
        double y1 = -999;
        double y2 = -999;

        // T= -2p1 - 2p2 +- sqrt(4p2p2 - 4p1p3 + 4Txp1 -8Txp2 + 4Txp3) ) / 2(p1-2p2+p3)
        // Calculate values of T where the curve overlaps with X
        if (((p1.getX()) - (2* p2.getX()) + (p3.getX())) == 0) {
            return false;
        }

        double t1 = ((2*p1.getX()) - (2*p2.getX()) +
                Math.sqrt((4*Math.pow(p2.getX(),2))-(4*p1.getX()*p3.getX()) + (4*x*p1.getX()) - (8*x*p2.getX()) + (4*x* p3.getX()))
        ) / ((2*p1.getX()) - (4* p2.getX()) + (2*p3.getX()));

        if (t1 >= 0 && t1 <= 1) {
            y1 = (Math.pow(1-t1,2)*p1.getY()) + ((2*t1*(1-t1))*p2.getY()) + (Math.pow(t1,2)*p3.getY());
            if (y >= y1) {
                ys_above += 1;
            }
        }
        if (Double.isNaN(t1)) {
            // I think this means the sqrt section is negative
            return false;
            /*System.out.println("ERROR: NAN?");
            System.out.println("A "+((p1.getX()) - (2* p2.getX()) + (p3.getX())));
            System.out.println("B "+((-2*p1.getX()) + (2*p2.getX())));
            System.out.println("C "+(-x+p1.getX()));

            System.out.println("1 "+((2*p1.getX()) - (2*p2.getX())));
            System.out.println("2 "+((4*Math.pow(p2.getX(),2))-(4*p1.getX()*p3.getX()) + (4*x*p1.getX()) - (8*x*p2.getX()) + (4*x* p3.getX())));
            System.out.println("3 "+((2*p1.getX()) - (4* p2.getX()) + (2*p3.getX())));
            System. exit(0);*/
        }

        double t2 = ((2*p1.getX()) - (2*p2.getX()) -
                Math.sqrt((4*Math.pow(p2.getX(),2))-(4*p1.getX()*p3.getX()) + (4*x*p1.getX()) - (8*x*p2.getX()) + (4*x* p3.getX()))
        ) / ((2*p1.getX()) - (4* p2.getX()) + (2*p3.getX()));

        if (v) {
            System.out.println("Got time values of " + t1 + ", " + t2);
        }
        if (t2 >= 0 && t2 <= 1) {
            y2 = (Math.pow(1-t2,2)*p1.getY()) + ((2*t2*(1-t2))*p2.getY()) + (Math.pow(t2,2)*p3.getY());
            if (y >= y2) {
                ys_above += 1;
            }
        }
        if (v) {
            System.out.println("At point "+x+", "+y+": Got y values of " + y1 + ", " + y2);
        }
        //if (x == 125) {
        //System.out.println("Number above: "+ys_above);
        //}

        //if ((t2 == 0 || t2 == 1) || (t1 == 0 || t1 == 1)) {
        //    System.out.println("check "+y+", "+ys_above);
        //    System.out.println("At point "+x+", "+y+": Got y values of " + y1 + ", " + y2);
        //}
        // if exactly at endpoint and loops over itself, needs to do a special check to prevent some odd results
        if ((t1 == 0 || t1 == 1) && (t2 > 0 && t2 < 1) && (y > y1)) {
            System.out.println("1 "+x+", "+y+", "+y2);
            ys_above -= 1;
        }
        if ((t2 == 0 || t2 == 1) && (t1 > 0 && t1 < 1) && (y > y2)) {
            System.out.println("2 "+x+", "+y+", "+y1);
            ys_above -= 1;
        }

        if (ys_above%2 == 1) {
            return true;
        } else {
            return false;
        }
    }
}
