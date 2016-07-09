/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.imgtools;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Simon
 */
public class ImageCompareService {

    private static final int BASE_SIZE = 64;

    private BufferedImage img0 = new BufferedImage(BASE_SIZE, BASE_SIZE, BufferedImage.TYPE_INT_RGB);
    private BufferedImage img1 = new BufferedImage(BASE_SIZE, BASE_SIZE, BufferedImage.TYPE_INT_RGB);

    public ImageCompareService() {
    }
    
    public synchronized double compare(BufferedImage a, BufferedImage b) {
        if (notSameSize(a, b)) {
            throw new IllegalArgumentException("images have to be the same size");
        }
        int width = a.getWidth();
        int height = a.getHeight();
        double amount = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                amount += distance(a.getRGB(x, y), b.getRGB(x, y));
            }
        }
        return 1 - (amount / (width * height)) ;
    }

    private static double distance(int rgb0, int rgb1) {
        Color a = new Color(rgb0);
        Color b = new Color(rgb1);

        double dr = Math.abs(a.getRed() - b.getRed()) / 255.0;
        double dg = Math.abs(a.getGreen() - b.getGreen()) / 255.0;
        double db = Math.abs(a.getBlue() - b.getBlue()) / 255.0;
        return Math.sqrt(dr * dr + dg * dg + db * db);
    }
    
    private static boolean notSameSize(BufferedImage a, BufferedImage b) {
        return a.getWidth() != b.getWidth() || a.getHeight() != b.getHeight();

    }

}
