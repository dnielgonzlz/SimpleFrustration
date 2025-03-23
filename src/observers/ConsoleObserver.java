package observers;

import board.IBoard;
import players.Player;
import util.ConsoleColors;

/**
 * Observer that prints game events to the console.
 */
public class ConsoleObserver implements GameObserver {
    private final IBoard board;
    
    public ConsoleObserver(IBoard board) {
        this.board = board;
    }
    
    @Override
    public void onMove(Player player, int oldPosition, int newPosition, int diceRoll) {
        String playCount = player.getTotalMoves() + "";
        
        boolean oldIsTail = oldPosition > board.getMainBoardSize();
        boolean newIsTail = newPosition > board.getMainBoardSize();
        
        String playerColor = player.getColor();
        
        System.out.println(ConsoleColors.colorize(playerColor + " play " + playCount + " rolls " + diceRoll, playerColor));
        System.out.println(ConsoleColors.colorize(playerColor + " moves from " + 
                           getPositionDisplay(player, oldPosition, oldIsTail) + " to " + 
                           getPositionDisplay(player, newPosition, newIsTail), playerColor));
    }
    
    @Override
    public void onHit(Player attacker, Player victim, int victimOriginalPosition) {
        String victimColor = victim.getColor();
        int hitPosition = attacker.getCurrentPosition(); // This is where the hit occurred
        
        System.out.println("[DEBUG Observer] HIT event: " + attacker.getColor() + 
                          " hit " + victimColor + 
                          " at position " + hitPosition);
        System.out.println("[DEBUG Observer] Victim original position: " + victimOriginalPosition + 
                          " (now at " + victim.getCurrentPosition() + ")");
        
        System.out.println(ConsoleColors.colorize(victimColor + " Position " + hitPosition + " \u001B[1mHIT!\u001B[0m", victimColor));
        System.out.println(ConsoleColors.colorize(victimColor + " moves from Position " + 
                           victimOriginalPosition + " to HOME (Position " + 
                           victim.getHomePosition() + ")", victimColor));
    }
    
    @Override
    public void onOvershoot(Player player) {
        String playerColor = player.getColor();
        System.out.println(ConsoleColors.colorize(playerColor + " overshoots!", playerColor));
    }
    
    @Override
    public void onWin(Player winner, int totalTurns) {
        String winnerColor = winner.getColor();
        System.out.println(ConsoleColors.colorize(winnerColor + " wins in " + winner.getTotalMoves() + " moves!", winnerColor));
        System.out.println("Total plays " + totalTurns);
    }
    
    @Override
    public void onUndo(Player player, boolean hitOccurred, Player hitVictim) {
        System.out.println("Undo");
        
        if (hitOccurred && hitVictim != null) {
            // If a hit was undone, show detailed restoration message
            // For the victim: Home -> Current Position (the position they were in before being hit)
            String victimColor = hitVictim.getColor();
            boolean victimIsTail = hitVictim.getCurrentPosition() > board.getMainBoardSize();
            System.out.println(ConsoleColors.colorize(victimColor + " moves from HOME (Position " + 
                               hitVictim.getHomePosition() + ") to " + 
                               getPositionDisplay(hitVictim, hitVictim.getCurrentPosition(), victimIsTail), 
                               victimColor));
            
            // For the attacker: Current Position -> Previous Position
            // Note: After an undo, player.getCurrentPosition() will be where they were before hitting
            String attackerColor = player.getColor();
            boolean attackerIsTail = player.getCurrentPosition() > board.getMainBoardSize();
            
            System.out.println(ConsoleColors.colorize(attackerColor + " moves from " + 
                               getPositionDisplay(player, hitVictim.getCurrentPosition(), false) + " to " + 
                               getPositionDisplay(player, player.getCurrentPosition(), attackerIsTail), 
                               attackerColor));
        }
    }
    
    private String getPositionDisplay(Player player, int position, boolean isTail) {
        if (position == player.getHomePosition()) {
            return "HOME (Position " + position + ")";
        } else if (position == player.getEndPosition()) {
            return "END";
        } else if (isTail) {
            // Yellow from Pos 6 rolls 7 should reach tail entry 13, then 2 more steps = Tail Pos 2
            // For a roll of 10, Yellow reaches tail entry 13 with 3 more steps which is END
            int tailPos = position - board.getMainBoardSize();
            return "TAIL (Tail Position " + tailPos + ")";
        } else {
            return "Position " + position;
        }
    }
}
