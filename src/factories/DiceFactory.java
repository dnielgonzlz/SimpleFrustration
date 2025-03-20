package factories;

import dices.Dice;
import dices.SingleDie;
import dices.TwoDice;

/**
 * Factory for creating the appropriate dice based on configuration.
 */
public class DiceFactory {
    
    /**
     * Create dice based on the specified type
     * @param diceType "single" or "double"
     * @return The appropriate dice implementation
     */
    public Dice createDice(String diceType) {
        if ("single".equalsIgnoreCase(diceType)) {
            return new SingleDie();
        } else {
            return new TwoDice(); // Default to two dice
        }
    }
    
    /**
     * Create dice with a seed for testing
     * @param diceType "single" or "double"
     * @param seed Random seed for predictable results
     * @return The appropriate dice implementation
     */
    public Dice createDice(String diceType, long seed) {
        if ("single".equalsIgnoreCase(diceType)) {
            return new SingleDie(seed);
        } else {
            return new TwoDice(seed); // Default to two dice
        }
    }
}
