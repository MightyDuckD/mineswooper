/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.util;

/**
 * TODO: implementieren und bessere api und schneller und multithreaded usw.
 * @author Simon
 */
public class HorizontalHough {

    public static void horizontalCount(double data[][], int result[], int offset, int len) {
        double tresh = 0.8;
        for (int i = offset; i < offset + len; i++) {
            int size = data[i].length;
            for (int j = 0; j < size; j++) {
                result[i] += data[i][j] > tresh ? 1 : 0;
            }
        }
    }

}
