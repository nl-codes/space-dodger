package spacedodger;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.*;

/**
 * The main game surface for Space Dodger.
 * <p>
 * Handles game state, rendering, input, and game loop logic. Tracks asteroids,
 * player, score, and exposes callbacks for UI updates and game over events.
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener {

    /**
     * Serialization ID for JFrame.
     */
    private static final long serialVersionUID = 1L;

    // ---------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------

    /**
     * Width of the game panel in pixels.
     */
    private static final int PANEL_WIDTH = 600;

    /**
     * Height of the game panel in pixels.
     */
    private static final int PANEL_HEIGHT = 650;

    /**
     * Delay between timer ticks in milliseconds (~60 fps).
     */
    private static final int TIMER_DELAY_MS = 16;

    /**
     * Number of timer ticks between asteroid spawns.
     */
    private static final int SPAWN_INTERVAL = 45;

    /**
     * Starting fall speed for asteroids (pixels per tick).
     */
    private static final int BASE_SPEED = 4;

    // ---------------------------------------------------------------
    // Game objects & state
    // ---------------------------------------------------------------

    /**
     * The player-controlled spaceship.
     */
    private Player player;

    /**
     * List of all active asteroids in the game.
     */
    final private ArrayList<Asteroid> asteroids;

    /**
     * Random number generator for asteroid spawning and other randomness.
     */
    final private Random rng;

    /**
     * True if the game is currently running (not idle or over).
     */
    private boolean gameRunning = false;

    /**
     * True if the game is over (player hit by asteroid).
     */
    private boolean gameOver = false;

    /**
     * The player's current score (number of asteroids dodged).
     */
    private int score = 0;

    /**
     * Number of timer ticks since the game started.
     */
    private int tickCount = 0;

    /**
     * Tracks the highest fall speed any asteroid has been given during the current
     * round.
     * Resets to BASE_SPEED each new game. Exposed via
     * {@link #getMaxAsteroidSpeed()} for UI display.
     */
    private int maxAsteroidSpeed = BASE_SPEED;

    // ── Callbacks wired up by SpaceDodger ──────────────────────────

    /**
     * Callback fired every time the score or speed changes so SpaceDodger can
     * refresh its UI labels.
     */
    final private Runnable onUpdate;

    /**
     * Callback fired once when the game ends. Used to show the save-score dialog
     * and Home button.
     */
    final private Runnable onGameOver;

    /**
     * Swing timer that drives the game loop.
     */
    final private Timer gameTimer;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Constructs the main game panel for Space Dodger.
     *
     * @param onUpdate   callback fired on every score or speed change and at
     *                   game-over
     * @param onGameOver callback fired exactly once when the player is hit
     */
    public GamePanel(Runnable onUpdate, Runnable onGameOver) {
        this.onUpdate = onUpdate;
        this.onGameOver = onGameOver;

        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);

        // Explicit double-buffering flag (Swing JPanel already buffers,
        // but this makes the intent clear per the spec)
        setDoubleBuffered(true);

        // Keyboard listener – panel must be focusable to receive key events
        setFocusable(true);

        rng = new Random();
        asteroids = new ArrayList<>();

        // Create timer but don't start until the player presses Start
        gameTimer = new Timer(TIMER_DELAY_MS, this);
    }

    /**
     * Attaches the KeyListener safely after construction.
     * <p>
     * Must be called before the panel can receive keyboard input.
     */
    public void init() {
        addKeyListener(this);
    }

    // ---------------------------------------------------------------
    // Public API called by SpaceDodger
    // ---------------------------------------------------------------

    /**
     * Resets all state and starts a fresh round.
     * Initializes the player, clears asteroids, resets score and speed, and starts
     * the timer.
     */
    public void startGame() {
        player = new Player(
                PANEL_WIDTH / 2 - 25,
                PANEL_HEIGHT - 80,
                PANEL_WIDTH);

        asteroids.clear();
        score = 0;
        tickCount = 0;
        maxAsteroidSpeed = BASE_SPEED;
        gameOver = false;
        gameRunning = true;

        if (onUpdate != null)
            onUpdate.run();

        gameTimer.start();
        requestFocusInWindow();
    }

    /**
     * Stops the game loop and returns to the idle splash screen.
     * <p>
     * Called by the Home button in SpaceDodger.
     * Resets all state and clears the panel.
     */
    public void resetToIdle() {
        gameTimer.stop();
        gameRunning = false;
        gameOver = false;
        player = null;
        maxAsteroidSpeed = BASE_SPEED;
        score = 0;
        tickCount = 0;
        asteroids.clear();
        repaint();
    }

    /**
     * Stops the timer and ends the game if the window closes or the game is exited.
     */
    public void stopGame() {
        gameTimer.stop();
        gameRunning = false;
    }

    /**
     * Returns the player's current score (number of asteroids dodged).
     *
     * @return the current score
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns whether the game is over (player hit by asteroid).
     *
     * @return {@code true} if the game is over, {@code false} otherwise
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Returns the highest asteroid fall speed reached in the current round.
     *
     * @return the maximum asteroid speed (pixels per tick)
     */
    public int getMaxAsteroidSpeed() {
        return maxAsteroidSpeed;
    }

    // ---------------------------------------------------------------
    // Game loop – fired by the Swing Timer every TIMER_DELAY_MS ms
    // ---------------------------------------------------------------

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameRunning)
            return;

        tickCount++;

        // Spawn a new asteroid on a fixed interval
        if (tickCount % SPAWN_INTERVAL == 0) {
            spawnAsteroid();
        }

        // Move every active asteroid downward
        for (Asteroid a : asteroids) {
            a.fall();
        }

        // Collision detection + remove off-screen asteroids
        processAsteroids();

        repaint();
    }

    /**
     * Creates a new Asteroid at a random X coordinate.
     * <p>
     * Speed increases with score, and the highest speed seen so far is recorded in
     * maxAsteroidSpeed.
     */
    private void spawnAsteroid() {
        int x = rng.nextInt(PANEL_WIDTH - 40); // random horizontal spawn
        int speedBonus = score / 10; // difficulty ramp
        int currentSpeed = BASE_SPEED + speedBonus;

        // Track the highest speed reached in this round
        if (currentSpeed > maxAsteroidSpeed) {
            maxAsteroidSpeed = currentSpeed;
            if (onUpdate != null)
                onUpdate.run(); // refresh speed label
        }

        asteroids.add(new Asteroid(x, currentSpeed));
    }

    /**
     * Iterates asteroids safely with an Iterator:
     * <ul>
     * <li>Collision with player → game over</li>
     * <li>Off the bottom → award 1 point, remove</li>
     * </ul>
     */
    private void processAsteroids() {
        Iterator<Asteroid> it = asteroids.iterator();

        while (it.hasNext()) {
            Asteroid a = it.next();

            // Bounding-box collision detection
            if (player.getBounds().intersects(a.getBounds())) {
                triggerGameOver();
                return;
            }

            // Asteroid safely dodged
            if (a.isOffScreen(PANEL_HEIGHT)) {
                it.remove();
                score++;
                if (onUpdate != null)
                    onUpdate.run();
            }
        }
    }

    /**
     * Stops the loop, repaints the game-over overlay, then fires the game-over
     * callback.
     * Ensures the overlay is visible before the dialog appears.
     */
    private void triggerGameOver() {
        gameRunning = false;
        gameOver = true;
        gameTimer.stop();

        if (onUpdate != null)
            onUpdate.run(); // turn score label red
        repaint(); // paint game-over overlay first

        // Fire the game-over callback AFTER repaint is queued so the
        // overlay is visible behind the dialog
        if (onGameOver != null) {
            SwingUtilities.invokeLater(onGameOver);
        }
    }

    // ---------------------------------------------------------------
    // Rendering
    // ---------------------------------------------------------------

    /**
     * Paints the game each frame.
     * <p>
     * Calls {@code super.paintComponent} to clear the previous frame and prevent
     * ghosting.
     * Draws asteroids, player, overlays, and splash/game-over screens as needed.
     *
     * @param g the {@code Graphics} context used for drawing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // IMPORTANT: must be called first

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (!gameRunning && !gameOver) {
            drawIdleScreen(g2d);
            return;
        }

        drawStars(g2d);

        for (Asteroid a : asteroids) {
            a.draw(g2d);
        }

        if (player != null) {
            player.draw(g2d);
        }

        if (gameOver) {
            drawGameOverOverlay(g2d);
        }
    }

    /**
     * Draws the idle/splash screen shown before the first game starts.
     *
     * @param g the {@code Graphics2D} context used for drawing
     */
    private void drawIdleScreen(Graphics2D g) {
        g.setColor(new Color(30, 30, 60));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g.setColor(new Color(0, 220, 255));
        g.setFont(new Font("Monospaced", Font.BOLD, 36));
        drawCenteredString(g, "SPACE DODGER", PANEL_HEIGHT / 2 - 20);

        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Monospaced", Font.PLAIN, 16));
        drawCenteredString(g, "Press  START  to play", PANEL_HEIGHT / 2 + 30);
    }

    /**
     * Draws a semi-transparent overlay when the round ends (game over).
     *
     * @param g the {@code Graphics2D} context used for drawing
     */
    private void drawGameOverOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g.setColor(new Color(255, 80, 80));
        g.setFont(new Font("Monospaced", Font.BOLD, 48));
        drawCenteredString(g, "GAME OVER", PANEL_HEIGHT / 2 - 50);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, 20));
        drawCenteredString(g, "Score: " + score, PANEL_HEIGHT / 2 + 10);
        drawCenteredString(g, "Max Speed: " + maxAsteroidSpeed + " px/tick",
                PANEL_HEIGHT / 2 + 42);
        drawCenteredString(g, "Press START to play again",
                PANEL_HEIGHT / 2 + 80);
        drawCenteredString(g, "or  HOME  to return to menu",
                PANEL_HEIGHT / 2 + 110);
    }

    /**
     *
     * /** Fixed-seed starfield so stars don't jump around each frame.
     */
    private void drawStars(Graphics2D g) {
        g.setColor(new Color(200, 200, 255, 140));
        Random starRng = new Random(42);
        for (int i = 0; i < 80; i++) {
            int sx = starRng.nextInt(PANEL_WIDTH);
            int sy = starRng.nextInt(PANEL_HEIGHT);
            int sr = starRng.nextInt(2) + 1;
            g.fillOval(sx, sy, sr, sr);
        }
    }

    /**
     * Draws a string horizontally centered on the panel at the specified
     * y-coordinate.
     *
     * @param g    the {@code Graphics2D} context used for drawing
     * @param text the string to draw
     * @param y    the y-coordinate for the baseline of the text
     */
    private void drawCenteredString(Graphics2D g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (PANEL_WIDTH - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    // ---------------------------------------------------------------
    // KeyListener – player movement
    // ---------------------------------------------------------------

    /**
     * Handles key press events for player movement.
     * Left/right arrow keys move the player. Ignores input if game is not running.
     *
     * @param e the {@code KeyEvent} representing the key press
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameRunning || player == null)
            return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> player.moveLeft();
            case KeyEvent.VK_RIGHT -> player.moveRight();
        }
        repaint();
    }

    /**
     * Handles key release events (unused).
     *
     * @param e the {@code KeyEvent} representing the key release
     */
    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Handles key typed events (unused).
     *
     * @param e the {@code KeyEvent} representing the key typed
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }
}
