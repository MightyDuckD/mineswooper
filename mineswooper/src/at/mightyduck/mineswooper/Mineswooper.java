/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper;

import at.mightyduck.mineswooper.core.AbstractField;
import at.mightyduck.mineswooper.core.CachedField;
import at.mightyduck.mineswooper.core.SimpleCountStrategy;
import at.mightyduck.mineswooper.core.Strategy;
import at.mightyduck.mineswooper.core.StrategyResult;
import at.mightyduck.mineswooper.util.ContextPickerJFrame;
import at.mightyduck.mineswooper.util.ImageCompareService;
import at.mightyduck.mineswooper.util.ImageUtils;
import at.mightyduck.mineswooper.util.Point2D;
import at.mightyduck.mineswooper.util.SerializableImageContainer;
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Simon
 */
public class Mineswooper {

    private static class Context {

        private int offx, offy, sx, sy, bx, by, fieldWidth, fieldHeight;

        public Context(int offx, int offy, int sx, int sy) {
            this(offx, offy, sx, sy, 0, 0);
        }

        public Context(int offx, int offy, int sx, int sy, int bx, int by) {
            this(offx, offy, sx, sy, bx, by, 30, 16);
        }
        
        public Context(int data[]) {
            this(data[0],data[1],data[2],data[3],data[4],data[5],data[6],data[7]);
        }

        public Context(int offx, int offy, int sx, int sy, int bx, int by, int fieldWidth, int fieldHeight) {
            this.offx = offx;
            this.offy = offy;
            this.sx = sx;
            this.sy = sy;
            this.bx = bx;
            this.by = by;
            this.fieldWidth = fieldWidth;
            this.fieldHeight = fieldHeight;
        }

        public int getFieldHeight() {
            return fieldHeight;
        }

        public int getFieldWidth() {
            return fieldWidth;
        }

        @Override
        public String toString() {
            return "Context{" + "offx=" + offx + ", offy=" + offy + ", sx=" + sx + ", sy=" + sy + ", bx=" + bx + ", by=" + by + '}';
        }

        private Rectangle get(int x, int y) {
            return new Rectangle(offx + x * (sx + bx), offy + y * (sy + by), sx, sy);
        }

        private static Context loadContext() {
            Object lock = new Object();
            ContextPickerJFrame frame = new ContextPickerJFrame();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            });
            frame.setVisible(true);
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Mineswooper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return new Context(frame.getAll());
        }

    }


    private static void move(Robot bot, Rectangle rect) {
        bot.mouseMove((int) rect.getCenterX(), (int) rect.getCenterY());
    }

    private static Database loadDatabase() {
        try {

            ObjectInputStream in = new ObjectInputStream(new FileInputStream("database.obj"));
            Database base = (Database) in.readObject();
            in.close();
            return base;
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return new Database();
    }
    
    public static void main(String[] args) throws IOException, Exception {
        Context context = Context.loadContext();
        int width = context.getFieldWidth();
        int height = context.getFieldHeight();
        System.out.println(context);
        if (true) {
            //throw new RuntimeException("do not run without setup.");
        }
        Database database = loadDatabase();
        CachedField field = new CachedField(new AbstractField(width, height) {
            @Override
            public char get(int x, int y) {
                try {
                    BufferedImage img = new Robot().createScreenCapture(context.get(x, y));
                    return database.search(img);
                } catch (AWTException ex) {
                    Logger.getLogger(Mineswooper.class.getName()).log(Level.SEVERE, null, ex);
                }
                return STONE_UNKOWN;
            }
        });

        Robot bot = new Robot();
        bot.setAutoDelay(60);
        boolean running = true;
        while (running) {
            System.out.println(field);
            bot.mouseMove(300, 300);
            System.out.println("move mouse now to stop the bot");

            Strategy st = new SimpleCountStrategy(field);
            StrategyResult result = st.call();

            Point mouse = MouseInfo.getPointerInfo().getLocation();
            if (mouse.x != 300 || mouse.y != 300) {
                System.out.println("mouse moved stopped the bot");
                break;
            }
            System.out.println("free " + result.getFree());

            for (Point2D point : result.getFree()) {
                System.out.println("free at " + point);
                move(bot, context.get(point.x, point.y));
                bot.mousePress(InputEvent.BUTTON1_MASK);
                bot.mouseRelease(InputEvent.BUTTON1_MASK);
            }
            for (Point2D point : result.getBombs()) {
                System.out.println("bomb at " + point);
                move(bot, context.get(point.x, point.y));
                bot.mousePress(InputEvent.BUTTON3_MASK);
                bot.mouseRelease(InputEvent.BUTTON3_MASK);
            }

            if (result.getFree().isEmpty() && result.getBombs().isEmpty()) {
                running = false;
            }

            bot.mouseMove(0, 0);
            field.setDirtyAll();
            System.out.println("next round");
        }
        System.out.println("finished");

        System.out.println("saving database");

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("database.obj"));
        out.writeObject(database);
        out.close();

        System.out.println("done - shutting down now");

    }
}
