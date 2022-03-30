package model;

import java.io.Serializable;

/**
 * This is a class for the tag options for photos.
 * 
 * @author Nathaniel Vele
 * @author Patrick Liang
 */

public class Tag implements Serializable
{
	private String name;
	private String val;
	
	/**
	 * Tag constructor
	 * 
	 * @param name
	 * @param val
	 */
	
	public Tag(String name, String val)
	{
		this.name = name;
		this.val = val;
	}
	
	/**
	 * This is a getter for the name of the tag.
	 * 
	 * @return String
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * This is a setter for the name of the tag.
	 * 
	 * @param temp
	 */
	public void setName(String temp)
	{
		name = temp;
	}
	
	/**
	 * This is a getter for the value of the tag.
	 * 
	 * @return String
	 */
	public String getValue()
	{
		return val;
	}
	
	/**
	 * This is a setter for the value of the tag.
	 * 
	 * @param temp
	 */
	public void setValue(String temp)
	{
		val = temp;
	}
	
	/**
	 * This function compares the names of two tags.
	 * 
	 * @param otherTag
	 * @return int
	 */
	public int compareName(String otherTag)
	{
		return name.compareTo(otherTag);
	}
	
	/**
	 * This function compares the values of two tags
	 * @param otherTag
	 * @return int
	 */
	public int compareValue(String otherTag)
	{
		return val.compareTo(otherTag);
	}
	
	/**
	 * This function will compare both values and names of two tags.
	 * 
	 * @param otherName
	 * @param otherValue
	 * @return boolean
	 */
	public boolean isEqual(String otherName, String otherValue)
	{
		return name.compareTo(otherName) == 0 && val.compareTo(otherValue) == 0;
	}
	
	/**
	 * This will parse the tag name and value into one string.
	 * 
	 * @return String
	 */
	public String toString()
	{
		return getName() + "~" + getValue();
	}
}