package spacedodger;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

/**
 * Main application window for Space Dodger.
 * <p>
 * Handles UI setup, game panel integration, score persistence, and user
 * interactions.
 * Displays instructions, manages game state transitions, and provides high
 * score functionality.
 */
public class SpaceDodger extends JFrame {

    /**
     * Serialization ID for JFrame.
     */
    final private static long serialVersionUID = 1L;

    // ── UI components ───────────────────────────────────────────────

    /**
     * The main game panel where gameplay occurs.
     */
    private GamePanel gamePanel;

    /**
     * Label displaying the current score or game-over message.
     */
    final private JLabel scoreLabel;

    /**
     * Label displaying the current highest asteroid fall speed.
     */
    final private JLabel speedLabel;

    /**
     * Button to start or restart the game.
     */
    private JButton startButton;

    /**
     * Button to return to the splash screen (hidden until game-over).
     */
    private JButton homeButton;

    /**
     * Button to show the high scores dialog.
     */
    final private JButton showScoresBtn;

    /**
     * Path to the scores file for saving and loading high scores.
     */
    final private static String SCORES_FILE = "output/scores.csv";

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Constructs the main application window for Space Dodger.
     * Sets up the UI, game panel, and event handlers.
     */
    public SpaceDodger() {
        super("Space Dodger 🚀");

        // Show instructions dialog before anything else
        showInstructions();

        // ── Build UI ───────────────────────────────────────────────
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        // Top bar: score (left) + speed (right)
        JPanel topBar = new JPanel(new GridLayout(1, 2, 0, 0));
        topBar.setBackground(new Color(10, 10, 30));

        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Monospaced", Font.BOLD, 22));
        scoreLabel.setForeground(Color.CYAN);
        scoreLabel.setOpaque(true);
        scoreLabel.setBackground(new Color(10, 10, 30));
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        // Displays the current highest asteroid speed; updated every tick
        speedLabel = new JLabel("Asteroid Speed: 4 px/tick", SwingConstants.CENTER);
        speedLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        speedLabel.setForeground(new Color(255, 165, 0)); // orange
        speedLabel.setOpaque(true);
        speedLabel.setBackground(new Color(10, 10, 30));
        speedLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        topBar.add(scoreLabel);
        topBar.add(speedLabel);
        add(topBar, BorderLayout.NORTH);

        // ── Game panel ─────────────────────────────────────────────
        // onUpdate : refreshes both labels every tick
        // onGameOver : shows save dialog + reveals Home button
        gamePanel = new GamePanel(this::updateLabels, this::onGameOver);
        add(gamePanel, BorderLayout.CENTER);
        gamePanel.init();

        // ── Bottom bar: Start | Home | Show Scores ─────────────────
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        bottomBar.setBackground(new Color(10, 10, 30));

        // START / RESTART button
        startButton = new JButton("▶  START");
        styleButton(startButton, new Color(0, 160, 80));
        startButton.addActionListener(e -> {
            gamePanel.startGame();
            startButton.setText("\u21BA  RESTART");
            startButton.setBackground(new Color(180, 60, 60));
            homeButton.setVisible(false); // hide Home until next game-over
        });

        // HOME button – hidden until game-over
        homeButton = new JButton("\uD83C\uDFE0  HOME");
        styleButton(homeButton, new Color(60, 60, 200));
        homeButton.setVisible(false);
        homeButton.addActionListener(e -> goHome());

        // SHOW SCORES button – always visible
        showScoresBtn = new JButton("\uD83C\uDFC6  SCORES");
        styleButton(showScoresBtn, new Color(120, 60, 180));
        showScoresBtn.addActionListener(e -> showScores());

        bottomBar.add(startButton);
        bottomBar.add(homeButton);
        bottomBar.add(showScoresBtn);
        add(bottomBar, BorderLayout.SOUTH);

        // ── Finalise ───────────────────────────────────────────────
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ---------------------------------------------------------------
    // Label updates (fired every game tick via onUpdate callback)
    // ---------------------------------------------------------------

    /**
     * Refreshes the score label and the speed label.
     * <p>
     * Called by {@link GamePanel} on every score change and at game-over.
     */
    private void updateLabels() {
        if (gamePanel.isGameOver()) {
            scoreLabel.setText("GAME OVER  —  Score: " + gamePanel.getScore());
            scoreLabel.setForeground(new Color(255, 80, 80));
        } else {
            scoreLabel.setText("Score: " + gamePanel.getScore());
            scoreLabel.setForeground(Color.CYAN);
        }
        // Always update the speed label with the latest highest speed
        speedLabel.setText("Top Speed: " + gamePanel.getMaxAsteroidSpeed() + " px/tick");
    }

    // ---------------------------------------------------------------
    // Game-over handler (fired once per round, after timer stops)
    // ---------------------------------------------------------------

    /**
     * Handles game-over events.
     * <p>
     * Called by {@link GamePanel} via {@code SwingUtilities.invokeLater} after the
     * game ends.
     * Shows the Home button, then prompts the player to save their score.
     */
    private void onGameOver() {
        updateLabels();
        homeButton.setVisible(true);

        // Ask the player if they want to save their score
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Your final score: " + gamePanel.getScore()
                        + "\nTop asteroid speed: " + gamePanel.getMaxAsteroidSpeed() + " px/tick"
                        + "\n\nWould you like to save your score?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            // Ask for the player's name
            String name = JOptionPane.showInputDialog(
                    this,
                    "Enter your name:",
                    "Save Score",
                    JOptionPane.PLAIN_MESSAGE);

            // Only save if the player entered a non-blank name
            if (name != null && !name.trim().isEmpty()) {
                saveScore(name.trim(), gamePanel.getScore());
            }
        }
        // If NO is chosen the dialog simply closes; nothing is saved
    }

    // ---------------------------------------------------------------
    // Home button handler
    // ---------------------------------------------------------------

    /**
     * Returns the game to the idle splash screen.
     * <p>
     * Resets all labels and button text to their initial states.
     */
    private void goHome() {
        gamePanel.resetToIdle();

        scoreLabel.setText("Score: 0");
        scoreLabel.setForeground(Color.CYAN);
        speedLabel.setText("Top Speed: 4 px/tick");

        startButton.setText("\u25B6  START");
        startButton.setBackground(new Color(0, 160, 80));
        homeButton.setVisible(false);
    }

    // ---------------------------------------------------------------
    // Score persistence (scores.csv)
    // ---------------------------------------------------------------

    /**
     * Appends one line — "userName,score" — to scores.csv.
     * <p>
     * Creates the file if it does not yet exist.
     *
     * @param userName the name the player entered
     * @param score    the score for this round
     */
    private void saveScore(String userName, int score) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SCORES_FILE, true))) {
            // Write a header row the very first time the file is created
            File f = new File(SCORES_FILE);
            if (f.length() == 0) {
                pw.println("userName,score");
            }
            pw.println(userName + "," + score);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not save score:\n" + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Reads scores.csv, sorts the rows by score descending, and displays them in a
     * scrollable monospaced dialog.
     * <p>
     * If no scores are found, shows an informational dialog instead.
     */
    private void showScores() {
        File file = new File(SCORES_FILE);

        if (!file.exists()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No scores have been saved yet.\nPlay a round and save your score!",
                    "High Scores",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Read every valid data row (skip header and blank lines)
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                } // skip header
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    rows.add(new String[] { parts[0].trim(), parts[1].trim() });
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not read scores:\n" + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (rows.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "No scores found in file.", "High Scores",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Sort by score descending (highest first)
        rows.sort((a, b) -> {
            try {
                return Integer.parseInt(b[1]) - Integer.parseInt(a[1]);
            } catch (NumberFormatException e) {
                return 0;
            }
        });

        // Build formatted table
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(" %-4s  %-20s  %s%n", "RANK", "PLAYER", "SCORE"));
        sb.append(" ").append("-".repeat(35)).append("\n");
        for (int i = 0; i < rows.size(); i++) {
            sb.append(String.format(" #%-3d  %-20s  %s%n",
                    i + 1, rows.get(i)[0], rows.get(i)[1]));
        }

        // Show in a scrollable monospaced text area
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setEditable(false);
        textArea.setBackground(UIManager.getColor("OptionPane.background"));

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(360, Math.min(300, rows.size() * 22 + 60)));

        JOptionPane.showMessageDialog(
                this,
                scroll,
                "\uD83C\uDFC6  High Scores",
                JOptionPane.PLAIN_MESSAGE);
    }

    // ---------------------------------------------------------------
    // Instructions dialog (shown once at startup)
    // ---------------------------------------------------------------

    /**
     * Displays the instructions dialog at startup.
     * Explains the objective, controls, and tips for playing Space Dodger.
     */
    private void showInstructions() {
        String text = """
                +--------------------------------------+
                |       SPACE DODGER  \ud83d\ude80     |
                +--------------------------------------+
                |  OBJECTIVE:                          |
                |  Dodge falling asteroids as long     |
                |  as possible. Each asteroid that     |
                |  passes you earns 1 point.           |
                +--------------------------------------+
                |  CONTROLS:                           |
                |   <  Left Arrow  -- move left        |
                |   >  Right Arrow -- move right       |
                +--------------------------------------+
                |  TIPS:                               |
                |  * Asteroids speed up over time.     |
                |  * Save your score after each round. |
                |  * Press HOME to return to the menu. |
                +--------------------------------------+""";

        JTextArea ta = new JTextArea(text);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 14));
        ta.setEditable(false);
        ta.setBackground(UIManager.getColor("OptionPane.background"));

        JOptionPane.showMessageDialog(
                null, ta, "How to Play", JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------------------------------------------------------------
    // Helper: uniform button styling
    // ---------------------------------------------------------------

    /**
     * Applies uniform styling to a JButton for consistent UI appearance.
     *
     * @param btn the button to style
     * @param bg  the background color to apply
     */
    private void styleButton(JButton btn, Color bg) {
        btn.setFont(new Font("Monospaced", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
    }

    // ---------------------------------------------------------------
    // Entry point
    // ---------------------------------------------------------------

    /**
     * Entry point for the Space Dodger application.
     * Launches the main window on the Swing event dispatch thread.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SpaceDodger::new);
    }
}
