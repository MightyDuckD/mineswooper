/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.util;

import com.sun.imageio.plugins.common.ImageUtil;
import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * TODO: implementieren und bessere api und schneller und multithreaded usw.
 * Maybe OpenCL oder irgendeine Library?
 *
 * @author Simon
 */
public class Hough {

    public static double TRESH = 0.6;
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
                result[i] += data[i][j] > TRESH ? 1 : 0;
            }
        }
        System.out.println("hough fin");
    }

    public static void main(String args[]) throws IOException {

        System.out.println("loading image");
        BufferedImage img0 =ImageIO.read(new File("htest.png"));
        double data[][] = ImageUtils.toLumField(img0);
        BufferedImage img = ImageUtils.toImage(data,val -> val < TRESH?Color.white:Color.black);
        int result[] = new int[data.length];
        System.out.println("running hough");
        ExecutorService service = Executors.newFixedThreadPool(Math.min(1, Runtime.getRuntime().availableProcessors() - 1));
        horizontalCountHT(data, result, service);
        System.out.println("printing result");
        String str = Arrays.toString(result);
        System.out.println("result " + str);
        int tresh[] = {80};
        ImageViewerFrame frame = new ImageViewerFrame("Hough");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BoundSlider slider = new BoundSlider(frame,tresh,0,max(result));
        frame.add(slider,BorderLayout.SOUTH);
        frame.setVisible(true);
        frame.setImage(img);
        frame.setOverlay((g) -> {
            for (int i = 0; i < result.length; i++) {
                if (result[i] > tresh[0]) {
                    g.drawLine(i, 0, i, img.getHeight());
                }
            }
        });
    }
    public static int max(int ar[]) {
        int mx = 0;
        for(int i : ar)mx = Math.max(i,mx);
        return mx;
    }
}
