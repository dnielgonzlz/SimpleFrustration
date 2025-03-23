package rules;

import board.IBoard;
import players.Player;
import players.PlayerManager;

/**
 * Implementation of hit home rule (sends hit players back to home).
 */
public class HitHomeRule implements RuleStrategy {
    private final RuleStrategy baseRule;
    
    /**
     * Constructor that takes a base rule to decorate
     * @param baseRule The base rule to extend
     */
    public HitHomeRule(RuleStrategy baseRule) {
        this.baseRule = baseRule;
    }
    
    @Override
    public int handleMovement(Player player, int diceRoll, IBoard board) {
        // Delegate to base rule for movement
        return baseRule.handleMovement(player, diceRoll, board);
    }
    
    @Override
    public boolean handleHit(Player attacker, Player victim, PlayerManager playerManager) {
        // Hit rule: send the victim back to home
        System.out.println("[DEBUG HitHomeRule] Processing hit. Attacker: " + attacker.getColor() + 
                         " at position " + attacker.getCurrentPosition() + 
                         ", Victim: " + victim.getColor() + 
                         " at position " + victim.getCurrentPosition());
        
        System.out.println("[DEBUG HitHomeRule] Sending " + victim.getColor() + 
                         " from position " + victim.getCurrentPosition() + 
                         " to HOME at position " + victim.getHomePosition());
        
        victim.resetToHome();
        return true;
    }
    
    @Override
    public String getDescription() {
        return baseRule.getDescription().split("\n")[0] + "\n" + 
               "Player will be sent HOME when HIT";
    }
}
