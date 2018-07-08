package scraper;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

import jetty.Main;

public class SQLHandler {

	public static final String MENU_TABLE_NAME = "menu_items";
	protected final String MENU_TABLE_TEMP_NAME = "menu_items_temp";
	private final String RESET_PASSWORD = "reset123";

	public void resetTable(String password, String tableName) {
		Connection c = null;
		Statement stmt = null;
		if (password.equals(RESET_PASSWORD)) {
			try {
				c = Main.getConnection();
				stmt = c.createStatement();

				String sql = "TRUNCATE \"" + tableName + "\";";
				stmt.executeUpdate(sql);

				stmt.close();
				c.close();
				System.out.println("Successfully reset table");
			} catch (Exception e) {
				System.out.println(e.getClass() + ": " + e.getMessage());
			}
		}
	}

	protected void addMenuItems(ArrayList<MenuItem> items, String menuTableName) {
		for (MenuItem mi : items) {
			addMenuItem(mi, menuTableName);
		}
	}

	protected void addMenuItem(MenuItem mi, String menuTableName) {
		Connection c = null;
		Statement stmt = null;
		try {
			c = Main.getConnection();
			stmt = c.createStatement();

			Array array = c.createArrayOf("text", mi.allergens.toArray());

			String sql = "INSERT INTO \"" + menuTableName
					+ "\" (name, restaurant, cost, calories, fat, saturated_fat, trans_fat, "
					+ "cholesterol, sodium, carbohydrates, fiber, sugars, protein, allergens,"
					+ " monday, tuesday, wednesday, thursday, friday, saturday, sunday) "

					+ "VALUES (" + "'" + mi.name.replace("\'", "\'\'") + "'," + "'" + mi.restaurant.replace("\'", "\'\'") + "'," + "'" + mi.cost + "'," + "'"
					+ mi.calories + "'," + "'" + mi.fat + "'," + "'" + mi.satFat + "'," + "'" + mi.transFat + "'," + "'"
					+ mi.cholesterol + "'," + "'" + mi.sodium + "'," + "'" + mi.carb + "'," + "'" + mi.fiber + "',"
					+ "'" + mi.sugars + "'," + "'" + mi.protein + "'," + "'" + array + "'," + "'" + mi.monday + "',"
					+ "'" + mi.tuesday + "'," + "'" + mi.wednesday + "'," + "'" + mi.thursday + "'," + "'" + mi.friday
					+ "'," + "'" + mi.saturday + "'," + "'" + mi.sunday + "');";
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

			ResultSet rs = stmt.executeQuery("SELECT * FROM \"" + MENU_TABLE_NAME + "\";");

			// sql to json
			json = new JSONArray();
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				int numColumns = rsmd.getColumnCount();
				JSONObject obj = new JSONObject();
				for (int i = 1; i <= numColumns; i++) {
					String column_name = rsmd.getColumnName(i);
					if (rsmd.getColumnType(i) == Types.ARRAY) {
						obj.put(column_name, new JSONArray((String[]) rs.getArray(column_name).getArray()));
					} else {
						obj.put(column_name, rs.getObject(column_name));
					}
				}
				json.put(obj);
			}

			rs.close();
			stmt.close();
			c.close();

			System.out.println("Retrieved menu items");
		} catch (Exception e) {
			System.out.println(e.getClass() + ": " + e.getMessage());
		}

		return json;
	}

	public JSONArray getMenuItems(ArrayList<String> restaurants) {
		Connection c = null;
		Statement stmt = null;
		JSONArray json = null;
		try {
			c = Main.getConnection();
			stmt = c.createStatement();
			String list = "(";
			for(String s : restaurants) {
				list += "\'" + s.replaceAll("\'", "\'\'") + "\'" + ",";
			}
			list = list.substring(0, list.length()-1) + ")";
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"" + MENU_TABLE_NAME + "\" WHERE restaurant IN " + list + " ORDER BY restaurant;");

			// sql to json
			json = new JSONArray();
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				int numColumns = rsmd.getColumnCount();
				JSONObject obj = new JSONObject();
				for (int i = 1; i <= numColumns; i++) {
					String column_name = rsmd.getColumnName(i);
					if (rsmd.getColumnType(i) == Types.ARRAY) {
						obj.put(column_name, new JSONArray((String[]) rs.getArray(column_name).getArray()));
					} else {
						obj.put(column_name, rs.getObject(column_name));
					}
				}
				json.put(obj);
			}
			rs.close();
			
			stmt.close();
			c.close();

			System.out.println("Retrieved menu items");
		} catch (Exception e) {
			System.out.println(e.getClass() + ": " + e.getMessage());
		}

		return json;
	}
	
	public void copyTable(String tableOrig, String tableDest) {
		Connection c = null;
		Statement stmt = null;

		try {
			c = Main.getConnection();
			stmt = c.createStatement();

			String sql = "INSERT INTO \"" + tableDest + "\" SELECT * FROM \"" + tableOrig + "\";";
			stmt.executeUpdate(sql);

			stmt.close();
			c.close();
			System.out.println("Copied " + tableOrig + " to " + tableDest);
		} catch (Exception e) {
			System.out.println(e.getClass() + ": " + e.getMessage());
			System.out.println("Error copying table");
		}
	}

	public void updateMenuItems() throws Exception {
		if(!Main.isUpdating) {
			Main.isUpdating = true;
			// add all items to temp table
			resetTable(RESET_PASSWORD, MENU_TABLE_TEMP_NAME);
			Scraper.addAllMenuItems(this, MENU_TABLE_TEMP_NAME);
	
			// transfer all items from temp table to actual table
			resetTable(RESET_PASSWORD, MENU_TABLE_NAME);
			copyTable(MENU_TABLE_TEMP_NAME, MENU_TABLE_NAME);
			
			System.out.println("Done updating");
			Main.isUpdating = false;
		}
		else {
			System.out.println("Received attempt to update, but already updating");
		}
	}

	public JSONArray getRestaurantNames() throws Exception{
		Connection c = Main.getConnection();
		Statement stmt = c.createStatement();

		ResultSet rs = stmt.executeQuery("SELECT DISTINCT restaurant FROM \"" + MENU_TABLE_NAME + "\";");
		
		ArrayList<String> names = new ArrayList<String>();
		while(rs.next()) {
			names.add(rs.getString(1));
		}
		JSONArray json = new JSONArray(names);

		rs.close();
		stmt.close();
		c.close();

		System.out.println("Retrieved menu items");
		
		return json;
	}

	public String getLastModified() throws Exception {
		Connection c = Main.getConnection();
		Statement stmt = c.createStatement();
		
		//ResultSet rs = stmt.executeQuery("SELECT stats_reset FROM pg_stat_database ORDER BY stats_reset DESC LIMIT 2;");
		
		ResultSet rs = stmt.executeQuery("SELECT OBJECT_NAME(OBJECT_ID) AS TableName," +
 "last_user_update,*" +
"FROM sys.dm_db_index_usage_stats" +
"WHERE OBJECT_ID=OBJECT_ID( '" + MENU_TABLE_NAME + "')");
		
		rs.next();
		rs.next();
		
		String lastModifiedString = rs.getString(1);

		rs.close();
		stmt.close();
		c.close();


		
		return lastModifiedString;
	}
}
