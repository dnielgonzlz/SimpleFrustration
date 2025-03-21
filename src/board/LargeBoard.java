package board;

import players.Player;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the large board (36 positions, 6 tail slots).
 */
public class LargeBoard implements IBoard {
    private int numPlayers = 2; // Default to 2 players
    private static final int MAIN_BOARD_SIZE = 36;
    private static final int TAIL_SIZE = 6; // Including END position
    
    // Map to store home positions for each player color
    private final Map<String, Integer> homePositions;
    // Map to store tail entry positions for each player color
    private final Map<String, Integer> tailEntryPositions;
    
    public LargeBoard() {
        this(2); // Default constructor with 2 players
    }
    
    /**
     * Constructor that specifies the number of players
     * @param numPlayers Number of players (2 or 4)
     */
    public LargeBoard(int numPlayers) {
        this.numPlayers = numPlayers;
        // Initialize home positions
        homePositions = new HashMap<>();
        homePositions.put("Red", 1); // Always position 1
        
        // Position depends on number of players
        if (numPlayers == 4) {
            homePositions.put("Blue", 10);
            homePositions.put("Green", 19);
            homePositions.put("Yellow", 27);
        } else {
            homePositions.put("Blue", 19);
        }
        
        // Initialize tail entry positions (position before home)
        tailEntryPositions = new HashMap<>();
        tailEntryPositions.put("Red", 36); // Position before home position 1
        
        // Tail entries also depend on number of players
        if (numPlayers == 4) {
            tailEntryPositions.put("Blue", 9);  // Position before home position 10
            tailEntryPositions.put("Green", 18); // Position before home position 19
            tailEntryPositions.put("Yellow", 26); // Position before home position 27
        } else {
            tailEntryPositions.put("Blue", 18); // Position before home position 19
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
        
        System.out.println("[DEBUG LargeBoard] Calculating new position for " + player.getColor() + 
                         " from position " + currentPosition + 
                         " with dice roll " + diceRoll);
        System.out.println("[DEBUG LargeBoard] Player home: " + player.getHomePosition() + 
                         ", end: " + player.getEndPosition() + 
                         ", tail entry: " + tailEntryPositions.get(player.getColor()));
        
        // If at home, just move directly by dice roll
        if (currentPosition == player.getHomePosition()) {
            newPosition = currentPosition + diceRoll;
            // Wrap around the board if needed
            if (newPosition > MAIN_BOARD_SIZE) {
                newPosition = (newPosition - 1) % MAIN_BOARD_SIZE + 1;
            }
            System.out.println("[DEBUG LargeBoard] Moving from HOME. New position: " + newPosition);
            return newPosition;
        }
        
        // Special case: Already at tail entry position
        int tailEntryPos = tailEntryPositions.get(player.getColor());
        if (currentPosition == tailEntryPos) {
            // Enter the tail directly with 1 step
            System.out.println("[DEBUG LargeBoard] At tail entry position. Moving into tail position " + (MAIN_BOARD_SIZE + 1));
            return MAIN_BOARD_SIZE + 1;
        }
        
        // If on the main board
        if (currentPosition <= MAIN_BOARD_SIZE) {
            // Calculate potential new position
            newPosition = currentPosition + diceRoll;
            System.out.println("[DEBUG LargeBoard] Initial calculation on main board: " + newPosition);
            
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
                // But still calculate and return the actual position to let rules handle it
                int cappedStepsIntoTail = Math.min(stepsIntoTail, TAIL_SIZE);
                
                // Position in tail - allow overshooting for ExactEndRule to handle
                newPosition = MAIN_BOARD_SIZE + stepsIntoTail;
                System.out.println("[DEBUG LargeBoard] Passed tail entry. Distance to tail: " + distanceToTailEntry + 
                                 ", Steps into tail: " + stepsIntoTail + 
                                 ", Capped steps: " + cappedStepsIntoTail +
                                 ", New position in tail: " + newPosition);
                return newPosition;
            }
            
            // Otherwise, just move on the main board, with wraparound
            if (newPosition > MAIN_BOARD_SIZE) {
                int oldPos = newPosition;
                newPosition = (newPosition - 1) % MAIN_BOARD_SIZE + 1;
                System.out.println("[DEBUG LargeBoard] Wrapped around board from " + oldPos + " to " + newPosition);
            }
            System.out.println("[DEBUG LargeBoard] Final position on main board: " + newPosition);
            return newPosition;
        }
        
        // If already in the tail
        int tailPosition = currentPosition - MAIN_BOARD_SIZE;
        int newTailPosition = tailPosition + diceRoll;
        int finalPosition = MAIN_BOARD_SIZE + newTailPosition;
        
        System.out.println("[DEBUG LargeBoard] Moving in tail from tail position " + tailPosition + 
                         " to tail position " + newTailPosition + 
                         " (actual position " + finalPosition + ")");
        System.out.println("[DEBUG LargeBoard] END position is " + player.getEndPosition() + 
                         ", max valid tail position is " + TAIL_SIZE);
        
        // Return the actual new position even if it overshoots the END
        // This allows the ExactEndRule decorator to handle bounce-back if needed
        return finalPosition;
    }
    
    private boolean passedTailEntry(int currentPosition, int newPosition, Player player) {
        int tailEntryPos = tailEntryPositions.get(player.getColor());
        
        System.out.println("[DEBUG LargeBoard] Checking if passed tail entry. Current: " + currentPosition + 
                         ", New: " + newPosition + 
                         ", Tail entry: " + tailEntryPos);
        
        // Check if already at tail entry
        if (currentPosition == tailEntryPos) {
            System.out.println("[DEBUG LargeBoard] Already at tail entry position. Passed: true");
            return true;
        }
        
        // If we're moving to a position less than or equal to the main board size,
        // check if we passed the tail entry during movement
        if (newPosition <= MAIN_BOARD_SIZE) {
            // Handle normal movement on main board
            // Check if we passed the tail entry position during this move
            if (tailEntryPos > player.getHomePosition()) {
                // Tail entry is after home position
                boolean passed = (currentPosition < tailEntryPos && newPosition >= tailEntryPos);
                System.out.println("[DEBUG LargeBoard] Tail entry after home. Passed: " + passed);
                return passed;
            } else {
                // Tail entry is before home position (wrap around case)
                if (currentPosition < tailEntryPos) {
                    // Simple case - we pass the tail entry without wrapping
                    boolean passed = newPosition >= tailEntryPos;
                    System.out.println("[DEBUG LargeBoard] Tail entry before home (current < tail). Passed: " + passed);
                    return passed;
                } else {
                    // Check if we've wrapped around and passed the tail entry
                    int wrappedPosition = (newPosition - 1) % MAIN_BOARD_SIZE + 1;
                    boolean passed = wrappedPosition >= player.getHomePosition() && 
                           wrappedPosition <= tailEntryPos;
                    System.out.println("[DEBUG LargeBoard] Tail entry before home (current >= tail). Wrapped position: " + 
                                     wrappedPosition + ", Passed: " + passed);
                    return passed;
                }
            }
        } else {
            // If new position is beyond main board size, we need special handling
            // First normalize the position to its wrapped equivalent
            int wrappedPosition = (newPosition - 1) % MAIN_BOARD_SIZE + 1;
            
            System.out.println("[DEBUG LargeBoard] New position beyond board size. Wrapped position: " + wrappedPosition);
            
            // Now check if we would have passed the tail entry
            boolean passed;
            if (tailEntryPos > player.getHomePosition()) {
                passed = (currentPosition < tailEntryPos && wrappedPosition >= tailEntryPos) || 
                       (currentPosition > wrappedPosition);
                System.out.println("[DEBUG LargeBoard] Case 1: Tail entry after home. Passed: " + passed);
            } else {
                passed = (currentPosition < tailEntryPos && wrappedPosition >= tailEntryPos) || 
                       (currentPosition > tailEntryPos && wrappedPosition >= player.getHomePosition());
                System.out.println("[DEBUG LargeBoard] Case 2: Tail entry before home. Passed: " + passed);
            }
            return passed;
        }
    }
    
    // Note: This method was removed as its logic has been inlined into calculateNewPosition
    
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
        return tailEntryPositions.getOrDefault(color, 36); // Default to red's position
    }
}
