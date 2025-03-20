package factories;

import board.BasicBoard;
import board.IBoard;
import board.LargeBoard;

/**
 * Factory for creating the appropriate board based on configuration.
 */
public class BoardFactory {
    
    /**
     * Create a board based on the specified type
     * @param boardType "basic" or "large"
     * @return The appropriate board implementation
     */
    public IBoard createBoard(String boardType) {
        if ("large".equalsIgnoreCase(boardType)) {
            return new LargeBoard();
        } else {
            return new BasicBoard(); // Default to basic board
        }
    }
}
