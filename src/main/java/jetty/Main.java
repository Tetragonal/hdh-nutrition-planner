package jetty;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class Main {
	public static boolean isUpdating = false;
	
    public static void main( String[] args ) throws Exception {
    	java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);  //turn off HtmlUnit logging
    	
    	int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
    	Server server = new Server(port);
            
        ApiHandler ah = new ApiHandler();
        ContextHandler apiContext = new ContextHandler();
        apiContext.setContextPath( "/api" );
        apiContext.setAllowNullPathInfo(true);
        apiContext.setHandler(ah);
        
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
        resource_handler.setResourceBase(".");
        
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {apiContext, resource_handler, new DefaultHandler()});
        server.setHandler(handlers);

        System.out.println("Server started on port " + port);
        
        server.start();
        server.join();
    }
    
    public static Connection getConnection() throws URISyntaxException, SQLException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));
        System.out.println("Database url: " + dbUri);

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        return DriverManager.getConnection(dbUrl, username, password);
    }
}