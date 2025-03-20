package board;

import players.Player;

/**
 * Interface defining board functionality.
 */
public interface IBoard {
    /**
     * Types of positions on the board
     */
    enum PositionType {
        HOME,
        MAIN,
        TAIL,
        END
    }
    
    /**
     * Gets the type of a position
     * @param position The position to check
     * @param player The player for whom to check the position type
     * @return The type of the position
     */
    PositionType getPositionType(int position, Player player);
    
    /**
     * Calculates a new position based on current position and dice roll
     * @param player The player moving
     * @param diceRoll The dice roll value
     * @return The new position
     */
    int calculateNewPosition(Player player, int diceRoll);
    
    /**
     * Gets the total number of positions on the main board
     * @return Number of main board positions
     */
    int getMainBoardSize();
    
    /**
     * Gets the total number of positions in the tail
     * @return Number of tail positions (including END)
     */
    int getTailSize();
    
    /**
     * Gets the home position for a player by color
     * @param color The player's color
     * @return The home position
     */
    int getHomePosition(String color);
    
    /**
     * Gets the position where a player should enter their tail
     * @param color The player's color
     * @return The tail entry position
     */
    int getTailEntryPosition(String color);
}
