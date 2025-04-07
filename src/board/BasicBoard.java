package board;

import players.Player;
import java.util.HashMap;
import java.util.Map;

public class BasicBoard implements IBoard {
    private int numPlayers = 2;
    private static final int MAIN_BOARD_SIZE = 18;
    private static final int TAIL_SIZE = 3;
    
  
    private final Map<String, Integer> homePositions;
    private final Map<String, Integer> tailEntryPositions;
    
    public BasicBoard() {
        this(2);
    }
    
    /**
     * Constructor that specifies the number of players
     * @param numPlayers Number of players (2 or 4)
     */
    public BasicBoard(int numPlayers) {
        this.numPlayers = numPlayers;

        homePositions = new HashMap<>();
        tailEntryPositions = new HashMap<>();
        
        homePositions.put("Red", 1);
        tailEntryPositions.put("Red", 18); 
        
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
        int tailEntryPos = tailEntryPositions.get(player.getColor());
        
        System.out.println("[DEBUG Board] Calculating new position for " + player.getColor() + 
                         " from position " + currentPosition + 
                         " with dice roll " + diceRoll);
        System.out.println("[DEBUG Board] Player home: " + player.getHomePosition() + 
                         ", end: " + player.getEndPosition() + 
                         ", tail entry: " + tailEntryPos);

        // Handle different position cases
        if (currentPosition == player.getHomePosition()) {
            return calculateMoveFromHome(currentPosition, diceRoll);
        }
        
        if (currentPosition == tailEntryPos) {
            return MAIN_BOARD_SIZE + 1; // Enter tail directly
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
        System.out.println("[DEBUG Board] Moving from HOME. New position: " + newPosition);
        return newPosition;
    }
    
    private int calculateMainBoardMove(int currentPosition, int diceRoll, Player player, int tailEntryPos) {
        int newPosition = currentPosition + diceRoll;
        System.out.println("[DEBUG Board] Initial calculation on main board: " + newPosition);
        
        if (passedTailEntry(currentPosition, newPosition, player)) {
            return calculateTailEntryMove(currentPosition, diceRoll, tailEntryPos);
        }
        
        // Handle wraparound on main board
        if (newPosition > MAIN_BOARD_SIZE) {
            int oldPos = newPosition;
            newPosition = (newPosition - 1) % MAIN_BOARD_SIZE + 1;
            System.out.println("[DEBUG Board] Wrapped around board from " + oldPos + " to " + newPosition);
        }
        
        System.out.println("[DEBUG Board] Final position on main board: " + newPosition);
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
        
        System.out.println("[DEBUG Board] Passed tail entry. Distance to tail: " + distanceToTailEntry + 
                         ", Steps into tail: " + stepsIntoTail + 
                         ", New position in tail: " + newPosition);
        return newPosition;
    }
    
    private int calculateTailMove(int currentPosition, int diceRoll) {
        int tailPosition = currentPosition - MAIN_BOARD_SIZE;
        int newTailPosition = tailPosition + diceRoll;
        int finalPosition = MAIN_BOARD_SIZE + newTailPosition;
        
        System.out.println("[DEBUG Board] Moving in tail from tail position " + tailPosition + 
                         " to tail position " + newTailPosition + 
                         " (actual position " + finalPosition + ")");
        
        return finalPosition;
    }
    
    private boolean passedTailEntry(int currentPosition, int newPosition, Player player) {
        int tailEntryPos = tailEntryPositions.get(player.getColor());
        int homePos = player.getHomePosition();
        
        System.out.println("[DEBUG Board] Checking if passed tail entry. Current: " + currentPosition + 
                         ", New: " + newPosition + 
                         ", Tail entry: " + tailEntryPos);

        // Already at tail entry position
        if (currentPosition == tailEntryPos) {
            System.out.println("[DEBUG Board] Already at tail entry position. Passed: true");
            return true;
        }

        // Normalize positions if beyond board size
        int wrappedNewPos = newPosition <= MAIN_BOARD_SIZE ? 
            newPosition : (newPosition - 1) % MAIN_BOARD_SIZE + 1;

        // Check if tail entry is after home position
        if (tailEntryPos > homePos) {
            boolean passed = (currentPosition < tailEntryPos && wrappedNewPos >= tailEntryPos) ||
                           (newPosition > MAIN_BOARD_SIZE && currentPosition > wrappedNewPos);
            System.out.println("[DEBUG Board] Tail entry after home. Passed: " + passed);
            return passed;
        }
        
        // Tail entry is before home position (wrap around case)
        if (currentPosition < tailEntryPos) {
            boolean passed = wrappedNewPos >= tailEntryPos;
            System.out.println("[DEBUG Board] Tail entry before home (current < tail). Passed: " + passed);
            return passed;
        }
        
        // Check wrap around case when current position is past tail entry
        boolean passed = wrappedNewPos >= homePos && wrappedNewPos <= tailEntryPos;
        System.out.println("[DEBUG Board] Tail entry before home (current >= tail). Wrapped position: " + 
                         wrappedNewPos + ", Passed: " + passed);
        return passed;
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
