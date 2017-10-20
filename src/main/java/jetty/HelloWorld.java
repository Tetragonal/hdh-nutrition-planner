package jetty;
import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.json.JSONObject;

public class HelloWorld extends AbstractHandler
{
    @Override
    public void handle( String target,
                        Request baseRequest,
                        HttpServletRequest request,
                        HttpServletResponse response ) throws IOException,
                                                      ServletException
    {
        // Declare response encoding and types
        response.setContentType("text/html; charset=utf-8");

        // Declare response status code
        response.setStatus(HttpServletResponse.SC_OK);

        // Write back response
        if (request.getPathInfo().equals("/api") && "POST".equalsIgnoreCase(request.getMethod())) 
        {	
        	try {
	            String resultString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
	        	JSONObject result= new JSONObject(resultString);
	        	JSONObject parsedResult = new JSONObject();
	        	parsedResult.put("num", 2*Integer.parseInt(result.getString("num")));
	        	parsedResult.put("text", cipher(result.getString("text"),13));
	        	response.getWriter().println(parsedResult.toString());
        	}catch(Exception e) {
        		System.out.println("Error, unexpected input?");
        	}
        	
        }else {
        	response.getWriter().print("<h3>Hello world!</h3>");
        }

        // Inform jetty that this request has now been handled
        baseRequest.setHandled(true);
    }
	//https://stackoverflow.com/questions/19108737/java-how-to-implement-a-shift-cipher-caesar-cipher
	private String cipher(String msg, int shift){
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
    public static void main( String[] args ) throws Exception
    {
        Server server = new Server(Integer.parseInt(args[0]));
        
        /*
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });

        resource_handler.setResourceBase(".");
		*/
        HelloWorld h;
        ContextHandler context = new ContextHandler();
        context.setContextPath( "/hello" );
        context.setHandler(h = new HelloWorld() );
        
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { h, new TestHandler()});
        server.setHandler(handlers);
        

        
        //server.setHandler(context);

        System.out.println("Server started on port " + args[0]);
        server.start();
        server.join();
    }
}