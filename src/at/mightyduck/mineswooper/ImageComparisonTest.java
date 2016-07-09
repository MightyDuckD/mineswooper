/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper;

import at.mightyduck.mineswooper.imgtools.ImageCompareService;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 *
 * @author Simon
 */
public class ImageComparisonTest {

    public static void main(String[] args) throws IOException {

        Map<String, BufferedImage> database = new HashMap<>();

        for (File file : new File("./res/imagecomparisontest/").listFiles()) {
            BufferedImage img = ImageIO.read(file);
            database.put(file.getName(), img);
        }

        ImageCompareService s = new ImageCompareService();
        BufferedImage result = new BufferedImage(database.size(), database.size(), BufferedImage.TYPE_INT_ARGB);
        Point2D pnt = new Point2D(0, 0);
        database.entrySet().stream().sorted((e0,e1)->e0.getKey().compareTo(e1.getKey())).forEach((_item0) -> {
            System.out.println(_item0.getKey());
            database.entrySet().stream().sorted((e0,e1)->e0.getKey().compareTo(e1.getKey())).forEach((_item1) -> {
                float color = (float) s.compare(_item0.getValue(), _item1.getValue());
                System.out.println(color);
                result.setRGB(pnt.x, pnt.y, new Color(color, color, color).getRGB());
                pnt.y++;
            });
            pnt.x++;
            pnt.y = 0;
        });
        
        JFrame jFrame = new JFrame() {
            @Override
            public void paint(Graphics g) {
                g.drawImage(result, 40, 40, 512, 512,null);
            }
        };
        jFrame.setSize(800,600);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
    }
}
