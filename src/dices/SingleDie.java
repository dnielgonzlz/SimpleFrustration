package dices;

import java.util.Random;

/**
 * Implementation of a single die.
 */
public class SingleDie implements Dice {
    private static final int SIDES = 6;
    private final Random random;
    private int lastRoll;
    
    public SingleDie() {
        this.random = new Random();
        this.lastRoll = 0;
    }
    
    /**
     * Constructor that allows specifying a seed for testing
     * @param seed Random seed for predictable results
     */
    public SingleDie(long seed) {
        this.random = new Random(seed);
        this.lastRoll = 0;
    }
    
    @Override
    public int[] roll() {
        lastRoll = random.nextInt(SIDES) + 1;
        return new int[]{ lastRoll };
    }
    
    @Override
    public int getTotal() {
        return lastRoll;
    }
    
    @Override
    public String getDescription() {
        return "Single random 6 sided die";
    }
}
