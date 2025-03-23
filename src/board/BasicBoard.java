package board;

import players.Player;
import java.util.HashMap;
import java.util.Map;

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
        homePositions = new HashMap<>();
        tailEntryPositions = new HashMap<>();
        
        // Initialize base positions for Red that are same in both cases
        homePositions.put("Red", 1);
        tailEntryPositions.put("Red", 18); // Position before home position 1
        
        switch(numPlayers) {
            case 4:
                // Initialize positions for 4 player game
                homePositions.put("Blue", 5);
                homePositions.put("Green", 10); 
                homePositions.put("Yellow", 14);
                
                tailEntryPositions.put("Blue", 4);  // Position before home position 5
                tailEntryPositions.put("Green", 9); // Position before home position 10
                tailEntryPositions.put("Yellow", 13); // Position before home position 14
                break;
                
            case 2:
                // Initialize positions for 2 player game
                homePositions.put("Blue", 10);
                tailEntryPositions.put("Blue", 9); // Position before home position 10
                break;
                
            default:
                throw new IllegalArgumentException("Number of players must be 2 or 4");
        }
    }
    
    @Override
    public PositionType getPositionType(int position, Player player) {
        // Check special positions first
        if (position == player.getHomePosition()) {
            return PositionType.HOME;
        }
        
        if (position == player.getEndPosition()) {
            return PositionType.END;
        }
        
        // Check if position is in a tail section
        if (position > MAIN_BOARD_SIZE) {
            // Only return TAIL if this is the player's own tail section
            return (player.getEndPosition() == MAIN_BOARD_SIZE + TAIL_SIZE) ? PositionType.TAIL : PositionType.MAIN;
        }
        
        // Default case - position is on main board
        return PositionType.MAIN;
    }
    
    @Override
    public int calculateNewPosition(Player player, int diceRoll) {
        int currentPosition = player.getCurrentPosition();
        int newPosition = currentPosition;
        
        System.out.println("[DEBUG Board] Calculating new position for " + player.getColor() + 
                         " from position " + currentPosition + 
                         " with dice roll " + diceRoll);
        System.out.println("[DEBUG Board] Player home: " + player.getHomePosition() + 
                         ", end: " + player.getEndPosition() + 
                         ", tail entry: " + tailEntryPositions.get(player.getColor()));
        
        // If at home, just move directly by dice roll
        if (currentPosition == player.getHomePosition()) {
            newPosition = currentPosition + diceRoll;
            // Wrap around the board if needed
            if (newPosition > MAIN_BOARD_SIZE) {
                newPosition = (newPosition - 1) % MAIN_BOARD_SIZE + 1;
            }
            System.out.println("[DEBUG Board] Moving from HOME. New position: " + newPosition);
            return newPosition;
        }
        
        // Special case: Already at tail entry position for this player
        int tailEntryPos = tailEntryPositions.get(player.getColor());
        if (currentPosition == tailEntryPos) {
            // Enter the tail directly with 1 step
            System.out.println("[DEBUG Board] At tail entry position. Moving into tail position " + (MAIN_BOARD_SIZE + 1));
            return MAIN_BOARD_SIZE + 1;
        }
        
        // If on the main board
        if (currentPosition <= MAIN_BOARD_SIZE) {
            // Calculate potential new position
            newPosition = currentPosition + diceRoll;
            System.out.println("[DEBUG Board] Initial calculation on main board: " + newPosition);
            
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
                System.out.println("[DEBUG Board] Passed tail entry. Distance to tail: " + distanceToTailEntry + 
                                 ", Steps into tail: " + stepsIntoTail + 
                                 ", Capped steps: " + cappedStepsIntoTail +
                                 ", New position in tail: " + newPosition);
                return newPosition;
            }
            
            // Otherwise, just move on the main board, with wraparound
            if (newPosition > MAIN_BOARD_SIZE) {
                int oldPos = newPosition;
                newPosition = (newPosition - 1) % MAIN_BOARD_SIZE + 1;
                System.out.println("[DEBUG Board] Wrapped around board from " + oldPos + " to " + newPosition);
            }
            System.out.println("[DEBUG Board] Final position on main board: " + newPosition);
            return newPosition;
        }
        
        // If already in the tail
        int tailPosition = currentPosition - MAIN_BOARD_SIZE;
        int newTailPosition = tailPosition + diceRoll;
        // Calculate the actual position without limit (for ExactEndRule)
        int finalPosition = MAIN_BOARD_SIZE + newTailPosition;
        
        System.out.println("[DEBUG Board] Moving in tail from tail position " + tailPosition + 
                         " to tail position " + newTailPosition + 
                         " (actual position " + finalPosition + ")");
        System.out.println("[DEBUG Board] END position is " + player.getEndPosition() + 
                         ", max valid tail position is " + TAIL_SIZE);
        
        // Return the actual new position even if it overshoots the END
        // This allows the ExactEndRule decorator to handle bounce-back if needed
        return finalPosition;
    }
    
    private boolean passedTailEntry(int currentPosition, int newPosition, Player player) {
        int tailEntryPos = tailEntryPositions.get(player.getColor());
        
        System.out.println("[DEBUG Board] Checking if passed tail entry. Current: " + currentPosition + 
                         ", New: " + newPosition + 
                         ", Tail entry: " + tailEntryPos);
        
        // First check if already at tail entry
        if (currentPosition == tailEntryPos) {
            System.out.println("[DEBUG Board] Already at tail entry position. Passed: true");
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
                System.out.println("[DEBUG Board] Tail entry after home. Passed: " + passed);
                return passed;
            } else {
                // Tail entry is before home position (wrap around case)
                if (currentPosition < tailEntryPos) {
                    // Simple case - we pass the tail entry without wrapping
                    boolean passed = newPosition >= tailEntryPos;
                    System.out.println("[DEBUG Board] Tail entry before home (current < tail). Passed: " + passed);
                    return passed;
                } else {
                    // Check if we've wrapped around and passed the tail entry
                    int wrappedPosition = (newPosition - 1) % MAIN_BOARD_SIZE + 1;
                    boolean passed = wrappedPosition >= player.getHomePosition() && 
                           wrappedPosition <= tailEntryPos;
                    System.out.println("[DEBUG Board] Tail entry before home (current >= tail). Wrapped position: " + 
                                     wrappedPosition + ", Passed: " + passed);
                    return passed;
                }
            }
        } else {
            // If new position is beyond main board size, we need special handling
            // First normalize the position to its wrapped equivalent
            int wrappedPosition = (newPosition - 1) % MAIN_BOARD_SIZE + 1;
            
            System.out.println("[DEBUG Board] New position beyond board size. Wrapped position: " + wrappedPosition);
            
            // Now check if we would have passed the tail entry
            boolean passed;
            if (tailEntryPos > player.getHomePosition()) {
                passed = (currentPosition < tailEntryPos && wrappedPosition >= tailEntryPos) || 
                       (currentPosition > wrappedPosition);
                System.out.println("[DEBUG Board] Case 1: Tail entry after home. Passed: " + passed);
            } else {
                passed = (currentPosition < tailEntryPos && wrappedPosition >= tailEntryPos) || 
                       (currentPosition > tailEntryPos && wrappedPosition >= player.getHomePosition());
                System.out.println("[DEBUG Board] Case 2: Tail entry before home. Passed: " + passed);
            }
            return passed;
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
        return homePositions.getOrDefault(color, 1); // Red's position
    }
    
    @Override
    public int getTailEntryPosition(String color) {
        return tailEntryPositions.getOrDefault(color, 18); // Red's position
    }
}
