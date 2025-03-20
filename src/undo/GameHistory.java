package undo;

import players.Player;
import players.PlayerManager;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * Manages saved game states for undo operations.
 */
public class GameHistory {
    private final Deque<GameStateMemento> history;
    
    /**
     * Constructor initializing an empty history
     */
    public GameHistory() {
        this.history = new ArrayDeque<>();
    }
    
    /**
     * Save the current game state
     * @param players List of all players
     * @param currentPlayerIndex Index of the current player
     * @param hitOccurred Whether a hit occurred in the last move
     * @param hitVictimColor Color of the player who was hit
     * @param gameOver Whether the game was over
     * @param winnerColor Color of the winning player 
     */
    public void saveState(List<Player> players, int currentPlayerIndex, boolean hitOccurred, String hitVictimColor, boolean gameOver, String winnerColor) {
        GameStateMemento memento = new GameStateMemento(players, currentPlayerIndex, hitOccurred, hitVictimColor, gameOver, winnerColor);
        history.push(memento);
    }
    
    /**
     * Restore the previous game state
     * @param playerManager The player manager to update
     * @return The restored GameStateMemento, or null if history is empty
     */
    public GameStateMemento undo(PlayerManager playerManager) {
        if (history.isEmpty()) {
            return null;
        }
        
        GameStateMemento memento = history.pop();
        List<Player> players = playerManager.getAllPlayers();
        
        // Restore player positions
        Map<String, Integer> positions = memento.getPlayerPositions();
        Map<String, Integer> moveCounts = memento.getPlayerMoveCounts();
        
        for (Player player : players) {
            int position = positions.get(player.getColor());
            // Set position without incrementing move counter
            player.setPosition(position);
            
            // Restore move count if available
            Integer moveCount = moveCounts.get(player.getColor());
            if (moveCount != null) {
                player.setTotalMoves(moveCount);
            }
        }
        
        // Restore current player index
        playerManager.setCurrentPlayerIndex(memento.getCurrentPlayerIndex());
        
        return memento;
    }
    
    /**
     * Check if undo is available
     * @return true if there are states to undo
     */
    public boolean canUndo() {
        return !history.isEmpty();
    }
    
    /**
     * Clear all history
     */
    public void clear() {
        history.clear();
    }
}
