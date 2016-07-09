/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.core;

import at.mightyduck.mineswooper.Point2D;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Simon
 */
public class SimpleCountStrategyTest {

    public SimpleCountStrategyTest() {
    }

    @Test
    public void testCase1() throws Exception {
        ArrayBackedField field = new ArrayBackedField(
                new char[][]{
                    " 101 ".toCharArray(),
                    "   0 ".toCharArray(),
                    " 1111".toCharArray(),
                    "11X10".toCharArray(),}
        );
        SimpleCountStrategy st = new SimpleCountStrategy(field);
        StrategyResult result = st.call();
        assertEquals(2, result.getBombs().size());
        assertEquals(6, result.getFree().size());

        Point2D[] free = new Point2D[]{
            new Point2D(1, 1),
            new Point2D(1, 2),
            new Point2D(0, 4),
            new Point2D(1, 4),
            new Point2D(1, 0),
            new Point2D(2, 0)
        };
        Point2D[] freeRes = result.getFree().toArray(new Point2D[result.getFree().size()]);
        Arrays.sort(free);
        Arrays.sort(freeRes);
        assertArrayEquals(free, freeRes);
    }

}
