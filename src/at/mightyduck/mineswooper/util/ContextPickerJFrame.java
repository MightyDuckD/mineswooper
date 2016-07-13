/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * TODO: find out why the transparent background isn't cleared on repaint.
 * Tested on Ubuntu 14.04 LTS
 *
 * @author slehner-dittenberger
 */
public class ContextPickerJFrame extends JFrame {

    public static final Color T = new Color(0, 0, 0, 0);
    private JTextField sx, sy, bx, by, width, height;
    private JPanel panel;
    private int offx = 30, offy = 30;

    public ContextPickerJFrame() {
        super("Context Picker");
        this.setSize(800, 600);
        this.setUndecorated(true);
        this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        sx = new JTextField("16");
        sy = new JTextField("16");
        bx = new JTextField("0");
        by = new JTextField("0");
        width = new JTextField("30");
        height = new JTextField("16");
        this.setBackground(new Color(0, 0, 0, 0));
        this.add(panel = new JPanelImpl());
        this.panel.setBackground(new Color(0, 0, 0, 12));
        for (JTextField t : new JTextField[]{sx, sy, bx, by, width, height}) {
            t.addActionListener((e) -> this.repaint());
        }
    }

    private int get(String text, int def) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
        }
        return def;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); //To change body of generated methods, choose Tools | Templates.
    }

    public int getOffX() {
        Point p = new Point(offx, offy);
        SwingUtilities.convertPointToScreen(p, panel);
        return p.x;
    }

    public int getOffY() {
        Point p = new Point(offx, offy);
        SwingUtilities.convertPointToScreen(p, panel);
        return p.y;
    }

    public int getSX() {
        return get(sx.getText(), 0);
    }

    public int getSY() {
        return get(sy.getText(), 0);
    }

    public int getBX() {
        return get(bx.getText(), 0);
    }

    public int getBY() {
        return get(by.getText(), 0);
    }

    public int getFieldWidth() {
        return get(width.getText(), 0);
    }

    public int getFieldHeight() {
        return get(height.getText(), 0);
    }

    public static void main(String[] args) {
        ContextPickerJFrame frame = new ContextPickerJFrame();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    private class JPanelImpl extends JPanel {

        public JPanelImpl() {
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D d = (Graphics2D) g.create();

            d.setComposite(AlphaComposite.Clear);
            d.setColor(Color.black);
            d.fillRect(0, 0, getWidth(), getHeight());
            d.setComposite(AlphaComposite.SrcOver);

            g.setColor(Color.RED);
            for (int x = 0; x < getFieldWidth(); x++) {
                for (int y = 0; y < getFieldHeight(); y++) {
                    int xx = offx + x * (getSX() + getBX());
                    int yy = offy + y * (getSY() + getBY());
                    g.drawRect(xx, yy, getSX(), getSY());
                }
            }
        }
    }

    @Override
    public void paintAll(Graphics g) {
        super.paintAll(g);
    }

    public int[] getAll() {
        return new int[]{
            getOffX(), getOffY(),
            getSX(), getSY(),
            getBX(), getBY(),
            getFieldWidth(), getFieldHeight()
        };
    }
}
