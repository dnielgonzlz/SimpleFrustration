import game.Game;
import game.GameConfig;
import observers.ConsoleObserver;
import java.util.Random;

/**
 * Main application class to run the Simple Frustration game.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("Starting Simple Frustration Simulation!");
        
        // Configure the game with hardcoded settings

        // Options: "basic", "large"
        String boardSize = "basic";
        // Options: 2 or 4
        int numPlayers = 2;
        // Options: "single", "double"
        String diceType = "double";
        // Options: empty array, "exactEnd", "hitHome", or both
        String[] ruleTypes = {};
        // Options: true or false
        boolean undoEnabled = false;
        
        // Create game configuration
        GameConfig config = new GameConfig(boardSize, numPlayers, diceType, ruleTypes);
        config.setUndoEnabled(undoEnabled);
        
        // Initialize game
        Game game = new Game();
        game.startGame(config);
        
        // Add console observer
        game.addObserver(new ConsoleObserver(game.getBoard()));
        
        // Create random number generator for undo decisions
        Random random = new Random();
        
        // Game loop - automatic simulation
        int turnCount = 0;
        while (!game.isGameOver()) {
            System.out.println("\n=== Turn " + (++turnCount) + " ===");
            game.playTurn();
            
            // Randomly decide whether to undo the current move
            if (config.isUndoEnabled() && !game.isGameOver() && turnCount > 1 && random.nextInt(10) < 3) {
                System.out.println("\n=== Randomly triggering UNDO ===");
                boolean undoSuccessful = game.undo();
                if (undoSuccessful) {
                    System.out.println("Move successfully undone!");
                    // Since we undid the move, decrement the turn counter
                    turnCount--;
                } else {
                    System.out.println("Failed to undo move.");
                }
            }
            
            // Add a small delay between turns for readability
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("\nSimulation completed after " + turnCount + " turns!");
    }
}
