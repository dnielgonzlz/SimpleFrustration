package players;

import board.IBoard;
import factories.PlayerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all players in the game.
 */
public class PlayerManager {
    private final List<Player> players;
    private int currentPlayerIndex;
    
    /**
     * Constructor initializing an empty player list
     */
    public PlayerManager() {
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
    }
    
    /**
     * Create players based on configuration
     * @param numPlayers Number of players (2 or 4)
     * @param board The game board
     */
    public void createPlayers(int numPlayers, IBoard board) {
        PlayerFactory factory = new PlayerFactory();
        
        // Clear any existing players
        players.clear();
        
        // Always add Red and Blue
        players.add(factory.createPlayer(PlayerColor.RED, board));
        players.add(factory.createPlayer(PlayerColor.BLUE, board));
        
        // Add Green and Yellow for 4-player games
        if (numPlayers == 4) {
            players.add(factory.createPlayer(PlayerColor.GREEN, board));
            players.add(factory.createPlayer(PlayerColor.YELLOW, board));
        }
        
        // Reset to first player
        currentPlayerIndex = 0;
    }
    
    /**
     * Get the current player whose turn it is
     * @return The current player
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
    
    /**
     * Get the index of the current player
     * @return Current player index
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
    
    /**
     * Set the current player index
     * @param index New player index
     */
    public void setCurrentPlayerIndex(int index) {
        if (index >= 0 && index < players.size()) {
            currentPlayerIndex = index;
        }
    }
    
    /**
     * Switch to the next player
     */
    public void switchPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }
    
    /**
     * Get all players
     * @return List of all players
     */
    public List<Player> getAllPlayers() {
        return new ArrayList<>(players);
    }
    
    /**
     * Check if a player exists at a specific position
     * @param position Position to check
     * @param excludePlayer Player to exclude from check
     * @return The player at that position, or null if none
     */
    public Player getPlayerAtPosition(int position, Player excludePlayer) {
        System.out.println("[DEBUG PlayerManager] Checking for players at position " + position + 
                          " (excluding " + excludePlayer.getColorString() + ")");
        
        for (Player player : players) {
            System.out.println("[DEBUG PlayerManager]   Checking " + player.getColorString() + 
                             " at position " + player.getCurrentPosition());
            
            if (player != excludePlayer && player.getCurrentPosition() == position) {
                System.out.println("[DEBUG PlayerManager]   Found " + player.getColorString() + 
                                 " at position " + position);
                return player;
            }
        }
        
        System.out.println("[DEBUG PlayerManager]   No player found at position " + position);
        return null;
    }
    
    /**
     * Get the total number of turns taken by all players
     * @return Total turns
     */
    public int getTotalTurns() {
        int total = 0;
        for (Player player : players) {
            total += player.getTotalMoves();
        }
        return total;
    }
}
