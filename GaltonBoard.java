import edu.princeton.cs.introcs.StdDraw;
import java.util.Random;
import java.util.Scanner;

public class GaltonBoard {
    private final int buckets; // Number of buckets
    private final int balls;   // Number of balls
    private final int rows;    // Number of rows (buckets - 1)
    private final double[] bucketHeights; // Tracks the height of balls in each bucket
    private final double pegSpacing = 1.0; // Spacing between pegs

    public GaltonBoard(int buckets, int balls) {
        this.buckets = buckets;
        this.balls = balls;
        this.rows = buckets - 1; // Last row of pegs has buckets - 1
        this.bucketHeights = new double[buckets];
    }

    public void simulate(int leftPercentage) {
        setupCanvas();

        // Draw the static elements
        drawPegs();
        drawBucketLines(leftPercentage);

        // Simulate each ball falling
        for (int i = 0; i < balls; i++) {
            dropBall(leftPercentage);
        }

        // Final display of results
    }

    private void setupCanvas() {
        StdDraw.setXscale(-buckets / 2.0 * pegSpacing - 1, buckets / 2.0 * pegSpacing + 1);
        StdDraw.setYscale(-2, rows + 8); // Increased Y scale to push the pyramid higher
        StdDraw.clear(StdDraw.WHITE);
    }

    private void drawPegs() {
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int row = 0; row < rows; row++) {
            int pegsInRow = row + 1;
            double rowOffset = (pegsInRow - 1) / 2.0; // Center the row horizontally
            for (int col = 0; col < pegsInRow; col++) {
                double x = (col - rowOffset) * pegSpacing;
                double y = rows - row + 3; // Offset the pyramid by 3 units higher
                StdDraw.filledCircle(x, y, 0.05); // Peg radius
            }
        }
    }

    private double[] calculateProb(int leftPercentage) {
        double probLeft = leftPercentage / 100.0; // Convert to decimal
        double probRight = 1 - probLeft; // Complement probability

        int levels = buckets - 1; // Number of levels is rows
        double[] probabilities = new double[buckets];

        // Dynamic programming arrays
        double[] current = new double[buckets];
        double[] next = new double[buckets];

        // Start at the top
        current[0] = 1.0;

        // Iterate through each level
        for (int level = 0; level < levels; level++) {
            for (int i = 0; i < buckets; i++) {
                next[i] = 0.0; // Reset for the next level
            }
            for (int i = 0; i <= level; i++) {
                // Ball goes left
                next[i] += current[i] * probLeft;
                // Ball goes right
                next[i + 1] += current[i] * probRight;
            }
            // Swap arrays for the next level
            double[] temp = current;
            current = next;
            next = temp;
        }

        // Final probabilities after all levels
        System.arraycopy(current, 0, probabilities, 0, buckets);

        return probabilities;
    }

    	
    
    
    
    private void drawBucketLines(int leftPercentage) {
    	
    double[] probabilities = calculateProb(leftPercentage);
    	
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int i = 0; i <= buckets; i++) {
            double x = (i - buckets / 2.0) * pegSpacing;
            StdDraw.line(x, -1, x, -0.5);
            
        if (i < buckets) {
        	double bucketCenterX = (i - buckets / 2.0 + 0.5) * pegSpacing;
            String probText = String.format("%.2f%%", probabilities[i] * 100);
            StdDraw.text((i - buckets / 2.0 + 0.5) * pegSpacing, -1.1, probText);

        }
        
        }  
    }

    private void dropBall(int leftPercentage) {
        Random random = new Random();
        double x = 0; // Start at the top center
        double y = rows + 3; // Start higher to match the pyramid's offset

        int[] directionArray = new int[100];
        for (int i = 0; i < 100; i++) {
            directionArray[i] = (i < leftPercentage) ? 1 : 0; // 1 means left, 0 means right
        }

        for (int i = 0; i < rows; i++) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.filledCircle(x, y, 0.1); // Draw ball at current position

            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.filledCircle(x, y, 0.1); // Erase the ball from the previous position

            int randomIndex = random.nextInt(100);
            boolean goLeft = (directionArray[randomIndex] == 1);

            x += goLeft ? -pegSpacing / 2 : pegSpacing / 2; // Move left or right
            y -= 0.9; // Move downward to the next row

            StdDraw.show();
            StdDraw.pause(10); // Faster animation
        }

        // Ball falls straight down from the last turn
        double finalX = x;
        double finalY = -0.5 + bucketHeights[(int) ((finalX / pegSpacing) + buckets / 2.0)] * 0.1;

        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.filledCircle(finalX, finalY, 0.05); // Draw ball in its final position

        // Increment the height of balls in the corresponding bucket
        int bucketIndex = (int) ((finalX / pegSpacing) + buckets / 2.0);
        bucketHeights[bucketIndex]++;
    }

    private void drawBins() {
        StdDraw.setPenColor(StdDraw.BLUE);
        for (int i = 0; i < buckets; i++) {
            double x = (i - buckets / 2.0) * pegSpacing;
            for (int j = 0; j < bucketHeights[i]; j++) {
                double y = -0.5 + j * 0.1;
                StdDraw.filledCircle(x, y, 0.05);
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of buckets: ");
        int buckets = scanner.nextInt();

        System.out.print("Enter the number of balls to drop: ");
        int balls = scanner.nextInt();

        System.out.print("Enter the percentage chance for the ball to go left (0-100): ");
        int leftPercentage = scanner.nextInt();

        scanner.close();

        GaltonBoard galtonBoard = new GaltonBoard(buckets, balls);
        galtonBoard.simulate(leftPercentage);
    }
}
