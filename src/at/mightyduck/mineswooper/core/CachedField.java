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
public class CachedField extends ArrayBackedField {

    private final boolean clean[][];
    private final AbstractField source;

    public CachedField(AbstractField source) {
        super(source.getWidth(), source.getHeight());
        this.source = source;
        this.clean = new boolean[source.getWidth()][source.getHeight()];
    }

    @Override
    public char get(int x, int y) {
        if (!clean[x][y]) {
            super.set(x, y, source.get(x, y));
            clean[x][y] = true;
        }
        return super.get(x, y);
    }

    @Override
    public void set(int x, int y, char ch) {
        if (source instanceof WriteableField) {
            ((WriteableField) source).set(x, y, ch);
            super.set(x, y, ch);
        } else {
            throw new UnsupportedOperationException("source is not writeable");
        }
    }

    public void setDirty(int x, int y) {
        this.clean[x][y] = false;
    }

    public void setDirtyAll() {
        this.forEach(this::setDirty);
    }

}
