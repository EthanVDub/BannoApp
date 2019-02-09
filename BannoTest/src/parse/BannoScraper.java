package parse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

/**
 * Class that parses html data from banno.com to find specific information
 * @author Ethan Vander Wiel
 *
 */
public class BannoScraper {
	
	/**
	 * Only instance variable, keeps track of number of products.
	 * Needed for when the method "getProducts" is called.
	 */
	private static int numberOfProducts;
	Document doc;
	
	/**
	 * This method finds the amount of products listed on banno.com.
		It first finds all the data within #sub-menu, the call tag from the index site that references
		both product categories: Digital Banking Suite and Web Products
	 * @return
	 */
	public ArrayList<String> getProducts() {
		try {
			doc = Jsoup.connect("https://banno.com/").get();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ArrayList<String> result = new ArrayList<>();
		Elements results = doc.select("#sub-menu a");
		for(int i = 0; i < results.size(); i++) {
			Element e = results.get(i);
			String products = e.attr("href"); //Grabs the link referenced in both menu items. This allows getProducts to know the URL to look into
			result.addAll(getProductsHelper(products));
		}
		return result;
	}
	
	/**
	 * Private method that returns all products, listed by .subtitle, on the url parameter
	 * @param url
	 * 	suffix url to banno.com
	 */
	private static ArrayList<String> getProductsHelper(String url) {
		ArrayList<String> result = new ArrayList<>();
		Document productDoc = null; //Will be the document jsoup returns
		try {
			productDoc = Jsoup.connect("https://banno.com" + url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Elements results = productDoc.select(".subtitle"); //Get all html with tags .subtitle
		
		//The first element of .subtitle is actually the product type, not a product itself
		Element firstCategory = results.get(0);
		String category = firstCategory.text();
		result.add("Product Category: "+ category);
		//For loop that goes through all the results, adds it to number of products and prints the product
		for(int i = 1; i < results.size(); i++) {
			Element e = results.get(i);
			String products = e.text();
			numberOfProducts++;
			result.add(products);
		}
		return result;
	}
	
	/**
	 * Simple method to return number of products to jsp file
	 * (Since technically the number of products is less than what is in the arraylist)
	 * @return
	 * 	Number of Products
	 */
	public int getNumberOfProducts() {
		return numberOfProducts;
	}
	
	/**
	 * This method goes through all the characters in the html document, checks if they are
		alphanumeric, and then adds them to a list. For this job, I decided to make my own class/data structure that acts
		as a custom map. This allows me to sort by values of the map instead of keys like a TreeMap. See below for class details.
	 * @return
	 * 	ArrayList of AlphaNum values
	 */
	public ArrayList<AlphaNum> getAlphaNum() {
		AlphaNumMap m = new AlphaNumMap(); //Creates new map
		String baseHTML = doc.toString();
		
		for(int i = 0; i < baseHTML.length(); i++) {
			//Basic string that allows me to check if the character is alphanumeric.
			String alphanumstring = "abcdefghijklmnopqrstuvwxyz0123456789";
		
			//Converts to lowercase, checks if it is alphanumeric, and adds to list within my custom map
			char current = Character.toLowerCase(baseHTML.charAt(i));
			if(!(alphanumstring.contains(current + ""))) continue;
			m.addToList(current);
		}
		
		//Sorts given the compareTo within the AlphaNum class and prints out to command line
		Collections.sort(m.list, Collections.reverseOrder());
		return m.list;
		
	}
	
	/**
	 * Method to return the amount of png files in the html of banno.com
	 * @return
	 * 	int number of png files
	 */
	public ArrayList<String> getImages() {
		Elements images = doc.select("img");
		ArrayList<String> allPNG = new ArrayList<>();
		for(int i = 0; i < images.size(); i++) {
			Element imgElement = images.get(i);
			String image = imgElement.attr("src");
			if(image.substring(image.length() - 3, image.length()).equals("png")) {
				allPNG.add(image);
			}
				
		}
		return allPNG;
	}
	
	
	/**
	 * Simple method that gathers the twitter handle. The twitter handle is within a meta tag with the name "twitter:site".
	 * @return
	 * 	Twitter Handle String
	 */
	public String getBannoTwitter() {
		Elements twitterHandle = doc.select("meta[name=twitter:site]");
		String e = twitterHandle.first().attr("content");
		return e;
	}
	
	/**
	 * countOcc is a simple class that checks the HTML source for the occurrences of a substring.
	 * @param s
	 * 	substring to be checked
	 * @return
	 * 	(int) Occurrences of s within html
	 */
	public int countOcc(String s) {
		String html = doc.toString();
		int startingIndex = 0;
		int result = 0;
		
		/*Basic substring within string check. If the string doesn't exist, it will return -1. 
		 * This allows a use of indexOf that will go through the whole html until it finds the next 
		 * substring*/
		while(startingIndex != -1) {
			startingIndex = html.indexOf(s, startingIndex);
			
			if(startingIndex != -1) {
				result++;
				startingIndex += s.length();
			}
		}
		return result;
	}
	
	
	/**
	 * AlphaNum is an incredibly simple class that simply contains a character and integer. 
	 * The character is the alphanumeric character and the integer is the amount of occurrences within the html document
	 * The overridden compareTo method allows AlphaNum's to be added to Lists and sorted quickly using Collections.sort().
	 * @author Ethan Vander Wiel
	 *
	 */
	public static class AlphaNum implements Comparable<AlphaNum> {
		/**
		 * Character instance variable. The "key" of the map entry
		 */
		char character;
		
		/**
		 * Integer instance variable. The "value" of the map entry.
		 */
		int integer;
		AlphaNum(char givenChar, int givenInt) {
			character = givenChar;
			integer = givenInt;
		}
		
		@Override
		public int compareTo(AlphaNum arg0) {
			return integer - (arg0.integer);
		}
		@Override
		public String toString() {
			return character + ": " + integer;
		}
	}
	
	/**
	 * AlphaNumMap is another simple class that acts as a custom map data structure.
	 * At it's core, it is a list within a class. The main reason for it's existence is 
	 * every time you try to add a character it will check to see if that character exists, like a map checks for a key.
	 * However, because AlphaNum has a compareTo, the AlphaNumMap list can be sorted based on values, which is slightly more difficult to do 
	 * within a traditional map that usually sorts using keys if it sorts at all.
	 * @author Ethan Vander Wiel
	 *
	 */
	static class AlphaNumMap {
		ArrayList<AlphaNum> list = new ArrayList<AlphaNum>();
		
		//Checks to see if character is already within list. If so, increment value, else, add character to list with value 1
		public AlphaNum addToList(char character) {
			for(int i = 0; i < list.size(); i++) {
				if(list.get(i).character == character) {
					list.get(i).integer++;
					return list.get(i);
				}
			}
			AlphaNum newAlphaNum = new AlphaNum(character, 1);
			list.add(newAlphaNum);
			return newAlphaNum;
		}
	}
	
	
}