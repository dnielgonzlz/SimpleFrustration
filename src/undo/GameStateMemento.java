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
    
    /**
     * Constructor for a game state memento
     * @param players List of players
     * @param currentPlayerIndex Index of the current player
     * @param hitOccurred Whether a hit occurred in the last move
     * @param hitVictimColor Color of the player who was hit (if any)
     */
    public GameStateMemento(List<Player> players, int currentPlayerIndex, boolean hitOccurred, String hitVictimColor) {
        this.playerPositions = new HashMap<>();
        this.playerMoveCounts = new HashMap<>();
        this.currentPlayerIndex = currentPlayerIndex;
        this.hitOccurred = hitOccurred;
        this.hitVictimColor = hitVictimColor;
        
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
}
