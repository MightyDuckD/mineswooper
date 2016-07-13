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
import java.awt.HeadlessException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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
//        this.setOpacity((float) 0.5);
        this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        sx = new JTextField("16");
        sy = new JTextField("16");
        bx = new JTextField("0");
        by = new JTextField("0");
        width = new JTextField("30");
        height = new JTextField("16");
        this.setBackground(new Color(0, 0, 0, 0));
        this.getContentPane().setBackground(new Color(0, 0, 0, 0));
        this.add(panel = new JPanelImpl());
        panel.setBackground(new Color(0, 0, 0, 0));
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
        this.remove(panel);
        this.add(panel = new JPanelImpl());
        super.paint(g); //To change body of generated methods, choose Tools | Templates.
    }

    public int getOffX() {
        System.out.printf("%d %d %d\n", getX(), panel.getX(), offx);
        return getX() + panel.getX() + offx;
    }

    public int getOffY() {
        System.out.printf("%d %d %d\n", getY(), panel.getY(), offy);
        return getY() + panel.getY() + offy;
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
        System.out.println(frame.getOffX());
        System.out.println(frame.getOffY());
    }

    private class JPanelImpl extends JPanel {

        public JPanelImpl() {
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D d = (Graphics2D) g.create();

            AlphaComposite co = AlphaComposite.getInstance(AlphaComposite.SRC);
            d.setComposite(co);
            d.setColor(T);
            d.create().clearRect(0, 0, getWidth(), getHeight());
            d.fillRect(0, 0, getWidth(), getHeight());

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

    public int[] getAll() {
        return new int[]{
            getOffX(),getOffY(),
            getSX(),getSY(),
            getBX(),getBY(),
            getFieldWidth(),getFieldHeight()
        };
    }
}
