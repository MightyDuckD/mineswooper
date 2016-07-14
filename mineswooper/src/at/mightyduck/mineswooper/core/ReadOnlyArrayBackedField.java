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
public class ReadOnlyArrayBackedField extends AbstractField {

    protected char field[][];

    public ReadOnlyArrayBackedField(int width, int height) {
        super(width, height);
        this.field = new char[width][height];
        this.forEach((x, y) -> field[x][y] = STONE_UNKOWN);
    }

    /**
     * Changes made to field will affect the ArrayBackedField Object and vice
     * versa.
     */
    public ReadOnlyArrayBackedField(char[][] field) {
        super(field.length, field[0].length);
        this.field = field;
    }

    @Override
    public char get(int x, int y) {
        return this.field[x][y];
    }

}
