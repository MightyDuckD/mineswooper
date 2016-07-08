/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.imageio.ImageIO;

/**
 * Because the BufferedImage class is not Serializable we have to put it into a
 * container which supports image serialization.
 *
 * @author Simon
 */
public class SerializableImageContainer implements Serializable {

    private transient BufferedImage img;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ImageIO.write(img, "png", bout);
        byte[] array = bout.toByteArray();
        out.writeInt(array.length);
        out.write(array);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int len = in.readInt();
        byte[] array = new byte[len];
        in.readFully(array);
        ByteArrayInputStream bin = new ByteArrayInputStream(array);
        img = ImageIO.read(bin);
    }

}
