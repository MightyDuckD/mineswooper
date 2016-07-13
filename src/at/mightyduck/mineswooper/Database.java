/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper;

import at.mightyduck.mineswooper.core.AbstractField;
import at.mightyduck.mineswooper.util.ImageCompareService;
import at.mightyduck.mineswooper.util.ImageUtils;
import at.mightyduck.mineswooper.util.SerializableImageContainer;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author simon
 */
public class Database implements Serializable {

    private static final long serialVersionUID = 1584371923167760269L;
    private Map<Character, List<SerializableImageContainer>> data = new HashMap<>();
    private transient ImageCompareService service;
    private double tresh = 0.95;
    private int basesize = 16;

    public Database() {
    }

    public List<Character> getAllKeys() {
        return new ArrayList<>(data.keySet());
    }
    
    /**
     * @param ch
     * @return The # of images in this database entry or -1 if no entry was found.
     */
    public int getSize(char ch) {
        List l = data.get(ch);
        if(l == null)
            return -1;
        return l.size();
    }

    public List<BufferedImage> getAll(char ch) {
        return data.getOrDefault(ch, Collections.emptyList())
                .stream()
                .map(c -> c.getImg())
                .collect(Collectors.toList());
    }

    public double getTresh() {
        return tresh;
    }

    public int getBasesize() {
        return basesize;
    }

    public char search(BufferedImage capture) {
        if (service == null) {
            service = new ImageCompareService();
        }
        capture = normalize(capture);
        Character result = null;
        double matchresult = 0;
        for (Map.Entry<Character, List<SerializableImageContainer>> entry : data.entrySet()) {
            for (SerializableImageContainer img : entry.getValue()) {
                double match = service.compare(capture, img.getImg());
                if (matchresult < match) {
                    matchresult = match;
                    result = entry.getKey();
                }
            }
        }
        if (matchresult > tresh) {
            return result;
        }
        char ch = ask(capture);
        put(ch, capture);
        return ch;
    }

    public char ask(BufferedImage capture) {
        Icon icon = new ImageIcon(capture);
        String res = (String) JOptionPane.showInputDialog(null, "Hello world", "Hello", JOptionPane.INFORMATION_MESSAGE, icon, null, "");
        return res.isEmpty() ? AbstractField.STONE_UNKOWN : res.charAt(0);
    }

    public static Database fromStream(InputStream input) throws IOException, ClassNotFoundException {
        ObjectInputStream objin = new ObjectInputStream(input);
        return (Database) objin.readObject();
    }

    /**
     * Removes the img from the database. Important note! This method does not
     * perform a img comparison algorithm. It just tests if this object has the
     * same reference like an image from the database.
     *
     * @param img
     */
    public void remove(BufferedImage img) {
        for (Map.Entry<Character, List<SerializableImageContainer>> entrySet : data.entrySet()) {
            List<SerializableImageContainer> value = entrySet.getValue();
            for (int i = 0; i < value.size(); i++) {
                if (value.get(i).getImg() == img) {
                    value.remove(i--);
                }
            }
        }
    }

    /**
     * @param ch
     * @param img
     * @return The image which is actually inserted(may be other image than
     * parameter)
     */
    public BufferedImage put(char ch, BufferedImage img) {
        if (img == null) {
            throw new IllegalArgumentException("image must not be null");
        }
        img = normalize(img);
        List<SerializableImageContainer> list = data.get(ch);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(new SerializableImageContainer(img));
        data.put(ch, list);
        return img;
    }

    private BufferedImage normalize(BufferedImage img) {
        img = ImageUtils.scaleToSize(basesize, basesize, img, null);
        img = ImageUtils.applyGaussianFilter(img, null);
        return img;
    }
}
