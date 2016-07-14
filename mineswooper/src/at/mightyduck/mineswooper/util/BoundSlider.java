/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.util;

import java.awt.Component;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author simon
 */
public class BoundSlider extends JSlider {

    public BoundSlider(Component listener,int[] binding, int min, int max) {
        super(min,max,(min+max)/2);
        this.addChangeListener((ChangeEvent e) -> {
            binding[0] = getValue();
            listener.repaint();
            System.out.println("value " + binding[0]);
        });
    }


}
