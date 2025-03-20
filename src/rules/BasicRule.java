package rules;

import board.IBoard;
import players.Player;
import players.PlayerManager;

/**
 * Implementation of basic rules (no exact END, no HIT penalties).
 */
public class BasicRule implements RuleStrategy {
    
    @Override
    public int handleMovement(Player player, int diceRoll, IBoard board) {
        // Basic rule: just calculate new position normally
        return board.calculateNewPosition(player, diceRoll);
    }
    
    @Override
    public boolean handleHit(Player attacker, Player victim, PlayerManager playerManager) {
        // Basic rule: hits are ignored
        return false;
    }
    
    @Override
    public String getDescription() {
        return "Player can land on or beyond the END position to win\n" +
               "HITS are ignored, multiple players can occupy the same position";
    }
}
