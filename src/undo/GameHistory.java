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
        System.out.println("[DEBUG History] Saving game state. Current player index: " + currentPlayerIndex + 
                          ", Hit occurred: " + hitOccurred + 
                          ", Hit victim: " + hitVictimColor);
        
        // Log player positions being saved
        System.out.println("[DEBUG History] Saving player positions:");
        for (Player player : players) {
            System.out.println("[DEBUG History]   " + player.getColor() + 
                               ": position " + player.getCurrentPosition() + 
                               ", moves " + player.getTotalMoves());
        }
        
        GameStateMemento memento = new GameStateMemento(players, currentPlayerIndex, hitOccurred, hitVictimColor, gameOver, winnerColor);
        history.push(memento);
        
        System.out.println("[DEBUG History] State saved. History size: " + history.size());
    }
    
    /**
     * Add a pre-created memento to the history
     * @param memento The memento to add
     */
    public void addMemento(GameStateMemento memento) {
        history.push(memento);
    }
    
    /**
     * Restore the previous game state
     * @param playerManager The player manager to update
     * @return The restored GameStateMemento, or null if history is empty
     */
    public GameStateMemento undo(PlayerManager playerManager) {
        if (history.isEmpty()) {
            System.out.println("[DEBUG History] Cannot undo - history is empty");
            return null;
        }
        
        System.out.println("[DEBUG History] Undoing to previous state. History size before: " + history.size());
        
        GameStateMemento memento = history.pop();
        List<Player> players = playerManager.getAllPlayers();
        
        // Restore player positions
        Map<String, Integer> positions = memento.getPlayerPositions();
        Map<String, Integer> moveCounts = memento.getPlayerMoveCounts();
        
        System.out.println("[DEBUG History] Restoring player positions from memento:");
        for (String color : positions.keySet()) {
            System.out.println("[DEBUG History]   " + color + 
                              ": position " + positions.get(color) + 
                              ", moves " + (moveCounts.containsKey(color) ? moveCounts.get(color) : "N/A"));
        }
        
        for (Player player : players) {
            int oldPosition = player.getCurrentPosition();
            int position = positions.get(player.getColor());
            
            // Set position without incrementing move counter
            player.setPosition(position);
            
            // Restore move count if available
            Integer moveCount = moveCounts.get(player.getColor());
            if (moveCount != null) {
                player.setTotalMoves(moveCount);
            }
            
            System.out.println("[DEBUG History] Restored " + player.getColor() + 
                              " from position " + oldPosition + 
                              " to position " + position);
        }
        
        // Restore current player index
        int oldIndex = playerManager.getCurrentPlayerIndex();
        playerManager.setCurrentPlayerIndex(memento.getCurrentPlayerIndex());
        System.out.println("[DEBUG History] Restored current player index from " + 
                          oldIndex + " to " + memento.getCurrentPlayerIndex());
        
        System.out.println("[DEBUG History] Undo complete. History size after: " + history.size());
        
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
