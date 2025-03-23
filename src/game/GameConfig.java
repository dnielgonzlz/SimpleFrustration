package game;

import util.ConsoleColors;

/**
 * Configuration for a game.
 */
public class GameConfig {
    private final String boardSize;
    private final int numPlayers;
    private final String diceType;
    private final String[] ruleTypes;
    
    /**
     * Constructor for game configuration
     * @param boardSize "basic" or "large"
     * @param numPlayers 2 or 4
     * @param diceType "single" or "double"
     * @param ruleTypes Array of rule types (e.g., "exactEnd", "hitHome")
     */
    public GameConfig(String boardSize, int numPlayers, String diceType, String[] ruleTypes) {
        this.boardSize = boardSize;
        this.numPlayers = numPlayers;
        this.diceType = diceType;
        this.ruleTypes = ruleTypes;
    }
    
    /**
     * Get the board size
     * @return "basic" or "large"
     */
    public String getBoardSize() {
        return boardSize;
    }
    
    /**
     * Get the number of players
     * @return 2 or 4
     */
    public int getNumPlayers() {
        return numPlayers;
    }
    
    /**
     * Get the dice type
     * @return "single" or "double"
     */
    public String getDiceType() {
        return diceType;
    }
    
    /**
     * Get the rule types
     * @return Array of rule types
     */
    public String[] getRuleTypes() {
        return ruleTypes;
    }
    
    /**
     * Check if a specific rule is enabled
     * @param ruleType Rule type to check
     * @return true if the rule is enabled
     */
    public boolean hasRule(String ruleType) {
        for (String rule : ruleTypes) {
            if (rule.equalsIgnoreCase(ruleType)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Create a description of the configuration
     * @return String describing the configuration
     */
    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        
        int boardPositions = "large".equalsIgnoreCase(boardSize) ? 36 : 18;
        int tailPositions = "large".equalsIgnoreCase(boardSize) ? 6 : 3;
        
        builder.append("Board positions=").append(boardPositions)
              .append(" Tail positions=").append(tailPositions)
              .append(" Players={");
        
        if (numPlayers == 2) {
            builder.append(ConsoleColors.colorize("Red", "Red"))
                  .append(", ")
                  .append(ConsoleColors.colorize("Blue", "Blue"));
        } else {
            builder.append(ConsoleColors.colorize("Red", "Red"))
                  .append(", ")
                  .append(ConsoleColors.colorize("Blue", "Blue"))
                  .append(", ")
                  .append(ConsoleColors.colorize("Green", "Green"))
                  .append(", ")
                  .append(ConsoleColors.colorize("Yellow", "Yellow"));
        }
        
        builder.append("}\n");
        
        if (hasRule("exactEnd")) {
            builder.append("Player must land exactly on the END position to win\n");
        } else {
            builder.append("Player can land on or beyond the END position to win\n");
        }
        
        if (hasRule("hitHome")) {
            builder.append("Player will be sent HOME when HIT\n");
        } else {
            builder.append("HITS are ignored, multiple players can occupy the same position\n");
        }

        builder.append("Dice: ");
        if ("single".equalsIgnoreCase(diceType)) {
            builder.append("Single random 6 sided die");
        } else {
            builder.append("Two random 6 sided dice");
        }
        
        return builder.toString();
    }
}
