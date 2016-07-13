/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.mightyduck.mineswooper.util;

import com.sun.javafx.tk.Toolkit;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author simon
 */
public class ContextPickerTest extends Application{

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        
            Parent parent = new AnchorPane();
            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.show();
            
        }
}
