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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import contacts.resource.service.Contact;
import contacts.resource.service.ContactDao;
import contacts.resource.service.Contacts;
import contacts.resource.service.DaoFactory;
import contacts.resource.service.mem.MemDaoFactory;

/**
 * ContactResource provides RESTful web resources using JAX-RS
 * 
 * @author Thunyathon Jaruchotrattanasakul 55105469782
 * 
 */
@Path("/contacts")
public class ContactsResource {
	private Map<String, String> greetings = new HashMap<>();
	private ContactDao dao;
	@Context
	UriInfo uriInfo;

	public ContactsResource() {
		dao = DaoFactory.getInstance().getContactDao();
		System.out.println(dao);
	}

	/**
	 * Get Contact by id
	 * 
	 * @param id
	 *            by url path
	 * @return contact xml
	 */
	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getContactID(@PathParam("id") String id,
			@HeaderParam("If-Match") String ifMatch,
			@HeaderParam("If-None-Match") String ifNoneMatch,
			@QueryParam("title") String title, @Context Request request) {

		Contact contact = dao.find(Long.parseLong(id));
		if(contact==null)return Response.noContent().build();
		Response response = checkMatch(ifMatch, ifNoneMatch,
				Integer.toString(contact.hashCode()), true);
		if (response != null) {
			return response;
		}

		CacheControl cc = new CacheControl();
		cc.setMaxAge(-1);
		cc.setPrivate(true);
		EntityTag etag = new EntityTag(Integer.toString(contact.hashCode()));

		return Response.ok(contact).cacheControl(cc).tag(etag).build();
	}

	/**
	 * Get Contact by QueryParam if no QueryParam it will return all contacts in
	 * list
	 * 
	 * @param q
	 *            substring that you what to search in contact title
	 * @return response with array of contacts
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getContects(@HeaderParam("If-Match") String ifMatch,
			@HeaderParam("If-None-Match") String ifNoneMatch,
			@QueryParam("title") String title, @Context Request request,
			@QueryParam("q") String q) {

		if (q == null) {
			Contacts out = dao.findAll();
			Response response = checkMatch(ifMatch, ifNoneMatch,
					Integer.toString(out.hashCode()), true);

			if (response != null) {
				return response;
			}
			CacheControl cc = new CacheControl();
			cc.setMaxAge(-1);
			cc.setPrivate(true);
			EntityTag etag = new EntityTag(Integer.toString(out.hashCode()));

			return Response.ok(out).cacheControl(cc).tag(etag).build();
		} else {
			Contacts out = dao.getByQuery(q);

			if (out != null) {
				Response response = checkMatch(ifMatch, ifNoneMatch,
						Integer.toString(out.hashCode()), true);

				if (response != null) {
					return response;
				}
				CacheControl cc = new CacheControl();
				cc.setMaxAge(-1);
				cc.setPrivate(true);
				EntityTag etag = new EntityTag(Integer.toString(out.hashCode()));

				return Response.ok(out).cacheControl(cc).tag(etag).build();
			}
			return Response.noContent().build();
		}

	}

	/**
	 * Put contact to update value
	 * 
	 * @param element
	 * @param id
	 *            contact id
	 * @return
	 * @throws URISyntaxException
	 */
	@PUT
	@Path("{id}")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response updateContact(@HeaderParam("If-Match") String ifMatch,
			@HeaderParam("If-None-Match") String ifNoneMatch,
			JAXBElement<Contact> element, @PathParam("id") String id,
			@Context UriInfo uriInfo) throws URISyntaxException {
		Contact contact = element.getValue();
		contact.setId(id);

		Response response = checkMatch(ifMatch, ifNoneMatch,
				Integer.toString(contact.hashCode()), false);
		if (response != null) {
			return response;
		}

		dao.update(contact);

		CacheControl cc = new CacheControl();
		cc.setMaxAge(-1);
		EntityTag etag = new EntityTag(Integer.toString(contact.hashCode()));

		return Response.created(uriInfo.getAbsolutePath()).cacheControl(cc)
				.tag(etag).build();
	}

	/**
	 * Create new Contact by POST
	 * 
	 * @param element
	 *            xml elements
	 * @param uriInfo
	 * @return
	 * @throws URISyntaxException
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_XML })
	public Response post(JAXBElement<Contact> element,
			@Context UriInfo uriInfo, @Context Request request)
			throws URISyntaxException {
		Contact contact = element.getValue();

		if (dao.save(contact)) {

			CacheControl cc = new CacheControl();
			cc.setMaxAge(-1);
			EntityTag etag = new EntityTag(Integer.toString(contact.hashCode()));
			URI uri = new URI(uriInfo.getAbsolutePath() + contact.getId());
			return Response.created(uri).cacheControl(cc).tag(etag).build();
		}
		return Response.serverError().build();

	}

	/**
	 * Delete contact by id
	 * 
	 * @param id
	 *            contact id
	 */
	@DELETE
	@Path("{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response deleteContact(@HeaderParam("If-Match") String ifMatch,
			@HeaderParam("If-None-Match") String ifNoneMatch,@PathParam("id") String id) {
		Contact contact = dao.find(Long.parseLong(id));
		if (contact != null) {
			
			Response response = checkMatch(ifMatch, ifNoneMatch,
					Integer.toString(contact.hashCode()), false);
			if(response!=null) return response;
			if (dao.delete(Long.parseLong(id)))
				return Response.ok().build();
		}
		return Response.notModified().build();
	}

	private Response checkMatch(String ifMatch, String ifNoneMatch,
			String etag, boolean isGetMethod) {
		System.out.println("IF-Match " + ifMatch + " , IF-None-Match "
				+ ifNoneMatch + " , etag " + etag);

		if (ifMatch != null && ifNoneMatch != null) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		if (ifMatch != null) {
			ifMatch = ifMatch.replace("\"", "");

			if (ifMatch.equals(etag)) {
				return null;
			} else {
				return Response.status(Response.Status.PRECONDITION_FAILED)
						.build();
			}
		}

		if (ifNoneMatch != null) {
			ifNoneMatch = ifNoneMatch.replace("\"", "");
			if (ifNoneMatch.equals(etag)) {
				if (isGetMethod) {
					return Response.status(Response.Status.NOT_MODIFIED)
							.build();
				} else {
					return Response.status(Response.Status.PRECONDITION_FAILED)
							.build();
				}
			} else {
				return null;
			}
		}
		return null;
	}
}
