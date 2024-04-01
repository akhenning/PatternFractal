import javax.swing.JFrame;

/**
 * Class that contains the main method for the program and creates the frame
 * containing the component.
 *
 * @author @akhenning
 * @version 3/9/24
 */
public class Main {

    // Total area considered
    public static int width = 1000;
    public static int height = 750;

    // pixels/pixel
    public static int detail = 1;

    public static boolean subtract = true;

    // Shrink scope of what is displayed to speed it up
    public static int LEFT_BOUNDS = (int) ((25) * detail);
    public static int RIGHT_BOUNDS = (int) ((width / 1.6) / detail);
    public static int UPPER_BOUNDS = (int) ((50) * detail);
    public static int LOWER_BOUNDS = (int) ((height / 1.9) / detail);

    public static int MAX_STEPS = 5;

    public static int v = 1; // 0 is none, 1 is some, 2 is all

    public static boolean DEBUG_SPECIFIC_X = false;
    public static int x_to_debug = 300;

    /**
     * main method for the program which creates and configures the frame for the
     * program
     *
     */
    public static void main(String[] args) throws InterruptedException {
        // create and configure the frame (window) for the program
        JFrame frame = new JFrame();

        frame.setSize(width, height);
        frame.setTitle("Pattern Fractal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        long timeElapsed = System.nanoTime();
        Canvas canvas = new Canvas(1);
        frame.add(canvas);
        long timeTook = (System.nanoTime() - timeElapsed) / 1000000;
        double timeSec = (double) timeTook / 1000;
        System.out.println("Constructor Time Took: " + timeSec);

        // make the frame visible which will result in the paintComponent method being
        // invoked on the component.
        frame.setVisible(true);

        /*
         * long timeElapsed = 0;
         * long timeTook = 0;
         * 
         * timeElapsed = System.nanoTime(); // starts counting here...
         * 
         * //canvas.advance();
         * 
         * timeTook = (System.nanoTime() - timeElapsed) / 1000000;
         * if (timeTook > 3) {
         * System.out.println("Lag Spike: " + timeTook +" milliseconds.");
         * if (timeTook > 21) {
         * System.out.println("WARNING: LAG SPIKE WAS PRETTY HIGH. 33 MS/F IS LIMIT");
         * }
         * }
         * System.out.println("Time Took: " + timeTook);
         * 
         * // 10fps. though loop is not implimented
         * try {
         * if (timeTook<100) {
         * Thread.sleep((long) (100-timeTook));
         * }
         * } catch (InterruptedException e) {
         * System.out.println("Error while sleeping\n");
         * e.printStackTrace();
         * }
         */
    }

}
