package factories;

import board.IBoard;
import players.Player;

/**
 * Factory for creating players with the correct home/end positions.
 */
public class PlayerFactory {
    
    /**
     * Create a player based on their color and the board
     * @param color Player color
     * @param board The game board
     * @return A new Player instance
     */
    public Player createPlayer(String color, IBoard board) {
        int homePosition = board.getHomePosition(color);
        int endPosition = board.getMainBoardSize() + board.getTailSize(); // End position is last tail position
        
        return new Player(color, homePosition, endPosition);
    }
}
