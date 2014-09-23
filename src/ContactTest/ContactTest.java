package ContactTest;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.Response.Status;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import contacts.JettyMain;

public class ContactTest {
	static int PORT = 8080;
	Server server;
	String url;
	HttpClient client = new HttpClient();
	String xmlHeaderS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><contacts><contact id=\"1000000000\"><title>Title</title><name>Name</name><email>email@mail.com</email><phoneNumber>0899999999</phoneNumber></contact>";
	String xmlHeaderE = "</contacts>";
	@Before
	public void beforeTest() throws Exception{
		//Start Jetty
		JettyMain.startServer(8080);
		server = new Server(8080);
		
		//Setup Clients
		client.start();
		
		//Setup URL
		url = "http://localhost:8080/contacts/";
	}
	
	@After
	public void afterTest() throws Exception {
		//Stop Jetty
		System.out.println("Stop Jetty");
		JettyMain.stopServer();
		server.stop();
		client.stop();
	}
	
	@Test
	public void testGet1() throws Exception {
		ContentResponse r = client.GET(url);
		System.out.println(r.getStatus());
		System.out.println(r.getContentAsString());
		assertEquals(Status.OK.getStatusCode(), r.getStatus());
	}
	
	@Test
	public void testGet2() throws Exception {
		ContentResponse r = client.GET(url+10000);
		System.out.println(r.getStatus());
		assertEquals(204, r.getStatus());
	}
	
	@Test
	public void testGet3() throws Exception {
		ContentResponse r = client.GET(url);
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><contacts><contact id=\"1000000000\"><title>Title</title><name>Name</name><email>email@mail.com</email><phoneNumber>0899999999</phoneNumber></contact></contacts>";
		assertEquals(content, r.getContentAsString());
	}
	
	@Test
	public void testPost() throws Exception {
		StringContentProvider contact = new StringContentProvider("<contact id=\"9595\">" +
																"<title>test title</title>" +
																"<name>contact'sadasdasdme</name>" +
																"<email>contact mail address</email>" +
																"<phoneNumber>contact's telephone number</phoneNumber>"+
																"</contact>");
		ContentResponse r = client.newRequest(url).content(contact, "text/xml").method(HttpMethod.POST).send();
		assertEquals(Status.CREATED.getStatusCode(), r.getStatus());
		//Delete for further test
		Request req = client.newRequest(url+9595);
		req = req.method(HttpMethod.DELETE);
		r = req.send();
	}
	
	@Test
	public void testParth() throws InterruptedException, TimeoutException, ExecutionException{
		StringContentProvider contact = new StringContentProvider("<contact id=\"9595\">" +
				"<title>test title</title>" +
				"<name>contact'sadasdasdme</name>" +
				"<email>contact mail address</email>" +
				"<phoneNumber>contact's telephone number</phoneNumber>"+
				"</contact>");
		ContentResponse r = client.newRequest(url).content(contact, "text/xml").method(HttpMethod.POST).send();
		r = client.GET(url+9595);
		
	}
	
	

}
