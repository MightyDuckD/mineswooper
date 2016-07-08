/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.core;

import at.mightyduck.mineswooper.Point2D;
import java.util.Collection;

/**
 *
 * @author Simon
 */
public class StrategyResult {
    private final Collection<Point2D> bombs,free;

    public StrategyResult(Collection<Point2D> bombs, Collection<Point2D> free) {
        this.bombs = bombs;
        this.free = free;
    }
    
    public Collection<Point2D> getBombs() {
        return bombs;
    }
    public Collection<Point2D> getFree() {
        return free;
    }
    
}
