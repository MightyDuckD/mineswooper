/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.util;

import com.sun.imageio.plugins.common.ImageUtil;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * TODO: implementieren und bessere api und schneller und multithreaded usw.
 * Maybe OpenCL oder irgendeine Library?
 *
 * @author Simon
 */
public class Hough {

    public static double TRESH = 0.6;

    @Deprecated
    public static void horizontalCountHT(double data[/*x*/][/*y*/], int result[], ExecutorService serv) {
        int batchSize = 150;
        List<Future> futures = new ArrayList<>();
        IntStream.iterate(0, i -> i + batchSize)
                .limit(1 + result.length / batchSize)
                .forEach((i -> {
                    serv.submit(() -> {
                        horizontalCount(data, result, i, Math.min(i + batchSize, result.length) - i);
                    });
                }));
        futures.stream().forEach(f -> {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(Hough.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public static void horizontalCount(double data[/*x*/][/*y*/], int result[], int offset, int len) {
        System.out.println("hough started " + offset + " " + len);
        double tresh = 0.5;
        for (int i = offset; i < offset + len; i++) {
            int size = data[i].length;
            for (int j = 0; j < size; j++) {
                result[i] += data[i][j] < TRESH ? 1 : 0;
            }
        }
        System.out.println("hough fin");
    }

    public static void verticalCount(double data[/*x*/][/*y*/], int result[], int offset, int len) {
        System.out.println("hough started " + offset + " " + len);
        double tresh = 0.5;
        for (int i = offset; i < offset + len; i++) {
            int size = data.length;
            for (int j = 0; j < size; j++) {
                result[i] += data[j][i] < TRESH ? 1 : 0;
            }
        }
        System.out.println("hough fin");
    }

    public static void main(String args[]) throws IOException, AWTException {

        System.out.println("loading image");
        BufferedImage img0 = ImageIO.read(new File("htest.png"));
//        Robot robot = new Robot();
//        GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
//        img0 = robot.createScreenCapture(config.getBounds());
        double test[][] = ImageUtils.toLumField(img0);
        double data1[][] = new double[test.length][test[0].length];
        //double data[][] = test;
        double data[][] = test;
        ImageUtils.sobelOperator(test, data, (x,y)->1-Math.min(1, Math.max(0,y)));
        BufferedImage img = ImageUtils.toImage(data, val -> val < TRESH ? Color.white : Color.black);
        int resultH[] = new int[data.length];
        int resultV[] = new int[data[0].length];
        System.out.println("running hough");

        horizontalCount(data, resultH, 0, resultH.length);
        verticalCount(data, resultV, 0, resultV.length);

        System.out.println("printing result");
        String str = Arrays.toString(resultH);
        System.out.println("result " + str);
        int tresh[] = {205};
        ImageViewerFrame frame = new ImageViewerFrame("Hough");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        BoundSlider slider = new BoundSlider(frame, tresh, 0, max(resultH));
        frame.add(slider, BorderLayout.SOUTH);
        frame.setVisible(true);
        frame.setImage(img);
        
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                frame.repaint();
            }
        });
        frame.setOverlay((g) -> {
            g.setColor(Color.darkGray);
            for (int i = 0; i < resultH.length; i++) {
                if (resultH[i] > tresh[0]) {
                    g.drawLine(i, 0, i, img.getHeight());
                }
            }
            for (int i = 0; i < resultV.length; i++) {
                if (resultV[i] > tresh[0]) {
                    g.drawLine(0, i, img.getWidth(), i);
                }
            }
            Group bestH = tryIt(resultH, tresh[0]);
            g.setColor(Color.red);
            if (bestH != null) {
                for (int i = 0; i < bestH.count; i++) {
                    int position = bestH.offset + bestH.size * i;
                    g.drawLine(position, 0, position, img.getHeight());
                }
            }
            Group bestV = tryIt(resultV, tresh[0]);
            g.setColor(Color.red);
            if (bestV != null) {
                for (int i = 0; i < bestV.count; i++) {
                    int position = bestV.offset + bestV.size * i;
                    g.drawLine(0, position, img.getWidth(), position);
                }
            }
        });
        frame.setOverlay((g) -> {
            Group bestH = tryIt(resultH, tresh[0]);
            Group bestV = tryIt(resultV, tresh[0]);
            if(bestH == null || bestV == null)
                return;
            int w = bestH.count;
            int h = bestV.count;
            int sx = bestH.size;
            int sy = bestV.size;
            int ox = bestH.offset;
            int oy = bestV.offset;
            Point o = new Point(ox, oy);
            //SwingUtilities.convertPointFromScreen(o, frame);
            int offx = o.x;
            int offy = o.y;
            frame.setTitle(w + " " + h);

            g.setColor(Color.RED);
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    int xx = offx + x * (sx);
                    int yy = offy + y * (sy);
                    g.drawRect(xx, yy, sx, sy);
                }
            }
        });
        //frame.setOverlay(null);
    }

    public static int max(int ar[]) {
        int mx = 0;
        for (int i : ar) {
            mx = Math.max(i, mx);
        }
        return mx;
    }

    public static Group tryIt(int result[], int tr) {
        System.out.println("tryit");
        boolean data[] = new boolean[result.length];
        for (int i = 0; i < result.length; i++) {
            data[i] = result[i] > tr;
        }
        //
        Group bestMatch = null;
        for (int i = 0; i < data.length; i++) {
            if (data[i]) {
                //minsize = 10 weil alles was kleiner ist sowieso kaum als bild erkannt wird
                Group next = extractLongestGroup(i, 10, data.length, (index) -> {
                    return data[index] || data[Math.max(0, index - 1)] || data[Math.min(data.length - 1, index + 1)];
                });
                if (bestMatch == null || next.count > bestMatch.count) {
                    bestMatch = next;
                }
            }
        }
        return bestMatch;
    }

    public static Group extractLongestGroup(int offset, int minsize, int length, Function<Integer, Boolean> hit) {
        Group longest = new Group(offset, 0, 1);
        for (int size = minsize; size < length - offset; size++) {
            int count;
            for (count = 0; offset + size * count < length; count++) {
                if (!hit.apply(offset + size * count)) {
                    break;
                }
            }
            if (longest.count < count) {
                longest = new Group(offset, size, count);
            }
        }

        return longest;
    }

    public static class Group {

        //    <-offset>   | <-size-> |         |          |   <- count = 4
        public final int offset, size, count;

        public Group(int offset, int size, int count) {
            this.offset = offset;
            this.size = size;
            this.count = count;
        }

        @Override
        public String toString() {
            return "Group{" + "offset=" + offset + ", size=" + size + ", count=" + count + '}';
        }

    }
}
