package observers;

import players.Player;

/**
 * Interface defining the contract for game observers.
 */
public interface GameObserver {
    /**
     * Called when a player moves
     * @param player The player who moved
     * @param oldPosition Previous position
     * @param newPosition New position
     * @param diceRoll The dice roll value
     */
    void onMove(Player player, int oldPosition, int newPosition, int diceRoll);
    
    /**
     * Called when a player is hit
     * @param attacker The player who caused the hit
     * @param victim The player who was hit
     * @param victimOriginalPosition The original position of the victim before being hit
     */
    void onHit(Player attacker, Player victim, int victimOriginalPosition);
    
    /**
     * Called when a player overshoots the end position
     * @param player The player who overshot
     */
    void onOvershoot(Player player);
    
    /**
     * Called when a player wins
     * @param winner The winning player
     * @param totalTurns Total turns taken in the game
     */
    void onWin(Player winner, int totalTurns);
    
    /**
     * Called when a move is undone
     * @param player The player whose move was undone
     * @param hitOccurred Whether a hit occurred in the undone move
     * @param hitVictim The player who was hit (if any)
     */
    void onUndo(Player player, boolean hitOccurred, Player hitVictim);
}
