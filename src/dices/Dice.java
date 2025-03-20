package dices;

/**
 * Interface defining dice behavior.
 */
public interface Dice {
    /**
     * Roll the dice
     * @return Array of dice values
     */
    int[] roll();
    
    /**
     * Get the total value of the last roll
     * @return Total value of all dice
     */
    int getTotal();
    
    /**
     * Get a description of the dice type
     * @return Description of the dice
     */
    String getDescription();
}
