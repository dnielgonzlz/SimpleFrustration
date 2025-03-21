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
        System.out.println("[DEBUG ExactEndRule] Handling movement for " + player.getColor() + 
                         " from position " + player.getCurrentPosition() + 
                         " with dice roll " + diceRoll);
        
        int newPosition = baseRule.handleMovement(player, diceRoll, board);
        
        System.out.println("[DEBUG ExactEndRule] Base rule calculated new position: " + newPosition + 
                         " (Player end position is " + player.getEndPosition() + ")");
        
        // If the new position is past the END position, bounce back
        if (newPosition == player.getEndPosition()) {
            System.out.println("[DEBUG ExactEndRule] Exact landing on END position. Return: " + newPosition);
            return newPosition; // Exact landing on END
        } else if (newPosition > player.getEndPosition()) {
            // Calculate bounce back distance
            int overshoot = newPosition - player.getEndPosition();
            int bouncePosition = player.getEndPosition() - overshoot;
            
            System.out.println("[DEBUG ExactEndRule] Overshoot detected. Overshoot distance: " + overshoot);
            System.out.println("[DEBUG ExactEndRule] Bouncing back to position: " + bouncePosition);
            
            return bouncePosition;
        }
        
        System.out.println("[DEBUG ExactEndRule] No overshoot or exact landing. Return: " + newPosition);
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
