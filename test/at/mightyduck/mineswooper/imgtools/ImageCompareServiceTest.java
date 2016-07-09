/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.imgtools;

import at.mightyduck.mineswooper.util.ImageCompareService;
import java.awt.Color;
import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Simon
 */
public class ImageCompareServiceTest {

    public ImageCompareServiceTest() {
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDifferentSizedImages() {
        ImageCompareService s = new ImageCompareService();
        BufferedImage img0 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        BufferedImage img1 = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
        s.compare(img0, img1);
    }

    @Test
    public void testSomeMethod() {
        double DELTA = 0.0002;

        ImageCompareService s = new ImageCompareService();
        BufferedImage black0 = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
        BufferedImage black1 = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
        BufferedImage white0 = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < 16; i++) {
            white0.setRGB(i / 4, i % 4, Color.WHITE.getRGB());
        }

        assertEquals("the same imgage should be 100% similar", 1, s.compare(black0, black0), DELTA);
        assertEquals("two black images should be 100% similar", 1, s.compare(black0, black1), DELTA);
        assertEquals("a black and a white images should be 0% similar", 0, s.compare(black1, white0), DELTA);

        //color one pixel of the 16 black again
        white0.setRGB(0, 0, Color.BLACK.getRGB());
        assertEquals("if 1/16 of the white image is black then this and a black image are 1/16 similar = 6.25%", 0.0625, s.compare(black0, white0), DELTA);
    }

}
