package observers;

import board.IBoard;
import players.Player;

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
        
        System.out.println(player.getColor() + " play " + playCount + " rolls " + diceRoll);
        System.out.println(player.getColor() + " moves from " + 
                           getPositionDisplay(player, oldPosition, oldIsTail) + " to " + 
                           getPositionDisplay(player, newPosition, newIsTail));
    }
    
    @Override
    public void onHit(Player attacker, Player victim) {
        System.out.println(victim.getColor() + " Position " + victim.getCurrentPosition() + " hit!");
        System.out.println(victim.getColor() + " moves from Position " + 
                           victim.getCurrentPosition() + " to HOME (Position " + 
                           victim.getHomePosition() + ")");
    }
    
    @Override
    public void onOvershoot(Player player) {
        System.out.println(player.getColor() + " overshoots!");
    }
    
    @Override
    public void onWin(Player winner, int totalTurns) {
        System.out.println(winner.getColor() + " wins in " + winner.getTotalMoves() + " moves!");
        System.out.println("Total plays " + totalTurns);
    }
    
    @Override
    public void onUndo(Player player) {
        System.out.println("Undo");
    }
    
    private String getPositionDisplay(Player player, int position, boolean isTail) {
        if (position == player.getHomePosition()) {
            return "HOME (Position " + position + ")";
        } else if (position == player.getEndPosition()) {
            return "END";
        } else if (isTail) {
            return "TAIL (Tail Position " + (position - board.getMainBoardSize()) + ")";
        } else {
            return "Position " + position;
        }
    }
}
