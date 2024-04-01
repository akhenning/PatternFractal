import java.awt.geom.Point2D;

public class MathChecker {
    public static void main(String[] args) throws InterruptedException {
        Point2D.Double p1 = new Point2D.Double(100, 100);
        Point2D.Double p2 = new Point2D.Double(100, 125);
        Point2D.Double p3 = new Point2D.Double(100, 125);
        Curve c1 = new Curve(p1, p2, p3);

        for (double i = 0; i < .01; i += .0003) {
            System.out.println(c1.getXForT(i));
        }
    }

    public static void old() {
        double x = 1.5;
        Point2D p1 = new Point2D.Double(1, 100);
        // Point2D cpt_2 = new Point2D.Double(200,100);
        // Point2D cpt_3 = new Point2D.Double(100,200);
        Point2D p2 = new Point2D.Double(2, 100);
        Point2D p3 = new Point2D.Double(2, 200);
        // T= -2p1 - 2p2 +- sqrt(4p2p2 - 4p1p3 + 4Txp1 -8Txp2 + 4Txp3) ) / 2(p1-2p2+p3)
        // Calculate values of T where the curve overlaps with X
        double t1 = ((2 * p1.getX()) - (2 * p2.getX()) +
                Math.sqrt((4 * Math.pow(p2.getX(), 2)) - (4 * p1.getX() * p3.getX()) + (4 * x * p1.getX())
                        - (8 * x * p2.getX()) + (4 * x * p3.getX())))
                / ((2 * p1.getX()) - (4 * p2.getX()) + (2 * p3.getX()));
        double t2 = ((2 * p1.getX()) - (2 * p2.getX()) -
                Math.sqrt((4 * Math.pow(p2.getX(), 2)) - (4 * p1.getX() * p3.getX()) + (4 * x * p1.getX())
                        - (8 * x * p2.getX()) + (4 * x * p3.getX())))
                / ((2 * p1.getX()) - (4 * p2.getX()) + (2 * p3.getX()));
        System.out.println("Got time values of " + t1 + ", " + t2);
        double y1 = (Math.pow(1 - t1, 2) * p1.getY()) + ((2 * t1 * (1 - t1)) * p2.getY())
                + (Math.pow(t1, 2) * p3.getY());
        double y2 = (Math.pow(1 - t2, 2) * p1.getY()) + ((2 * t2 * (1 - t2)) * p2.getY())
                + (Math.pow(t2, 2) * p3.getY());
        System.out.println("Got y values of " + y1 + ", " + y2);

        System.out.println("A " + ((p1.getX()) - (2 * p2.getX()) + (p3.getX())));
        System.out.println("B " + ((-2 * p1.getX()) + (2 * p2.getX())));
        System.out.println("C " + (-x + p1.getX()));

        System.out.println("1 " + ((2 * p1.getX()) - (2 * p2.getX())));
        System.out.println("2 " + ((4 * Math.pow(p2.getX(), 2)) - (4 * p1.getX() * p3.getX()) + (4 * x * p1.getX())
                - (8 * x * p2.getX()) + (4 * x * p3.getX())));
        System.out.println("3 " + ((2 * p1.getX()) - (4 * p2.getX()) + (2 * p3.getX())));
    }
}
