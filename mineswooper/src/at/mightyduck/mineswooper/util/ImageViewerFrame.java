/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.util;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author simon
 */
public class ImageViewerFrame extends JFrame {

    private BufferedImage img;
    private Consumer<Graphics2D> overlay;
    private final JPanel panel = new JPanel() {

        @Override
        public void paint(Graphics g) {
            if (img != null) {
                g.drawImage(img, 0, 0, null);
            }
            if (overlay != null) {
                overlay.accept((Graphics2D) g);
            }
        }

    };

    public ImageViewerFrame() {
        this(null, null);
    }

    public ImageViewerFrame(String title) {
        this(null, null, title);
    }

    public ImageViewerFrame(BufferedImage img, Consumer<Graphics2D> overlay) {
        this(img, overlay, "ImageViewer");
    }

    public ImageViewerFrame(BufferedImage img, Consumer<Graphics2D> overlay, String title) {
        super(title);
        this.img = img;
        this.overlay = overlay;
        this.add(panel,BorderLayout.CENTER);
    }

    public void setImage(BufferedImage img) {
        this.img = img;
        if (img != null) {
            this.setSize(img.getWidth(), img.getHeight());
        }
        this.panel.repaint();
    }

    public void setOverlay(Consumer<Graphics2D> overlay) {
        this.overlay = overlay;
        this.panel.repaint();
    }

    public JPanel getPanel() {
        return panel;
    }

}
