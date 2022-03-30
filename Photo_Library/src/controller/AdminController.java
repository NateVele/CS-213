package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.User;
import model.Scanner;
import model.Main;

/**
 * This is the controller for the admin page.  It handles all things that the admin can do.
 * 
 * @author Nathaniel Vele
 * @author Patrick Liang
 *
 */
public class AdminController implements Initializable{
	
	@FXML 
	private Button addButton ,deleteButton, editButton, logoutButton, quitButton;  
	
	@FXML
	private TextField username;  
	
	@FXML
	private ListView<User> list;  
	
	private ObservableList<User> userDup;
	
	/**
	 * This function will run every time the admin scene is started.  It loads all the necessary tools for the admin.  It overrides
	 * the initialize function from Initializable.
	 * 
	 * @param location
	 * @param resources
	 */
	@Override
	public void initialize(URL location, ResourceBundle res) {  
		
		userDup = FXCollections.observableArrayList(); 
		
		Iterator<User> it = Scanner.userIterator();  
		
		while(it.hasNext()) {  
			
			userDup.add(it.next());
		}
		
		list.setItems(userDup);  
		
		if(!userDup.isEmpty()) {  
			
			list.getSelectionModel().select(0);
		}
		
	}
	
	/**
	 * This function will take a name input from the username text box and create a new user in the user list.
	 * 
	 * @param e 
	 */
	public void addUser(ActionEvent e) {
				
		String inputName = username.getText().trim();  
		
		
		
		if(inputName.isEmpty()) {  
			
			Alert error = new Alert(AlertType.ERROR);
			error.setTitle("Input Error");
			error.setContentText("Username field is empty.");
			error.show();
			
			return;
		}
		
		String[] check = inputName.split(" ");
		
		if(check.length > 1) {
			
			Alert error = new Alert(AlertType.ERROR);
			error.setTitle("Input Error");
			error.setContentText("Username cannot be more than one word.");
			error.show();
			
			username.clear();
			
			return;
		}
		
		
		for(int i = 0; i < userDup.size(); i++)
		{  
			
			if(inputName.equals(userDup.get(i).toString())) {   
				
				Alert error = new Alert(AlertType.ERROR);
				error.setTitle("Input Error");
				error.setContentText("This user already exists.");
				error.show();
				
				username.clear();
				
				return;
				
			}
						
		}
		
		User temp = new User(inputName);  
		
		userDup.add(temp);
				
		Scanner.addUser(temp);  
		
		list.getSelectionModel().select(temp);  
		
		username.clear(); 
		
		
	}
	
	/**
	 * Deletes a user from the master user list.
	 * 
	 * @param e 
	 */
	public void deleteUser(ActionEvent e) {
		
		if(userDup.isEmpty()) {  
			
			return;
		}
		
		User selected = list.getSelectionModel().getSelectedItem();  
		
		int count = 0;
		

		while(count <= userDup.size() && !(selected.toString().equals(userDup.get(count).toString()))) {
			count++;

		}
//		System.out.println(count);
//		if(count == userDup.size()-1 )
//			count--;
//		System.out.println(count);
		userDup.remove(count); 
		
		Scanner.delUser(count);  
		
		if(userDup.size() == count) {  
			
			list.getSelectionModel().select(--count);  
			
		} else {
			
			list.getSelectionModel().select(count);  
			
		}
	}
	
	/**
	 * Gets a user from the master list and edits their name.
	 * 
	 * @param e 
	 */
	public void editUser(ActionEvent e) {
		
		if(userDup.isEmpty()) {
			return;
		}
				
		String input = editHelper();  
		
		if(input == null) { 
			
			return;
		}
				
		int userIndex = list.getSelectionModel().getSelectedIndex();
		
		User u = list.getSelectionModel().getSelectedItem();
		
		u.setUserName(input);
		
		userDup.get(userIndex).setUserName(input);
								
		
		list.setItems(null);
		list.setItems(userDup);
		
		list.getSelectionModel().select(userIndex);  
	}
	
	/**
	 * Logs the admin out of the program and sends them back to the login page.
	 * 
	 * @param e 
	 */
	public void logout(ActionEvent e) throws IOException {
		
		FXMLLoader l = new FXMLLoader();
		l.setLocation(getClass().getResource("/fxml/Login.fxml"));
		BorderPane r = (BorderPane) l.load();
		
		Scene s = new Scene(r);
		Stage myStage = (Stage) logoutButton.getScene().getWindow();  
		myStage.setScene(s);
		myStage.setResizable(false);
		myStage.show();
		
	}
	
	/**
	 * Quits the program.
	 * 
	 * @param e 
	 */
	public void quit(ActionEvent e) throws IOException {
		
		Scanner.writeUser();
		
		e.consume();
		
		Stage s = (Stage) quitButton.getScene().getWindow();  
		s.close();
		
	}
	
	/**
	 * User editing helper.  Gets the name that will be edited from the input box.
	 * 
	 * @return
	 */
	public static String editHelper() {
		
		TextInputDialog in = new TextInputDialog();
		in.setTitle("Edit Username");
		in.setHeaderText("Edit Username");
		in.setContentText("Username:");
		
		Optional<String> newName = in.showAndWait();
		
		if(newName.isPresent()) {
			
			if(newName.get().isEmpty()) {
				
				Alert error = new Alert(AlertType.ERROR);
				error.setTitle("Input Error");
				error.setContentText("Field is empty.");
				error.show();
				
				return null;
			}
						
			Iterator<User> it = Scanner.userIterator();
			
			while(it.hasNext()) {
				
				
				if(newName.get().equals(it.next().toString())) {
					
					Alert error = new Alert(AlertType.ERROR);
					error.setTitle("Input Error");
					error.setContentText("This user already exists.");
					error.show();
					
					return null;
					
				}
				
			}
			
			return newName.get().trim();
		}
		
		return null;
	}
}