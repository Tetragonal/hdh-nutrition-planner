package scraper;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import jetty.Main;

public class SQLHandler {
	
	protected final String MENU_TABLE_NAME = "menu_items";
	private final String RESET_PASSWORD = "reset123";
	
	public void resetDatabase(String password) {
		Connection c = null;
		Statement stmt = null;
		if(password.equals(RESET_PASSWORD)) {
			try {
				c = Main.getConnection();
				stmt = c.createStatement();
	
				String sql = "TRUNCATE \"" + MENU_TABLE_NAME + "\";";
				stmt.executeUpdate(sql);
				
				stmt.close();
				c.close();
				System.out.println("Successfully reset database");
			} catch (Exception e) {
				System.out.println(e.getClass() + ": " + e.getMessage());
			}
		}
	}

	protected void addMenuItems(ArrayList<MenuItem> items) {
		for(MenuItem mi : items) {
			addMenuItem(mi);
		}
	}
	
	protected void addMenuItem(MenuItem mi) {
		Connection c = null;
		Statement stmt = null;
		try {
			c = Main.getConnection();
			stmt = c.createStatement();

			Array array = c.createArrayOf("text", mi.allergens.toArray());
			
			String sql = "INSERT INTO \"" + MENU_TABLE_NAME + "\" (name, restaurant, cost, calories, fat, saturated_fat, trans_fat, "
						+ "cholesterol, sodium, carbohydrates, fiber, sugars, protein, allergens,"
						+ " monday, tuesday, wednesday, thursday, friday, saturday, sunday) "
			
					+ "VALUES ("
					+ "'" + mi.name 		+ "',"
					+ "'" + mi.restaurant 	+ "',"
					+ "'" + mi.cost		 	+ "',"
					+ "'" + mi.calories 	+ "',"
					+ "'" + mi.fat 			+ "',"
					+ "'" + mi.satFat		+ "',"
					+ "'" + mi.transFat 	+ "',"
					+ "'" + mi.cholesterol 	+ "',"
					+ "'" + mi.sodium 		+ "',"
					+ "'" + mi.carb 		+ "',"
					+ "'" + mi.fiber 		+ "',"
					+ "'" + mi.sugars 		+ "',"
					+ "'" + mi.protein 		+ "',"
					+ "'" + array		 	+ "',"
					+ "'" + mi.monday 		+ "',"
					+ "'" + mi.tuesday 		+ "',"
					+ "'" + mi.wednesday 	+ "',"
					+ "'" + mi.thursday 	+ "',"
					+ "'" + mi.friday 		+ "',"
					+ "'" + mi.saturday 	+ "',"
					+ "'" + mi.sunday 		+ "');";
			stmt.executeUpdate(sql);
			
			System.out.println("Successfully added menu item " + mi.name + " " + mi.restaurant);
		} catch (Exception e) {
			System.out.println(e.getClass() + ": " + e.getMessage());
		}
		try {
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.out.println(e.getClass() + ": " + e.getMessage());
		}
	}

	public JSONArray getMenuItems() {
		Connection c = null;
		Statement stmt = null;
		JSONArray json = null;
		try {
			c = Main.getConnection();
			stmt = c.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT json_agg(\"" + MENU_TABLE_NAME + "\") FROM \"" + MENU_TABLE_NAME + "\";");
			
			/*
			//sql to json
			json = new JSONArray();
			ResultSetMetaData rsmd = rs.getMetaData();
			while(rs.next()) {
			  int numColumns = rsmd.getColumnCount();
			  JSONObject obj = new JSONObject();
			  for (int i=1; i<=numColumns; i++) {
			    String column_name = rsmd.getColumnName(i);
			    if(rsmd.getColumnType(i) == Types.ARRAY) {
			    	obj.put(column_name, rs.getArray(column_name));
			    }
			    else{
			    	obj.put(column_name, rs.getObject(column_name));
			    }
			  }
			  json.put(obj);
			}
			*/
			
			System.out.println(rs.getObject(0));
			json = new JSONArray(rs.getObject(0));
			
			rs.close();
			stmt.close();
			c.close();

			System.out.println("Retrieved menu items");
		} catch (Exception e) {
			System.out.println(e.getClass() + ": " + e.getMessage());
		}

		return json;
	}
	
	public void updateMenuItems() throws Exception {
		resetDatabase(RESET_PASSWORD);
		Scraper.addAllMenuItems(this);
	}
}
