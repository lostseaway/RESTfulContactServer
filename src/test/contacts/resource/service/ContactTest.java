package test.contacts.resource.service;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

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
import contacts.resource.service.Contact;

/**
 * Junite Test For Contact DAO
 * @author Thunyathon Jaruchotrattanasakul 55105469782
 *
 */
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
	/**
	 * UnMarshalling method for changing byte array -> contact class
	 * @param byteArray
	 * @return contact
	 */
	private Contact unMarshall( byte[] byteArray ) {
		InputStream bodyStream = new ByteArrayInputStream( byteArray );
		try {
			Contact contact = new Contact();
			JAXBContext context = JAXBContext.newInstance( Contact.class ) ;
			Unmarshaller unmarshaller = context.createUnmarshaller();	
			contact = (Contact) unmarshaller.unmarshal( bodyStream );
			return contact;
		} catch ( JAXBException e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	@After
	public void afterTest() throws Exception {
		//Stop Jetty
		System.out.println("Stop Jetty");
		JettyMain.stopServer();
		server.stop();
		client.stop();
	}
	
	/**
	 * Test Normal Get Request
	 * @throws Exception
	 */
	@Test
	public void testGet1() throws Exception {
		ContentResponse r = client.GET(url);
		assertEquals(Status.OK.getStatusCode(), r.getStatus());
	}
	
	/**
	 * Test bad url Get Request
	 * @throws Exception
	 */
	@Test
	public void testGet2() throws Exception {
		ContentResponse r = client.GET(url+100000003);
		System.out.println(r.getStatus());
		assertEquals(200, r.getStatus());
	}
	
	/**
	 * Test Success Post Request
	 * @throws Exception
	 */
	@Test
	public void testPost1() throws Exception {
		StringContentProvider contact = new StringContentProvider("<contact id=\"999\">" +
																"<title>Title!</title>" +
																"<name>unknow</name>" +
																"<email>email@mail.com</email>" +
																"<phoneNumber>0819999999</phoneNumber>"+
																"</contact>");
		ContentResponse r = client.newRequest(url).content(contact, "text/xml").method(HttpMethod.POST).send();
		assertEquals(Status.CREATED.getStatusCode(), r.getStatus());
		//Delete 
		Request req = client.newRequest(url+999);
		req = req.method(HttpMethod.DELETE);
		r = req.send();
	}
	
	/**
	 * Test Post and Checking content inside
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws ExecutionException
	 */
	@Test
	public void testPost2() throws InterruptedException, TimeoutException, ExecutionException{
		StringContentProvider contact = new StringContentProvider("<contact id=\"999\">" +
				"<title>Title!</title>" +
				"<name>unknow</name>" +
				"<email>email@mail.com</email>" +
				"<phoneNumber>0819999999</phoneNumber>"+
				"</contact>");
		ContentResponse r = client.newRequest(url).content(contact, "text/xml").method(HttpMethod.POST).send();
		r = client.GET(url+999);
		Contact con = this.unMarshall(r.getContent());
		System.out.println(r.getStatus());
		assertEquals("999",con.getId());
		assertEquals("Title!",con.getTitle());
		assertEquals("unknow",con.getName());
		assertEquals("email@mail.com",con.getEmail());
		assertEquals("0819999999",con.getPhoneNumber());
		
		//Delete 
		Request req = client.newRequest(url+9595);
		req = req.method(HttpMethod.DELETE);
		r = req.send();
		
	}
	
	/**
	 * Test success PUT Request
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws ExecutionException
	 */
	@Test
	public void testPut1() throws InterruptedException, TimeoutException, ExecutionException{
		StringContentProvider contact = new StringContentProvider("<contact id=\"999\">" +
				"<title>Title!</title>" +
				"<name>unknow</name>" +
				"<email>email@mail.com</email>" +
				"<phoneNumber>0819999999</phoneNumber>"+
				"</contact>");
		ContentResponse r = client.newRequest(url).content(contact, "text/xml").method(HttpMethod.POST).send();
		assertEquals(r.getStatus(),Status.CREATED.getStatusCode());
		
		contact = new StringContentProvider("<contact id=\"999\">" +
				"<title>tedit</title>" +
				"<name>unknow</name>" +
				"<email>email@mail.com</email>" +
				"<phoneNumber>0819999999</phoneNumber>"+
				"</contact>");
		r = client.newRequest(url+999).content(contact, "text/xml").method(HttpMethod.PUT).send();
		System.out.println(r.getContentAsString());
		assertEquals(Status.CREATED.getStatusCode(),r.getStatus());
		r = client.GET(url+999);
		Contact con = this.unMarshall(r.getContent());
		assertEquals("999",con.getId());
		assertEquals("tedit",con.getTitle());
		
		//Delete 
		Request req = client.newRequest(url+9595);
		req = req.method(HttpMethod.DELETE);
		r = req.send();
		
	}
	
	/**
	 * Test PUT Request and checking content inside
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws ExecutionException
	 */
	@Test
	public void TestPut() throws InterruptedException, TimeoutException, ExecutionException{
		StringContentProvider contact = new StringContentProvider("<contact id=\"999\">" +
				"<title>Title!</title>" +
				"<name>unknow</name>" +
				"<email>email@mail.com</email>" +
				"<phoneNumber>0819999999</phoneNumber>"+
				"</contact>");
		ContentResponse r = client.newRequest(url).content(contact, "text/xml").method(HttpMethod.POST).send();
		assertEquals(r.getStatus(),Status.CREATED.getStatusCode());
		
		contact = new StringContentProvider("<contact id=\"999\">" +
				"<title>tedit</title>" +
				"<name>qwerty</name>" +
				"<email>e@mail.com</email>" +
				"<phoneNumber>9999999</phoneNumber>"+
				"</contact>");
		r = client.newRequest(url+999).content(contact, "text/xml").method(HttpMethod.PUT).send();
		System.out.println(r.getContentAsString());
		assertEquals(Status.CREATED.getStatusCode(),r.getStatus());
		r = client.GET(url+999);
		Contact con = this.unMarshall(r.getContent());
		assertEquals("999",con.getId());
		assertEquals("tedit",con.getTitle());
		assertEquals("qwerty",con.getName());
		assertEquals("e@mail.com",con.getEmail());
		assertEquals("9999999",con.getPhoneNumber());
		//Delete 
		Request req = client.newRequest(url+9595);
		req = req.method(HttpMethod.DELETE);
		r = req.send();
	}
	
	/**
	 * Test success DELETE Request
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws ExecutionException
	 */
	@Test
	public void TestDelete1() throws InterruptedException, TimeoutException, ExecutionException{
		StringContentProvider contact = new StringContentProvider("<contact id=\"999\">" +
				"<title>Title!</title>" +
				"<name>unknow</name>" +
				"<email>email@mail.com</email>" +
				"<phoneNumber>0819999999</phoneNumber>"+
				"</contact>");
		ContentResponse r = client.newRequest(url).content(contact, "text/xml").method(HttpMethod.POST).send();
		assertEquals(r.getStatus(),Status.CREATED.getStatusCode());
		r = client.newRequest(url+999).method(HttpMethod.DELETE).send();
		assertEquals(Status.OK.getStatusCode(),r.getStatus());
		r = client.GET(url+999);
		assertEquals(Status.NO_CONTENT.getStatusCode(),r.getStatus());
	}
	
	/**
	 * Test Unsuccess DELETE Request
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws ExecutionException
	 */
	@Test
	public void TestDelete2() throws InterruptedException, TimeoutException, ExecutionException{
		ContentResponse r = client.newRequest(url+999).method(HttpMethod.DELETE).send();
		assertEquals(Status.NOT_MODIFIED.getStatusCode(),r.getStatus());
	}
	
	
	

}
