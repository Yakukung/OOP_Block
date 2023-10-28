package project_game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Tetris extends JPanel implements ActionListener {

    private static final long serialVersionUID = -8715353373678321308L;
    private static final int WIDTH = 12 * 26 + 10;
    private static final int HEIGHT = 26 * 23 + 25;

    private final Point[][][] Tetraminos = {
        // I-Piece
        {
            { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
            { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) },
            { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
            { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) }
        },

        // J-Piece
        {
            { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0) },
            { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2) },
            { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2) },
            { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0) }
        },

        // L-Piece
        {
            { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2) },
            { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2) },
            { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0) },
            { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0) }
        },

        // O-Piece
        {
            { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
            { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
            { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
            { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) }
        },

        // S-Piece
        {
            { new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
            { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
            { new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
            { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) }
        },

        // T-Piece
        {
            { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) },
            { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
            { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2) },
            { new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2) }
        },

        // Z-Piece
        {
            { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
            { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) },
            { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
            { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) }
        }
    };

    private final Color[] tetraminoColors = {
        Color.cyan, Color.blue, Color.orange, Color.yellow, Color.green, Color.pink, Color.red
    };

    private Point pieceOrigin;
    private int currentPiece;
    private int rotation;
    private ArrayList<Integer> nextPieces = new ArrayList<Integer>();

    private long score;
    private Color[][] well;

    private boolean isFallingFinished = false;
    private Timer timer;

    private boolean isGameOver = false; // สถานะของเกม

    public Tetris() {
        timer = new Timer(200, this);
        timer.start();
        setFocusable(true);
        addKeyListener(new TetrisKeyListener());
        init(); // เริ่มต้นเกม
        newPiece(); // สร้างเพิ่มครั้งแรก
    }

    private void init() {
        well = new Color[12][24];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 23; j++) {
                if (i == 0 || i == 11 || j == 22) {
                    well[i][j] = Color.GRAY;
                } else {
                    well[i][j] = Color.BLACK;
                }
            }
        }
    }

    public void newPiece() {
        pieceOrigin = new Point(5, 2);
        rotation = 0;
        if (nextPieces.isEmpty()) {
            Collections.addAll(nextPieces, 0, 1, 2, 3, 4, 5, 6);
            Collections.shuffle(nextPieces);
        }
        currentPiece = nextPieces.get(0);
        nextPieces.remove(0);
    }

    private boolean collidesAt(int x, int y, int rotation) {
        for (Point p : Tetraminos[currentPiece][rotation]) {
            if (well[p.x + x][p.y + y] != Color.BLACK) {
                return true;
            }
        }
        return false;
    }

    public void rotate(int i) {
        int newRotation = (rotation + i) % 4;
        if (newRotation < 0) {
            newRotation = 3;
        }
        if (!collidesAt(pieceOrigin.x, pieceOrigin.y, newRotation)) {
            rotation = newRotation;
        }
        repaint();
    }

    public void move(int i) {
        if (!collidesAt(pieceOrigin.x + i, pieceOrigin.y, rotation)) {
            pieceOrigin.x += i;
        }
        repaint();
    }

    public void dropDown() {
        if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
            pieceOrigin.y += 1;
        } else {
            fixToWell();
        }
        repaint();
    }

    public void fixToWell() {
        for (Point p : Tetraminos[currentPiece][rotation]) {
            well[pieceOrigin.x + p.x][pieceOrigin.y + p.y] = tetraminoColors[currentPiece];
        }
        clearRows();
        newPiece();
    }

    public void deleteRow(int row) {
        for (int j = row - 1; j > 0; j--) {
            for (int i = 1; i < 11; i++) {
                well[i][j + 1] = well[i][j];
            }
        }
    }

    public void clearRows() {
        boolean gap;
        int numClears = 0;

        for (int j = 21; j > 0; j--) {
            gap = false;
            for (int i = 1; i < 11; i++) {
                if (well[i][j] == Color.BLACK) {
                    gap = true;
                    break;
                }
            }
            if (!gap) {
                deleteRow(j);
                j += 1;
                numClears += 1;
            }
        }

        switch (numClears) {
            case 1:
                score += 100;
                break;
            case 2:
                score += 300;
                break;
            case 3:
                score += 500;
                break;
            case 4:
                score += 800;
                break;
        }
    }

    private void drawPiece(Graphics g) {
        g.setColor(tetraminoColors[currentPiece]);
        for (Point p : Tetraminos[currentPiece][rotation]) {
            g.fillRect((p.x + pieceOrigin.x) * 26,
                    (p.y + pieceOrigin.y) * 26,
                    25, 25);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) {
            if (!isFallingFinished) {
                if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
                    pieceOrigin.y += 1;
                } else {
                    fixToWell();
                    isFallingFinished = true;
                }
            } else {
                isFallingFinished = false;
                newPiece();
            }

            if (well[1][0] != Color.BLACK) {
                gameOver(); // ถ้าบล็อกชนด้านบนแล้วให้เรียกเมธอด gameOver()
            }
        } else {
            gameOver(); // ถ้าเกมเป็น "Game Over" ให้เรียกเมธอด gameOver()
        }

        repaint();
    }

    private class TetrisKeyListener implements KeyListener {
        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    rotate(-1);
                    break;
                case KeyEvent.VK_DOWN:
                    rotate(+1);
                    break;
                case KeyEvent.VK_LEFT:
                    move(-1);
                    break;
                case KeyEvent.VK_RIGHT:
                    move(+1);
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown();
                    score += 1;
                    break;
            }
        }

        public void keyReleased(KeyEvent e) {
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.fillRect(0, 0, 26 * 12, 26 * 23);
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 23; j++) {
                g.setColor(well[i][j]);
                g.fillRect(26 * i, 26 * j, 25, 25);
            }
        }

        g.setColor(Color.WHITE);
        g.drawString("" + score, 19 * 12, 25);

        drawPiece(g);
    }

    // เมธอดในการจัดการเมื่อเกมจบ
    private void gameOver() {
        setGameOver(true);
        timer.stop(); // หยุดตัวจับเวลา
        int choice = JOptionPane.showOptionDialog(
            this,
            "Game Over!\nYour score: " + score + "\nPlay again?",
            "Game Over",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            new String[]{"New Game", "Exit"},
            "New Game"
        );

        if (choice == JOptionPane.YES_OPTION) {
            startNewGame();
        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Tetris");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(WIDTH, HEIGHT);
        Tetris tetris = new Tetris();
        f.add(tetris);
        f.setVisible(true);
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    private void startNewGame() {
        score = 0;
        init();
        newPiece();
        setGameOver(false);
        timer.start();
    }
}
