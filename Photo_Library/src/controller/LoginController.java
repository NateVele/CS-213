package controller;

import java.io.IOException;
import java.util.Iterator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Scanner;
import model.User;


/**
 * This handles everything that goes on in the login screen. It will direct a user to either the admin page or their user page.
 * 
 * @author Nathaniel Vele
 * @author Patrick Liang
 * 
 *
 */
public class LoginController {
	
	@FXML
	private TextField username;  
	
	@FXML
	private Button loginButton, quitButton;
	
	private static int globalIndex;
	
	/**
	 * Takes the input in the text field and will direct the user to their respective home page.
	 * 
	 * @param e 
	 * @throws IOException
	 */
	public void login(ActionEvent e) throws IOException {
		
		String name = username.getText();
		
		if(name.equals("admin") ) {
			
			FXMLLoader l = new FXMLLoader();
			l.setLocation(getClass().getResource("/fxml/AdminHome.fxml"));
			GridPane r = (GridPane) l.load();
			
			Scene scene = new Scene(r);
			Stage s = (Stage) loginButton.getScene().getWindow(); 
			s.setScene(scene);
			s.setResizable(false);
			s.show();
			
			return;
		}
		
		Iterator<User> it = Scanner.userIterator();
		
		for(int i = 0; it.hasNext(); i++) {
						
			if(it.next().toString().equals(name)) {
				
				globalIndex = i; 
						
				FXMLLoader l = new FXMLLoader();
				l.setLocation(getClass().getResource("/fxml/AlbumHome.fxml"));
				BorderPane r = (BorderPane) l.load();
				
				Scene scene = new Scene(r);
				Stage s = (Stage) loginButton.getScene().getWindow();  
				s.setScene(scene);
				s.setResizable(false);
				s.show();
				
				return;
							
			}
						
		}
		
		Alert error = new Alert(AlertType.ERROR);
		error.setTitle("Input Error");
		error.setContentText("User does not exist.");
		error.show();
		
		username.clear();
	}
	
	/**
	 * Will quit the program for the user.
	 * 
	 * @param e 
	 * @throws IOException 
	 */
	public void quit(ActionEvent e) throws IOException {
		
		Scanner.writeUser();
		
		e.consume();
		
		Stage s = (Stage) quitButton.getScene().getWindow(); 
		s.close();
		
	}
	
	/**
	 * This is a getter function for the global variable globalIndex.
	 * 
	 * @return 
	 */
	public static int getUserIndex() {
		
		return globalIndex;
	}

}