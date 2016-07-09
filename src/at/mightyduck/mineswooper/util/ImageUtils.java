/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.util;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

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
}
