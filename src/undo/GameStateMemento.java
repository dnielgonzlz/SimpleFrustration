package undo;

import players.Player;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a saved game state for undo operations.
 */
public class GameStateMemento {
    private final Map<String, Integer> playerPositions;
    private final Map<String, Integer> playerMoveCounts; // Store move counts
    private final int currentPlayerIndex;
    private final int turnCount;
    private final boolean hitOccurred;
    private final String hitVictimColor;
    private final boolean gameOver;        
    private final String winnerColor;
    private final Map<String, Integer> previousPositions; // Track previous positions for better undo messaging
    
    /**
     * Constructor for a game state memento
     * @param players List of players
     * @param currentPlayerIndex Index of the current player
     * @param hitOccurred Whether a hit occurred in the last move
     * @param hitVictimColor Color of the player who was hit (if any)
     * @param gameOver Whether the game was over
     * @param winnerColor Color of the winning player (if any)
     */
    public GameStateMemento(List<Player> players, int currentPlayerIndex, boolean hitOccurred, String hitVictimColor, boolean gameOver, String winnerColor) {
        this.playerPositions = new HashMap<>();
        this.playerMoveCounts = new HashMap<>();
        this.previousPositions = new HashMap<>(); // Initialize previous positions map
        this.currentPlayerIndex = currentPlayerIndex;
        this.hitOccurred = hitOccurred;
        this.hitVictimColor = hitVictimColor;
        this.gameOver = gameOver;
        this.winnerColor = winnerColor;
        
        // Calculate turn count and save player positions and move counts
        int turnCount = 0;
        for (Player player : players) {
            this.playerPositions.put(player.getColor(), player.getCurrentPosition());
            this.playerMoveCounts.put(player.getColor(), player.getTotalMoves());
            turnCount += player.getTotalMoves();
        }
        this.turnCount = turnCount;
    }
    
    /**
     * Get the saved position of a player
     * @param color Player color
     * @return Saved position
     */
    public int getPlayerPosition(String color) {
        return playerPositions.getOrDefault(color, -1);
    }

    /**
     * Get the player move counts
     * @return Map of player colors to move counts
     */
    public Map<String, Integer> getPlayerMoveCounts() {
        return new HashMap<>(playerMoveCounts);
    }
    
    /**
     * Get the index of the current player
     * @return Current player index
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
    
    /**
     * Get the total turn count
     * @return Turn count
     */
    public int getTurnCount() {
        return turnCount;
    }
    
    /**
     * Check if a hit occurred in the saved state
     * @return true if a hit occurred
     */
    public boolean isHitOccurred() {
        return hitOccurred;
    }
    
    /**
     * Get the color of the player who was hit
     * @return Player color or null if no hit
     */
    public String getHitVictimColor() {
        return hitVictimColor;
    }
    
    /**
     * Get all player positions
     * @return Map of player colors to positions
     */
    public Map<String, Integer> getPlayerPositions() {
        return new HashMap<>(playerPositions);
    }
    
    /**
     * Check if the game was over in the saved state
     * @return true if game was over
     */
    public boolean getGameOver() {
        return gameOver;
    }
    
    /**
     * Get the winner's color from the saved state
     * @return Winner's color or null if none
     */
    public String getWinnerColor() {
        return winnerColor;
    }
    
    /**
     * Set the previous position for a player
     * @param color Player color
     * @param position Previous position
     */
    public void setPreviousPosition(String color, int position) {
        this.previousPositions.put(color, position);
    }
    
    /**
     * Get the previous position for a player
     * @param color Player color
     * @return Previous position or current position if not set
     */
    public int getPreviousPosition(String color) {
        return previousPositions.getOrDefault(color, getPlayerPosition(color));
    }
    
    /**
     * Get all previous positions
     * @return Map of player colors to previous positions
     */
    public Map<String, Integer> getPreviousPositions() {
        return new HashMap<>(previousPositions);
    }
}
