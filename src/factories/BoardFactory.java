package factories;

import board.BasicBoard;
import board.IBoard;
import board.LargeBoard;

/**
 * Factory for creating the appropriate board based on configuration.
 */
public class BoardFactory {
    
    /**
     * Create a board based on the specified type and number of players
     * @param boardType "basic" or "large"
     * @param numPlayers Number of players (2 or 4)
     * @return The appropriate board implementation
     */
    public IBoard createBoard(String boardType, int numPlayers) {
        if ("large".equalsIgnoreCase(boardType)) {
            return new LargeBoard(numPlayers);
        } else {
            return new BasicBoard(numPlayers); // Default to basic board
        }
    }
    
    /**
     * Create a board based on the specified type (default to 2 players)
     * @param boardType "basic" or "large"
     * @return The appropriate board implementation
     */
    public IBoard createBoard(String boardType) {
        return createBoard(boardType, 2); // Default to 2 players
    }
}
