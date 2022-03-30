package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * This will serialize and save the data in between sessions so that users can access their data after logging out.
 * 
 * @author Nathaniel Vele
 * 
 * @author Patrick Liang
 */
public class Scanner {
	
	private static List<User> list = new ArrayList<User>();  
	
	
	/**
	 * This will add a user to the list.
	 * 
	 * @param u 
	 */
	public static void addUser(User u) {
		
		list.add(u);
	}
	
	/**
	 * This will remove a user from the list.
	 * 
	 * @param num 
	 */
	public static void delUser(int num) {
		
		list.remove(num);
	}
	
	/**
	 * This is a getter to retrieve a user from the list.
	 * 
	 * @param num
	 * @return User
	 */
	public static User getUser(int num) {
		
		return list.get(num);
	}
	
	/**
	 * This will return an iterator so we can look through all users when needed.
	 * 
	 * @return 
	 */
	public static Iterator<User> userIterator() {
		
		return list.iterator();
	}
	
	/**
	 * This will write info to a file that can be read later to retrieve info.
	 * 
	 * @throws IOException throws if file does not exist
	 */
	public static void writeUser() throws IOException {
		
		String file = "src/info/users.dat";
				
		ObjectOutputStream oos = null;

		try {
			
			 oos = new ObjectOutputStream(new FileOutputStream(file));
			 
			 oos.writeObject(list);
			
		} catch(Exception e) {
			
			e.printStackTrace();
		}

		
		oos.close();
		
	}
	
	/**
	 * @throws IOException throws if file does not exist
	 * @throws ParseException throws if date cannot be parsed
	 */
	public static void readUser() throws IOException, ParseException {
		
		String userFile = "src/info/users.dat";
		
		File f = new File("src/info/users.dat");
		
		String[] photoNames = {"Photo1", "Photo2", "Photo3", "Photo4", "Photo5"};
		
		String[] locations = {"src/stock/photo1.jpg", "src/stock/photo2.jpeg", "src/stock/photo3.jpg", "src/stock/photo4.jpg", "src/stock/photo5.jpg"};
		
		User.Album stockAlbum = new User.Album("stock album");
		
		if(f.length() == 0) {
			
			User stock = new User("stock");
			
			list.add(stock);
			
			for(int i = 0; i < locations.length; i++) {
		
				User.Album.Photo img = new User.Album.Photo(photoNames[i],locations[i]);
				
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				
				String str = sdf.format(f.lastModified());
				
				Date date = sdf.parse(str);
				
				img.setPhotoDate(date); 
				
				stockAlbum.setBeginDate(img.getPhotoDate());
				stockAlbum.setEndDate(img.getPhotoDate());
				
				if(img.getPhotoDate().before(stockAlbum.getBeginDate())) {
					
					stockAlbum.setBeginDate(img.getPhotoDate());
				}
				
				if(img.getPhotoDate().after(stockAlbum.getEndDate())) {
										
					stockAlbum.setEndDate(img.getPhotoDate());
				}
				
				stockAlbum.addPhoto(img);	
			
			}
			
			stock.addAlbum(stockAlbum);
			
			return;
		}
				
		try {
			
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(userFile));
			 
	        list = (List<User>) in.readObject(); 	
	        
	        in.close();
	        
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}