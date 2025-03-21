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
    public void onHit(Player attacker, Player victim) {
        String victimColor = victim.getColor();
        
        System.out.println(ConsoleColors.colorize(victimColor + " Position " + victim.getCurrentPosition() + " \u001B[1mHIT!\u001B[0m", victimColor));
        System.out.println(ConsoleColors.colorize(victimColor + " moves from Position " + 
                           victim.getCurrentPosition() + " to HOME (Position " + 
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
    public void onUndo(Player player) {
        System.out.println("Undo");
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
