package spacedodger;

import java.awt.Graphics;

/**
 * Abstract base class for all drawable game objects in Space Dodger.
 *
 * Stores position and dimension, and enforces a draw contract for subclasses.
 * Provides bounding box for collision detection.
 */
public abstract class GameObject {

    /**
     * The x-coordinate of the object's position on the game panel.
     * How much horizontally from the upper-left corner.
     */
    protected int x;

    /**
     * The y-coordinate of the object's position on the game panel
     * How much vertically from the upper-left corner).
     */
    protected int y;

    /**
     * The width of the object's bounding box in pixels.
     */
    protected int width;

    /**
     * The height of the object's bounding box in pixels.
     */
    protected int height;

    /**
     * Constructs a {@code GameObject} with a given position and size.
     *
     * @param x      the initial x-coordinate (upper-left corner)
     * @param y      the initial y-coordinate (upper-left corner)
     * @param width  the object's width in pixels
     * @param height the object's height in pixels
     */
    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Draws this game object using the provided {@link Graphics} context.
     * <p>
     * Every subclass must implement this method to render itself.
     * Called once per frame by the {@code GamePanel}.
     *
     * @param g the {@code Graphics} context provided by {@code paintComponent}
     */
    public abstract void draw(Graphics g);

    /**
     * Returns a {@link java.awt.Rectangle} representing this object's bounding box.
     * <p>
     * Used for collision detection with other game objects.
     *
     * @return a {@code Rectangle} with this object's position and dimensions
     */
    public java.awt.Rectangle getBounds() {
        return new java.awt.Rectangle(x, y, width, height);
    }

    /**
     * Returns the x-coordinate of this object's position (upper-left corner).
     *
     * @return the x-coordinate in pixels
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of this object's position (upper-left corner).
     *
     * @return the y-coordinate in pixels
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the width of this object's bounding box in pixels.
     *
     * @return the width in pixels
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of this object's bounding box in pixels.
     *
     * @return the height in pixels
     */
    public int getHeight() {
        return height;
    }
}
