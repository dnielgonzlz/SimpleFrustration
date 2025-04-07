package players;

import util.ConsoleColors;

/**
 * Represents a player in the game.
 */
public class Player {
    private final String color;
    private int currentPosition;
    private final int homePosition;
    private final int endPosition;
    private int totalMoves;
    
    /**
     * Constructor for a player
     * @param color Player color
     * @param homePosition Starting home position
     * @param endPosition Winning end position
     */
    public Player(String color, int homePosition, int endPosition) {
        this.color = color;
        this.homePosition = homePosition;
        this.currentPosition = homePosition;
        this.endPosition = endPosition;
        this.totalMoves = 0;
    }
    
    /**
     * Get the player's color
     * @return Player's color
     */
    public String getColor() {
        return color;
    }
    
    /**
     * Get the player's color with ANSI color formatting
     * @return Colored text of the player's color name
     */
    public String getColoredDisplay() {
        return ConsoleColors.colorize(color, color);
    }
    
    /**
     * Get the player's current position
     * @return Current position
     */
    public int getCurrentPosition() {
        return currentPosition;
    }
    
    /**
     * Get the player's home position
     * @return Home position
     */
    public int getHomePosition() {
        return homePosition;
    }
    
    /**
     * Get the player's end position
     * @return End position
     */
    public int getEndPosition() {
        return endPosition;
    }
    
    /**
     * Get the total number of moves made by this player
     * @return Total moves
     */
    public int getTotalMoves() {
        return totalMoves;
    }
    
    /**
     * Set the total number of moves (used for undo)
     * @param totalMoves New total moves count
     */
    public void setTotalMoves(int totalMoves) {
        this.totalMoves = totalMoves;
    }
    
    /**
     * Move the player to a new position
     * @param newPosition The new position
     */
    public void move(int newPosition) {
        System.out.println("[DEBUG Player] " + color + " moving from position " + currentPosition + " to " + newPosition);
        this.currentPosition = newPosition;
        this.totalMoves++;
    }
    
    /**
     * Set the player's position without incrementing move counter (for undo)
     * @param position The position to set
     */
    public void setPosition(int position) {
        System.out.println("[DEBUG Player] " + color + " position directly set from " + currentPosition + " to " + position + " (undo operation)");
        this.currentPosition = position;
    }
    
    /**
     * Reset the player to their home position
     */
    public void resetToHome() {
        System.out.println("[DEBUG Player] " + color + " reset from position " + currentPosition + " to HOME at position " + homePosition);
        this.currentPosition = homePosition;
    }
    
    /**
     * Check if the player has reached their end position
     * @return True if at end position
     */
    public boolean hasWon() {
        return currentPosition == endPosition;
    }
    
    /**
     * Get a string representation of the player's position
     * @param isTailPosition Whether the position is in a tail
     * @return String describing the position
     */
    public String getPositionDisplay(boolean isTailPosition) {
        if (currentPosition == homePosition) {
            return "HOME (Position " + homePosition + ")";
        } else if (currentPosition == endPosition) {
            return "END";
        } else if (isTailPosition) {
            int mainBoardSize = endPosition - tailSize();
            int tailPos = currentPosition - mainBoardSize;
            return "TAIL (Tail Position " + tailPos + ")";
        } else {
            return "Position " + currentPosition;
        }
    }
    
    /**
     * Calculate the tail size based on home and end positions
     * @return The size of the tail
     */
    private int tailSize() {
        // Calculate tail size based on the difference between end position and main board size
        // Basic board: mainBoardSize = 18, tailSize = 3, endPosition = 21
        // Large board: mainBoardSize = 36, tailSize = 6, endPosition = 42
        if (endPosition == 21) { // Basic board
            return 3;
        } else { // Large board
            return 6;
        }
    }
}
