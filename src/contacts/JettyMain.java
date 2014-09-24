package contacts;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import contacts.resource.service.DaoFactory;

public class JettyMain {
	static final int PORT = 8080;
	static Server server;
	public static void main(String[] args) throws Exception {
		
		startServer(8080);
		
		System.out.println("Server started.  Press ENTER to stop it.");
		int ch = System.in.read();
		
		System.out.println("Stopping server.");
		DaoFactory dao = DaoFactory.getInstance();
		dao.shutdown();
		
		System.out.println("Files written");
		stopServer();
	}
	public static void startServer(int port) throws Exception{
		server = new Server( PORT );
		ServletContextHandler context = new ServletContextHandler( ServletContextHandler.SESSIONS );
//		context.setContextPath("/");
		ServletHolder holder = new ServletHolder( org.glassfish.jersey.servlet.ServletContainer.class );
		context.addServlet( holder, "/*" );
		holder.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "contacts.resource");
		server.setHandler( context );
		
		System.out.println("Starting Jetty server on port " + PORT);
		server.start();
	}
	
	public static void stopServer() throws Exception{
		System.out.println("Stopping server.");
		server.stop();
	}
	
}

