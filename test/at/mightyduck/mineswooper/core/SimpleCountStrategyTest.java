/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.core;

import java.awt.Point;
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
            new  char[][]{
                " 101 ".toCharArray(),
                "   0 ".toCharArray(),
                " 1111".toCharArray(),
                "11X10".toCharArray(),
            }
        );
        SimpleCountStrategy st = new SimpleCountStrategy(field);
        StrategyResult result = st.call();
        assertEquals(2, result.getBombs().size());
        System.out.println(result.getFree());
    }
    
}
