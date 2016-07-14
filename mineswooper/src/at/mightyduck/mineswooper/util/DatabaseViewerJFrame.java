/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.util;

import at.mightyduck.mineswooper.Database;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * I wrote this class as fast as i could. Neither the prettiest piece of code
 * out there nor the best graphical interface. But hey! At least i can edit
 * database object without writing code now :D.
 *
 * @author simon
 */
public class DatabaseViewerJFrame extends JFrame {

    private final Database database;
    private JPanel list;
    private JScrollPane main;

    public DatabaseViewerJFrame(Database database) {
        this.setSize(800, 600);
        this.database = database;
        this.list = new JPanel();
        this.list.setLayout(new BoxLayout(this.list, BoxLayout.Y_AXIS));
        this.main = new JScrollPane(list);
        for (char c : database.getAllKeys()) {
            this.list.add(new EntryViewer(c));
        }
        this.add(this.main);
        this.pack();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Database database = Database.fromStream(new FileInputStream("database.obj"));
        DatabaseViewerJFrame frame = new DatabaseViewerJFrame(database);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private class EntryViewer extends JPanel {

        private final char me;
        private JPanel list;
        private JButton addbutton;

        public EntryViewer(char me) {
            this.me = me;
            this.setLayout(new BorderLayout(5, 5));
            this.add(new JLabel("  " + me), BorderLayout.WEST);
            this.add(this.list = new JPanel(), BorderLayout.CENTER);
            this.add(this.addbutton = new JButton("Add"), BorderLayout.EAST);
            this.list.setLayout(new BoxLayout(this.list, BoxLayout.X_AXIS));
            this.setBorder(BorderFactory.createLineBorder(Color.darkGray));
            this.addbutton.addActionListener((event) -> {
                try {
                    BufferedImage img = loadImage(DatabaseViewerJFrame.this);
                    if (img != null) {
                        img = DatabaseViewerJFrame.this.database.put(me, img);
                        this.list.add(new ImgViewer(img));
                    }
                } catch (Exception ex) {
                    Logger.getLogger(DatabaseViewerJFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            for (BufferedImage img : database.getAll(me)) {
                this.list.add(new ImgViewer(img));
            }
        }

        private class ImgViewer extends JPanel {

            private final BufferedImage img;

            public ImgViewer(BufferedImage img) {
                this.img = img;
                this.hookLeftClick(() -> {
                    EntryViewer.this.list.remove(ImgViewer.this);
                    DatabaseViewerJFrame.this.database.remove(ImgViewer.this.img);
                    DatabaseViewerJFrame.this.repaint();
                });
            }

            private void hookLeftClick(Runnable rn) {
                this.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            rn.run();
                        }
                    }
                });
            }

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                int size = Math.min(getWidth(), getHeight());
                g.drawImage(img, 0, 0, size, size, null);
            }
        }
    }

    private static BufferedImage loadImage(Component parent) throws Exception {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return ImageIO.read(chooser.getSelectedFile());
        }
        return null;
    }

}
