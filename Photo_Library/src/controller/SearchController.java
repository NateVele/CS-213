package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Scanner;
import model.Tag;
import model.User;


/**
 * This class will help search for albums.
 * 
 * @author Nathaniel Vele
 * @author Patrick Liang
 *
 */
public class SearchController implements Initializable{
	
	@FXML
	private ListView<User.Album.Photo> list;
	
	@FXML
    private ImageView pImage;
	
	@FXML 
	private Button addButton, returnButton, logoutButton, quitButton;
	
	@FXML
	private Label captionLabel, dateLabel, personTagLabel, locationTagLabel;
	

	private static ObservableList<User.Album.Photo> photoDisplayList;
	
	public static String pTag, lTag, initialDate, finalDate;
	
	/**
	 * This function will initialize all the needed variables for the other methods in this class to function, such as global lists.
	 * 
	 * @param loc
	 * @param res
	 */
	
	@Override
	public void initialize(URL loc, ResourceBundle res) {
		
		photoDisplayList = FXCollections.observableArrayList();
		
		list.setItems(photoDisplayList);
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		
		Date start = null;
		Date end = null;
	
		try {
			
			if(finalDate != null) {
				
			 start = sdf.parse(finalDate);
			 
			}
			
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
		try {
			
			if(initialDate != null) {
				
				end = sdf.parse(initialDate);
				
			}
			
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
		Iterator<User.Album> it = Scanner.getUser(LoginController.getUserIndex()).albumIterator();
		
		while(it.hasNext()) {
			
			User.Album a = it.next();
			
			Iterator<User.Album.Photo> pIt = a.photoIterator();
			
			while(pIt.hasNext()) {
				
				User.Album.Photo photo = pIt.next();
				
				Date pDate = photo.getPhotoDate();
					
				if(!pTag.isEmpty()) {
					
					boolean tagFound = false;
					
					Iterator<Tag> tagIter = photo.tagIterator();
					
					while(tagIter.hasNext()) {
						
						Tag tag = tagIter.next();
						
						if(tag.getName().equals("person") && tag.getValue().equals(pTag)) {
							
								tagFound = true;		
						}
					}
					
					if(!tagFound) {
						
						continue;
					}
				}
				
				if(!lTag.isEmpty()) {
					
					boolean found = false;
					
					Iterator<Tag> tIt = photo.tagIterator();
					
					while(tIt.hasNext()) {
						
						Tag t = tIt.next();
						
						if(t.getName().equals("location") && t.getValue().equals(lTag)) {
							
								found = true;		
						}
					}
					
					if(!found) {
						
						continue;
					}
				
				}
				
				if(start != null && end != null) {
					
					if(!(pDate.after(start) && pDate.before(end))  && !pDate.equals(start) && !pDate.equals(end)) {
						
						continue;
			
					}
					
				} else if(start != null) {
					
					if(!pDate.after(start) && !pDate.equals(start)) {
						
						continue;
					}
					
				} else if(end != null) {
					
					if(pDate.before(end) && !pDate.equals(end)) {
						
						continue;
					}
				}
				
				photoDisplayList.add(photo);
			
			}	
			
			if(photoDisplayList.size() == 0) {
				
				wipe();
			}
		}
		
		
		list.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> displayImagePreview());
		
		if(!photoDisplayList.isEmpty()) {
			
			list.getSelectionModel().select(0);
			
		}
	
	}
	
	/**
	 * This will handle adding a new album to the album list.
	 * 
	 * @param e t
	 * @throws IOException
	 */
	public void add(ActionEvent e) throws IOException {
		
		int index = LoginController.getUserIndex();
		
		String title = AlbumController.addHelper(); 
		
		if(title == null) {  
			
			return; 
		}
		
		User.Album tempAlbum = new User.Album(title);
		
		int count = 0;
		
		for(User.Album.Photo p: photoDisplayList) {
			
			if(count == 0) {
				
				tempAlbum.setBeginDate(p.getPhotoDate());
				tempAlbum.setEndDate(p.getPhotoDate());
				
			}
			
			if(p.getPhotoDate().before(tempAlbum.getBeginDate())) {
				
				tempAlbum.setBeginDate(p.getPhotoDate());
			}
			
			if(p.getPhotoDate().after(tempAlbum.getEndDate())) {
									
				tempAlbum.setEndDate(p.getPhotoDate());
			}
			
			tempAlbum.addPhoto(p);
			
			count++;
		}
				
		User u = Scanner.getUser(index);
	
		u.addAlbum(tempAlbum);
		
		FXMLLoader l = new FXMLLoader();
		l.setLocation(getClass().getResource("/fxml/AlbumHome.fxml"));
		BorderPane r = (BorderPane) l.load();
		
		Scene scene = new Scene(r);
		Stage s = (Stage) logoutButton.getScene().getWindow(); 
		s.setScene(scene);
		s.setResizable(false);
		s.show();
			
		
	}

	/**
	 * This function lets a user logout and directs them to the login page.
	 * 
	 * @param e 
	 * @throws IOException
	 */
	public void logout(ActionEvent e) throws IOException {
		
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
	 * This functions lets a user quit the program and automatically saves their data.
	 * 
	 * @param e 
	 * @throws IOException 
	 */
	public void quit(ActionEvent e) throws IOException {
		
		Scanner.writeUser();
		e.consume();
		Stage s = (Stage) logoutButton.getScene().getWindow();
		s.close();
	}
	
	/**
	 * This function will help the user go back to the previous scene.
	 * 
	 * @param e
	 * @throws IOException 
	 */
	public void goBack(ActionEvent e) throws IOException {
		
		FXMLLoader l = new FXMLLoader();
		l.setLocation(getClass().getResource("/fxml/AlbumHome.fxml"));
		BorderPane r = (BorderPane) l.load();
		
		Scene scene = new Scene(r);
		Stage s = (Stage) logoutButton.getScene().getWindow();  
		s.setScene(scene);
		s.setResizable(false);
		s.show();
		
	}

	/**
	 * This function will help to update the display for an image when it is clicked on.
	 */
	private void displayImagePreview() {
		
		if(!photoDisplayList.isEmpty())
		{
			User.Album.Photo temp = list.getSelectionModel().getSelectedItem();	
			File file = new File(temp.getPhotoLocation());

			if (file.exists())
			{
				Image display = new Image(file.toURI().toString());
				pImage.setImage(display);
				captionLabel.setText(temp.getCaption());
				SimpleDateFormat x = new SimpleDateFormat("MM/dd/yyyy");
				dateLabel.setText(x.format(temp.getPhotoDate()));
				personTagLabel.setText(temp.getTags("person"));
				locationTagLabel.setText(temp.getTags("location"));	
			} 
		}
	}
	
	/**
	 * This function will clear the display and set it blank.
	 */
	public void wipe()
	{
		pImage.setImage(null);
		captionLabel.setText("");
		dateLabel.setText("");
		personTagLabel.setText("");
		locationTagLabel.setText("");
	}

}