package rules;

import board.IBoard;
import players.Player;
import players.PlayerManager;

/**
 * Implementation of exact end rule (must land exactly on END to win).
 */
public class ExactEndRule implements RuleStrategy {
    private final RuleStrategy baseRule;
    
    /**
     * Constructor that takes a base rule to decorate
     * @param baseRule The base rule to extend
     */
    public ExactEndRule(RuleStrategy baseRule) {
        this.baseRule = baseRule;
    }
    
    @Override
    public int handleMovement(Player player, int diceRoll, IBoard board) {
        int newPosition = baseRule.handleMovement(player, diceRoll, board);
        
        // If the new position is past the END position, bounce back
        if (newPosition == player.getEndPosition()) {
            return newPosition; // Exact landing on END
        } else if (newPosition > player.getEndPosition()) {
            // Calculate bounce back distance
            int overshoot = newPosition - player.getEndPosition();
            return player.getEndPosition() - overshoot;
        }
        
        return newPosition;
    }
    
    @Override
    public boolean handleHit(Player attacker, Player victim, PlayerManager playerManager) {
        // Delegate to base rule
        return baseRule.handleHit(attacker, victim, playerManager);
    }
    
    @Override
    public String getDescription() {
        return "Player must land exactly on the END position to win\n" + 
               baseRule.getDescription().split("\n")[1]; // Keep the hit rule description
    }
}
