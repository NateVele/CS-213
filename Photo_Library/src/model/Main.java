package model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


/**
 * This is the main function, where the program is ran from.  It will always open the login screen first.
 * 
 * @author Nathaniel Vele
 *
 * @author Patrick Liang
 */
public class Main extends Application {
		
	@Override
	public void start(Stage s) {
		
		try {
			
			Scanner.readUser();
			
			FXMLLoader l = new FXMLLoader();
			l.setLocation(getClass().getResource("/fxml/login.fxml"));
			BorderPane r = (BorderPane) l.load();
			
			
			Scene scene = new Scene(r);
			s.setScene(scene);
			s.setResizable(false);
			s.show();
			
		} catch(Exception e) {
			
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}