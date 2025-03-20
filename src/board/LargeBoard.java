package board;

import players.Player;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the large board (36 positions, 6 tail slots).
 */
public class LargeBoard implements IBoard {
    private static final int MAIN_BOARD_SIZE = 36;
    private static final int TAIL_SIZE = 6; // Including END position
    
    // Map to store home positions for each player color
    private final Map<String, Integer> homePositions;
    // Map to store tail entry positions for each player color
    private final Map<String, Integer> tailEntryPositions;
    
    public LargeBoard() {
        // Initialize home positions
        homePositions = new HashMap<>();
        homePositions.put("Red", 1);
        homePositions.put("Blue", 19);
        homePositions.put("Green", 10); // For 4-player support
        homePositions.put("Yellow", 28); // For 4-player support
        
        // Initialize tail entry positions (position before home)
        tailEntryPositions = new HashMap<>();
        tailEntryPositions.put("Red", 36); // Position before home position 1
        tailEntryPositions.put("Blue", 18); // Position before home position 19
        tailEntryPositions.put("Green", 9); // Position before home position 10
        tailEntryPositions.put("Yellow", 27); // Position before home position 28
    }
    
    @Override
    public PositionType getPositionType(int position, Player player) {
        if (position == player.getHomePosition()) {
            return PositionType.HOME;
        } else if (position == player.getEndPosition()) {
            return PositionType.END;
        } else if (position > MAIN_BOARD_SIZE) { // Position is in tail
            return PositionType.TAIL;
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
        
        // Special case: Already at tail entry position
        int tailEntryPos = tailEntryPositions.get(player.getColor());
        if (currentPosition == tailEntryPos) {
            return MAIN_BOARD_SIZE + diceRoll;
        }
        
        // If on the main board
        if (currentPosition <= MAIN_BOARD_SIZE) {
            // Calculate potential new position
            newPosition = currentPosition + diceRoll;
            
            // Check if we need to enter the tail
            if (passedTailEntry(currentPosition, newPosition, player)) {
                int distanceToTailEntry;
                
                // Calculate distance to tail entry point
                if (currentPosition < tailEntryPos) {
                    distanceToTailEntry = tailEntryPos - currentPosition;
                } else {
                    // We need to go around the board
                    distanceToTailEntry = (MAIN_BOARD_SIZE - currentPosition) + tailEntryPos;
                }
                
                // Calculate how many steps into the tail
                int stepsIntoTail = diceRoll - distanceToTailEntry;
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
        
        // Check if we went beyond END (tail overflow)
        if (newTailPosition > TAIL_SIZE) {
            // Calculate bounce back
            int bounceBack = newTailPosition - TAIL_SIZE;
            return MAIN_BOARD_SIZE + TAIL_SIZE - bounceBack;
        }
        
        return MAIN_BOARD_SIZE + newTailPosition;
    }
    
    private boolean passedTailEntry(int currentPosition, int newPosition, Player player) {
        int tailEntryPos = tailEntryPositions.get(player.getColor());
        
        // Check if already at tail entry
        if (currentPosition == tailEntryPos) {
            return true;
        }
        
        // Different logic needed depending on board layout
        if (tailEntryPos > player.getHomePosition()) {
            // Tail entry is after home position
            return (currentPosition < tailEntryPos && newPosition >= tailEntryPos);
        } else {
            // Tail entry is before home position (might involve wrap around)
            if (currentPosition < tailEntryPos) {
                // Simple case - we pass the tail entry without wrapping
                return newPosition >= tailEntryPos;
            } else {
                // We need to handle wrap-around
                int wrappedPosition = (newPosition - 1) % MAIN_BOARD_SIZE + 1;
                return wrappedPosition >= player.getHomePosition() && 
                       wrappedPosition <= tailEntryPos;
            }
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
