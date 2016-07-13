/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.util;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Simon
 */
public class ImageCompareService {

    public ImageCompareService() {
    }

    public synchronized double compare(BufferedImage a, BufferedImage b) {
        if (notSameSize(a, b)) {
            throw new IllegalArgumentException("images have to be the same size");
        }
        int width = a.getWidth();
        int height = a.getHeight();
        double fac = 0.01;
        double amount = 1;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double dist = distance(a.getRGB(x, y), b.getRGB(x, y)) * fac;
                amount *= 1 - dist + (fac / 2.0);
            }
        }
        double max = Math.pow(1 + (fac/2), width * height);
        amount = amount / max;
        return Math.min(1, Math.max(0, amount));
    }

    private static double distance(int rgb0, int rgb1) {
        Color a = new Color(rgb0);
        Color b = new Color(rgb1);

        double dr = Math.abs(a.getRed() - b.getRed()) / 255.0;
        double dg = Math.abs(a.getGreen() - b.getGreen()) / 255.0;
        double db = Math.abs(a.getBlue() - b.getBlue()) / 255.0;
        return Math.sqrt((dr * dr + dg * dg + db * db) / 3);
    }

    private static boolean notSameSize(BufferedImage a, BufferedImage b) {
        return a.getWidth() != b.getWidth() || a.getHeight() != b.getHeight();

    }

}
