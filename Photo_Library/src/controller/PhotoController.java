package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Scanner;
import model.User;


/**
 * This is the controller that will handle all things related to the photo object and manipulating any photos.
 * 
 * @author Nathaniel Vele
 * @author Patrick Liang
 *
 */
public class PhotoController implements Initializable
{

	@FXML
	private ListView<User.Album.Photo> list;
	

	@FXML
    private ImageView imageDisplay;
	

	@FXML 
	private Button addPhoto, movePhoto, removePhoto, copyPhoto, addTag, slideshowButton, returnButton, logout, quit, captionOp, removeTag;
	

	@FXML
	Label captionLabel, dateLabel, personTagLabel, locationTagLabel;
	

	private static ObservableList<User.Album.Photo> displayList;
	
	private static final String split = "~";
	
	private static final int pathDoesNotExist = 0;
	private static final int inputEmpty = 1;
	private static final int tagExists = 2;
	private static final int tagDoesNotExist = 3;
	
	/**
	 * Run every time the scene is opened, and initializes the list and observable list for viewing.
	 * 
	 * @param loc
	 * @param res
	 */
	@Override
	public void initialize(URL loc, ResourceBundle res)
	{
		displayList = FXCollections.observableArrayList();
		
		User u = Scanner.getUser(LoginController.getUserIndex());
		
		User.Album a = u.getAlbum(AlbumController.getIndexOfAlbum());
		
		Iterator<User.Album.Photo> it = u.getAlbum(AlbumController.getIndexOfAlbum()).photoIterator();
		
		boolean invalid = false;
		
		while(it.hasNext())
		{
			User.Album.Photo p = it.next();
			File f = new File(p.getPhotoLocation());
			
			if (f.exists())
			{
				displayList.add(p);
			} else {
				invalid = true;
				it.remove();
				a.opNumOfPhotos(1, '-');
			}
		}
		
		list.setItems(displayList);
		
		wipe();
		list.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> update());
		
		list.getSelectionModel().select(0);
		
		update();
		
		if (invalid)
		{
			errorHandler(pathDoesNotExist);
		}
		
	}
	

	
	/**
	 * Sets the display to default, which is everything blank.
	 */
	public void wipe()
	{
		imageDisplay.setImage(null);
		captionLabel.setText("");
		dateLabel.setText("");
		personTagLabel.setText("");
		locationTagLabel.setText("");
	}
	
	/**
	 * This function will take a photo and add it to a user album. 
	 * 
	 * @param e
	 * @throws IOException
	 * @throws ParseException
	 */
	public void add(ActionEvent e) throws IOException, ParseException
	{
		FileChooser photoToBeAdded = new FileChooser();
		configureFileChooser(photoToBeAdded);
		File f = photoToBeAdded.showOpenDialog(null);
		
		if (f != null)
		{
			User.Album.Photo p = new User.Album.Photo(f.getName(), f.toString());
			
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			
			String s = sdf.format(f.lastModified());
			
			Date d = sdf.parse(s);
			
			p.setPhotoDate(d); 
		
			User u = Scanner.getUser(LoginController.getUserIndex());		
			User.Album a = u.getAlbum(AlbumController.getIndexOfAlbum());
			a.addPhoto(p);
		
			displayList.add(p);
		
			list.getSelectionModel().select(displayList.size() - 1);
		
			update();
			
			if(displayList.size() == 1) {
				changeDate(a, p);
				return;
			}
			
			adjustDateRange(a);
		}
	}
	
	/**
	 * This function helps to configure the FileChooser for selecting photos to be displayed.
	 * 
	 * @param chooser
	 */
	private static void configureFileChooser(final FileChooser chooser)
	{
		chooser.setTitle("Add a Photo");
		
		chooser.setInitialDirectory(new File(System.getProperty("user.home")));
		
		chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Images", "*.*"),new FileChooser.ExtensionFilter("JPG", "*.jpg"),new FileChooser.ExtensionFilter("PNG", "*.png"));
	}
	
	/**
	 * Delete a photo from an album.
	 * 
	 * @param e
	 * @throws IOException
	 */
	public void removePhoto(ActionEvent e) throws IOException
	{
		if(!displayList.isEmpty()) {
			delete();
			update();
		}
		
		User currentUser = Scanner.getUser(LoginController.getUserIndex());		
		User.Album currentAlbum = currentUser.getAlbum(AlbumController.getIndexOfAlbum());

		adjustDateRange(currentAlbum);
	}
	
	/**
	 * Updates the UI display with new photos, names, tags, caption and date.
	 */
	private void update()
	{
		if(!displayList.isEmpty())
		{
			User.Album.Photo p = list.getSelectionModel().getSelectedItem();	
			File f = new File(p.getPhotoLocation());

			if (f.exists())
			{
				Image photoToBeShown = new Image(f.toURI().toString());
				imageDisplay.setImage(photoToBeShown);
				captionLabel.setText(p.getCaption());
				SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy");
				dateLabel.setText(date.format(p.getPhotoDate()));
				personTagLabel.setText(p.getTags("person"));
				locationTagLabel.setText(p.getTags("location"));
			} else {
				errorHandler(pathDoesNotExist);
				delete();
				
				if (displayList.isEmpty())
				{
					wipe();
				} else {
					update();
				}
			}
		} else {
			wipe();
		}
	}
	

	
	/**
	 * This function will take a photo from one album and copy it to another.
	 * 
	 * @param e
	 */
	public void copy(ActionEvent e)
	{
		User.Album.Photo p = list.getSelectionModel().getSelectedItem();
		User u = Scanner.getUser(LoginController.getUserIndex());
		User.Album destination;
		
		List<String> albums = new ArrayList<>();
		
		Iterator<User.Album> it = u.albumIterator();
		
		String albumTitle = null;
		
		while(it.hasNext())
		{
			User.Album a = it.next();
			if (a != u.getAlbum(AlbumController.getIndexOfAlbum()))
			{
				albums.add(a.getAlbumName());
			}
		}
		
		if (albums.size() > 0)
		{
			albumTitle = moveHandler("copy", albums);
			
			if (albumTitle != null)
			{
				it = u.albumIterator();
				destination = searchForAlbum(albumTitle, it);
			
				destination.addPhoto(p);
				adjustDateRange(destination);
			
				update();
			}
		}
	}
	
	/**
	 * This function is similar to the copy function, except it moves the photo from one album to another instead of copying it.
	 * 
	 * @param e
	 */
	public void movePhoto(ActionEvent e)
	{
		User.Album.Photo p = list.getSelectionModel().getSelectedItem();
		User u = Scanner.getUser(LoginController.getUserIndex());
		User.Album start = u.getAlbum(AlbumController.getIndexOfAlbum());
		User.Album destination;
		
		List<String> albums = new ArrayList<>();
		
		Iterator<User.Album> it = u.albumIterator();
		
		String title = null;
		
		while(it.hasNext())
		{
			User.Album album = it.next();
			if (album != u.getAlbum(AlbumController.getIndexOfAlbum()))
			{
				albums.add(album.getAlbumName());
			}
		}
		
		if (albums.size() > 0)
		{
			title = moveHandler("move", albums);
			
			if (title != null)
			{
				it = u.albumIterator();
				destination = searchForAlbum(title, it);
			
				destination.addPhoto(p);
				adjustDateRange(destination);
			
				start.deletePhoto(p);
				adjustDateRange(start);
				displayList.remove(p);
			
				update();
			}
		}
	}
	
	/**
	 * Deletes a photo from an album
	 */
	public void delete()
	{
		User.Album.Photo p = list.getSelectionModel().getSelectedItem();
		
		User u = Scanner.getUser(LoginController.getUserIndex());		
		User.Album a = u.getAlbum(AlbumController.getIndexOfAlbum());
		a.deletePhoto(p);

		displayList.remove(p);
	}
	
	/**
	 * This function is used to help find out if a user wants to copy or move a photo.
	 * 
	 * @param str
	 * @param myList
	 * @return the album the user wishes to move/copy a photo to
	 */
	public static String moveHandler(String str, List<String> myList)
	{
		ChoiceDialog<String> words = new ChoiceDialog<String>(myList.get(0), myList);
		Optional<String> finalString;
		
		if (str == "move")
		{
			words.setTitle("Move a Photo");
			words.setHeaderText("Choose an album.");
			words.setContentText("Albums:");
		
			finalString = words.showAndWait();
			
			if (finalString.isPresent())
			{
				return finalString.get().trim();
			}
		} else if (str == "copy") {
			words.setTitle("Copy a Photo");
			words.setHeaderText("Choose an album.");
			words.setContentText("Albums:");
		
			finalString = words.showAndWait();
			
			if (finalString.isPresent())
			{
				return finalString.get().trim();
			}
		}
		
		return null;
	}
	
	
	/**
	 * This function will get a new tag and add it to a selected photo.
	 * 
	 * @param e
	 */
	public void addTag(ActionEvent e)
	{
		String newTag = tagHelper("add");
		
		if (newTag != null)
		{
			User.Album.Photo p = list.getSelectionModel().getSelectedItem();
			
			String[] splitTag = newTag.split(split);
			
			if(p.addTag(splitTag[0], splitTag[1]))
			{
				update();
			} else {
				errorHandler(tagExists);
			}
		}
	}
	
	/**
	 * This is a helper to identify a tag to edit.  It can also edit captions.
	 * 
	 * @param tagKey
	 * @return the tag the user entered in
	 */
	public static String editHelper(String tagKey, String str)
	{
		TextInputDialog holder = new TextInputDialog();
		Optional<String> finalTag;
		
		if ((tagKey == "person" || tagKey == "location") && str.equals("add"))
		{
			holder.setTitle("Tag Editing");
			holder.setHeaderText("Add a " + tagKey + " Tag");
			holder.setContentText("Enter Tag:");
			
			finalTag = holder.showAndWait();
			
			if(finalTag.isPresent())
			{
				if(finalTag.get().isEmpty())
				{
					errorHandler(inputEmpty);
					
					return null;
				}
				
				return tagKey + split + finalTag.get().trim();
			}
		} else if (tagKey == "caption") {
			holder.setTitle("Caption Editing");
			holder.setHeaderText("Edit your " + tagKey);
			holder.setContentText("Enter Caption:");
			
			finalTag = holder.showAndWait();
			
			if(finalTag.isPresent())
			{
				return finalTag.get().trim();
			}
		} else if ((tagKey == "person" || tagKey == "location") && str.equals("remove")) {
			holder.setTitle("Tag Editing");
			holder.setHeaderText("Remove a " + tagKey + " Tag");
			holder.setContentText("Enter Tag:");
			
			finalTag = holder.showAndWait();
			
			if(finalTag.isPresent())
			{
				if(finalTag.get().isEmpty())
				{
					errorHandler(inputEmpty);
					
					return null;
				}
				
				return tagKey + split + finalTag.get().trim();
			}
		}
		
		return null;
		
	}
	
	/**
	 * This function searches for an album by name.
	 * 
	 * @param title
	 * @param it
	 * @return an album matching the key
	 */
	public User.Album searchForAlbum(String title, Iterator<User.Album> it)
	{
		while(it.hasNext())
		{
			User.Album a = it.next();
			if (a.getAlbumName().equals(title))
			{
				return a;
			}
		}
		
		return null;
	}
	
	/**
	 * This is a helper function to help confirm what the user wants to edit, in terms of tags.
	 * They can delete, edit or add tags.
	 * 
	 * @param operation
	 * @return the tag the user wishes to edit
	 */
	public static String tagHelper(String str)
	{
		Alert a = new Alert(AlertType.CONFIRMATION);
		a.setTitle("Tag Editing");
		a.setHeaderText("Tag Type");
		
		ButtonType personButton = new ButtonType("Person");
		ButtonType locationButton = new ButtonType("Location");
		ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		
		a.getButtonTypes().setAll(personButton, locationButton, cancelButton);
		
		if (str == "add")
		{
			a.setContentText("Select the type of tag you want to add.");

			Optional<ButtonType> finalTag = a.showAndWait();
			if (finalTag.get() == personButton)
			{
			    return editHelper("person", "add");
			} else if (finalTag.get() == locationButton) {
				return editHelper("location", "add");
			} else {
			}
		} else if (str == "delete") {
			a.setContentText("Select the type of tag you want to delete.");

			Optional<ButtonType> finalTag = a.showAndWait();
			if (finalTag.get() == personButton)
			{
				
				return editHelper("person", "remove");
			} else if (finalTag.get() == locationButton) {
				return editHelper("location", "remove");
			} else {
			}
		}
		
		return null;
	}
	
	/**
	 * This function will remove a tag from a selected photo by the user.
	 * 
	 * @param e
	 */
	public void removeTag(ActionEvent e)
	{
		String selected = tagHelper("delete");
		
		if (selected != null)
		{
			User.Album.Photo p = list.getSelectionModel().getSelectedItem();
			
			String[] splitTag = selected.split(split);

			if(p.removeTag(splitTag[0], splitTag[1]))
			{
				update();
			} else {
				errorHandler(tagDoesNotExist);
			}
		}
	}
	
	/**
	 * This function is a helper to switch to the slideshow scene.
	 *  
	 * @param e
	 * @throws IOException
	 */
	public void slideshowHelper(ActionEvent e) throws IOException
	{
		FXMLLoader l = new FXMLLoader();
		l.setLocation(getClass().getResource("/fxml/SlideshowUI.fxml"));
		AnchorPane r = (AnchorPane) l.load();
		
		Scene scene = new Scene(r);
		Stage s = (Stage) logout.getScene().getWindow(); 
		s.setScene(scene);
		s.setResizable(false);
		s.show();
	}
	
	/**
	 * This function will go back to the last scene.
	 * 
	 * @param e
	 * @throws IOException
	 */
	public void goBack(ActionEvent e) throws IOException
	{
		FXMLLoader l = new FXMLLoader();
		l.setLocation(getClass().getResource("/fxml/AlbumHome.fxml"));
		BorderPane r = (BorderPane) l.load();
		
		Scene scene = new Scene(r);
		Stage s = (Stage) returnButton.getScene().getWindow();
		s.setScene(scene);
		s.setResizable(false);
		s.show();
	}
	
	/**
	 * This function will handle captions and all options the user has to edit or delete them.
	 * 
	 * @param e
	 */
	public void captionHelper(ActionEvent e)
	{
		Alert a = new Alert(AlertType.CONFIRMATION);
		a.setTitle("Caption Options");
		a.setHeaderText("Tag Type");
		
		ButtonType editButton = new ButtonType("Edit Caption");
		ButtonType removeButton = new ButtonType("Remove Caption");
		ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		
		a.getButtonTypes().setAll(editButton, removeButton, cancelButton);
		
		Optional<ButtonType> finalCap = a.showAndWait();
		if (finalCap.get() == editButton)
		{
		    String str = editHelper("caption", "num");
		    
			if (str != null)
			{
				User.Album.Photo p = list.getSelectionModel().getSelectedItem();
				
				p.setCaption(str);
				update();
			}
		} else if (finalCap.get() == removeButton) {
			User.Album.Photo p = list.getSelectionModel().getSelectedItem();
			
			if (p.getCaption() != null)
			{
				p.setCaption("");
				update();
			}
		} else {
		}
	}
	
	/**
	 * This function will help log a user out.
	 * 
	 * @param e
	 * @throws IOException
	 */
	public void logout(ActionEvent e) throws IOException
	{
		
		FXMLLoader l = new FXMLLoader();
		l.setLocation(getClass().getResource("/fxml/Login.fxml"));
		BorderPane r = (BorderPane) l.load();
		
		Scene scene = new Scene(r);
		Stage s = (Stage) logout.getScene().getWindow(); 
		s.setScene(scene);
		s.setResizable(false);
		s.show();
		
		
	}
	
	/**
	 * This will handle when a user quits and will save any data.
	 * 
	 * @param e
	 * @throws IOException
	 */
	public void quit(ActionEvent e) throws IOException {
		
		Scanner.writeUser();
		e.consume();
		Stage s = (Stage) quit.getScene().getWindow();
		s.close();
	}
	
	/**
	 * Chnages the date of an album.
	 * 
	 * @param a
	 * @param photo
	 */
	public static void changeDate(User.Album a, User.Album.Photo photo)
	{
		a.setBeginDate(photo.getPhotoDate());
		a.setEndDate(photo.getPhotoDate());
	}
	
	/**
	 * This is an error handler to show a popup if something goes wrong.
	 * 
	 * @param errorCode
	 */
	public static void errorHandler(int x)
	{
		Alert e = new Alert(AlertType.ERROR);
		
		switch (x)
		{
			case pathDoesNotExist :
				e.setTitle("Invalid File Path");
				e.setContentText("The given file path is invalid.");
				break;
			case inputEmpty :
				e.setTitle("Tag Input Error");
				e.setContentText("The tag field is empty.");
				break;
			case tagExists :
				e.setTitle("Same Tag Error");
				e.setContentText("The tag input was a duplicate. Please try again.");
				break;
			case tagDoesNotExist :
				e.setTitle("Invalid Tag Error");
				e.setContentText("The input tag does not exist.");
				break;
			default:
				e.setTitle("Default error.");
				e.setContentText("Error.");
				break;
		}
		
		e.show();
	}
	
	/**
	 * Adjusts the date range of an album
	 * @param album
	 */
	public static void adjustDateRange(User.Album a)
	{
		User.Album.Photo p;
		Iterator<User.Album.Photo> it = a.photoIterator();
		
		if(!it.hasNext())
		{
			a.setEndDate(null);
			a.setBeginDate(null);
		} else {
			if (a.getBeginDate() == null || a.getEndDate() == null)
			{
				p = it.next();
				changeDate(a, p);
			} else {
				a.setBeginDate(new Date());
				
				Date end = new Date();
				end.setYear(0);
				a.setEndDate(end);
				
				while(it.hasNext())
				{
					p = it.next();
					if(p.getPhotoDate().before(a.getBeginDate()))
					{				
						a.setBeginDate(p.getPhotoDate());
					}
			
					if(p.getPhotoDate().after(a.getEndDate())) 
					{		
						a.setEndDate(p.getPhotoDate());
					}
				}
			}
		}
	}
	

	
	/**
	 * This is a getter function that returns the String split.
	 * 
	 * @return 
	 */
	public static String getSplit()
	{
		return split;
	}

}