package dices;

import java.util.Random;

/**
 * Implementation of two dice.
 */
public class TwoDice implements Dice {
    private static final int SIDES = 6;
    private final Random random;
    private int[] lastRoll;
    
    public TwoDice() {
        this.random = new Random();
        this.lastRoll = new int[2];
    }
    
    /**
     * Constructor that allows specifying a seed for testing
     * @param seed Random seed for predictable results
     */
    public TwoDice(long seed) {
        this.random = new Random(seed);
        this.lastRoll = new int[2];
    }
    
    @Override
    public int[] roll() {
        lastRoll[0] = random.nextInt(SIDES) + 1;
        lastRoll[1] = random.nextInt(SIDES) + 1;
        return lastRoll;
    }
    
    @Override
    public int getTotal() {
        return lastRoll[0] + lastRoll[1];
    }
    
    @Override
    public String getDescription() {
        return "Two random 6 sided dice";
    }
}
