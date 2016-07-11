/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.JTextField;

/**
 *
 * @author slehner-dittenberger
 */
public class ContextPickerJFrame extends JFrame {

    private JTextField sx, sy, bx, by, width, height;
    private int offx = 30, offy = 30;

    public ContextPickerJFrame() {
        super("Context Picker");
        this.setSize(800, 600);
        this.setUndecorated(true);
        this.setOpacity((float) 0.5);
        this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {

            }
        });
        sx = new JTextField("16");
        sy = new JTextField("16");
        bx = new JTextField("0");
        by = new JTextField("0");
        width = new JTextField("30");
        height = new JTextField("16");
        for (JTextField t : new JTextField[]{sx, sy, bx, by, width, height}) {
            t.addActionListener((e) -> this.repaint());
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(Color.RED);
        for (int x = 0; x < getFieldWidth(); x++) {
            for (int y = 0; y < getFieldHeight(); y++) {
                int xx = offx + x * (getSX() + getBX());
                int yy = offy + y * (getSY() + getBY());
                g.drawRect(xx, yy, getSX(), getSY());
            }
        }

    }

    private int get(String text, int def) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
        }
        return def;
    }

    public int getOffX() {
        return getX() + offx;
    }

    public int getOffY() {
        return getY() + offy;
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
}
