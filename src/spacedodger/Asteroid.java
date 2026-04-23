package spacedodger;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 * Represents a single falling asteroid in the Space Dodger game.
 * <p>
 * Asteroids spawn at a random X position off the top of the screen
 * and fall downward at a configurable speed. Inherits position and dimension
 * tracking from {@link GameObject}.
 */
public class Asteroid extends GameObject {

    /**
     * Vertical speed in pixels per timer tick.
     * Determines how fast this asteroid falls.
     */
    private final int fallSpeed;

    /**
     * The color of this asteroid, varies slightly per asteroid for a natural,
     * varied look.
     */
    private final Color asteroidColor;

    /**
     * Shared {@link Random} instance for color variation among asteroids.
     */
    private static final Random rand = new Random();

    /**
     * Constructs a new {@code Asteroid} at the specified horizontal position and
     * with the given fall speed.
     * <p>
     * The asteroid spawns just above the visible screen and is assigned a random
     * shade of yellow/gold.
     *
     * @param x         the horizontal spawn position (randomized by GamePanel)
     * @param fallSpeed how fast (pixels per tick) this asteroid falls
     */
    public Asteroid(int x, int fallSpeed) {
        super(x, -40, 36, 36); // start just above the visible screen
        this.fallSpeed = fallSpeed;

        // Pick a random shade of yellow/gold
        int brightness = 200 + rand.nextInt(55); // High values (200-255) for Red and Green
        asteroidColor = new Color(brightness, brightness - 20, 0);
    }

    /**
     * Moves the asteroid downward by its fall speed.
     * <p>
     * Called once per timer tick by {@code GamePanel} to animate the asteroid's
     * descent.
     */
    public void fall() {
        y += fallSpeed;
    }

    /**
     * Checks if the asteroid has moved fully below the game panel.
     * <p>
     * Used by {@code GamePanel} to remove the asteroid and award a point.
     *
     * @param panelHeight the height of the game panel in pixels
     * @return {@code true} if the asteroid is off the bottom of the screen;
     *         {@code false} otherwise
     */
    public boolean isOffScreen(int panelHeight) {
        return y > panelHeight;
    }

    /**
     * Draws this asteroid as a rough octagonal shape with a highlight to suggest a
     * 3-D rocky surface.
     * <p>
     * The asteroid is rendered with a slightly varied color and uneven edges for a
     * natural look.
     *
     * @param g the {@code Graphics} context used for drawing
     */
    @Override
    public void draw(Graphics g) {
        int cx = x + width / 2;
        int cy = y + height / 2;
        int r = width / 2;

        // Octagonal body (approximated with a polygon)
        int sides = 8;
        int[] px = new int[sides];
        int[] py = new int[sides];
        for (int i = 0; i < sides; i++) {
            double angle = 2 * Math.PI * i / sides + Math.PI / sides;
            // Vary radius slightly so it looks uneven, like a real asteroid
            int jitter = (i % 3 == 0) ? -4 : 2;
            px[i] = (int) (cx + (r + jitter) * Math.cos(angle));
            py[i] = (int) (cy + (r + jitter) * Math.sin(angle));
        }
        g.setColor(asteroidColor);
        g.fillPolygon(px, py, sides);

        // Dark outline
        g.setColor(asteroidColor.darker());
        g.drawPolygon(px, py, sides);

        // Small highlight dot (upper-left)
        g.setColor(new Color(255, 255, 255, 80));
        g.fillOval(cx - r / 2, cy - r / 2, r / 3, r / 3);
    }
}
