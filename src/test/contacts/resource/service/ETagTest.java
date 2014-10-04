package test.contacts.resource.service;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import contacts.JettyMain;
import contacts.resource.service.Contact;

public class ETagTest {

	Server server;
	String url;
	HttpClient client = new HttpClient();

	@Before
	public void beforeTest() throws Exception {
		// Start Jetty
		JettyMain.startServer(8080);
		server = new Server(8080);

		// Setup Clients
		client.start();

		// Setup URL
		url = "http://localhost:8080/contacts/";
	}

	/**
	 * UnMarshalling method for changing byte array -> contact class
	 * 
	 * @param byteArray
	 * @return contact
	 */
	private Contact unMarshall(byte[] byteArray) {
		InputStream bodyStream = new ByteArrayInputStream(byteArray);
		try {
			Contact contact = new Contact();
			JAXBContext context = JAXBContext.newInstance(Contact.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			contact = (Contact) unmarshaller.unmarshal(bodyStream);
			return contact;
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	@After
	public void afterTest() throws Exception {
		// Stop Jetty
		System.out.println("Stop Jetty");
		JettyMain.stopServer();
		server.stop();
		client.stop();
	}

	@Test
	public void testGET() throws InterruptedException, TimeoutException,
			ExecutionException {
		Request request = client.newRequest(url)
				.header(HttpHeader.IF_NONE_MATCH, null).method(HttpMethod.GET);
		ContentResponse res = request.send();

		// First GET
		assertEquals("Expect 200 OK", Status.OK.getStatusCode(),
				res.getStatus());

		String etag = res.getHeaders().get(HttpHeader.ETAG);
		request = client.newRequest(url).header(HttpHeader.IF_NONE_MATCH, etag)
				.method(HttpMethod.GET);

		// GET after add ETAG
		res = request.send();
		assertEquals("Expect NOT Modified",
				Status.NOT_MODIFIED.getStatusCode(), res.getStatus());
	}

	@Test
	public void testPOST() throws InterruptedException, TimeoutException,
			ExecutionException {
		StringContentProvider content = new StringContentProvider(
				"<contact id=\"999\">" + "<title>Title!</title>"
						+ "<name>unknow</name>"
						+ "<email>email@mail.com</email>"
						+ "<phoneNumber>0819999999</phoneNumber>"
						+ "</contact>");
		Request request = client.newRequest(url)
				.content(content, "application/xml").method(HttpMethod.POST);

		ContentResponse res = request.send();
		assertEquals("Expect 201 created", Status.CREATED.getStatusCode(),
				res.getStatus());

		String etag = res.getHeaders().get(HttpHeader.ETAG);

		request = client.newRequest(url).content(content, "application/xml")
				.method(HttpMethod.POST);

		res = request.send();
		assertEquals("Should response 409 Conflict",
				Status.CONFLICT.getStatusCode(), res.getStatus());

		request = client.newRequest(url + 999).method(HttpMethod.DELETE);
		res = request.send();
	}

	@Test
	public void testPUT() throws InterruptedException, TimeoutException,
			ExecutionException {
		StringContentProvider content = new StringContentProvider(
				"<contact id=\"999\">" + "<title>Title!</title>"
						+ "<name>unknow</name>"
						+ "<email>email@mail.com</email>"
						+ "<phoneNumber>0819999999</phoneNumber>"
						+ "</contact>");

		Request request = client.newRequest(url)
				.content(content, "application/xml").method(HttpMethod.POST);

		ContentResponse res = request.send();
		assertEquals("Expect 201 created", Status.CREATED.getStatusCode(),
				res.getStatus());

		request = client.newRequest(url + 999).method(HttpMethod.GET);
		res = request.send();
		String etag = res.getHeaders().get(HttpHeader.ETAG);

		content = new StringContentProvider("<contact id=\"999\">"
				+ "<title>Title!</title>" + "<name>edited</name>"
				+ "<email>email@mail.com</email>"
				+ "<phoneNumber>0819999999</phoneNumber>" + "</contact>");

		request = client.newRequest(url + 999)
				.content(content, "application/xml")
				.header(HttpHeader.IF_MATCH, etag).method(HttpMethod.PUT);

		res = request.send();
		assertEquals("Expect 201 OK", Status.CREATED.getStatusCode(),
				res.getStatus());

	}

	@Test
	public void testDELETE() throws InterruptedException, TimeoutException,
			ExecutionException {
		StringContentProvider content = new StringContentProvider(
				"<contact id=\"999\">" + "<title>Title!</title>"
						+ "<name>unknow</name>"
						+ "<email>email@mail.com</email>"
						+ "<phoneNumber>0819999999</phoneNumber>"
						+ "</contact>");

		Request request = client.newRequest(url)
				.content(content, "application/xml").method(HttpMethod.POST);
		ContentResponse res = request.send();
		String etag = res.getHeaders().get(HttpHeader.ETAG);

		request = client.newRequest(url + 999).header(HttpHeader.IF_NONE_MATCH, etag).method(HttpMethod.DELETE);
		res = request.send();
		assertEquals("Expect 200",
				Status.OK.getStatusCode(), res.getStatus());
	}
}
