/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.util;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.function.Function;

/**
 *
 * @author Simon
 */
public class ImageUtils {

    private ImageUtils() {
    }

    public static BufferedImage scaleToSize(int width, int height, BufferedImage source, BufferedImage target) {
        double fx = (double) width / source.getWidth();
        double fy = (double) height / source.getHeight();
        AffineTransform t = new AffineTransform();
        t.scale(fx, fy);
        AffineTransformOp op = new AffineTransformOp(t, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(source, target);
    }

    public static BufferedImage applyGaussianFilter(BufferedImage img, BufferedImage target) {
        Kernel kernel = new Kernel(3, 3, new float[]{1f / 9f, 1f / 9f, 1f / 9f,
            1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f});
        BufferedImageOp op = new ConvolveOp(kernel);
        return op.filter(img, target);
    }

    /**
     *
     * @param color
     * @return lum = red * 0.30 + green * 0.59 + blue * 0.11
     */
    public static double toLuminance(Color color) {
        return (0.30 * color.getRed() + 0.59 * color.getGreen() + 0.11 * color.getBlue()) / 255.0;
    }

    public static double[][] toLumField(BufferedImage img) {
        double[][] data = new double[img.getWidth()][img.getHeight()];
        for (int x = 0; x < data.length; x++) {
            for (int y = 0; y < data[x].length; y++) {
                data[x][y] = toLuminance(new Color(img.getRGB(x, y)));
            }
        }
        return data;
    }

    public static BufferedImage toImage(double[][] data) {
        return toImage(data, (val) -> new Color(val.floatValue(),val.floatValue(), val.floatValue()));
    }

    public static BufferedImage toImage(double[][] data, Function<Double, Color> map) {
        BufferedImage image = new BufferedImage(data.length, data[0].length, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < data.length; x++) {
            for (int y = 0; y < data[x].length; y++) {
                Color color = map.apply(data[x][y]);
                image.setRGB(x, y, color.getRGB());
            }
        }
        return image;

    }
}
