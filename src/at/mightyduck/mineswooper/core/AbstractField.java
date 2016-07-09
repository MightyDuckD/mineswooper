/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.core;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 *
 * @author Simon
 */
public abstract class AbstractField {

    public final static char STONE_UNKOWN = ' ';
    public final static char STONE_BOMB = 'X';
    public final static char[] STONE_COUNT = "012345678".toCharArray();

    public final int width;
    public final int height;

    public AbstractField(int width, int height) {
        if(width < 1 || height < 1)
            throw new IllegalArgumentException("width and height have to be at least 1");
        this.width = width;
        this.height = height;
    }

    public abstract char get(int x, int y);

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int asCount(int x, int y) {
        if (!isCount(x, y)) {
            throw new RuntimeException("not a count at " + x + " " + y);
        }
        return get(x, y) - '0';
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public boolean isBomb(int x, int y) {
        return get(x, y) == STONE_BOMB;
    }

    public boolean isCount(int x, int y) {
        return Arrays.binarySearch(STONE_COUNT, get(x, y)) >= 0;
    }

    public boolean isUnkown(int x, int y) {
        return get(x, y) == STONE_UNKOWN;
    }

    public void forEach(BiConsumer<Integer, Integer> c) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                c.accept(x, y);
            }
        }
    }

    public void forEachNeighbor(int x, int y, BiConsumer<Integer, Integer> c) {
        _forEachNeighbor(x, y, this::isValid, c);
    }

    public void forEachNeighbor(int x, int y, BiFunction<Integer, Integer, Boolean> filter, BiConsumer<Integer, Integer> c) {
        _forEachNeighbor(x, y, (xx, yy) -> {
            return isValid(xx, yy) && filter.apply(xx, yy);
        }, c);
    }

    public int countNeighbors(int x, int y, BiFunction<Integer, Integer, Boolean> filter) {
        int cnt[] = {0};
        forEachNeighbor(x, y, filter, (xx, yy) -> {
            cnt[0]++;
        });
        return cnt[0];
    }

    private void _forEachNeighbor(int x, int y, BiFunction<Integer, Integer, Boolean> filter, BiConsumer<Integer, Integer> c) {
        for (int xOffset = -1; xOffset < 2; xOffset++) {
            for (int yOffset = -1; yOffset < 2; yOffset++) {
                if (filter.apply(x + xOffset, y + yOffset)) {
                    c.accept(x + xOffset, y + yOffset);
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                builder.append(get(x, y));
            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

}
