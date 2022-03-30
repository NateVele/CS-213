package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.User.Album.Photo;
import controller.LoginController;

/**
 * This is the main model for our application.  The User class also contains Album and Photo classes.
 * 
 * @author Nathaniel Vele
 * 
 * @author Patrick Liang
 *
 */
public class User implements Serializable{
	
	private static final long serialVersionUID = 1L;

	String username;  
	
	private List<Album> userAlbumList = new ArrayList<Album>();  
	
	private List<Album.Photo> userPhotoList = new ArrayList<Album.Photo>();
	
	/**
	 * This is a constructor for the User class
	 * 
	 * @param username
	 */
	public User(String username) { 
		
		this.username = username;
	}
	
	/**
	 * This will return username as a string.
	 */
	public String toString() {
		
		return username;
	}
	
	/**
	 * This is a setter for a Users username.
	 * 
	 * @param username
	 */
	public void setUserName(String username) {
		
		this.username = username;
	}
		
	/**
	 * This will add an album to the album list.
	 * 
	 * @param album
	 */
	public void addAlbum(Album album) {
		
		userAlbumList.add(album);
	}
	
	/**
	 * This will delete an album from the album list.
	 * 
	 * @param x
	 */
	public void delAlbum(int x) {
		
		userAlbumList.remove(x);
	}
	
	/**
	 * This is a getter for a given album at an index x.
	 * 
	 * @param x
	 * @return
	 */
	public Album getAlbum(int x) {
		
		return userAlbumList.get(x);
	}
	
	/**
	 * This will return an iterator for the album list.
	 * 
	 * @return Iterator<Album>
	 */
	public Iterator<Album> albumIterator() {
		
		return userAlbumList.iterator();
	}
	
	/**
	 * This will return an iterator for the photo list.
	 * 
	 * @return Iterator<Photo>
	 */
	public Iterator<Photo> userPhotosIterator() {
		
		return userPhotoList.iterator();
	}
	
	/**
	 * This will update a photo list for a user.
	 */
	public void updateUserPhotos()
	{
		boolean photoExists = false;
		
		Iterator<Album> aIt = albumIterator();
		Iterator<Photo> uIt = userPhotosIterator();
		
		if (uIt.hasNext())
		{
			if (aIt.hasNext())
			{
				while(uIt.hasNext())
				{
					photoExists = false;
					Photo check = uIt.next();
					
					while (aIt.hasNext())
					{
						Album a = aIt.next();
						Iterator<Photo> pIt = a.photoIterator();
						
						while (pIt.hasNext())
						{
							Photo p = pIt.next();
							
							if (p.isEqual(check))
							{
								photoExists = true;
								break;
							}
					
						}
						
						if (photoExists)
						{
							break;
						}
					}
					
					
					if (!photoExists)
					{
						userPhotoList.remove(check);
					}
					
				}
			} else {
				while (uIt.hasNext())
				{
					uIt.next();
					uIt.remove();
				}
			}
		}
	}
	
	/**
	 * This is the album class.  This holds the album object.
	 * 
	 * @author Nathaniel Vele
	 * 
	 * @author Patrick Liang
	 *
	 */
	public static class Album implements Serializable{  
		
		private static final long serialVersionUID = 1L;

		private List<Photo> photoList = new ArrayList<Photo>();  
		
		private String title;
		
		private Date beginDate;

		private Date endDate;
		
		private String dateRange; 
		
		private int photoCount;
		
		/**
		 * Constructor for the album object.
		 * 
		 * @param title
		 */
		public Album(String title) {
			
			this.title = title;
			
			photoCount = 0;
		}
		
		/**
		 * This is a setter for the album title.
		 * 
		 * @param title
		 */
		public void setAlbumName(String title) {
			
			this.title = title;
		}
		
		/**
		 * This is a getter for the album title.
		 * 
		 * @return String
		 */
		public String getAlbumName() {
			
			return title;
		}
		
		/**
		 * This is a getter for the photo count.
		 * 
		 * @return String
		 */
		public String getNumOfPhotos() {
			
			return Integer.toString(photoCount);
		}
		
		/**
		 * This function changes the photo count variable +/- a num.
		 * 
		 * @param x
		 * @param op
		 */
		public void opNumOfPhotos(int x, char plusMinus)
		{
			if (plusMinus == '+')
			{
				photoCount += x;
			} else if (plusMinus == '-')
			{
				photoCount -= x;
			}
		}
		
		/**
		 * This function will add a photo to the list.
		 * 
		 * @param p
		 */
		public void addPhoto(Photo newPhoto)
		{
			User u = Scanner.getUser(LoginController.getUserIndex());
			Iterator<Photo> pIt = u.userPhotoList.iterator();
			Photo temp = null;
			
			while (pIt.hasNext())
			{
				temp = pIt.next();
				if(newPhoto.isFileLocationEqual(temp))
				{
					newPhoto.setCaption(temp.caption);
					break;
				} else {
					temp = null;
				}
			}
			
			if (temp != null)
			{
				photoList.add(temp);
			} else {
				u.userPhotoList.add(newPhoto);
				photoList.add(newPhoto);
			}
			photoCount++;
		}
		
		/**
		 * This function will delete a photo from the list.
		 * 
		 * @param p
		 */
		public void deletePhoto(Photo myPhoto)
		{
			User u = Scanner.getUser(LoginController.getUserIndex());
			int copyCount = 0;
			
			photoList.remove(myPhoto);
			
			Iterator<Album> aIt = u.albumIterator();
			while(aIt.hasNext())
			{
				Album a = aIt.next();
				Iterator<Photo> albumPhotos = a.photoIterator();
				while (albumPhotos.hasNext())
				{
					Photo p = albumPhotos.next();
					if (myPhoto.isEqual(p))
					{
						copyCount = 1;
						break;
					}
				}
				
				if (copyCount == 1)
				{
					break;
				}
			}
			
			if (copyCount == 0)
			{
				u.userPhotoList.remove(myPhoto);
			}

			if (photoCount <= 0)
			{
				photoCount = 0;
			} else {
				photoCount--;
			}
		}
		
		/**
		 * This will set the begin date for an album.
		 * 
		 * @param d
		 */
		public void setBeginDate(Date begin) {
			
			beginDate = begin;
		}
		
		/**
		 * This is a getter for the begin date of an album.
		 * 
		 * @return Date
		 */
		public Date getBeginDate() {
			
			return beginDate;
		}
		
		/**
		 * This is a getter for the end date of an album.
		 * 
		 * @return Date
		 */
		public Date getEndDate() {
			
			return endDate;
		}
		
		/**
		 * This is a setter for the end date of an album.
		 * 
		 * @param d
		 */
		public void setEndDate(Date d) {
			
			endDate = d;
		}
		
		/**
		 * This function will return an iterator for the photo list.
		 * 
		 * @return Iterator<Album.Photo>
		 */
		public Iterator<Album.Photo> photoIterator() {
			
			return photoList.iterator();
		}
		
		/**
		 * This will return the date range of the album.
		 * 
		 * @return String
		 */
		public String getDateRange() {
			
			if(beginDate == null) {
				
				return " - ";
			}
			
			SimpleDateFormat myDate = new SimpleDateFormat("MM/dd/yyyy");
			
			return  myDate.format(beginDate) + " - " + myDate.format(endDate);
		}
		
		/**
		 * This function returns the date in string form.
		 * 
		 * @return String
		 */
		public String toString() {
			
			return String.format("%s %50s %s - %s", title,photoCount,beginDate,endDate);
		}
		
		/**
		 * This is the photo class, which handles the photo object.
		 * 
		 * @author Nathaniel Vele
		 * 
		 * @author Patrick Liang
		 *
		 */
		public static class Photo implements Serializable {  
			
			private static final long serialVersionUID = 1L;

			private String title;
			
			private String loc;
			
			private String caption = "";
						
			private Date photoDate;
			
			private List<Tag> tags = new ArrayList<Tag>();
			
			/**
			 * This is a constructor for the photo object.
			 * 
			 * @param name
			 * @param location
			 */
			public Photo(String name, String location)
			{
				title = name;
				this.loc = location;
			}
			
			/**
			 * This will add a tag to the tag list.
			 * 
			 * @param name
			 * @param value
			 * @return boolean 
			 */
			public boolean addTag(String tag, String val)
			{
				for (Iterator<Tag> it = tags.iterator(); it.hasNext();)
				{
					Tag element = it.next();
					if(element.isEqual(tag, val))
					{
						return false;
					}
				}
				tags.add(new Tag(tag, val));
				
				return true;
			}
			
			/**
			 * This function will remove a tag from the list.
			 * 
			 * @param name
			 * @param value
			 * @return
			 */
			public boolean removeTag(String tag, String val)
			{
				for (Iterator<Tag> it = tags.iterator(); it.hasNext();)
				{
					Tag element = it.next();
					if(element.isEqual(tag, val))
					{
						it.remove();
						return true;
					}
				}

				return false;
			}
			
			/**
			 * This is a function that returns an iterator for the tag list.
			 * 
			 * @return Iterator<Tag>
			 */
			public Iterator<Tag> tagIterator() {
				
				return tags.iterator();
			}
			
			/**
			 * This is a getter to retrieve the photo title.
			 * 
			 * @return String
			 */
			public String getPhotoName()
			{
				return title;
			}
			
			/**
			 * This is a setter for the photo name.
			 * 
			 * @param name
			 */
			public void setPhotoName(String name)
			{
				title = name;
			}
			
			/**
			 * This is a getter for the location of the photo.
			 * 
			 * @return String
			 */
			public String getPhotoLocation()
			{
				return loc;
			}
			
			/**
			 * This is a setter for the photo location.
			 * 
			 * @param filePath
			 */
			public void setPhotoLocation(String filePath)
			{
				loc = filePath;
			}
			
			/**
			 * This is a getter for the caption of a photo.
			 * 
			 * @return String
			 */
			public String getCaption()
			{
				return caption;
			}
			
			/**
			 * This is a setter for the caption of a photo.
			 * 
			 * @param caption
			 */
			public void setCaption(String caption)
			{
				this.caption = caption;
			}
			
			/**
			 * This is a getter for the caption of a photo.
			 * 
			 * @return Data
			 */
			public Date getPhotoDate() {
				
				return photoDate;
			}
			
			/**
			 * This is a getter for the date of the photo in string form.
			 * 
			 * @return String
			 */
			public String getPhotoDateString()
			{
				return photoDate.toString();
			}
			
			/**
			 * This is a setter for the date of a photo.
			 * 
			 * @param d
			 */
			public void setPhotoDate(Date d) {
				
				photoDate = d;
			}
			
			/**
			 * This is a getter for the tags in a photo.
			 * 
			 * @param name
			 * @return String
			 */
			public String getTags(String title)
			{
				StringBuilder myTags = new StringBuilder();
				
				for (Iterator<Tag> it = tags.iterator(); it.hasNext();)
				{
					Tag e = it.next();
					String set[] = e.toString().split("~");
					if (set[0].equals(title))
					{
						myTags.append(set[1] + ",");
					}
				}
				
				if (myTags.length() > 0)
				{
					myTags.deleteCharAt(myTags.length()-1);
				}
				
				return myTags.toString();
			}
			
			/**
			 * This will set the tags of a photo.
			 * 
			 * @param name
			 * @param tempTags
			 */
			public void setTags(String name, String tempTags)
			{
				String[] myTags = tempTags.split(",");
				
				for (int i = 0; i < myTags.length; i++)
				{
					Tag temp = new Tag(name, myTags[i]);
					tags.add(temp);
				}
			}
			
			/**
			 * This retrieves all the tags in the tag list. 
			 * 
			 * @return String
			 */
			public String getAllTags()
			{
				StringBuilder myTags = new StringBuilder();
				
				for (Iterator<Tag> it = tags.iterator(); it.hasNext();)
				{
					Tag e = it.next();
					String set[] = e.toString().split("~");
					myTags.append(set[1] + ",");
				}
				
				if (myTags.length() > 0)
				{
					myTags.deleteCharAt(myTags.length()-1);
				}
				
				return myTags.toString();
			}
			
			/**
			 * This checks to see if the file location is the same for two photos.
			 * 
			 * @param p
			 * @return boolean
			 */
			public boolean isFileLocationEqual(Photo p)
			{
				return this.getPhotoLocation().compareTo(p.getPhotoLocation()) == 0;
			}
			
			/**
			 * This checks if two photos are equal.
			 * 
			 * @param p
			 * @return boolean
			 */
			public boolean isEqual(Photo p)
			{
				return this.getPhotoName().compareTo(p.getPhotoName()) == 0
						&& this.getPhotoLocation().compareTo(p.getPhotoLocation()) == 0
						&& this.getPhotoDateString().compareTo(p.getPhotoDateString()) == 0
						&& this.getCaption().compareTo(p.getCaption()) == 0
						&& this.getAllTags().compareTo(p.getAllTags()) == 0;
			}
			
			/**
			 * This returns the name of the photo in string form.
			 * 
			 * @return String
			 */
			public String toString()
			{
				return getPhotoName();
			}
		}
		
	}

}