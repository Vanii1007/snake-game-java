// GamePanel.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    // Screen & grid
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 180;

    // Snake state
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten = 0;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;

    Timer timer;
    Random random;

    public GamePanel() {
        random = new Random();
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());  // <— inner class below
        startGame();
    }

    public void startGame() {
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';

        for (int i = 0; i < GAME_UNITS; i++) { x[i] = 0; y[i] = 0; }
        for (int i = 0; i < bodyParts; i++) {
            x[i] = (bodyParts - 1 - i) * UNIT_SIZE;
            y[i] = 0;
        }

        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            // grid (optional)
            g.setColor(new Color(40, 40, 40));
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }

            // apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) g.setColor(new Color(0, 180, 0));
                else g.setColor(new Color(0, 120, 0));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // score
            g.setColor(Color.white);
            g.setFont(new Font("Consolas", Font.BOLD, 24));
            FontMetrics m = getFontMetrics(g.getFont());
            String score = "Score: " + applesEaten;
            g.drawString(score, (SCREEN_WIDTH - m.stringWidth(score)) / 2, g.getFont().getSize() + 8);
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U' -> y[0] -= UNIT_SIZE;
            case 'D' -> y[0] += UNIT_SIZE;
            case 'L' -> x[0] -= UNIT_SIZE;
            case 'R' -> x[0] += UNIT_SIZE;
        }
    }

    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
                break;
            }
        }
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }
        if (!running && timer != null) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Consolas", Font.BOLD, 24));
        FontMetrics m1 = getFontMetrics(g.getFont());
        String score = "Score: " + applesEaten;
        g.drawString(score, (SCREEN_WIDTH - m1.stringWidth(score)) / 2, g.getFont().getSize() + 8);

        g.setColor(Color.red);
        g.setFont(new Font("Consolas", Font.BOLD, 64));
        FontMetrics m2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - m2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        g.setColor(Color.lightGray);
        g.setFont(new Font("Consolas", Font.PLAIN, 22));
        FontMetrics m3 = getFontMetrics(g.getFont());
        String hint = "Press R to Restart";
        g.drawString(hint, (SCREEN_WIDTH - m3.stringWidth(hint)) / 2, SCREEN_HEIGHT / 2 + 40);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    // === Keyboard controls (inner class) ===
    class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> { if (direction != 'R') direction = 'L'; }
                case KeyEvent.VK_RIGHT -> { if (direction != 'L') direction = 'R'; }
                case KeyEvent.VK_UP -> { if (direction != 'D') direction = 'U'; }
                case KeyEvent.VK_DOWN -> { if (direction != 'U') direction = 'D'; }
                case KeyEvent.VK_R -> { if (!running) startGame(); }
            }
        }
    }
}
