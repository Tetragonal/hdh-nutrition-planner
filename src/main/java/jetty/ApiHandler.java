package jetty;


import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import scraper.SQLHandler;

public class ApiHandler extends AbstractHandler {
	private static final int JSON_INDENTATION = 2;
	
	private SQLHandler sqlHandler;
	
	public ApiHandler() {
		super();
		sqlHandler = new SQLHandler();
	}
	
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		System.out.println("Received request");
		// Declare response encoding and types
		response.setContentType("text/html; charset=utf-8");
		
		// Write back response
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			// Declare response status code
			response.setStatus(HttpServletResponse.SC_OK);
			
			
			String resultString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
			System.out.println("Received \n" + resultString);
			JSONObject result = new JSONObject(resultString);
			JSONObject parsedResult = new JSONObject();
			try {
				switch (result.getString("operation")) {
				case "ping":
					parsedResult.put("response", "pong");
					break;
				case "test":
					parsedResult.put("success", true);
					parsedResult.put("num", 2 * Integer.parseInt(result.getString("num")));
					parsedResult.put("text", cipher(result.getString("text"), 13));
					break;
				case "reset":
					String password = result.getString("password");
					sqlHandler.resetTable(password, SQLHandler.MENU_TABLE_NAME);
					break;
				case "update":
					parsedResult.put("updating", Main.isUpdating);
					Thread t = new Thread(new Runnable() {
				         @Override
				         public void run() {
				        	 try {
								sqlHandler.updateMenuItems();
							} catch (Exception e) {
								e.printStackTrace(System.out);
							}
				         }
					});
					t.start();
					break;
				case "get":
					if(result.has("restaurants")) {
						try {
							ArrayList<String> restaurants = new ArrayList<String>();
							for(int i=0; i<result.getJSONArray("restaurants").length(); i++) {
								restaurants.add(result.getJSONArray("restaurants").getString(i));
							}
							parsedResult.put("menuData", sqlHandler.getMenuItems(restaurants));
						}catch(Exception e) {
							parsedResult.put("success", false);
						}
					}else {
						parsedResult.put("menuData", sqlHandler.getMenuItems());
					}
					break;
				case "status":
					parsedResult.put("updating", Main.isUpdating);
					break;
				case "restaurants":
					parsedResult.put("restaurants", sqlHandler.getRestaurantNames());
					break;
				default:
					parsedResult.put("success", false);
					break;
				}
				
				if(!parsedResult.has("success")) {
					parsedResult.put("success", true);
				}
				
				response.getWriter().println(parsedResult.toString());
			} catch (Exception e) {
				parsedResult.put("success", false);
				System.out.println("Error, unexpected input");
				System.out.println(e);
			}finally {
				// Inform jetty that this request has now been handled
				baseRequest.setHandled(true);
			}
		}else {
			System.out.println(request.getMethod());
		}
	}

	// https://stackoverflow.com/questions/19108737/java-how-to-implement-a-shift-cipher-caesar-cipher
	private static String cipher(String msg, int shift) {
		String s = "";
		for (int x = 0; x < msg.length(); x++) {
			char c = (char) (msg.charAt(x) + shift);
			if (c > 'z')
				s += (char) (msg.charAt(x) - (26 - shift));
			else
				s += (char) (msg.charAt(x) + shift);
		}
		return s;
	}
}