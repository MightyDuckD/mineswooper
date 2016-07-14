/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.core;

/**
 *
 * @author Simon
 */
public class ArrayBackedField extends ReadOnlyArrayBackedField implements WriteableField {

    public ArrayBackedField(int width, int height) {
        super(width, height);
    }

    public ArrayBackedField(char[][] field) {
        super(field);
    }

    @Override
    public void set(int x, int y, char ch) {
        this.field[x][y] = ch;
    }
    
}
