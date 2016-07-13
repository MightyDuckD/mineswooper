
import at.mightyduck.mineswooper.Mineswooper;
import java.util.logging.Level;
import java.util.logging.Logger;
import minesweeper.Minesweeper;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author simon
 */
public class MainSplitter {

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                Mineswooper.main(args);
            } catch (Exception ex) {
                Logger.getLogger(MainSplitter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();
        new Thread(() -> Minesweeper.main(args)).start();
    }
}
