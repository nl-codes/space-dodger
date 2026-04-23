package spacedodger;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Represents the player-controlled spaceship in Space Dodger.
 * <p>
 * Inherits position and dimension tracking from {@link GameObject}.
 * The player moves left and right in response to keyboard input.
 */
public class Player extends GameObject {

    /**
     * How many pixels the player moves per key press.
     */
    private static final int MOVE_SPEED = 20;

    /**
     * The width of the game panel, used to clamp player movement within the screen.
     */
    private final int panelWidth;

    /**
     * Constructs the player ship centered horizontally near the bottom of the
     * panel.
     *
     * @param startX     initial x position (upper-left corner)
     * @param startY     initial y position (upper-left corner)
     * @param panelWidth width of the game area (used for boundary clamping)
     */
    public Player(int startX, int startY, int panelWidth) {
        super(startX, startY, 50, 40); // ship is 50 x 40 px
        this.panelWidth = panelWidth;
    }

    /**
     * Moves the player left by {@code MOVE_SPEED} pixels.
     * <p>
     * Stops at the left edge of the panel.
     */
    public void moveLeft() {
        x = Math.max(0, x - MOVE_SPEED);
    }

    /**
     * Moves the player right by {@code MOVE_SPEED} pixels.
     * <p>
     * Stops at the right edge of the panel.
     */
    public void moveRight() {
        x = Math.min(panelWidth - width, x + MOVE_SPEED);
    }

    /**
     * Draws the player spaceship as a simple triangular shape with an engine glow.
     * <p>
     * The ship is drawn in bright cyan to stand out against the dark background.
     *
     * @param g the {@code Graphics} context used for drawing
     */
    @Override
    public void draw(Graphics g) {
        // --- Ship body (triangle pointing upward) ---
        int[] bodyX = { x + width / 2, x, x + width };
        int[] bodyY = { y, y + height, y + height };
        g.setColor(new Color(0, 220, 255)); // cyan
        g.fillPolygon(bodyX, bodyY, 3);

        // --- Engine glow at the base ---
        g.setColor(new Color(255, 140, 0)); // orange
        g.fillRect(x + width / 2 - 6, y + height - 6, 12, 8);

        // --- Outline so the shape is crisp ---
        g.setColor(Color.WHITE);
        g.drawPolygon(bodyX, bodyY, 3);
    }
}
