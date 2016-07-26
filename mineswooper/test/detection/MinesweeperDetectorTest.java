/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package detection;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author simon
 */
public class MinesweeperDetectorTest {

    private MinesweeperDetector det = new MinesweeperDetector();

    public MinesweeperDetectorTest() {
    }

    private BufferedImage load(String file) {
        try {
            return ImageIO.read(MinesweeperDetectorTest.class.getResourceAsStream(file));
        } catch (IOException ex) {
            Logger.getLogger(MinesweeperDetectorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Test
    public void test30x16() {
        test("resource/img30x16.png", 30, 16);
    }

    @Test
    public void testFail() {
        test("resource/imgFail1.png");
    }

    @Test
    public void testConsistency() {
        //When running the same test multiple times the result should be consistent.
        for (int i = 0; i < 20; i++) {
            test("resource/img30x16.png", 30, 16);
            test("resource/imgFail1.png");
        }
    }

    private void test(String img) {
        String message = String.format("%s doesn't contain a board", img);
        Board b1 = det.detect(load(img));
        assertNull(b1);
    }

    private void test(String img, int width, int height) {
        String message = String.format("%s contains a board with bounds %dx%d", img, width, height);
        Board b1 = det.detect(load(img));
        assertNotNull(message, b1);
        assertEquals(message, width, b1.width);
        assertEquals(message, height, b1.height);
    }
}
