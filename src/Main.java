import game.Game;
import game.GameConfig;
import observers.ConsoleObserver;

import java.util.Scanner;

/**
 * Main application class to run the Simple Frustration game.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("Welcome to Simple Frustration!");
        
        Scanner scanner = new Scanner(System.in);
        Game game = new Game();
        
        // Configure the game based on user input
        String boardSize = promptBoardSize(scanner);
        int numPlayers = promptNumPlayers(scanner);
        String diceType = promptDiceType(scanner);
        String[] ruleTypes = promptRuleTypes(scanner);
        
        // Create game configuration
        GameConfig config = new GameConfig(boardSize, numPlayers, diceType, ruleTypes);
        
        // Start the game
        game.startGame(config);
        
        // Add console observer
        game.addObserver(new ConsoleObserver(game.getBoard()));
        
        // Game loop
        while (!game.isGameOver()) {
            System.out.println("\nPress Enter to roll the dice, or type 'undo' to undo the last move:");
            String input = scanner.nextLine();
            
            if (input.equalsIgnoreCase("undo")) {
                boolean undoSuccessful = game.undo();
                if (!undoSuccessful) {
                    System.out.println("Cannot undo - no previous moves.");
                }
            } else {
                game.playTurn();
            }
        }
        
        scanner.close();
    }
    
    /**
     * Prompt the user for the board size
     */
    private static String promptBoardSize(Scanner scanner) {
        System.out.println("Select board size:");
        System.out.println("1. Basic (18 positions, 3 tail positions)");
        System.out.println("2. Large (36 positions, 6 tail positions)");
        
        while (true) {
            System.out.print("Enter your choice (1-2): ");
            String input = scanner.nextLine();
            
            if (input.equals("1")) {
                return "basic";
            } else if (input.equals("2")) {
                return "large";
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /**
     * Prompt the user for the number of players
     */
    private static int promptNumPlayers(Scanner scanner) {
        System.out.println("Select number of players:");
        System.out.println("1. Two players (Red, Blue)");
        System.out.println("2. Four players (Red, Blue, Green, Yellow)");
        
        while (true) {
            System.out.print("Enter your choice (1-2): ");
            String input = scanner.nextLine();
            
            if (input.equals("1")) {
                return 2;
            } else if (input.equals("2")) {
                return 4;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /**
     * Prompt the user for the dice type
     */
    private static String promptDiceType(Scanner scanner) {
        System.out.println("Select dice type:");
        System.out.println("1. Single die");
        System.out.println("2. Two dice");
        
        while (true) {
            System.out.print("Enter your choice (1-2): ");
            String input = scanner.nextLine();
            
            if (input.equals("1")) {
                return "single";
            } else if (input.equals("2")) {
                return "double";
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /**
     * Prompt the user for the rule types
     */
    private static String[] promptRuleTypes(Scanner scanner) {
        System.out.println("Select rules (you can choose multiple):");
        System.out.println("1. Player must land exactly on END position to win");
        System.out.println("2. Player will be sent HOME when HIT");
        System.out.println("3. Basic rules only (no exact END, no HIT penalties)");
        
        while (true) {
            System.out.print("Enter your choices (e.g., '1 2' for both rules, '3' for basic rules): ");
            String input = scanner.nextLine();
            
            if (input.contains("3")) {
                return new String[]{};
            }
            
            String[] choices = input.split("\\s+");
            String[] rules = new String[choices.length];
            
            boolean validChoices = true;
            for (int i = 0; i < choices.length; i++) {
                if (choices[i].equals("1")) {
                    rules[i] = "exactEnd";
                } else if (choices[i].equals("2")) {
                    rules[i] = "hitHome";
                } else {
                    validChoices = false;
                    break;
                }
            }
            
            if (validChoices) {
                return rules;
            } else {
                System.out.println("Invalid choices. Please try again.");
            }
        }
    }
}
