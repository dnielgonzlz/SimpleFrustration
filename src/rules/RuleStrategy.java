package rules;

import board.IBoard;
import players.Player;
import players.PlayerManager;

/**
 * Interface defining the contract for rule behavior.
 */
public interface RuleStrategy {
    /**
     * Handle player movement according to the rules
     * @param player The player moving
     * @param diceRoll The dice roll value
     * @param board The game board
     * @return The new position after applying rules
     */
    int handleMovement(Player player, int diceRoll, IBoard board);
    
    /**
     * Handle a potential hit according to the rules
     * @param attacker The player who might cause a hit
     * @param victim The player who might be hit
     * @param playerManager The player manager
     * @return true if a hit occurred, false otherwise
     */
    boolean handleHit(Player attacker, Player victim, PlayerManager playerManager);
    
    /**
     * Get a description of the rule
     * @return Description of the rule
     */
    String getDescription();
}
