package board;

import players.Player;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the basic board (18 positions, 3 tail slots).
 */
public class BasicBoard implements IBoard {
    private int numPlayers = 2; // Default to 2 players
    private static final int MAIN_BOARD_SIZE = 18;
    private static final int TAIL_SIZE = 3;
    
  
    private final Map<String, Integer> homePositions;
    private final Map<String, Integer> tailEntryPositions;
    
    public BasicBoard() {
        this(2); // Default constructor with 2 players
    }
    
    /**
     * Constructor that specifies the number of players
     * @param numPlayers Number of players (2 or 4)
     */
    public BasicBoard(int numPlayers) {
        this.numPlayers = numPlayers;
        // Initialize home positions
        homePositions = new HashMap<>();
        homePositions.put("Red", 1); // Always position 1
        
        // Blue position depends on number of players
        if (numPlayers == 4) {
            homePositions.put("Blue", 5);
            homePositions.put("Green", 10);
            homePositions.put("Yellow", 14);
        } else {
            homePositions.put("Blue", 10);
        }
        
        // Initialize tail entry positions (position before home)
        tailEntryPositions = new HashMap<>();
        tailEntryPositions.put("Red", 18); // Position before home position 1
        tailEntryPositions.put("Blue", 9); // Position before home position 10
        
        // Tail entries also depend on number of players
        if (numPlayers == 4) {
            tailEntryPositions.put("Blue", 4);  // Position before home position 5
            tailEntryPositions.put("Green", 9); // Position before home position 10
            tailEntryPositions.put("Yellow", 13); // Position before home position 14
        } else {
            tailEntryPositions.put("Blue", 9); // Position before home position 10
        }
    }
    
    @Override
    public PositionType getPositionType(int position, Player player) {
        if (position == player.getHomePosition()) {
            return PositionType.HOME;
        } else if (position == player.getEndPosition()) {
            return PositionType.END;
        } else if (position > MAIN_BOARD_SIZE) { // Position is in tail
            // For tail positions, we need to check if this is the player's tail
            int tailOffset = position - MAIN_BOARD_SIZE;
            // Calculate expected end position based on player's home position
            int expectedEndPos = MAIN_BOARD_SIZE + TAIL_SIZE;
            if (player.getEndPosition() == expectedEndPos) {
                return PositionType.TAIL;
            } else {
                // Player is in another player's tail
                return PositionType.MAIN;
            }
        } else {
            return PositionType.MAIN;
        }
    }
    
    @Override
    public int calculateNewPosition(Player player, int diceRoll) {
        int currentPosition = player.getCurrentPosition();
        int newPosition = currentPosition;
        
        // If at home, just move directly by dice roll
        if (currentPosition == player.getHomePosition()) {
            newPosition = currentPosition + diceRoll;
            // Wrap around the board if needed
            if (newPosition > MAIN_BOARD_SIZE) {
                newPosition = (newPosition - 1) % MAIN_BOARD_SIZE + 1;
            }
            return newPosition;
        }
        
        // Special case: Already at tail entry position for this player
        int tailEntryPos = tailEntryPositions.get(player.getColor());
        if (currentPosition == tailEntryPos) {
            // Enter the tail directly with 1 step
            return MAIN_BOARD_SIZE + 1;
        }
        
        // If on the main board
        if (currentPosition <= MAIN_BOARD_SIZE) {
            // Calculate potential new position
            newPosition = currentPosition + diceRoll;
            
            // Check if we need to enter the tail
            if (passedTailEntry(currentPosition, newPosition, player)) {
                // Calculate distance to tail entry
                int distanceToTailEntry;
                
                if (currentPosition < tailEntryPos) {
                    distanceToTailEntry = tailEntryPos - currentPosition;
                } else {
                    // We need to go around the board
                    distanceToTailEntry = (MAIN_BOARD_SIZE - currentPosition) + tailEntryPos;
                }
                
                // Calculate steps into tail (dice roll minus steps to reach tail entry)
                int stepsIntoTail = diceRoll - distanceToTailEntry;
                
                // Ensure at least 1 step into tail
                stepsIntoTail = Math.max(stepsIntoTail, 1);
                
                // Cannot go beyond END (tail position TAIL_SIZE)
                stepsIntoTail = Math.min(stepsIntoTail, TAIL_SIZE);
                
                // Position in tail
                return MAIN_BOARD_SIZE + stepsIntoTail;
            }
            
            // Otherwise, just move on the main board, with wraparound
            if (newPosition > MAIN_BOARD_SIZE) {
                newPosition = (newPosition - 1) % MAIN_BOARD_SIZE + 1;
            }
            return newPosition;
        }
        
        // If already in the tail
        int tailPosition = currentPosition - MAIN_BOARD_SIZE;
        int newTailPosition = tailPosition + diceRoll;
        
        // When in the tail with basic rules, overshooting should be treated as reaching END
        // The ExactEndRule decorator will handle bounce-back if needed
        if (newTailPosition > TAIL_SIZE) {
            // For basic rules, we need to set position to END
            return MAIN_BOARD_SIZE + TAIL_SIZE; // END position
        }
        
        return MAIN_BOARD_SIZE + newTailPosition;
    }
    
    private boolean passedTailEntry(int currentPosition, int newPosition, Player player) {
        int tailEntryPos = tailEntryPositions.get(player.getColor());
        
        // First check if already at tail entry
        if (currentPosition == tailEntryPos) {
            return true;
        }
        
        // If we're moving to a position less than or equal to the main board size,
        // check if we passed the tail entry during movement
        if (newPosition <= MAIN_BOARD_SIZE) {
            // Handle normal movement on main board
            // Check if we passed the tail entry position during this move
            if (tailEntryPos > player.getHomePosition()) {
                // Tail entry is after home position
                return (currentPosition < tailEntryPos && newPosition >= tailEntryPos);
            } else {
                // Tail entry is before home position (wrap around case)
                if (currentPosition < tailEntryPos) {
                    // Simple case - we pass the tail entry without wrapping
                    return newPosition >= tailEntryPos;
                } else {
                    // Check if we've wrapped around and passed the tail entry
                    int wrappedPosition = (newPosition - 1) % MAIN_BOARD_SIZE + 1;
                    return wrappedPosition >= player.getHomePosition() && 
                           wrappedPosition <= tailEntryPos;
                }
            }
        } else {
            // If new position is beyond main board size, we need special handling
            // First normalize the position to its wrapped equivalent
            int wrappedPosition = (newPosition - 1) % MAIN_BOARD_SIZE + 1;
            
            // Now check if we would have passed the tail entry
            if (tailEntryPos > player.getHomePosition()) {
                return (currentPosition < tailEntryPos && wrappedPosition >= tailEntryPos) || 
                       (currentPosition > wrappedPosition);
            } else {
                return (currentPosition < tailEntryPos && wrappedPosition >= tailEntryPos) || 
                       (currentPosition > tailEntryPos && wrappedPosition >= player.getHomePosition());
            }
        }
    }
    

    @Override
    public int getMainBoardSize() {
        return MAIN_BOARD_SIZE;
    }
    
    @Override
    public int getTailSize() {
        return TAIL_SIZE;
    }
    
    @Override
    public int getHomePosition(String color) {
        return homePositions.getOrDefault(color, 1); // Default to red's position
    }
    
    @Override
    public int getTailEntryPosition(String color) {
        return tailEntryPositions.getOrDefault(color, 18); // Default to red's position
    }
}
