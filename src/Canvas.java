import javax.swing.JPanel;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;

public class Canvas extends JPanel {

    private double[][] postpros;

    private Color[] grey_scale = new Color[21];

    ArrayList<Arm> roots = new ArrayList<>();

    public Canvas(int seed) {
        this(seed, 1);
    }

    public Canvas(int seed, int arms) {
        System.out.println("Drawn area: " +
                Main.LEFT_BOUNDS + ", " + Main.RIGHT_BOUNDS + ", " + Main.UPPER_BOUNDS + ", " + Main.LOWER_BOUNDS);
        /*
         * Point2D.Double p1 = new Point2D.Double(100, 100);
         * Point2D.Double p2 = new Point2D.Double(400, 150);
         * Point2D.Double p3 = new Point2D.Double(200, 200);
         * roots.add(new Curve(p1, p2, p3, 1));
         * p1 = new Point2D.Double(100, 100);
         * p2 = new Point2D.Double(475, 250);
         * p3 = new Point2D.Double(200, 200);
         * roots.add(new Curve(p1, p2, p3, .8));
         * p1 = new Point2D.Double(350, 150);
         * p2 = new Point2D.Double(250, 75);
         * p3 = new Point2D.Double(125, 250);
         * roots.add(new Curve(p1, p2, p3, .6));
         * p1 = new Point2D.Double(350, 150);
         * p2 = new Point2D.Double(50, 200);
         * p3 = new Point2D.Double(125, 250);
         * roots.add(new Curve(p1, p2, p3, .4));
         */

        Point2D.Double p1 = new Point2D.Double(100, 100);
        Point2D.Double p2 = new Point2D.Double(250, 160);
        Point2D.Double p3 = new Point2D.Double(100, 200);
        Curve c1 = new Curve(p1, p2, p3);
        p1 = new Point2D.Double(100, 125);
        p2 = new Point2D.Double(200, 150);
        p3 = new Point2D.Double(100, 200);
        Curve c2 = new Curve(p1, p2, p3);
        roots.add(new Arm(c1, c2, true, 3, .75, 100, 1, .3, .1, 1, 0, seed));

        p1 = new Point2D.Double(200, 100);
        p2 = new Point2D.Double(350, 150);
        p3 = new Point2D.Double(300, 200);
        c1 = new Curve(p1, p2, p3);
        p1 = new Point2D.Double(225, 125);
        p2 = new Point2D.Double(300, 150);
        p3 = new Point2D.Double(300, 200);
        c2 = new Curve(p1, p2, p3);
        roots.add(new Arm(c1, c2, true, 3, .75, 100, 1, .3, .1, 1, 0, seed));

        for (Arm arm : roots) {
            arm.calculate();
        }

        for (double i = 0; i <= 20; i += 1) {
            // System.out.println("Color: "+(255 * (i/10)));
            grey_scale[20 - (int) i] = new Color((int) (255 * (i / 20)), (int) (255 * (i / 20)),
                    (int) (255 * (i / 20)));
        }

        // entire canvas, even bits not displayed
        // one per pixel even on low res
        int width = (int) (Main.width / Main.detail);
        int height = (int) (Main.height / Main.detail);
        postpros = new double[width][height];

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // g2.setStroke(new BasicStroke(4));
        g2.setColor(Color.BLACK);
        // for (int x = Main.LEFT_BOUNDS; x < Main.RIGHT_BOUNDS; x += Main.detail) {
        // for (int y = Main.UPPER_BOUNDS; y < Main.LOWER_BOUNDS; y += Main.detail) {
        fillPostprocessArray();

        postprocess();

        draw(g2);
        // g2.setColor(Color.RED);
        // g2.fillRect(200, 200, 10, 10);

    }

    public void fillPostprocessArray() {
        for (int x = Main.LEFT_BOUNDS; x < Main.RIGHT_BOUNDS; x += 1) {
            // for (int x = 124; x <= 126; x += 1) {
            for (int y = Main.UPPER_BOUNDS; y < Main.LOWER_BOUNDS; y += 1) {
                // System.out.print("Checking "+x+", "+y);
                // System.out.println("Checking "+x+", "+y);
                double contains = 0;
                for (Arm arm : roots) {
                    double shade = arm.contains(x * Main.detail, y * Main.detail);
                    if (shade != 0) {
                        if (contains == 0) {
                            contains = shade;
                        } else {
                            if (Main.subtract) {
                                contains = 0;
                            } else {
                                if (shade > contains) {
                                    contains = contains + (1 - shade) / 2;
                                } else {
                                    contains = shade + (1 - contains) / 2;
                                }
                                if (contains > 1) {
                                    System.out.println("ERROR TOO HIGH");
                                }
                                // oh fuck this needs a whole overhaul for checking within an arm specifically
                                // check so that if arm is not within greater arm, sees if within range...? But,
                                // the end is open...
                                // wait, how are the roots going to work? Gah
                                // need to figure out how we work in teh arm implimentation with the curve as
                                // well
                            }
                        }
                    }
                }
                if (contains != 0) {
                    // System.out.println(". Is in curve!");
                    // System.out.println(x+", "+y+". Is in curve!");
                    // g2.fillRect(x, y, Main.detail, Main.detail);
                    postpros[x][y] = contains;
                } else {
                    // System.out.println(". No.");
                    postpros[x][y] = 0;
                }
            }
        }
    }

    public void postprocess() {
        int max_v_x = (Main.RIGHT_BOUNDS >= postpros.length ? postpros.length - 1 : Main.RIGHT_BOUNDS);
        int max_v_y = (Main.LOWER_BOUNDS >= postpros[0].length ? postpros[0].length - 1 : Main.LOWER_BOUNDS);
        // if not adjacent to wall of postpros
        for (int x = Main.LEFT_BOUNDS <= 0 ? 1 : Main.LEFT_BOUNDS; x < max_v_x; x += 1) {
            for (int y = Main.UPPER_BOUNDS <= 0 ? 1 : Main.UPPER_BOUNDS; y < max_v_y; y += 1) {
                if (postpros[x][y] == 1) {
                    double num_adj = 0;
                    if (postpros[x - 1][y] == 0) {
                        num_adj += 1;
                    }
                    if (postpros[x][y - 1] == 0) {
                        num_adj += 1;
                    }
                    if (postpros[x + 1][y] == 0) {
                        num_adj += 1;
                    }
                    if (postpros[x][y + 1] == 0) {
                        num_adj += 1;
                    }
                    if (postpros[x - 1][y - 1] == 0) {
                        num_adj += 1;
                    }
                    if (postpros[x + 1][y - 1] == 0) {
                        num_adj += 1;
                    }
                    if (postpros[x + 1][y + 1] == 0) {
                        num_adj += 1;
                    }
                    if (postpros[x - 1][y + 1] == 0) {
                        num_adj += 1;
                    }
                    // 0 color modifications should be 10 for maximum dark, corresponding to
                    // original 1
                    // should be relatively linear from the max
                    double max = 8;
                    postpros[x][y] = (max - num_adj) / max;
                }
            }
        }

    }

    public void draw(Graphics2D g2) {
        for (int x = 0; x < postpros.length; x += 1) {
            for (int y = 0; y < postpros[0].length; y += 1) {
                if (postpros[x][y] != 0) {
                    // System.out.println((int)(10*postpros[x][y])+", "+postpros[x][y]);
                    g2.setColor(grey_scale[(int) (20 * postpros[x][y])]);
                    g2.fillRect(x * Main.detail, y * Main.detail, Main.detail, Main.detail);
                }
            }
        }
    }

    public void advance() {

    }
}
