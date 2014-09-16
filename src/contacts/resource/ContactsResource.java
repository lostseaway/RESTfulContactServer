package contacts.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
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
	public Contact getContactID( @PathParam("id")String id ) {
		return dao.find(Long.parseLong(id));
	}

	@GET
	@Produces("text/xml")
	public Response getContects(@QueryParam("q") String q) {
		List<Contact> arr;
		GenericEntity<List<Contact>> out;
		if(q==null){
			arr = dao.findAll();
			out = new GenericEntity<List<Contact>>(arr) {};
			return Response.ok(out).build();
		}
		else{
			arr = dao.getByQuery(q);
			if(arr!=null){
				out = new GenericEntity<List<Contact>>(arr) {};
				return Response.ok(out).build();
			}
			return Response.noContent().build();
		}
			
	}
	
	
	/**
	 * Put a greeting. JAX-RS automatically provides an InputStream
	 * or Reader (!) to read the request body.
	 * @param name name derived from path parameter
	 * @param reader body of the request
	 */
	@PUT
	@Path("{name}")
	public Response putGreeting(JAXBElement<Contact> element, @PathParam("id") String id) throws URISyntaxException {
		Contact contact = element.getValue();
		contact.setId(id);
		dao.update(contact);
		return Response.created(new URI("localhost:8080/contacts/"+id)).build();
	}
	
	@POST
	@Consumes(MediaType.TEXT_XML ) 
	public Response post(JAXBElement<Contact> element, @Context UriInfo uriInfo )
	      {
	            Contact contact = element.getValue();
	            if(dao.save(contact)){
	            	URI uri = uriInfo.getAbsolutePath();
	            	return Response.created(uri).build();	
	            }
	            return Response.serverError().build();
	            
	            
	}
	
}
