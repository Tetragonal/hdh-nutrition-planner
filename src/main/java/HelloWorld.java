import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONObject;

public class HelloWorld extends AbstractHandler
{
    @SuppressWarnings("deprecation")
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
        response.getWriter().println("blah " + target + "<br />blah2 " + baseRequest + "<br />blah3 " + request + "<br />blah4 " + response);
        if ("POST".equalsIgnoreCase(request.getMethod())) 
        {
            String resultString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        	JSONObject result= new JSONObject(resultString);
        	response.getWriter().println("<br /" + result);
        	response.getWriter().println("<br /" + result.getString("num"));
        	response.getWriter().println("<br /" + result.getString("text"));
        }

        // Inform jetty that this request has now been handled
        baseRequest.setHandled(true);
    }

    public static void main( String[] args ) throws Exception
    {
        Server server = new Server(Integer.parseInt(args[0]));
        server.setHandler(new HelloWorld());

        System.out.println(args[0]);
        server.start();
        server.join();
    }
}