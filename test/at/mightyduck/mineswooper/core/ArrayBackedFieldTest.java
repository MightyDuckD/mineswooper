/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.core;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Simon
 */
public class ArrayBackedFieldTest {

    public ArrayBackedFieldTest() {
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalSize() {
        ArrayBackedField field = new ArrayBackedField(-1, 100);
    }

    @Test
    public void testSizeConstructor() {
        ArrayBackedField field = new ArrayBackedField(10, 15);
        assertEquals(10, field.getWidth());
        assertEquals(15, field.getHeight());
        assertEquals(AbstractField.STONE_UNKOWN, field.get(4, 11));
    }

    @Test
    public void testArrayConstructor() {
        ArrayBackedField field = new ArrayBackedField(
                new char[][]{
                    "   A".toCharArray(),// <- x=0
                    " B  ".toCharArray(),//    x=1
                    "----".toCharArray() //    x=2
                }
        );
        assertEquals('A', field.get(0, 3));
        assertEquals('B', field.get(1, 1));
        assertEquals(3, field.countNeighbors(1, 1, (x, y) -> field.get(x, y) == '-'));
        assertEquals(2, field.countNeighbors(2, 0, (x, y) -> field.get(x, y) == '-'));
    }
}
