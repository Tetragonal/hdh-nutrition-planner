package jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class Main {
    public static void main( String[] args ) throws Exception
    {
        Server server = new Server(Integer.parseInt(args[0]));
        
        
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });

        resource_handler.setResourceBase(".");
		
        ContextHandler context = new ContextHandler();
        context.setContextPath( "/hello" );
        context.setHandler(new ApiHandler());
        
        
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { context, resource_handler, new TestHandler()});
        server.setHandler(handlers);
        
        //server.setHandler(context);

        System.out.println("Server started on port " + args[0]);
        server.start();
        server.join();
    }
}
