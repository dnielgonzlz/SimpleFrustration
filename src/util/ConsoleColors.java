package util;

/**
 * Utility class for ANSI color codes to display colored text in console.
 */
public class ConsoleColors {
    // Reset code
    public static final String RESET = "\u001B[0m";
    
    // Regular foreground colors
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    
    /**
     * Colorize text based on player color
     * @param text Text to colorize
     * @param playerColor Player color (Red, Blue, Green, Yellow)
     * @return Colorized text string with reset code
     */
    public static String colorize(String text, String playerColor) {
        String ansiColor;
        
        switch (playerColor.toLowerCase()) {
            case "red":
                ansiColor = RED;
                break;
            case "blue":
                ansiColor = BLUE;
                break;
            case "green":
                ansiColor = GREEN;
                break;
            case "yellow":
                ansiColor = YELLOW;
                break;
            default:
                return text;
        }
        
        return ansiColor + text + RESET;
    }
} 