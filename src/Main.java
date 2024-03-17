import javax.swing.JFrame;

/**
 * Class that contains the main method for the program and creates the frame containing the component.
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

    // Shrink scope of what is displayed to speed it up
    public static int LEFT_BOUNDS = (int)((50) * detail);
    public static int RIGHT_BOUNDS = (int)((width/5) / detail);
    public static int UPPER_BOUNDS = (int)((100) * detail);
    public static int LOWER_BOUNDS = (int)((height/1.9) / detail);


    /**
     * main method for the program which creates and configures the frame for the program
     *
     */
    public static void main(String[] args) throws InterruptedException
    {
        // create and configure the frame (window) for the program
        JFrame frame = new JFrame();

        frame.setSize(width, height);
        frame.setTitle("Pattern Fractal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Canvas canvas=new Canvas(1);
        frame.add(canvas);

        // make the frame visible which will result in the paintComponent method being invoked on the
        //  component.
        frame.setVisible(true);

/*
        long timeElapsed = 0;
        long timeTook = 0;

        timeElapsed = System.nanoTime(); // starts counting here...

        //canvas.advance();

        timeTook = (System.nanoTime() - timeElapsed) / 1000000;
        if (timeTook  > 3) {
            System.out.println("Lag Spike: " + timeTook +" milliseconds.");
            if (timeTook > 21) {
                System.out.println("WARNING: LAG SPIKE WAS PRETTY HIGH. 33 MS/F IS LIMIT");
            }
        }
        System.out.println("Time Took: " + timeTook);

        // 10fps. though loop is not implimented
        try {
            if (timeTook<100) {
                Thread.sleep((long) (100-timeTook));
            }
        } catch (InterruptedException e) {
            System.out.println("Error while sleeping\n");
            e.printStackTrace();
        }
*/
    }

}
