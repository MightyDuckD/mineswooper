/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.util;

import java.awt.Color;
import java.util.function.Function;

/**
 * Converts the given Color value to an Character based on its luminance value.
 * The luminance is calculated with this formular: lum = red * 0.30 + green *
 * 0.59 + blue * 0.11. The result of this calculation [0-1.0] is then mapped
 * linear onto the charmap. e.g.: if the charmap = "abcd" then a = [0-0.25], b =
 * ]0.25-0.50], ...
 *
 * @author simon
 */
public class ColorToAsciiConverterGreyscale implements Function<Color, Character> {

    public static final String EXAMPLE_MAP_PLAIN_ASCII = " .:+oO#";
    public static final String EXAMPLE_MAP_UNICODE = "\u2591\u2592\u2593\u2588";

    private final char charmap[];

    public ColorToAsciiConverterGreyscale() {
        this.charmap = EXAMPLE_MAP_PLAIN_ASCII.toCharArray();
    }

    public ColorToAsciiConverterGreyscale(char map[]) {
        this.charmap = map;
    }

    public ColorToAsciiConverterGreyscale(String map) {
        this.charmap = map.toCharArray();
    }

    @Override
    public Character apply(Color color) {
        double lumInv = 1 - (0.30 * color.getRed() + 0.59 * color.getGreen() + 0.11 * color.getBlue()) / 255.0;
        for (int i = 0; i < charmap.length; i++) {
            double lower = ((i + 0.0) / charmap.length);
            double upper = ((i + 1.0) / charmap.length);
            if (lower <= lumInv && lumInv <= upper) {
                return charmap[i];
            }
        }
        return 'E';

    }

}
