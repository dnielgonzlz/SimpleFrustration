package players;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
/**
 * Value object representing a player's color.
 */
public class PlayerColor {
    // Define valid colors first before using them
    private static final List<String> VALID_COLORS = 
        Arrays.asList("Red", "Blue", "Green", "Yellow");
    
    // Standard color constants - defined after VALID_COLORS
    public static final PlayerColor RED = new PlayerColor("Red");
    public static final PlayerColor BLUE = new PlayerColor("Blue");
    public static final PlayerColor GREEN = new PlayerColor("Green");
    public static final PlayerColor YELLOW = new PlayerColor("Yellow");
    
    private final String value;
    
    /**
     * Constructor for a PlayerColor
     * @param value The color string value
     * @throws IllegalArgumentException if color is invalid
     */
    public PlayerColor(String value) {
        if (!isValidColor(value)) {
            throw new IllegalArgumentException("Invalid player color: " + value);
        }
        this.value = value;
    }
    
    /**
     * Check if a color string is valid
     * @param color Color string to check
     * @return true if valid
     */
    public static boolean isValidColor(String color) {
        return VALID_COLORS.contains(color);
    }
    
    /**
     * Get all valid player colors
     * @return List of valid color strings
     */
    public static List<String> getValidColors() {
        return VALID_COLORS;
    }
    
    /**
     * Create a PlayerColor from a string if valid
     * @param color Color string
     * @return PlayerColor object or null if invalid
     */
    public static PlayerColor fromString(String color) {
        if (isValidColor(color)) {
            return new PlayerColor(color);
        }
        return null;
    }
    
    /**
     * Get the string value of this color
     * @return Color string value
     */
    public String getValue() { 
        return value; 
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerColor that = (PlayerColor) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
} 