package factories;

import board.IBoard;
import players.Player;
import players.PlayerColor;

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
    public Player createPlayer(PlayerColor color, IBoard board) {
        int homePosition = board.getHomePosition(color.getValue());
        int endPosition = board.getMainBoardSize() + board.getTailSize(); // End position is last tail position
        
        return new Player(color, homePosition, endPosition);
    }
    
    /**
     * Create a player based on their color string and the board
     * @param colorString Player color as a string
     * @param board The game board
     * @return A new Player instance
     * @throws IllegalArgumentException if color is invalid
     */
    public Player createPlayer(String colorString, IBoard board) {
        PlayerColor color = new PlayerColor(colorString);
        return createPlayer(color, board);
    }
}
