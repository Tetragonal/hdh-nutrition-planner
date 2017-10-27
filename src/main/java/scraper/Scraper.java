package scraper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Scraper {
	private static final String DINING_MENU_URL = "https://hdh.ucsd.edu/DiningMenus/";
	private static final String RESTAURANT_EXTENSION_URL = "default.aspx?i=";

	private static final int DAYS_IN_WEEK = 7;
	
	//testing purposes
	public static void main(String args[]) throws Exception {
		ArrayList<String> URLs = getRestaurantURLs();
		ArrayList<MenuItem> menuItems = downloadMenuItems(URLs.get(0));
		for(MenuItem m : menuItems) {
			System.out.println(m);
		}		
	}
	
	public static void addAllMenuItems(SQLHandler handler) throws Exception {
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);  //turn off HtmlUnit logging
		ArrayList<String> URLs = getRestaurantURLs();
		
		for(String s : URLs) {
			ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
			menuItems.addAll(downloadMenuItems(s));
			handler.addMenuItems(menuItems);
			System.out.println("Done with " + menuItems.get(0).restaurant);
		}
		
	}
	
	public static ArrayList<MenuItem> downloadMenuItems(String restaurantLink) throws Exception {
		ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
	    try (WebClient webClient = new WebClient()) {
	        HtmlPage page = webClient.getPage(DINING_MENU_URL + restaurantLink);
	        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	        //click button to go to next day
	        for(int i=0; i<DAYS_IN_WEEK; i++) {
	        	System.out.println("Loading day");
	        	//sunday = 1, monday = 2, etc
		        List<HtmlElement> aElements = page.getElementById("MenuListing_divRestaurants").getElementsByTagName("a");
		        for(HtmlElement dm : aElements) {
		        	MenuItem mi = null;
		        	try {
		        		mi = getMenuItem(page.getElementById("HoursLocations_locationName").getTextContent(), dm.getAttribute("href"), Double.parseDouble(dm.getTextContent().substring(dm.getTextContent().indexOf("($")).replace(")", "").replace("($", "")));
		        	}catch(Exception e) {
		        		//if no price listed, initialize with "-1"
		        		mi = getMenuItem(page.getElementById("HoursLocations_locationName").getTextContent(), dm.getAttribute("href"), -1);
		        	}
		        	//only add if the list doesn't have 
		        	boolean contains = false;
		        	for(int j=0; j<menuItems.size(); j++) {
		        		MenuItem tempMi = menuItems.get(j); //gets jth menu item
		        		if(tempMi.equals(mi)) {
		        			tempMi.addDay((dayOfWeek+i-1)%DAYS_IN_WEEK+1);
		        			contains = true;
		        			break;
		        		}
		        	}
		        	if(!contains) {
		        		menuItems.add(mi);
		        	}
		        }
		        
		        HtmlInput nextButton = (HtmlInput) page.getElementById("MenuListing_imgRightArrowSpecial");
		        page = nextButton.click();
		     
	        }
	    }
	    return menuItems;
	}
	
	public static MenuItem getMenuItem(String restaurant, String extension, double cost) throws Exception {
		MenuItem mi = new MenuItem();
		mi.restaurant = restaurant;
		mi.cost = cost;
		
		Document doc = Jsoup.connect(DINING_MENU_URL+extension).userAgent("Mozilla").get();

		mi.name = doc.getElementById("lblItemHeader").ownText();
		mi.calories = Integer.parseInt(doc.getElementById("tblFacts").child(0).child(2).child(0).child(0).text().replaceAll("[^\\d]", "").replaceAll("\\u00a0", ""));
		
		Elements nutritionTableRows = doc.getElementById("tblNutritionDetails").child(0).children();
		
		mi.fat = Double.parseDouble(nutritionTableRows.get(1).child(0).ownText().replace("g", ""));
		
		mi.carb = Double.parseDouble(nutritionTableRows.get(1).child(2).ownText().replace("g", ""));
		try {
			mi.satFat = Double.parseDouble(nutritionTableRows.get(2).child(0).text().replace("g", "").replace("Sat. Fat", "").replaceAll("\\u00a0", ""));
		}catch (NumberFormatException nfe){
			mi.satFat = 0;
		}mi.fiber = Double.parseDouble(nutritionTableRows.get(2).child(2).text().replace("g", "").replace("Dietary Fiber", "").replaceAll("\\u00a0", ""));
		try {
			mi.transFat = Double.parseDouble(nutritionTableRows.get(3).children().get(0).ownText().replace("g", "").replace("Trans Fat", "").replaceAll("\\u00a0", ""));
		}catch (NumberFormatException nfe){
			mi.transFat = 0;
		}
		mi.sugars = Double.parseDouble(nutritionTableRows.get(3).child(2).ownText().replace("Sugars", "").replace("g", "").replaceAll("\\u00a0", ""));
		mi.cholesterol = Double.parseDouble(nutritionTableRows.get(4).child(0).ownText().replace("mg", "").replace("Cholesterol", "").replaceAll("\\u00a0", ""));
		mi.protein = Double.parseDouble(nutritionTableRows.get(4).child(2).ownText().replace("g", "").replace("Protein", "").replaceAll("\\u00a0", ""));
		mi.sodium = Double.parseDouble(nutritionTableRows.get(5).child(0).ownText().replace("mg", "").replace("Sodium", "").replaceAll("\\u00a0", ""));
		
		mi.allergens = new ArrayList<String>(Arrays.asList(doc.getElementById("lblAllergens").ownText().split(", ")));
		return mi;
	}
	
	public static ArrayList<String> getRestaurantURLs() throws IOException{
		ArrayList<String> restaurantURLs = new ArrayList<String>();
		
		Document doc = Jsoup.connect(DINING_MENU_URL).get();
		Elements newsHeadlines = doc.select("#topnav a");
		for(int i = 0; i < newsHeadlines.size(); i++) {
			String link = newsHeadlines.get(i).attr("href");
			if(link.contains(RESTAURANT_EXTENSION_URL)) {
				restaurantURLs.add(link);
			}
		}
		
		return restaurantURLs;
	}
}
