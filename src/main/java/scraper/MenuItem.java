package scraper;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MenuItem {
	public static DecimalFormat df = new DecimalFormat("0.00");
	public static final int SUNDAY=1,MONDAY=2,TUESDAY=3,WEDNESDAY=4,THURSDAY=5,FRIDAY=6,SATURDAY=7;
	
	public String name;
	
	public double cost;
	
	public int calories;
	public double fat, satFat, transFat, cholesterol, sodium, carb, fiber, sugars, protein;
	public boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday;
	public String restaurant;
	
	public ArrayList<String> allergens = new ArrayList<String>();
	
	public String toString() {
		String allergenString = "";
		for(String a : allergens) {
			allergenString += a + " ";
		}
		return "Name: " + name + " ($" + df.format(cost) + ")"
				+ "\n	Restaurant: " + restaurant
				+ "\n	Calories: " + calories
				+ "\n	Fat: " + fat + "g"
				+ "\n	Saturated Fat: " + satFat + "g"
				+ "\n	Trans Fat: " + transFat + "g"
				+ "\n	Cholesterol: " + cholesterol + "mg"
				+ "\n	Sodium: " + sodium + "mg"
				+ "\n	Carbohydrates: " + carb + "g"
				+ "\n	Fiber: " + fiber + "g"
				+ "\n	Sugars: " + sugars + "g"
				+ "\n	Protein: " + protein + "g"
				+ "\n	Allergens: " + allergenString;
	}
	
	public boolean equals(MenuItem mi) {
		return this.name.equals(mi.name) && this.restaurant.equals(mi.restaurant);
	}
	
	public void addDay(int day) {
		switch(day) {
			case SUNDAY:
				sunday = true;
				break;
			case MONDAY:
				monday = true;
				break;
			case TUESDAY:
				tuesday = true;
				break;
			case WEDNESDAY:
				wednesday = true;
				break;
			case THURSDAY:
				thursday = true;
				break;
			case FRIDAY:
				friday = true;
				break;
			case SATURDAY:
				saturday = true;
				break;
		}
	}
	
}
