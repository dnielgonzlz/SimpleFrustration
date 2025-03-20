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
        // Create board
        BoardFactory boardFactory = new BoardFactory();
        this.board = boardFactory.createBoard(config.getBoardSize());
        
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
        
        // Save the current state before making any changes
        lastMoveWasHit = false;
        lastHitVictim = null;
        // Pass the current player index to save state
        gameHistory.saveState(playerManager.getAllPlayers(), playerManager.getCurrentPlayerIndex(), false, null);
        
        // Roll the dice
        int[] rollValues = dice.roll();
        int totalRoll = dice.getTotal();
        
        // Save the old position
        int oldPosition = currentPlayer.getCurrentPosition();
        
        // Calculate the new position according to the rules
        int newPosition = rules.handleMovement(currentPlayer, totalRoll, board);
        
        // Move the player
        currentPlayer.move(newPosition);
        
        // Notify observers about the move
        for (GameObserver observer : observers) {
            observer.onMove(currentPlayer, oldPosition, newPosition, totalRoll);
        }
        
        // Check for a hit (if we're not at the end)
        if (newPosition != currentPlayer.getEndPosition()) {
            Player victim = playerManager.getPlayerAtPosition(newPosition, currentPlayer);
            if (victim != null) {
                lastMoveWasHit = rules.handleHit(currentPlayer, victim, playerManager);
                if (lastMoveWasHit) {
                    lastHitVictim = victim.getColor();
                    for (GameObserver observer : observers) {
                        observer.onHit(currentPlayer, victim);
                    }
                }
            }
        }
        
        // Check for a win
        if (newPosition == currentPlayer.getEndPosition()) {
            gameOver = true;
            winner = currentPlayer;
            
            for (GameObserver observer : observers) {
                observer.onWin(winner, playerManager.getTotalTurns());
            }
        }
        
        // Switch to the next player if game is not over
        if (!gameOver) {
            playerManager.switchPlayer();
        }
    }
    
    /**
     * Undo the last move
     * @return true if undo was successful
     */
    public boolean undo() {
        boolean undoResult = gameHistory.undo(playerManager);
        
        if (undoResult) {
            // Reset game over status
            gameOver = false;
            winner = null;
            
            // Notify observers
            for (GameObserver observer : observers) {
                observer.onUndo(playerManager.getCurrentPlayer());
            }
        }
        
        return undoResult;
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
