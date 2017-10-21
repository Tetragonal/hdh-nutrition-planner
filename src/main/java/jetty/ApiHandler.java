package jetty;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONObject;

public class ApiHandler extends AbstractHandler
{
    @Override
    public void handle( String target,
                        Request baseRequest,
                        HttpServletRequest request,
                        HttpServletResponse response ) throws IOException,
                                                      ServletException
    {
    	System.out.println("HelloWorld");
        // Declare response encoding and types
        response.setContentType("text/html; charset=utf-8");

        // Declare response status code
        response.setStatus(HttpServletResponse.SC_OK);

        // Write back response
        if ("POST".equalsIgnoreCase(request.getMethod())) 
        {
            String resultString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        	JSONObject result= new JSONObject(resultString);
        	JSONObject parsedResult = new JSONObject();
        	try {
        		switch(result.getString("operation")) {
        			case "test":
        				parsedResult.put("num", 2*Integer.parseInt(result.getString("num")));
        	        	parsedResult.put("text", cipher(result.getString("text"),13));
        	        	response.getWriter().println(parsedResult.toString());
        				break;
        			case "reset":
        				resetDatabase();
        				break;
        				
        			default:
        				break;
        		}
        	}catch(Exception e) {
        		System.out.println("Error, unexpected input");
        	}
        	
        }else {
        	response.getWriter().print("Error");
        }

        // Inform jetty that this request has now been handled
        baseRequest.setHandled(true);
    }
	//https://stackoverflow.com/questions/19108737/java-how-to-implement-a-shift-cipher-caesar-cipher
	private static String cipher(String msg, int shift){
	    String s = "";
	    for(int x = 0; x < msg.length(); x++){
	        char c = (char)(msg.charAt(x) + shift);
	        if (c > 'z')
	            s += (char)(msg.charAt(x) - (26-shift));
	        else
	            s += (char)(msg.charAt(x) + shift);
	    }
	    return s;
	}
	
	private void resetDatabase() {
		Connection c = null;
		Statement stmt = null;	
		try {
			c = Main.getConnection();
			stmt = c.createStatement();
			
			String sql = "DROP TABLE menu_items";
			stmt.execute(sql);
			
			sql = "CREATE TABLE menu_items ("
					+ "name varchar(255),"
					+ "restaurant varchar(255),"
					+ "cost money,"
					+ "calories int,"
					+ "fat float8,"
					+ "saturated_fat float8,"
					+ "trans_fat float8,"
					+ "cholesterol float8,"
					+ "sodium float8,"
					+ "carbohydrates float8,"
					+ "fiber float8,"
					+ "sugars float8,"
					+ "protein float8,"
					+ "allergens varchar(255)[],"
					+ "monday boolean,"
					+ "tuesday boolean,"
					+ "wednesday boolean,"
					+ "thursday boolean,"
					+ "friday boolean,"
					+ "saturday boolean,"
					+ "sunday boolean"
					+ ");";
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
			System.out.println("Successfully reset database");
		}catch (Exception e) {
			System.out.println(e.getClass()+": "+e.getMessage());
		}
	}
}