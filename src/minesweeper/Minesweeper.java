package minesweeper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.PixelFormat;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author simon
 */
public class Minesweeper {

    private static final char NUMBERS[] = "012345678".toCharArray();
    private static final char DIGITS[] = "pqrstuvwxyz".toCharArray();
    private static final char DIGIT_MINUS = 'z';
    private static final char BOMBS[] = "BbD".toCharArray();
    private static final char QUESTIONMARK[] = "Ff".toCharArray();
    private static final char FLAG = 'X';
    private static final char UNKOWN = ' ';

    private static class GameState {

        private final int width;
        private final int height;
        private Cell field[][];
        private boolean bombs[][];
        private boolean gameOver;

        private GameState(int width, int height) {
            this.width = width;
            this.height = height;
            this.field = new Cell[width][height];
            this.bombs = new boolean[width][height];
            this.gameOver = false;
        }

        public boolean invalid(int x, int y) {
            return x < 0 || y < 0 || x >= getWidth() || y >= getHeight();
        }

        public boolean valid(int x, int y) {
            return !invalid(x, y);
        }

        public boolean isBomb(int x, int y) {
            if (invalid(x, y)) {
                return false;
            }
            return bombs[x][y];
        }

        public int count(int x, int y) {
            int cnt = 0;
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (i != 0 || j != 0) {
                        cnt += isBomb(x + i, y + j) ? 1 : 0;
                    }
                }
            }
            return cnt;
        }

        public synchronized void mark(int x, int y) {
            int val = field[x][y].getValue();
            if (val == UNKOWN) {
                field[x][y].setValue(FLAG);
            }
            if (val == FLAG) {
                field[x][y].setValue(QUESTIONMARK[0]);
            }
            if (val == QUESTIONMARK[0]) {
                field[x][y].setValue(UNKOWN);
            }
        }

        public synchronized void open(int x, int y) {
            if (gameOver) {
                return;
            }
            if (field[x][y].getValue() == UNKOWN) {
                if (bombs[x][y]) {
                    gameOver = true;
                }
                unlock(x, y);
            }
        }

        public void unlock(int x, int y) {
            if (bombs[x][y]) {
                field[x][y].setValue(BOMBS[0]);
            } else if (field[x][y].getValue() == UNKOWN) {
                unlock(x, y, new boolean[getWidth()][getHeight()]);
            }
        }

        public void unlock(int x, int y, boolean visited[][]) {
            if (invalid(x, y)) {
                return;
            }
            int cnt = count(x, y);
            field[x][y].setValue(NUMBERS[cnt]);
            if (count(x, y) == 0 && !visited[x][y]) {
                visited[x][y] = true;
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        unlock(x + i, y + j, visited);

                    }
                }
            }
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public static GameState createNewField(int width, int height, Consumer<JLabel> view, BiFunction<Integer, Integer, Boolean> isbomb) {
            GameState state = new GameState(width, height);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    view.accept(state.field[x][y] = new Cell(x, y));
                    state.bombs[x][y] = isbomb.apply(x, y);
                    state.field[x][y].setRightClickHook(state::mark);
                    state.field[x][y].setLeftClickHook(state::open);
                }
            }
            return state;
        }

        public static GameState createNewField(int width, int height, Consumer<JLabel> view) {
            return createNewField(width, height, view, (x, y) -> Math.random() < 0.01);
        }
    }

    public static void main(String[] args) {
        JPanel field = initGame(30, 16);
        JFrame frame = new JFrame("Minesweeper - Simon Lehner-D.");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setLayout(new BorderLayout());
        frame.add(field, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static JPanel initGame(int width, int height) {
        Dimension dim = new Dimension(width * 16, height * 16);

        JPanel panel = new JPanel(new GridLayout(height, width, 0, 0));
        panel.setBackground(Color.red);
        panel.setMaximumSize(dim);
        panel.setSize(dim);

        GameState.createNewField(width, height, panel::add, 
                (x,y) -> {
                    return Math.random() < 0.1;
                }
        );

        JPanel container = new JPanel();
        container.setSize(800, 500);
        container.add(panel);
        return container;
    }

    /**
     * TODO: maybe separate the view from the model so that saving the instance
     * state gets easier.
     */
    private static final class Cell extends JLabel {

        private int x, y;
        private char value;
        private BiConsumer<Integer, Integer> leftClickHook, rightClickHook;

        public Cell(int x, int y) {
            this(x, y, UNKOWN);
        }

        public Cell(int x, int y, char value) {
            this.x = x;
            this.y = y;
            this.setValue(value);
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    switch (e.getButton()) {
                        case MouseEvent.BUTTON3:
                            if (rightClickHook != null) {
                                rightClickHook.accept(x, y);
                            }
                            break;
                        case MouseEvent.BUTTON1:
                            if (leftClickHook != null) {
                                leftClickHook.accept(x, y);
                            }
                            break;
                    }
                }
            });
        }

        public void setRightClickHook(BiConsumer<Integer, Integer> rightClickHook) {
            this.rightClickHook = rightClickHook;
        }

        public void setLeftClickHook(BiConsumer<Integer, Integer> leftClickHook) {
            this.leftClickHook = leftClickHook;
        }

        public char getValue() {
            return this.value;
        }

        public void setValue(char value) {
            Icon me = Minesweeper.getIcon(value);
            this.value = value;
            this.setIcon(me);
            this.setSize(me.getIconWidth(), me.getIconHeight());
        }

    }

    public static Icon getIcon(char value) {
        return icons.computeIfAbsent(value, Minesweeper::loadIcon);
    }

    //the whole sprite with all the icons
    private static BufferedImage sprite = null;
    //the information where to cut which icon from sprite
    private static Map<Character, Rectangle> bounding = null;
    //the chached icons which where cut out of sprite with the help of bounding
    private static Map<Character, Icon> icons = new HashMap<>();

    private static Icon loadIcon(char value) {
        BufferedImage sprite = getSprite();
        Map<Character, Rectangle> bounding = getBounding();
        Rectangle rect = bounding.get(value);
        System.out.println("loading icon \"" + value + "\"");
        return new ImageIcon(sprite.getSubimage(
                rect.x, rect.y,
                rect.width, rect.height
        ));

    }

    private static BufferedImage getSprite() {
        if (sprite == null) {
            try {
                sprite = ImageIO.read(Minesweeper.class
                        .getResourceAsStream("res/sprites.png"));
            } catch (IOException ex) {
                Logger.getLogger(Minesweeper.class
                        .getName()).log(Level.SEVERE, null, ex);
                //denullify it to prevent it from reloading the same missing resource over and over again.
                sprite = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            }
        }
        return sprite;
    }

    private static Map<Character, Rectangle> getBounding() {
        if (bounding == null) {
            try {
                bounding = new HashMap<>();
                BufferedReader reader = new BufferedReader(new InputStreamReader(Minesweeper.class
                        .getResourceAsStream("res/sprites.config")));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#")) {
                        continue;
                    }
                    String data[] = line.substring(2).split(" ");
                    System.out.println(line.charAt(0) + " " + Arrays.toString(data));
                    bounding.put(line.charAt(0),
                            new Rectangle(
                                    Integer.parseInt(data[0]),
                                    Integer.parseInt(data[1]),
                                    Integer.parseInt(data[2]),
                                    Integer.parseInt(data[3])
                            ));
                }
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException | IOException ex) {
                Logger.getLogger(Minesweeper.class
                        .getName()).log(Level.SEVERE, "sprites.config is corrupt or missing", ex);
            }
        }
        return bounding;
    }

}
