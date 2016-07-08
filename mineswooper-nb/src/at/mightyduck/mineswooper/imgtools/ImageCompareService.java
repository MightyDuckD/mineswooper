/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.imgtools;

import java.awt.Image;

/**
 *
 * @author Simon
 */
public interface ImageCompareService {
   
    double compare(Image a, Image b);
    
}
