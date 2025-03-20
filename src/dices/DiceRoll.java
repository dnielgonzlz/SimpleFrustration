package dices;

import java.util.Arrays;
import java.util.Objects;

/**
 * Value object representing the result of rolling dice.
 * Immutable once created.
 */
public class DiceRoll {
    private final int[] values;
    private final int total;
    
    /**
     * Constructor for a DiceRoll
     * @param values The values of individual dice
     */
    public DiceRoll(int[] values) {
        this.values = Arrays.copyOf(values, values.length);
        this.total = Arrays.stream(values).sum();
    }
    
    /**
     * Get individual dice values
     * @return Copy of the dice values array
     */
    public int[] getValues() { 
        return Arrays.copyOf(values, values.length); 
    }
    
    /**
     * Get the total of all dice
     * @return Sum of all dice values
     */
    public int getTotal() { 
        return total; 
    }
    
    /**
     * Get number of dice in this roll
     * @return Number of dice
     */
    public int getNumberOfDice() {
        return values.length;
    }
    
    /**
     * Get the value of a specific die
     * @param index Die index (0-based)
     * @return Value of the specified die
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public int getDieValue(int index) {
        return values[index];
    }
    
    /**
     * Checks if this is a double (all dice have the same value)
     * @return true if all dice show the same value
     */
    public boolean isDouble() {
        if (values.length <= 1) {
            return false;
        }
        int firstValue = values[0];
        for (int i = 1; i < values.length; i++) {
            if (values[i] != firstValue) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiceRoll diceRoll = (DiceRoll) o;
        return total == diceRoll.total && Arrays.equals(values, diceRoll.values);
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(total);
        result = 31 * result + Arrays.hashCode(values);
        return result;
    }
    
    @Override
    public String toString() {
        return "DiceRoll{values=" + Arrays.toString(values) + 
               ", total=" + total + "}";
    }
} 