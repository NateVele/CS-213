package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Scanner;
import model.User;

/**
 * This class will control the slideshow aspect of our application.  Users will be able to go through all photos in a given album.
 * 
 * @author Nathaniel Vele
 * 
 * @author Patrick Liang
 *
 */
public class SlideController implements Initializable
{

	@FXML
    private ImageView pImage;
	
	@FXML 
	private Button lButton, rButton, returnButton, logout, quitButton;
	
	private static User.Album.Photo[] slides;
	
	private static int currP = 0;
	
	/**
	 * This function will initialize everything in the scene and display the first photo in the array.
	 * 
	 * @param url
	 * @param res
	 */
	@Override
	public void initialize(URL url, ResourceBundle res)
	{
		currP = 0;
		
		User u = Scanner.getUser(LoginController.getUserIndex());
		
		Iterator<User.Album.Photo> it = u.getAlbum(AlbumController.getIndexOfAlbum()).photoIterator();
		
		List<User.Album.Photo> list = new ArrayList<>();
		
		while(it.hasNext())
		{
			list.add(it.next());
		}
		
		slides = list.toArray(new User.Album.Photo[0]);
		
		updateDisplay("init");
		
	}
	
	/**
	 * This will update the photos in the slideshow and keep it moving.
	 * 
	 * @param str
	 */
	private void updateDisplay(String str)
	{
		if (slides.length != 0)
		{
			User.Album.Photo p;
			File f;
			
			if (str.equals("init"))
			{
				p = slides[currP];
				f = new File(p.getPhotoLocation());
				
				if(f.exists())
				{
					Image show = new Image(f.toURI().toString());
					pImage.setImage(show);
				}
			} else if (str.equals("left")) {
				if (currP <= 0)
				{
					currP = slides.length - 1;
				} else {
					currP --;
				}
				
				p = slides[currP];
				f = new File(p.getPhotoLocation());
				
				if(f.exists())
				{
					Image show = new Image(f.toURI().toString());
					pImage.setImage(show);
				}
				
			} else if (str.equals("right")) {
				if (currP == slides.length - 1)
				{
					currP = 0;
				} else {
					currP ++;
				}
				
				p = slides[currP];
				f = new File(p.getPhotoLocation());
				
				if(f.exists())
				{
					Image show = new Image(f.toURI().toString());
					pImage.setImage(show);
				}
			}
		} else {
			wipe();
		}
	}
	
	/**
	 * This lets the user go back and forth between photos.
	 * 
	 * @param e
	 */
	public void leftOrRight(ActionEvent e)
	{
		if(slides.length != 0)
		{
			if(e.getSource() == rButton)
			{
				updateDisplay("right");
				
			} else if (e.getSource() == lButton)
				updateDisplay("left");
			{
				
			}
		}
	}
	
	/**
	 * This function will just set the display to a blank image.
	 */
	public void wipe()
	{
		pImage.setImage(null);
	}
	
	/**
	 * This function will allow the user to log out from this scene.
	 * 
	 * @param e
	 * @throws IOException
	 */
	public void logout(ActionEvent e) throws IOException
	{
		
		FXMLLoader l = new FXMLLoader();
		l.setLocation(getClass().getResource("../fxml/Login.fxml"));
		BorderPane r = (BorderPane) l.load();
		
		Scene scene = new Scene(r);
		Stage s = (Stage) logout.getScene().getWindow();  
		s.setScene(scene);
		s.setResizable(false);
		s.show();
		
		
	}
	
	/**
	 * This function will allow the user to quit the program and save their data.
	 * 
	 * @param e
	 * @throws IOException
	 */
	public void quit(ActionEvent e) throws IOException
	{
		
		Scanner.writeUser();
		
		e.consume();
		
		Stage s = (Stage) quitButton.getScene().getWindow();  
		s.close();
	}
	
	/**
	 * This will return the user to the last scene.
	 * 
	 * @param e
	 * @throws IOException
	 */
	public void goBack(ActionEvent e) throws IOException
	{
		FXMLLoader l = new FXMLLoader();
		l.setLocation(getClass().getResource("../fxml/Photo.fxml"));
		AnchorPane r = (AnchorPane) l.load();
		
		Scene scene = new Scene(r);
		Stage s = (Stage) returnButton.getScene().getWindow();
		s.setScene(scene);
		s.setResizable(false);
		s.show();
	}
}