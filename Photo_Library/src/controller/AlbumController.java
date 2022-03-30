package controller;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Scanner;
import model.User;
import controller.LoginController;

/**
 * This class handles all things to do with albums in this program.
 * 
 * @author Nathaniel Vele
 * @author Patrick Liang
 *
 */
public class AlbumController implements Initializable{
	
	@FXML
	private TableView<User.Album> view;
	
	@FXML
	private TableColumn<User.Album, String> title;
	
	@FXML
	private TableColumn<User.Album, String> photo;
	
	@FXML
	private TableColumn<User.Album, String> date;
	
	@FXML 
	private Button addButton, deleteButton, openButton, editButton, searchButton, logoutButton, quitButton;
	
	private static ObservableList<User.Album> albumDup;
	
	private static int indexOfAlbum = -1;
	
	@Override
	public void initialize(URL url, ResourceBundle rec) {
		
		albumDup = FXCollections.observableArrayList();

		User current = Scanner.getUser(LoginController.getUserIndex()); 
		
		Iterator<User.Album> it = current.albumIterator();
		
		while(it.hasNext()) {  
			
			albumDup.add(it.next());
						
		}			
				
		view.setItems(albumDup);
		
		view.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);	
		
		date.setCellValueFactory(new PropertyValueFactory<>("dateRange"));  
		date.setResizable(false);
		date.setSortable(false);
		
		title.setCellValueFactory(new PropertyValueFactory<>("albumName"));
		title.setResizable(false);
		title.setSortable(false);
		
		photo.setCellValueFactory(new PropertyValueFactory<>("numOfPhotos"));
		photo.setResizable(false);
		photo.setSortable(false);
		
		title.setStyle( "-fx-alignment: CENTER;");
		
		photo.setStyle( "-fx-alignment: CENTER;");
		
		date.setStyle( "-fx-alignment: CENTER;");
		
		if(!albumDup.isEmpty()) {  
			
			view.getSelectionModel().select(0);
		}
	}
	
	/**
	 * This will add a new album to the list.
	 * 
	 * @param e 
	 */
	public void add(ActionEvent e) {		
		
		int uIndex = LoginController.getUserIndex();
		
		String title = addHelper();
		
		if(title == null) {  
			
			return; 
		}
		
		User.Album a = new User.Album(title);  
				
		User u = Scanner.getUser(uIndex);
	
		u.addAlbum(a);
		
		albumDup.add(a);  	
		
		view.getSelectionModel().select(albumDup.size()-1);  
		
	}
	
	/**
	 * Edits an album for a user.
	 * 
	 * @param e 
	 */
	public void edit(ActionEvent e) {
		
		if(albumDup.isEmpty()) {
			
			return;
		}
		
		int indexOfUser = LoginController.getUserIndex();
		
		String title = editHelper();  
		
		if(title == null) {
			
			return;
		}
				
		int x = view.getSelectionModel().getSelectedIndex();
				
		User u = Scanner.getUser(indexOfUser);
				
		u.getAlbum(x).setAlbumName(title);
		
		
		view.getColumns().get(x).setVisible(false);  
		view.getColumns().get(x).setVisible(true);
		
		view.getSelectionModel().select(x);  
		
	}
	
	
	/**
	 * Deletes an album for a user.
	 * 
	 * @param e 
	 */
	public void delete(ActionEvent e) {
		
		if(albumDup.isEmpty()) {
			
			return;
		}
		
		User.Album curr = view.getSelectionModel().getSelectedItem(); 
		
		int count = 0;
		for(User.Album album: albumDup) { 
			
			if(curr.toString().equals(album.toString())) {  
				
				break;
				
			}
			
			count++;
		}	
		
		User u = Scanner.getUser(LoginController.getUserIndex());
		
		u.delAlbum(count);
		
		albumDup.remove(count);
		
		if(albumDup.size() == count) {  
			
			view.getSelectionModel().select(--count);  
			
		} else {
			view.getSelectionModel().select(count);  
		}
	}
	
	
	/**
	 * This function searches for an album.
	 * 
	 * @param e 
	 * @throws IOException 
	 */
	public void search(ActionEvent e) throws IOException {
		
		boolean isValid = searchHelper();
		
		if(!isValid) {
			
			return;
		}
		
		FXMLLoader l = new FXMLLoader();
		l.setLocation(getClass().getResource("/fxml/Search.fxml"));
		AnchorPane r = (AnchorPane) l.load();
		
		Scene scene = new Scene(r);
		Stage s = (Stage) logoutButton.getScene().getWindow();  
		s.setScene(scene);
		s.setResizable(false);
		s.show();
	}

	/**
	 * Opens an album to view the pictures in it.
	 * 
	 * @param e 
	 * @throws IOException 
	 */
	public void openAlbum(ActionEvent e) throws IOException
	{
		if(albumDup.isEmpty()) {
			
			return;
		}
		
		indexOfAlbum = view.getSelectionModel().getSelectedIndex();
		
		FXMLLoader l = new FXMLLoader();
		l.setLocation(getClass().getResource("/fxml/Photo.fxml"));
		AnchorPane r = (AnchorPane) l.load();
		
		Scene scene = new Scene(r);
		Stage s = (Stage) openButton.getScene().getWindow();  
		s.setScene(scene);
		s.setResizable(false);
		s.show();
		
	}
	
	/**
	 * This will log out the current user and return them to the login page.
	 * 
	 * @param e 
	 */
	public void logout(ActionEvent e) throws IOException {
		
		albumDup = FXCollections.observableArrayList(); 
		
		FXMLLoader l = new FXMLLoader();
		l.setLocation(getClass().getResource("/fxml/Login.fxml"));
		BorderPane r = (BorderPane) l.load();
		
		Scene scene = new Scene(r);
		Stage s = (Stage) logoutButton.getScene().getWindow(); 
		s.setScene(scene);
		s.setResizable(false);
		s.show();
	}
	
	/**
	 * This function is a helper for adding an album name to the list.
	 * 
	 * @return String
	 */
	public static String addHelper() {
		
		
		TextInputDialog getTitle = new TextInputDialog();
		getTitle.setTitle("Add Album");
		getTitle.setHeaderText("Create Album");
		getTitle.setContentText("Album Title:");
		
		Optional<String> title = getTitle.showAndWait();
		
		if(title.isPresent()) {
			
			if(title.get().isEmpty()) {
				
				Alert error = new Alert(AlertType.ERROR);
				error.setTitle("Input Error");
				error.setContentText("Must Enter an album title.");
				error.show();
				
				return null;
			}
			
			User u = Scanner.getUser(LoginController.getUserIndex());
			
			Iterator<User.Album> it = u.albumIterator();
			
			while(it.hasNext()) {
				
				
				if(title.get().equals(it.next().getAlbumName())) {
					
					Alert error = new Alert(AlertType.ERROR);
					error.setTitle("Input Error");
					error.setContentText("This album already exists.");
					error.show();
					
					return null;
					
				}
				
			}
			
			
			return title.get().trim();
		}
		
		return null;
		
	}
	
	/**
	 * @return returns the album name to edit. Returns null if album name is invalid. 
	 */
	public static String editHelper() {
		
		TextInputDialog getTitle = new TextInputDialog();
		getTitle.setTitle("Edit Album");
		getTitle.setHeaderText("Edit Album Title");
		getTitle.setContentText("Album Title:");
		
		Optional<String> title = getTitle.showAndWait();
		
		if(title.isPresent()) {
			
			if(title.get().isEmpty()) {
				
				Alert error = new Alert(AlertType.ERROR);
				error.setTitle("Input Error");
				error.setContentText("Must enter and album title.");
				error.show();
				
				return null;
			}
			
			User u = Scanner.getUser(LoginController.getUserIndex());
			
			Iterator<User.Album> it = u.albumIterator();
			
			while(it.hasNext()) {
				
				
				if(title.get().equals(it.next().getAlbumName())) {
					
					Alert error = new Alert(AlertType.ERROR);
					error.setTitle("Input Error");
					error.setContentText("Existing Album");
					error.show();
					
					return null;
					
				}
				
			}
			
			return title.get().trim();
		}
		
		return null;
	}
	
	/**
	 * Quits the program.
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
	 * This function is a getter for the global variable indexOfAlbum.
	 * 
	 * @return 
	 */
	public static int getIndexOfAlbum() {
		
		return indexOfAlbum;
	}
	
	/**
	 * Helper for searching for an album
	 * 
	 * @return 
	 */
	public static boolean searchHelper() {
		
		SearchController.pTag = null;
		SearchController.lTag = null;
		SearchController.initialDate = null;
		SearchController.finalDate = null;
		
		Dialog<ButtonType> text = new Dialog<>();
		text.setTitle("Search Photos");
		text.setHeaderText("Input Search Criteria\nAll Fields are Optional");
		text.setResizable(true);

		Label pLabel = new Label("Person Tag: ");
		Label locLabel = new Label("Location Tag: ");
		Label dLabel = new Label("Date Range: ");
		//Label toDate = new Label("Date: ");
		TextField pTag = new TextField();
		TextField locTag = new TextField();
		Label to = new Label("   to   ");
		
		DatePicker date1 = new DatePicker();
		DatePicker date2 = new DatePicker();
				
		GridPane gridPane = new GridPane();
		
		gridPane.add(pLabel, 1, 1);
		gridPane.add(pTag, 2, 1);
		gridPane.add(locLabel, 1, 2);
		gridPane.add(locTag, 2, 2);
		gridPane.add(dLabel, 1, 3);
		gridPane.add(date1, 2, 3);
		gridPane.add(to, 3, 3);
		gridPane.add(date2, 5, 3);
		
		gridPane.setVgap(15);
		text.getDialogPane().setContent(gridPane);
				
		ButtonType ok = new ButtonType("Okay", ButtonData.OK_DONE);
		text.getDialogPane().getButtonTypes().add(ok);
		
		ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		text.getDialogPane().getButtonTypes().add(cancel);
		
		
		Optional<ButtonType> input = text.showAndWait();
		
		if(input.get() == cancel) {
			
			return false;
		}
				
		if(pTag.getText().isEmpty() && locTag.getText().isEmpty() 
				&& date1.getValue() == null && date2.getValue() == null) {
			
			Alert error = new Alert(AlertType.ERROR);
			error.setTitle("Input Error");
			error.setContentText("Must enter search criteria");
			error.show();
			
			return false;
		}
		
		SearchController.pTag = pTag.getText().trim();
		SearchController.lTag = locTag.getText().trim();
		
		if(date1.getValue() != null) {
			
			SearchController.initialDate = date1.getValue().format(DateTimeFormatter.ofPattern("MM/dd/yyy"));
			
		}
		
		if(date2.getValue() != null) {
			
			SearchController.finalDate = date2.getValue().format(DateTimeFormatter.ofPattern("MM/dd/yyy"));
			
		}	
 		return true;			
	}
}