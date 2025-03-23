package board;

import java.util.Objects;

/**
 * Value object representing a position on the board.
 * Immutable once created.
 */
public class Position {
    private final int value;
    private final IBoard.PositionType type;
    
    /**
     * Constructor for a Position
     * @param value The numeric value of the position
     * @param type The type of position (HOME, MAIN, TAIL, END)
     */
    public Position(int value, IBoard.PositionType type) {
        this.value = value;
        this.type = type;
    }
    
    /**
     * Get the numeric value of the position
     * @return Position value
     */
    public int getValue() { 
        return value; 
    }
    
    /**
     * Get the type of position
     * @return Position type
     */
    public IBoard.PositionType getType() { 
        return type; 
    }
    
    /**
     * Checks if this position is a tail position
     * @return true if this is a tail position
     */
    public boolean isTail() {
        return type == IBoard.PositionType.TAIL;
    }
    
    /**
     * Checks if this position is a home position
     * @return true if this is a home position
     */
    public boolean isHome() {
        return type == IBoard.PositionType.HOME;
    }
    
    /**
     * Checks if this position is an end position
     * @return true if this is an end position
     */
    public boolean isEnd() {
        return type == IBoard.PositionType.END;
    }
    
    /**
     * Checks if this position is on the main board
     * @return true if this is a main board position
     */
    public boolean isMain() {
        return type == IBoard.PositionType.MAIN;
    }
    
    /**
     * Checks if two Position objects are equal by comparing their values and types
     * @param o The object to compare with
     * @return true if the objects have the same value and type, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        // First check if we're comparing the same object
        if (o == this) {
            return true;
        }
        
        // Make sure the other object exists and is a Position
        if (o == null) {
            return false;
        }
        if (!(o instanceof Position)) {
            return false;
        }
        
        Position other = (Position) o;
        if (this.value != other.value) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }
    
    @Override
    public String toString() {
        return "Position{value=" + value + ", type=" + type + "}";
    }
} 