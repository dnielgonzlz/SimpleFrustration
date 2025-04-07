package board;

import players.Player;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the large board (36 positions, 6 tail slots).
 */
public class LargeBoard implements IBoard {
    private int numPlayers = 2;
    private static final int MAIN_BOARD_SIZE = 36;
    private static final int TAIL_SIZE = 6;
    

    private final Map<String, Integer> homePositions;
    private final Map<String, Integer> tailEntryPositions;
    
    public LargeBoard() {
        this(2);
    }
    
    /**
     * Constructor that specifies the number of players
     * @param numPlayers Number of players (2 or 4)
     */
    public LargeBoard(int numPlayers) {
        this.numPlayers = numPlayers;

        homePositions = new HashMap<>();
        tailEntryPositions = new HashMap<>();


        homePositions.put("Red", 1);
        tailEntryPositions.put("Red", 36);
        
        switch(numPlayers) {
            case 4:
                homePositions.put("Blue", 10);
                homePositions.put("Green", 19); 
                homePositions.put("Yellow", 27);
                
                tailEntryPositions.put("Blue", 9);  
                tailEntryPositions.put("Green", 18); 
                tailEntryPositions.put("Yellow", 26); 
                
                break;
            case 2:
                // Initialize positions for 2 player game
                homePositions.put("Blue", 19);
                tailEntryPositions.put("Blue", 18); 
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
        int tailEntryPos = tailEntryPositions.get(player.getColor());
        
        System.out.println("[DEBUG LargeBoard] Calculating new position for " + player.getColor() + 
                         " from position " + currentPosition + 
                         " with dice roll " + diceRoll);
        System.out.println("[DEBUG LargeBoard] Player home: " + player.getHomePosition() + 
                         ", end: " + player.getEndPosition() + 
                         ", tail entry: " + tailEntryPos);

        // Handle different position cases
        if (currentPosition == player.getHomePosition()) {
            return calculateMoveFromHome(currentPosition, diceRoll);
        }
        
        if (currentPosition == tailEntryPos) {
            return MAIN_BOARD_SIZE + 1;
        }
        
        if (currentPosition <= MAIN_BOARD_SIZE) {
            return calculateMainBoardMove(currentPosition, diceRoll, player, tailEntryPos);
        }
        
        return calculateTailMove(currentPosition, diceRoll);
    }
    
    private int calculateMoveFromHome(int currentPosition, int diceRoll) {
        int newPosition = currentPosition + diceRoll;
        if (newPosition > MAIN_BOARD_SIZE) {
            newPosition = (newPosition - 1) % MAIN_BOARD_SIZE + 1;
        }
        System.out.println("[DEBUG LargeBoard] Moving from HOME. New position: " + newPosition);
        return newPosition;
    }
    
    private int calculateMainBoardMove(int currentPosition, int diceRoll, Player player, int tailEntryPos) {
        int newPosition = currentPosition + diceRoll;
        System.out.println("[DEBUG LargeBoard] Initial calculation on main board: " + newPosition);
        
        if (passedTailEntry(currentPosition, newPosition, player)) {
            return calculateTailEntryMove(currentPosition, diceRoll, tailEntryPos);
        }
        
        // Handle wraparound on main board
        if (newPosition > MAIN_BOARD_SIZE) {
            int oldPos = newPosition;
            newPosition = (newPosition - 1) % MAIN_BOARD_SIZE + 1;
            System.out.println("[DEBUG LargeBoard] Wrapped around board from " + oldPos + " to " + newPosition);
        }
        
        System.out.println("[DEBUG LargeBoard] Final position on main board: " + newPosition);
        return newPosition;
    }
    
    private int calculateTailEntryMove(int currentPosition, int diceRoll, int tailEntryPos) {
        int distanceToTailEntry;
        
        if (currentPosition < tailEntryPos) {
            distanceToTailEntry = tailEntryPos - currentPosition;
        } else {
            distanceToTailEntry = (MAIN_BOARD_SIZE - currentPosition) + tailEntryPos;
        }
        
        int stepsIntoTail = Math.max(diceRoll - distanceToTailEntry, 1);
        int newPosition = MAIN_BOARD_SIZE + stepsIntoTail;
        
        System.out.println("[DEBUG LargeBoard] Passed tail entry. Distance to tail: " + distanceToTailEntry + 
                         ", Steps into tail: " + stepsIntoTail + 
                         ", New position in tail: " + newPosition);
        return newPosition;
    }
    
    private int calculateTailMove(int currentPosition, int diceRoll) {
        int tailPosition = currentPosition - MAIN_BOARD_SIZE;
        int newTailPosition = tailPosition + diceRoll;
        int finalPosition = MAIN_BOARD_SIZE + newTailPosition;
        
        System.out.println("[DEBUG LargeBoard] Moving in tail from tail position " + tailPosition + 
                         " to tail position " + newTailPosition + 
                         " (actual position " + finalPosition + ")");
        
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
