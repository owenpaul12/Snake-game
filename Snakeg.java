import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

class SnakeGame extends JPanel implements ActionListener {

    // Game settings
    private final int TILE_SIZE = 25;
    private final int WIDTH = 600;
    private final int HEIGHT = 400;

    private LinkedList<Point> snake;
    private Point food;
    private int direction;
    private boolean gameOver;
    private Timer timer;
    private int score;
    private int timerInterval;  // Variable to control the speed (timer interval)

    // Direction constants
    private final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        snake = new LinkedList<>();
        snake.add(new Point(WIDTH / 2, HEIGHT / 2)); // Starting point of the snake
        direction = RIGHT;

        spawnFood();

        timerInterval = 100;  // Initial speed interval (100ms)
        timer = new Timer(timerInterval, this);
        timer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameOver) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            if (direction != DOWN) direction = UP;
                            break;
                        case KeyEvent.VK_DOWN:
                            if (direction != UP) direction = DOWN;
                            break;
                        case KeyEvent.VK_LEFT:
                            if (direction != RIGHT) direction = LEFT;
                            break;
                        case KeyEvent.VK_RIGHT:
                            if (direction != LEFT) direction = RIGHT;
                            break;
                        case KeyEvent.VK_W:  // Increase speed
                            adjustSpeed(true);
                            break;
                        case KeyEvent.VK_S:  // Decrease speed
                            adjustSpeed(false);
                            break;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    resetGame();
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            moveSnake();
            checkCollisions();
            checkFoodCollision();
            repaint();
        }
    }

    private void moveSnake() {
        Point head = snake.getFirst();
        Point newHead = null;

        switch (direction) {
            case UP:
                newHead = new Point(head.x, head.y - TILE_SIZE);
                break;
            case RIGHT:
                newHead = new Point(head.x + TILE_SIZE, head.y);
                break;
            case DOWN:
                newHead = new Point(head.x, head.y + TILE_SIZE);
                break;
            case LEFT:
                newHead = new Point(head.x - TILE_SIZE, head.y);
                break;
        }

        snake.addFirst(newHead);
        if (newHead.equals(food)) {
            score++;  // Increase score when food is eaten
            spawnFood();
        } else {
            snake.removeLast();
        }
    }

    private void checkCollisions() {
        Point head = snake.getFirst();

        // Check wall collision
        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            gameOver = true;
        }

        // Check self-collision
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver = true;
                break;
            }
        }
    }

    private void checkFoodCollision() {
        Point head = snake.getFirst();
        if (head.equals(food)) {
            spawnFood();
        }
    }

    private void spawnFood() {
        Random rand = new Random();
        // Ensure food is not placed on top of the snake
        boolean foodOnSnake = true;
        while (foodOnSnake) {
            food = new Point(rand.nextInt(WIDTH / TILE_SIZE) * TILE_SIZE, rand.nextInt(HEIGHT / TILE_SIZE) * TILE_SIZE);
            foodOnSnake = false;
            for (Point p : snake) {
                if (p.equals(food)) {
                    foodOnSnake = true;
                    break;
                }
            }
        }
    }

    private void resetGame() {
        snake.clear();
        snake.add(new Point(WIDTH / 2, HEIGHT / 2));
        direction = RIGHT;
        spawnFood();
        gameOver = false;
        score = 0;  // Reset the score
        timerInterval = 100;  // Reset speed
        timer.setDelay(timerInterval);
        repaint();
    }

    // Method to adjust the speed (timer interval)
    private void adjustSpeed(boolean increase) {
        if (increase && timerInterval > 30) {  // Minimum speed limit (30ms)
            timerInterval -= 10;  // Decrease the interval to increase speed
        } else if (!increase && timerInterval < 200) {  // Maximum speed limit (200ms)
            timerInterval += 10;  // Increase the interval to decrease speed
        }
        timer.setDelay(timerInterval);  // Update timer delay
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!gameOver) {
            // Draw the snake
            g.setColor(Color.GREEN);
            for (Point p : snake) {
                g.fillRect(p.x, p.y, TILE_SIZE, TILE_SIZE);
            }

            // Draw the food
            g.setColor(Color.RED);
            g.fillRect(food.x, food.y, TILE_SIZE, TILE_SIZE);

            // Draw the score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 10, 20);  // Display score at the top left
        } else {
            // Game over message
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over! Press Enter to Restart", 100, HEIGHT / 2);
            g.drawString("Final Score: " + score, 100, HEIGHT / 2 + 40); // Display final score
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
