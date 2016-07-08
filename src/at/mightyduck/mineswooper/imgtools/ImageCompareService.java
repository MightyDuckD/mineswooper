/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.imgtools;

import com.sun.imageio.plugins.common.ImageUtil;
import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
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
        return amount / (width * height);
    }

    private static double distance(int rgb0, int rgb1) {
        Color c0 = new Color(rgb0);
        Color c1 = new Color(rgb1);
        return Math.abs(grey(c0) - grey(c1));
    }

    private static double grey(Color a) {
        return (a.getRed() * 0.3 + a.getGreen() * 0.59 + a.getBlue() * 0.11);
    }

    private static boolean notSameSize(BufferedImage a, BufferedImage b) {
        return a.getWidth() != b.getWidth() || a.getHeight() != b.getHeight();

    }

}
