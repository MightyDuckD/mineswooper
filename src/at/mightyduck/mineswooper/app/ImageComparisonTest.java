/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.app;

import at.mightyduck.mineswooper.util.Point2D;
import at.mightyduck.mineswooper.util.Point2D;
import at.mightyduck.mineswooper.util.ImageCompareService;
import at.mightyduck.mineswooper.util.ImageCompareService;
import at.mightyduck.mineswooper.util.ImageUtils;
import at.mightyduck.mineswooper.util.ImageUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Simon
 */
public class ImageComparisonTest {

    public void main() throws IOException {

        Map<String, BufferedImage> database = new HashMap<>();

        for (File file : new File("./res/imagecomparisontest/").listFiles()) {
            BufferedImage img = ImageIO.read(file);
            img = ImageUtils.scaleToSize(16, 16, img, null);
            img = ImageUtils.applyGaussianFilter(img, null);
            database.put(file.getAbsolutePath(), img);
        }

        ImageCompareService s = new ImageCompareService();
//        ImageHistogramCompare s = new ImageHistogramCompare();
        double result[][] = new double[database.size()][database.size()];
        Point2D pnt = new Point2D(0, 0);
        database.entrySet().stream().sorted((e0, e1) -> e0.getKey().compareTo(e1.getKey())).forEach((itemA) -> {
            System.out.println(itemA.getKey());
            database.entrySet().stream().sorted((e0, e1) -> e0.getKey().compareTo(e1.getKey())).forEach((itemB) -> {
                result[pnt.x][pnt.y] = (float) s.compare(itemA.getValue(), itemB.getValue());
                pnt.y++;
            });
            pnt.x++;
            pnt.y = 0;
        });

        BufferedImage resultImg = new BufferedImage(result.length, result.length, BufferedImage.TYPE_INT_ARGB);
        int binding[] = new int[] {80};
        JPanel panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                for (int x = 0; x < result.length; x++) {
                    for (int y = 0; y < result.length; y++) {
                        float tresh = binding[0]/100.0f;
                        float color = (float) result[x][y];
                        float colorA = (color < tresh) ? color : 0f;
                        float colorB = (color >= tresh) ? color : 0f;
                        resultImg.setRGB(x,y, new Color(colorA, 0, colorB).getRGB());
                    }
                }
                g.drawImage(resultImg, 40, 40, 512, 512, null);
            }
        };
        JSlider slider = new JSlider(0, 100, 80);
        slider.addChangeListener((ChangeEvent e) -> {
            panel.repaint();
            binding[0] = slider.getValue();
        });
        
        JFrame jFrame = new JFrame();
        jFrame.setLayout(new BorderLayout());
        jFrame.add(slider,BorderLayout.SOUTH);
        jFrame.add(panel,BorderLayout.CENTER);
        jFrame.setSize(800, 600);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
    }
}
