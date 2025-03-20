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
        
        System.out.println(ConsoleColors.colorize(victimColor + " Position " + victim.getCurrentPosition() + " hit!", victimColor));
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
            return "TAIL (Tail Position " + (position - board.getMainBoardSize()) + ")";
        } else {
            return "Position " + position;
        }
    }
}
