package game;

import board.IBoard;
import dices.Dice;
import factories.BoardFactory;
import factories.DiceFactory;
import observers.GameObserver;
import players.Player;
import players.PlayerManager;
import rules.BasicRule;
import rules.ExactEndRule;
import rules.HitHomeRule;
import rules.RuleStrategy;
import undo.GameHistory;
import undo.GameStateMemento;

import java.util.ArrayList;
import java.util.List;

/**
 * Main game class that manages the game flow.
 */
public class Game {
    private IBoard board;
    private Dice dice;
    private PlayerManager playerManager;
    private RuleStrategy rules;
    private GameConfig config;
    private final GameHistory gameHistory;
    private final List<GameObserver> observers;
    private boolean gameOver;
    private Player winner;
    private boolean lastMoveWasHit;
    private String lastHitVictim;
    
    /**
     * Constructor initializing a new game
     */
    public Game() {
        this.observers = new ArrayList<>();
        this.gameHistory = new GameHistory();
        this.gameOver = false;
        this.lastMoveWasHit = false;
        this.lastHitVictim = null;
    }
    
    /**
     * Start a new game with the given configuration
     * @param config Game configuration
     */
    public void startGame(GameConfig config) {
        // Store the config
        this.config = config;
        
        // Create board
        BoardFactory boardFactory = new BoardFactory();
        this.board = boardFactory.createBoard(config.getBoardSize(), config.getNumPlayers());
        
        // Create dice
        DiceFactory diceFactory = new DiceFactory();
        this.dice = diceFactory.createDice(config.getDiceType());
        
        // Create players
        this.playerManager = new PlayerManager();
        playerManager.createPlayers(config.getNumPlayers(), board);
        
        // Create rules (using decorator pattern)
        this.rules = new BasicRule();
        
        if (config.hasRule("exactEnd")) {
            this.rules = new ExactEndRule(rules);
        }
        
        if (config.hasRule("hitHome")) {
            this.rules = new HitHomeRule(rules);
        }
        
        // Reset game state
        this.gameOver = false;
        this.winner = null;
        this.gameHistory.clear();
        
        // Print configuration description
        System.out.println(config.getDescription());
    }
    
    /**
     * Play a turn for the current player
     */
    public void playTurn() {
        if (gameOver) {
            return;
        }
        
        Player currentPlayer = playerManager.getCurrentPlayer();
        
        System.out.println("\n[DEBUG Game] === Starting turn for " + currentPlayer.getColorString() + " player ===");
        System.out.println("[DEBUG Game] Current position: " + currentPlayer.getCurrentPosition() + 
                         ", Home: " + currentPlayer.getHomePosition() + 
                         ", End: " + currentPlayer.getEndPosition());

        // Save the current state BEFORE any move is made - this is critical for undo
        gameHistory.saveState(
            playerManager.getAllPlayers(),
            playerManager.getCurrentPlayerIndex(),
            false,  // Reset hit tracking for the new state
            null,
            gameOver,
            (winner != null ? winner.getColorString() : null)
        );
        
        // Reset hit tracking for the new turn
        lastMoveWasHit = false;
        lastHitVictim = null;
        
        // Roll the dice
        dice.roll();
        int totalRoll = dice.getTotal();
        System.out.println("[DEBUG Game] Dice roll: " + totalRoll);
        
        // Save the old position
        int oldPosition = currentPlayer.getCurrentPosition();
        
        // Calculate the new position according to the rules
        int newPosition = rules.handleMovement(currentPlayer, totalRoll, board);
        System.out.println("[DEBUG Game] Final calculated position after rules: " + newPosition);
        
        // Move the player
        currentPlayer.move(newPosition);
        
        // Notify observers about the move
        for (GameObserver observer : observers) {
            observer.onMove(currentPlayer, oldPosition, newPosition, totalRoll);
        }
        
        // Check for a hit (if we're not at the end and not in the tail)
        if (newPosition != currentPlayer.getEndPosition() && newPosition <= board.getMainBoardSize()) {
            Player victim = playerManager.getPlayerAtPosition(newPosition, currentPlayer);
            if (victim != null) {
                System.out.println("[DEBUG Game] HIT detected: " + currentPlayer.getColorString() + 
                                 " hit " + victim.getColorString() + 
                                 " at position " + newPosition);
                
                // Save the victim's original position before they get sent home
                int victimOriginalPosition = victim.getCurrentPosition();
                System.out.println("[DEBUG Game] Saved victim original position: " + victimOriginalPosition);
                
                // Note: We don't need to save state here anymore since we saved at start of turn
                
                lastMoveWasHit = rules.handleHit(currentPlayer, victim, playerManager);
                if (lastMoveWasHit) {
                    lastHitVictim = victim.getColorString();
                    System.out.println("[DEBUG Game] Hit occurred. Victim: " + victim.getColorString() + 
                                     " sent to HOME at position " + victim.getHomePosition() + 
                                     " from original position " + victimOriginalPosition);
                    for (GameObserver observer : observers) {
                        // Pass the victim's original position to the observer
                        observer.onHit(currentPlayer, victim, victimOriginalPosition);
                    }
                }
            }
        }
        
        // Check for a win
        if (newPosition == currentPlayer.getEndPosition()) {
            System.out.println("[DEBUG Game] Player reached END position exactly. Game over!");
            gameOver = true;
            winner = currentPlayer;
            
            for (GameObserver observer : observers) {
                observer.onWin(winner, playerManager.getTotalTurns());
            }
        } else if (newPosition > currentPlayer.getEndPosition() && 
                   !config.hasRule("exactEnd") && 
                   newPosition > board.getMainBoardSize()) {
            // For Basic Rule: Also win if position is beyond END and we're in a tail
            System.out.println("[DEBUG Game] Player passed END position with Basic Rule. Game over!");
            gameOver = true;
            winner = currentPlayer;
            
            for (GameObserver observer : observers) {
                observer.onWin(winner, playerManager.getTotalTurns());
            }
        } else {
            System.out.println("[DEBUG Game] Player did not reach END position (" + 
                             currentPlayer.getEndPosition() + "), currently at: " + newPosition);
        }
        
        // Switch to the next player if game is not over
        if (!gameOver) {
            playerManager.switchPlayer();
            System.out.println("[DEBUG Game] Next player: " + 
                             playerManager.getCurrentPlayer().getColorString());
        }
        
        System.out.println("[DEBUG Game] === End of turn ===\n");
    }
    
    /**
     * Undo the last move
     * @return true if undo was successful
     */
    public boolean undo() {
        System.out.println("\n[DEBUG Game] === Starting UNDO operation ===");
        
        GameStateMemento memento = gameHistory.undo(playerManager);
        
        if (memento != null) {
            System.out.println("[DEBUG Game] Successfully retrieved previous game state");
            
            // Restore game-over state and winner from the saved state
            gameOver = memento.getGameOver();
            String restoredWinnerColor = memento.getWinnerColor();
            winner = null;
            if (restoredWinnerColor != null) {
                for (Player player : playerManager.getAllPlayers()) {
                    if (player.getColorString().equals(restoredWinnerColor)) {
                        winner = player;
                        break;
                    }
                }
            }
            
            // Restore hit-tracking state
            lastMoveWasHit = memento.isHitOccurred();
            lastHitVictim = memento.getHitVictimColor();
            
            System.out.println("[DEBUG Game] Restored game state. Game over: " + gameOver + 
                           ", Last move was hit: " + lastMoveWasHit + 
                           ", Last hit victim: " + lastHitVictim);
            
            // Dump all player positions after undo
            System.out.println("[DEBUG Game] Player positions after undo:");
            for (Player player : playerManager.getAllPlayers()) {
                System.out.println("[DEBUG Game]   " + player.getColorString() + 
                                 ": position " + player.getCurrentPosition());
            }
            
            // Notify observers of undo
            for (GameObserver observer : observers) {
                Player currentPlayer = playerManager.getCurrentPlayer();
                Player hitVictim = null;
                
                // If there was a hit, find the hit victim
                if (lastMoveWasHit && lastHitVictim != null) {
                    for (Player player : playerManager.getAllPlayers()) {
                        if (player.getColorString().equals(lastHitVictim)) {
                            hitVictim = player;
                            break;
                        }
                    }
                }
                
                observer.onUndo(currentPlayer, lastMoveWasHit, hitVictim);
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Add an observer to the game
     * @param observer Observer to add
     */
    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }
    
    /**
     * Remove an observer from the game
     * @param observer Observer to remove
     */
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * Check if the game is over
     * @return true if the game is over
     */
    public boolean isGameOver() {
        return gameOver;
    }
    
    /**
     * Get the winner of the game
     * @return Winner or null if game is not over
     */
    public Player getWinner() {
        return winner;
    }
    
    /**
     * Get the board
     * @return Game board
     */
    public IBoard getBoard() {
        return board;
    }
    
    /**
     * Get the player manager
     * @return Player manager
     */
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
