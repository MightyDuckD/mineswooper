/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.util;

import java.awt.Point;

/**
 *
 * @author Simon
 */
public class Point2D extends Point implements Comparable<Point>{

    public Point2D() {
    }

    public Point2D(Point p) {
        super(p);
    }

    public Point2D(int x, int y) {
        super(x, y);
    }
    
    @Override
    public int compareTo(Point o) {
        if(o.x == x)
            return o.y - y;
        return o.x - x;
    }

    
    
    
}
