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
import javax.swing.JOptionPane;

/**
 *
 * @author Simon
 */
public class Mineswooper {

    public static class Context {

        int offx, offy, sx, sy, bx, by;

        public Context(int offx, int offy, int sx, int sy) {
            this(offx, offy, sx, sy, 0, 0);
        }

        public Context(int offx, int offy, int sx, int sy, int bx, int by) {
            this.offx = offx;
            this.offy = offy;
            this.sx = sx;
            this.sy = sy;
            this.bx = bx;
            this.by = by;
        }

        private Rectangle get(int x, int y) {
            return new Rectangle(offx + x * (sx + bx), offy + y * (sy + by), sx, sy);
        }

    }

    public static class Database implements Serializable{

        private Map<Character, List<SerializableImageContainer>> data = new HashMap<>();
        private transient ImageCompareService service;
        private double tresh = 0.95;

        public Database() {
        }

        private char get(BufferedImage capture) {
            if(service == null)
                service = new ImageCompareService();
            capture = ImageUtils.scaleToSize(16, 16, capture, null);
            capture = ImageUtils.applyGaussianFilter(capture, null);
            Character result = null;
            double matchresult = 0;
            for (Entry<Character, List<SerializableImageContainer>> entry : data.entrySet()) {
                double match = 0;
                for (SerializableImageContainer img : entry.getValue()) {
                    match += service.compare(capture, img.getImg());
                }
                match /= entry.getValue().size();
                if (matchresult < match) {
                    matchresult = match;
                    result = entry.getKey();
                }
//                System.out.println("match " + match);
            }
            if (matchresult > tresh) {
                return result;
            }
            char ch = ask(capture);
            List<SerializableImageContainer> list = data.get(ch);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(new SerializableImageContainer(capture));
            data.put(ch, list);
            return ch;
        }

        private char ask(BufferedImage capture) {
            Icon icon = new ImageIcon(capture);
            String res = (String) JOptionPane.showInputDialog(
                    null,
                    "Hello world",
                    "Hello", JOptionPane.INFORMATION_MESSAGE,
                    icon,
                    null, "");
            return res.isEmpty() ? AbstractField.STONE_UNKOWN : res.charAt(0);
        }
    }

    public static void move(Robot bot, Rectangle rect) {
        bot.mouseMove((int) rect.getCenterX(), (int) rect.getCenterY());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        int width = 16;
        int height = 30;
        Context context = loadContext();
        Database database = loadDatabase();
        CachedField field = new CachedField(new AbstractField(width, height) {

            @Override
            public char get(int x, int y) {
                try {
//                    System.out.println("context " + context.get(x, y));
                    BufferedImage img = new Robot().createScreenCapture(context.get(x, y));
//                    try {
//                        ImageIO.write(img, "png", new File(String.format("./res/test/out.x%03d.y%03d.png", x,y)));
//                    } catch (IOException ex) {
//                        Logger.getLogger(Mineswooper.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                    return database.get(img);
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
    }

    private static Context loadContext() {
        return new Context(1471, 162, 20, 20, 5, 5);
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

}
