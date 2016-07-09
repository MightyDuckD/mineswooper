/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.core;

import at.mightyduck.mineswooper.util.Point2D;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Simon
 */
public class SimpleCountStrategy implements Strategy {
    private final AbstractField field;

    public SimpleCountStrategy(AbstractField field) {
        this.field = field;
    }
    
    @Override
    public StrategyResult call() throws Exception {
        Set<Point2D> bombs = new HashSet<>();
        Set<Point2D> free = new HashSet<>();
        field.forEach((x, y) -> {
            if (field.isCount(x, y)) {
                int totalCount = field.asCount(x, y);
                int bombsCount = field.countNeighbors(x, y, field::isBomb);
                int unknownCount = field.countNeighbors(x, y, field::isUnkown);
                if (bombsCount + unknownCount == totalCount) {
                    field.forEachNeighbor(x, y, field::isUnkown, (bombX,bombY) -> bombs.add(new Point2D(bombX,bombY)));
                }
                if (bombsCount == totalCount) {
                    field.forEachNeighbor(x, y, field::isUnkown, (freeX,freeY) -> free.add(new Point2D(freeX,freeY)));
                }
            }
        });
        return new StrategyResult(bombs, free);
    }
    
}
