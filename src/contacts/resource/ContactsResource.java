package contacts.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import contacts.resource.service.Contact;
import contacts.resource.service.ContactsDAO;
import contacts.resource.service.DaoFactory;
@Path("/contacts")
public class ContactsResource {
	private Map<String, String> greetings = new HashMap<>();
	private ContactsDAO dao;
	@Context
	UriInfo uriInfo;
	
	public ContactsResource() {
		
		dao = DaoFactory.getInstance().getContactDao();
	}

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Contact getID( @PathParam("id")String id ) {
		return dao.find(id);
	}

	@GET
	@Produces("text/html")
	public String getHtmlGreeting() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><head><title>Greetings!</title></head>");
		sb.append("\n<body>\n");
		sb.append("<h1 style=\"color:red\">Hello, Web Surfer, > w <</h1>\n");
		sb.append("</body>\n</html>\n");
		return sb.toString();
	}
	
	/**
	 * Put a greeting. JAX-RS automatically provides an InputStream
	 * or Reader (!) to read the request body.
	 * @param name name derived from path parameter
	 * @param reader body of the request
	 */
	@PUT
	@Path("{name}")
	public Response putGreeting(@PathParam("name") String name, Reader reader) {
		BufferedReader buff = new BufferedReader(reader);
		try {
			URI uri = uriInfo.getAbsolutePath();
			String greeting = buff.readLine().trim();
			greetings.put(name, greeting);
			System.out.printf("New greeting for %s is %s\n", name, greeting);
			return Response.created(uri).build();			
		} catch (IOException | NullPointerException ioe) {
			return Response.serverError().build();
		} finally {
			try { buff.close(); } catch (IOException e) { /* ignore */ }
		}
	}
	
	@POST
	@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } ) 
	public Response post(JAXBElement<Contact> element, @Context UriInfo uriInfo )
	      {
	            Contact contact = element.getValue();
	            if(dao.save( contact )){
	            	URI uri = uriInfo.getAbsolutePath();
	            	return Response.created(uri).build();	
	            }
	            return Response.notModified().build();
	            
	            
	}
}
