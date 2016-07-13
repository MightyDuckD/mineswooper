/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.util;

import at.mightyduck.mineswooper.Database;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author simon
 */
public class DatabaseViewerConsole {
    private static final HashMap<String, BiFunction<String, Context, Boolean>> mapping = new HashMap<>();
    private static final Function<Color, Character> colorToAscii = new ColorToAsciiConverterGreyscale();

    static {
        mapping.put("exit", (s, c) -> false);
        mapping.put("stop", (s, c) -> false);
        mapping.put("halt", (s, c) -> false);
        mapping.put("help", DatabaseViewerConsole::help);
        mapping.put("list", DatabaseViewerConsole::listEntries);
        mapping.put("show", DatabaseViewerConsole::showEntry);
        mapping.put("charmap", DatabaseViewerConsole::setCharmap);
    }

    private static Boolean setCharmap(String s, Context context) {
        
        return true;
    }
    private static Boolean showEntry(String s, Context context) {
        String[] parts = s.split(" ");
        //because an whitespace can be an entry-id and it would be splitted away
        if ("show  ".equals(s)) {
            parts = new String[]{"show", " "};
        }
        if (parts.length != 2 || parts[1].length() != 1) {
            context.out.println("usage: show [entry-id]");
        } else {
            char ch = parts[1].charAt(0);
            for (BufferedImage img : context.database.getAll(ch)) {
                if (!dumpImage(img, context)) {
                    break;
                }
            }
        }
        return true;
    }

    /**
     * If the image is too big the user will be asked if he really wants to
     * print this image.
     *
     * @param context
     * @return true if the image was actually printed.
     */
    private static Boolean dumpImage(BufferedImage img, Context context) {
        if (img.getWidth() > 64 || img.getHeight() > 64) {//64 because power of 2 and std terminal size = 80 (it fits)
            context.out.println("image is very big. should it be printed anyway(Y/N)?");
            context.out.print(" > ");
            context.out.flush();
            try {
                String line = context.in.readLine();
                if (!line.equals("Y")) {
                    return false;
                }
            } catch (IOException ex) {
                return false;
            }
        }

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                context.out.print(colorToAscii.apply(new Color(img.getRGB(x, y))));
            }
            context.out.println();
        }

        return true;
    }

    private static Boolean help(String s, Context context) {
        context.out.println("This is just a list of available commands: ");
        for (String key : mapping.keySet()) {
            context.out.println(" - " + key);
        }
        return true;
    }

    private static Boolean listEntries(String s, Context context) {
        List<Character> keys = context.database.getAllKeys();
        if (keys.isEmpty()) {
            context.out.println("database is empty!");
        } else {
            for (char c : keys) {
                System.out.printf(" - '%c' with %d entries\n", c, context.database.getSize(c));
            }
        }
        return true;
    }

    private static Boolean unkown(String s, Context c) {
        c.out.printf("unknwon command \"%s\". maybe need some \"help\"?\n", s.split(" ")[0]);
        return true;
    }

    private static void run(Database d, InputStream inputstream, OutputStream outputstream) throws IOException {
        PrintWriter out = new PrintWriter(outputstream, true);
        BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));
        Context context = new Context(d, out, in);
        String line;
        boolean running = true;

        out.println("Database Viewer Console App - V1.0");
        out.println("Ein Toolinger by Simon Lehner-D.");
        out.print(" > ");
        out.flush();

        while (running && (line = in.readLine()) != null) {
            String[] parts = line.split(" ");
            BiFunction<String, Context, Boolean> function = mapping.getOrDefault(
                    parts[0],
                    DatabaseViewerConsole::unkown
            );
            try {
                running = function.apply(line, context);
            } catch (Exception ex) {
                ex.printStackTrace(out);
                running = false;
            }
            if (running) {
                out.print(" > ");
                out.flush();
            }
        }

    }

    /**
     * Container so that i dont have to pass 3 args to the commando functions.
     */
    private static class Context {

        public final Database database;
        public final PrintWriter out;
        public final BufferedReader in;

        public Context(Database d, PrintWriter out, BufferedReader in) {
            this.database = d;
            this.out = out;
            this.in = in;
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        Database database = Database.fromStream(new FileInputStream("database.obj"));
        run(database, System.in, System.out);
    }

}
