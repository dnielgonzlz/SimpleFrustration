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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return value == position.value && type == position.type;
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