package scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Scraper {
	private static final String DINING_MENU_URL = "https://hdh.ucsd.edu/DiningMenus/";
	private static final String RESTAURANT_EXTENSION_URL = "default.aspx?i=";

	private static final int DAYS_IN_WEEK = 7;

	// testing purposes
	public static void main(String args[]) throws Exception {
		ArrayList<String> URLs = getRestaurantURLs();
		System.out.println(URLs);
		/*
		 * ArrayList<MenuItem> menuItems = downloadMenuItems(URLs.get(0)); for(MenuItem
		 * m : menuItems) { System.out.println(m); }
		 */
	}

	public static void addAllMenuItems(SQLHandler handler, String menuTableName) throws IOException {
		ArrayList<String> URLs = getRestaurantURLs();
		for (String s : URLs) {
			ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
			try {
				menuItems.addAll(downloadMenuItems(s));
				handler.addMenuItems(menuItems, menuTableName);
				System.out.println("Done with " + menuItems.get(0).restaurant);
			} catch (Exception e) {
				e.printStackTrace(System.out);
				System.out.println("Couldnt add");
			}
		}

	}

	public static ArrayList<MenuItem> downloadMenuItems(String restaurantLink) {
		// few threads to speed stuff up
		ExecutorService exec = Executors.newFixedThreadPool(15);
		AtomicReference<Integer> loaded = new AtomicReference<Integer>(new Integer(0));
		AtomicReference<ArrayList<MenuItem>> menuItems = new AtomicReference<ArrayList<MenuItem>>(
				new ArrayList<MenuItem>());
		HtmlPage page;
		boolean specialty = false;
		try (WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
			page = (webClient.getPage(DINING_MENU_URL + restaurantLink));
			int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

			// click button to go to next day
			for (int i = 0; i < DAYS_IN_WEEK; i++) {
				System.out.println("Loading day");
				// sunday = 1, monday = 2, etc
				List<HtmlElement> aElements = null;
				try {
					aElements = page.getElementById("MenuListing_divRestaurants").getElementsByTagName("a");
				} catch (Exception e) {
					System.out.println("Either loading specialty restaurant or something went wrong");
					specialty = true;
					try {
						if (aElements == null || aElements.size() == 0) {
							aElements = page.getElementById("MenuListing_divSpecialtyRestaurants")
									.getElementsByTagName("a");
						}
					} catch (Exception e2) {
						e2.printStackTrace(System.out);
					}
				}
				for (HtmlElement dm : aElements) {
					exec.execute(new RunnableScraper(page, i) {
						@Override
						public void run() {
							MenuItem mi = null;
							try {
								try {
									mi = getMenuItem(
											page.getElementById("HoursLocations_locationName").getTextContent(),
											dm.getAttribute("href"),
											Double.parseDouble(
													dm.getTextContent().substring(dm.getTextContent().indexOf("($"))
															.replace(")", "").replace("($", "")));
								} catch (StringIndexOutOfBoundsException e) {
									// cost not listed
									mi = getMenuItem(
											page.getElementById("HoursLocations_locationName").getTextContent(),
											dm.getAttribute("href"), -1);
									System.out.println("cost not listed for " + mi.name + " in " + mi.restaurant);
								}
								// print only on first item
								if (menuItems.get().size() == 0) {
									System.out.println("Starting on restaurant " + mi.restaurant);
								}
								// only add if the list doesn't have
								boolean contains = false;
								for (int j = 0; j < menuItems.get().size(); j++) {
									MenuItem tempMi = menuItems.get().get(j); // gets jth menu item
									if (tempMi.equals(mi)) {
										tempMi.addDay((dayOfWeek + day - 1) % DAYS_IN_WEEK + 1);
										contains = true;
										break;
									}
								}
								if (!contains) {
									menuItems.get().add(mi);
								}
								loaded.set(loaded.get() + 1);

							} catch (Exception e) {
								System.out.println(
										"Failed to load menu item (successfully loaded the last " + loaded + ")");
								e.printStackTrace(System.out);
								loaded.set(0);

							}
						}
					});
				}
				HtmlInput nextButton = null;
				if (!specialty) {
					nextButton = (HtmlInput) page.getElementById("MenuListing_imgRightArrowSpecial");
				} else {
					nextButton = (HtmlInput) page.getElementById("MenuListing_imgRightArrowSpecialtyRest");
				}
				page = nextButton.click();
			}
		} catch (Exception e1) {
			e1.printStackTrace(System.out);
		} finally {
			exec.shutdown();
			while (!exec.isTerminated()) {
			} // wait for termination
		}
		return menuItems.get();
	}

	public static MenuItem getMenuItem(String restaurant, String extension, double cost) throws Exception {
		MenuItem mi = new MenuItem();
		mi.restaurant = restaurant;
		mi.cost = cost;

		Document doc = Jsoup.connect(DINING_MENU_URL + extension).userAgent("Chrome").get();

		mi.name = doc.getElementById("lblItemHeader").ownText();
		mi.calories = Integer.parseInt(doc.getElementById("tblFacts").child(0).child(2).child(0).child(0).text()
				.replaceAll("[^\\d]", "").replaceAll("\\u00a0", ""));

		Elements nutritionTableRows = doc.getElementById("tblNutritionDetails").child(0).children();

		mi.fat = Double.parseDouble(nutritionTableRows.get(1).child(0).ownText().replace("g", ""));

		mi.carb = Double.parseDouble(nutritionTableRows.get(1).child(2).ownText().replace("g", ""));
		try {
			mi.satFat = Double.parseDouble(nutritionTableRows.get(2).child(0).text().replace("g", "")
					.replace("Sat. Fat", "").replaceAll("\\u00a0", ""));
		} catch (NumberFormatException nfe) {
			mi.satFat = 0;
		}
		mi.fiber = Double.parseDouble(nutritionTableRows.get(2).child(2).text().replace("g", "")
				.replace("Dietary Fiber", "").replaceAll("\\u00a0", ""));
		try {
			mi.transFat = Double.parseDouble(nutritionTableRows.get(3).children().get(0).ownText().replace("g", "")
					.replace("Trans Fat", "").replaceAll("\\u00a0", ""));
		} catch (NumberFormatException nfe) {
			mi.transFat = 0;
		}
		mi.sugars = Double.parseDouble(nutritionTableRows.get(3).child(2).ownText().replace("Sugars", "")
				.replace("g", "").replaceAll("\\u00a0", ""));
		mi.cholesterol = Double.parseDouble(nutritionTableRows.get(4).child(0).ownText().replace("mg", "")
				.replace("Cholesterol", "").replaceAll("\\u00a0", ""));
		mi.protein = Double.parseDouble(nutritionTableRows.get(4).child(2).ownText().replace("g", "")
				.replace("Protein", "").replaceAll("\\u00a0", ""));
		mi.sodium = Double.parseDouble(nutritionTableRows.get(5).child(0).ownText().replace("mg", "")
				.replace("Sodium", "").replaceAll("\\u00a0", ""));

		mi.allergens = new ArrayList<String>(
				Arrays.asList(doc.getElementById("lblAllergens").ownText().replaceAll("\\s+", "").split(",")));
		return mi;
	}

	public static ArrayList<String> getRestaurantURLs() throws IOException {
		ArrayList<String> restaurantURLs = new ArrayList<String>();

		Document doc = Jsoup.connect(DINING_MENU_URL).get();
		Elements newsHeadlines = doc.select("#topnav a");
		for (int i = 0; i < newsHeadlines.size(); i++) {
			String link = newsHeadlines.get(i).attr("href");
			if (link.contains(RESTAURANT_EXTENSION_URL)) {
				restaurantURLs.add(link);
			}
		}

		return restaurantURLs;
	}
}
